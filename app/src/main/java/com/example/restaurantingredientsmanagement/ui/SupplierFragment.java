package com.example.restaurantingredientsmanagement.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.data.dao.SupplierDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.Supplier;
import com.example.restaurantingredientsmanagement.ui.adapter.SupplierAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Optional;

public class SupplierFragment extends Fragment {
    TextInputEditText edtName, edtContact, edtAddress;
    RecyclerView rvSuppliers;
    SupplierAdapter supplierAdapter;
    MaterialButton btnAdd, btnEdit, btnSearch, btnAll;

    List<Supplier> supplierList;
    AppDatabase appDatabase;
    SupplierDAO supplierDAO;
    Supplier selectedSupplier = null;

    public SupplierFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_supplier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);

        appDatabase = AppDatabase.getInstance(requireContext());
        supplierDAO = appDatabase.createSupplierDAO();
        supplierList = supplierDAO.getAllSupplier();
        supplierAdapter.updateList(supplierList);

        btnAdd.setOnClickListener(v -> handleSaveOrUpdate(supplierDAO, false));
        btnEdit.setOnClickListener(v -> handleSaveOrUpdate(supplierDAO, true));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position, supplierDAO);
            }
        });
        itemTouchHelper.attachToRecyclerView(rvSuppliers);

        btnSearch.setOnClickListener(v -> showSearchDialog(supplierDAO));
        btnAll.setOnClickListener(v -> supplierAdapter.updateList(supplierList));
    }

    private boolean validate(String name, String address, String contact) {
        if (name.isBlank()) {
            edtName.requestFocus();
            edtName.setError("Tên không được để trống");
            return false;
        }

        if (address.isBlank()) {
            edtAddress.requestFocus();
            edtAddress.setError("Địa chỉ không được để trống");
            return false;
        }

        if (!contact.matches("\\d{10}")) {
            edtContact.requestFocus();
            edtContact.setError("Số liên hệ phải gồm đúng 10 chữ số");
            return false;
        }

        return true;
    }

    public void handleSaveOrUpdate(SupplierDAO supplierDAO, boolean isUpdate) {
        String name = Optional.ofNullable(edtName.getText())
                .map(Editable::toString)
                .orElse("");
        String contact = Optional.ofNullable(edtContact.getText())
                .map(Editable::toString)
                .orElse("");
        String address = Optional.ofNullable(edtAddress.getText())
                .map(Editable::toString)
                .orElse("");
        if (!validate(name, address, contact))
            return;

        Supplier supplier;
        if (isUpdate) {
            if (selectedSupplier == null) {
                showToast("Vui lòng chọn nhà cung cấp trong danh sách!");
                return;
            }
            supplier = Supplier.builder()
                    .supplierId(selectedSupplier.getSupplierId())
                    .name(name)
                    .contactInfo(contact)
                    .address(address)
                    .isDeleted(false)
                    .build();
            if (supplierDAO.update(supplier) > 0) {
                supplierList = supplierDAO.getAllSupplier();
                supplierAdapter.updateList(supplierList);
                showToast("Cập nhật nhà cung cấp thành công!");
            } else {
                showToast("Có lỗi khi cập nhật nhà cung cấp!");
            }
        } else {
            if (supplierDAO.existByName(name) != null) {
                edtName.requestFocus();
                edtName.selectAll();
                edtName.setError("Nhà cung cấp đã tồn tại");
                return;
            }
            supplier = Supplier.builder()
                    .name(name)
                    .contactInfo(contact)
                    .address(address)
                    .isDeleted(false)
                    .build();
            if (supplierDAO.insert(supplier) > 0) {
                supplierList = supplierDAO.getAllSupplier();
                supplierAdapter.updateList(supplierList);
                showToast("Thêm nhà cung cấp thành công!");
            } else {
                showToast("Có lỗi khi thêm nhà cung cấp!");
            }
        }
        clearInputData();
        selectedSupplier = null;
    }

    private void mapping(View view) {
        edtName = view.findViewById(R.id.edtSupplierName);
        edtContact = view.findViewById(R.id.edtContactInfo);
        edtAddress = view.findViewById(R.id.edtAddress);

        rvSuppliers = view.findViewById(R.id.supplierRecyclerView);
        rvSuppliers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSuppliers.setNestedScrollingEnabled(true);
        supplierAdapter = new SupplierAdapter(supplier -> {
            showToast("Chọn: " + supplier.getName());
            edtName.setText(supplier.getName());
            edtContact.setText(supplier.getContactInfo());
            edtAddress.setText(supplier.getAddress());
            selectedSupplier = supplier;
        });
        rvSuppliers.setAdapter(supplierAdapter);


        btnAdd = view.findViewById(R.id.btnAddSupplier);
        btnEdit = view.findViewById(R.id.btnEditSupplier);
        btnSearch = view.findViewById(R.id.btnSearchSupplier);
        btnAll = view.findViewById(R.id.btnAll);
    }

    private void clearInputData() {
        edtName.setText("");
        edtAddress.setText("");
        edtContact.setText("");
        edtName.requestFocus();
        edtName.setError(null);
        edtAddress.setError(null);
        edtContact.setError(null);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showDeleteConfirmationDialog(int itemPosition, SupplierDAO supplierDAO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa?");
        builder.setMessage("Bạn chắc chắn muốn xóa mục này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            supplierDAO.softDelete(supplierList.get(itemPosition).getSupplierId());
            supplierList = supplierDAO.getAllSupplier();
            supplierAdapter.updateList(supplierList);
            showToast("Xóa nhà cung cấp thành công!");
            dialog.dismiss();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            supplierAdapter.notifyItemChanged(itemPosition);
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSearchDialog(SupplierDAO supplierDAO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_search_supplier, null);
        builder.setTitle("Tìm nhà cung cấp");
        builder.setView(view);

        TextInputEditText edtSearch = view.findViewById(R.id.edtSearchByName);
        builder.setPositiveButton("Tìm", (dialog, which) -> {
            String keyword = Optional.ofNullable(edtSearch.getText())
                    .map(Editable::toString)
                    .orElse("");
            if (keyword.isBlank()) {
                showToast("Không tìm được nhà cung cấp");
                dialog.dismiss();
            } else {
                List<Supplier> result = supplierDAO.getByName(String.format("%%%s%%", keyword));
                if (result.isEmpty()) {
                    showToast("Không tìm được nhà cung cấp");
                    dialog.dismiss();
                    return;
                }
                supplierAdapter.updateList(result);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}