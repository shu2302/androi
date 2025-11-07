package com.example.restaurantingredientsmanagement.ui;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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
import com.example.restaurantingredientsmanagement.utils.BitMapBase64;
import com.example.restaurantingredientsmanagement.utils.ImagePickerUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    AutoCompleteTextView ingredientDropDown;
    ImageButton btnDel;
    MaterialButton btnSelectImage, btnUpdate;
    ImageView imageView;
    AppDatabase appDatabase;
    ArrayAdapter<Ingredient> adapter;
    List<Ingredient> ingredients;
    Ingredient selectedIngredient;

    IngredientDAO ingredientDAO;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);

        appDatabase = AppDatabase.getInstance(requireContext());
        ingredientDAO = appDatabase.createIngredientDAO();
        ingredients = ingredientDAO.getAll();
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ingredients);
        ingredientDropDown.setAdapter(adapter);
        ingredientDropDown.setOnItemClickListener(((parent, v, position, id) -> {
            selectedIngredient = ingredients.get(position);
            imageView.setImageBitmap(BitMapBase64.base64ToBitMap(selectedIngredient.getImage()));
            btnSelectImage.setEnabled(true);
            btnSelectImage.setAlpha(1f);
        }));

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(requireContext().getContentResolver(),
                                            result.getData().getData());
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        showToast("Lỗi chọn ảnh");
                    } else {
                        showToast("Hủy chọn ảnh");
                    }
                });

        btnSelectImage.setOnClickListener(v -> {
            if ("Chọn ảnh".equals(btnSelectImage.getText().toString())) {
                ImagePickerUtil.openImageChooser(this, imagePickerLauncher);
                btnSelectImage.setText("Hủy");
                btnSelectImage.setBackgroundColor(Color.RED);
                btnUpdate.setEnabled(true);
                btnUpdate.setAlpha(1f);
            } else {
                resetDataInput();
            }
        });

        btnUpdate.setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            selectedIngredient.setImage(BitMapBase64.bitMapToBase64(bitmap));
            ingredientDAO.update(selectedIngredient);
            ingredients = ingredientDAO.getAll();
            adapter.notifyDataSetChanged();
            showToast("Cập nhật ảnh thành công");
            resetDataInput();
        });

        btnDel.setOnClickListener(v -> {
            if (ingredientDropDown.getText().toString().isEmpty()) {
                showToast("Vui lòng chọn nguyên liệu!");
            } else
                showDeleteConfirmationDialog(selectedIngredient, ingredientDAO);

        });
    }

    private void mapping(View view) {
        ingredientDropDown = view.findViewById(R.id.ingredientDropdown);
        btnDel = view.findViewById(R.id.btnDelImgIngredient);
        btnSelectImage = view.findViewById(R.id.btnSelectImageInImageManage);
        btnSelectImage.setEnabled(false);
        btnUpdate = view.findViewById(R.id.btnUpdateImageInImageManage);
        imageView = view.findViewById(R.id.ivImageIngredientInImageManage);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void resetDataInput() {
        btnSelectImage.setText("Chọn ảnh");
        btnSelectImage.setEnabled(false);
        btnSelectImage.setBackgroundColor(Color.BLACK);
        btnSelectImage.setAlpha(0.5f);

        btnUpdate.setEnabled(false);
        btnUpdate.setAlpha(0.5f);

        imageView.setImageResource(R.drawable.ic_placeholder);
        ingredientDropDown.setText("", false);
    }

    public void reloadData() {
        resetDataInput();
        ingredients = ingredientDAO.getAll();
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ingredients);
        ingredientDropDown.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog(Ingredient selectedIngredient, IngredientDAO ingredientDAO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa?");
        builder.setMessage(String.format("Bạn chắc chắn muốn xóa ảnh của nguyên liệu %s ?", selectedIngredient.getName()));

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            imageView.setImageResource(R.drawable.ic_placeholder);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            selectedIngredient.setImage(BitMapBase64.bitMapToBase64(bitmap));
            ingredientDAO.update(selectedIngredient);
            resetDataInput();
            showToast("Xóa ảnh thành công!");
            dialog.dismiss();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}