package com.example.restaurantingredientsmanagement.ui;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.data.dao.IngredientDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.Ingredient;
import com.example.restaurantingredientsmanagement.ui.adapter.IngredientAdapter;
import com.example.restaurantingredientsmanagement.utils.BitMapBase64;
import com.example.restaurantingredientsmanagement.utils.ImagePickerUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredienFragment} factory method to
 * create an instance of this fragment.
 */
public class IngredienFragment extends Fragment {
    AutoCompleteTextView unitDropDown;
    ImageButton btnEdit, btnAdd, btnSearch, btnReload;
    TextInputEditText edtName;
    ImageView imageIngredientView;
    MaterialButton btnSelectImage;
    AppDatabase appDatabase;
    IngredientDAO ingredientDAO;
    RecyclerView rvIngredients;
    List<Ingredient> ingredientList;
    IngredientAdapter adapter;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Ingredient selectedIngredient = null;

    public IngredienFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredien, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(requireContext().getContentResolver(),
                                            result.getData().getData());
                            imageIngredientView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        showToast("Lỗi chọn ảnh");
                    } else {
                        showToast("Hủy chọn ảnh");
                    }
                });

        appDatabase = AppDatabase.getInstance(requireContext());
        ingredientDAO = appDatabase.createIngredientDAO();
        mapping(view);
        ingredientList = ingredientDAO.getAll();
        adapter.updateList(ingredientList);

        btnAdd.setOnClickListener(v -> handleSaveOrUpdate(ingredientDAO, false));
        btnEdit.setOnClickListener(v -> handleSaveOrUpdate(ingredientDAO, true));
        btnSearch.setOnClickListener(v -> showSearchDialog(ingredientDAO));
        btnReload.setOnClickListener(v -> adapter.updateList(ingredientList));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position, ingredientDAO);
            }
        });
        itemTouchHelper.attachToRecyclerView(rvIngredients);
    }

    private void handleSaveOrUpdate(IngredientDAO ingredientDAO, boolean isUpdate) {
        String name = Optional.ofNullable(edtName.getText())
                .map(Editable::toString)
                .orElse("");
        String unit = Optional.ofNullable(unitDropDown.getText())
                .map(Editable::toString)
                .orElse("");
        Bitmap imgBitMap = ((BitmapDrawable) imageIngredientView.getDrawable()).getBitmap();

        if (!validate(name, unit))
            return;

        Ingredient ingredient;
        if (isUpdate) {
            if (selectedIngredient == null) {
                showToast("Vui lòng chọn nguyên liệu trong danh sách!");
                return;
            }
            ingredient = Ingredient.builder()
                    .id(selectedIngredient.getId())
                    .name(name)
                    .unit(unit)
                    .image(BitMapBase64.bitMapToBase64(imgBitMap))
                    .currentQuantity(selectedIngredient.getCurrentQuantity())
                    .isLowStock(selectedIngredient.isLowStock())
                    .isDeleted(selectedIngredient.isDeleted())
                    .build();
            if (ingredientDAO.update(ingredient) > 0) {
                reloadData();
                showToast("Cập nhật nguyên liệu thành công!");
            } else {
                showToast("Có lỗi khi cập nhật nguyên liệu!");
            }

        } else {
            if (ingredientDAO.existByName(name) != null) {
                edtName.requestFocus();
                edtName.selectAll();
                edtName.setError("Nguyên liệu đã tồn tại");
                return;
            }
            long result;
            ingredient = Ingredient.builder()
                    .name(name)
                    .unit(unit)
                    .image(BitMapBase64.bitMapToBase64(imgBitMap))
                    .currentQuantity(0)
                    .isLowStock(false)
                    .isDeleted(false)
                    .build();
            result = ingredientDAO.insert(ingredient);
            if (result > 0) {
                reloadData();
                showToast("Thêm nguyên liệu thành công!");
            } else {
                showToast("Có lỗi khi thêm nguyên liệu!");
            }

        }
        clearInput();
        selectedIngredient = null;
    }

    private void mapping(View view) {
        edtName = view.findViewById(R.id.edtIngredientName);
        imageIngredientView = view.findViewById(R.id.ivImageIngredient);

        unitDropDown = view.findViewById(R.id.unit_dropdown);
        btnAdd = view.findViewById(R.id.btnAddIngredient);
        btnEdit = view.findViewById(R.id.btnEditIngredient);
        btnSearch = view.findViewById(R.id.btnSearchIngredient);
        btnReload = view.findViewById(R.id.btnReloadIngredientList);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);

        rvIngredients = view.findViewById(R.id.ingredientRecyclerView);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setNestedScrollingEnabled(true);
        adapter = new IngredientAdapter(ingredient -> {
            edtName.setText(ingredient.getName());
            unitDropDown.setText(ingredient.getUnit(), false);
            imageIngredientView.setImageBitmap(BitMapBase64.base64ToBitMap(ingredient.getImage()));
            selectedIngredient = ingredient;
            showToast("Chọn: " + ingredient.getName());
        });
        rvIngredients.setAdapter(adapter);

        String[] units = {"kilogram", "gram", "lít", "ml", "cái", "hộp"};
        ArrayAdapter<String> unitDropdownAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, units);
        unitDropDown.setAdapter(unitDropdownAdapter);
        unitDropDown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                unitDropDown.showDropDown();
        });

        btnSelectImage.setOnClickListener(
                v -> ImagePickerUtil.openImageChooser(this, imagePickerLauncher));
    }

    private boolean validate(String name, String unit) {
        if (name.isBlank()) {
            edtName.requestFocus();
            edtName.setError("Tên nguyên liệu không được để trống");
            return false;
        }

        if (unit.isBlank()) {
            unitDropDown.requestFocus();
            unitDropDown.setError("Vui lòng chọn đơn vị");
            return false;
        }
        return true;
    }

    private void clearInput() {
        edtName.setText("");
        unitDropDown.setText("", false);
        imageIngredientView.setImageResource(R.drawable.ic_placeholder);
        edtName.requestFocus();
        edtName.setError(null);
        unitDropDown.setError(null);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showDeleteConfirmationDialog(int itemPosition, IngredientDAO ingredientDAO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa?");
        builder.setMessage("Bạn chắc chắn muốn xóa mục này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            ingredientDAO.softDelete(ingredientList.get(itemPosition).getId());
            reloadData();
            showToast("Xóa nguyên liệu thành công!");
            dialog.dismiss();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            adapter.notifyItemChanged(itemPosition);
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSearchDialog(IngredientDAO ingredientDAO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_search_supplier, null);
        builder.setTitle("Tìm nguyên liệu");
        builder.setView(view);

        TextInputEditText edtSearch = view.findViewById(R.id.edtSearchByName);
        builder.setPositiveButton("Tìm", (dialog, which) -> {
            String keyword = Optional.ofNullable(edtSearch.getText())
                    .map(Editable::toString)
                    .orElse("");
            if (keyword.isBlank()) {
                showToast("Không tìm được nguyên liệu");
                dialog.dismiss();
            } else {
                List<Ingredient> result = ingredientDAO.getByName(String.format("%%%s%%", keyword));
                if (result.isEmpty()) {
                    showToast("Không tìm được nguyên liệu");
                    dialog.dismiss();
                    return;
                }
                adapter.updateList(result);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void reloadData() {
        ingredientList = ingredientDAO.getAll();
        adapter.updateList(ingredientList);
    }
}