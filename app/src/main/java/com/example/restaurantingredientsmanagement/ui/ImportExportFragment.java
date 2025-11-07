package com.example.restaurantingredientsmanagement.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.data.dao.IngredientDAO;
import com.example.restaurantingredientsmanagement.data.dao.IngredientLotDAO;
import com.example.restaurantingredientsmanagement.data.dao.SupplierDAO;
import com.example.restaurantingredientsmanagement.data.dao.TransactionDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.Ingredient;
import com.example.restaurantingredientsmanagement.model.IngredientLot;
import com.example.restaurantingredientsmanagement.model.Supplier;
import com.example.restaurantingredientsmanagement.model.Transaction;
import com.example.restaurantingredientsmanagement.utils.Converters;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImportExportFragment} factory method to
 * create an instance of this fragment.
 */
public class ImportExportFragment extends Fragment {

    AutoCompleteTextView ingredientDropDown, supplierDropDown;
    TextInputEditText edtIngredientQuantity, edtIngredientPrice, edtIngredientUnit, edtTransactionDate, edtIngredientExDate, edtNote;
    ImageButton btnPickTransDate, btnPickExDate;
    RadioButton radioImport, radioExport;
    FloatingActionButton btnAddTrans, btnClear;
    TextInputLayout tilSupplierDropDown;
    AppDatabase appDatabase;
    IngredientDAO ingredientDAO;
    SupplierDAO supplierDAO;

    Ingredient selectedIngredient;
    Supplier selectedSupplier;
    List<Ingredient> ingredientList;
    List<Supplier> supplierList;

    ArrayAdapter<Ingredient> ingredientAdapter;
    ArrayAdapter<Supplier> supplierAdapter;

    public ImportExportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_import_export, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appDatabase = AppDatabase.getInstance(requireContext());
        ingredientDAO = appDatabase.createIngredientDAO();
        supplierDAO = appDatabase.createSupplierDAO();
        IngredientLotDAO ingredientLotDAO = appDatabase.createIngredientLotDAO();
        TransactionDAO transactionDAO = appDatabase.createTransactionDAO();
        mapping(view);
        loadData();

        ingredientDropDown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                reloadIngredientList();
        });
        ingredientDropDown.setOnItemClickListener((parent, v, position, id) -> {
            selectedIngredient = ingredientList.get(position);
            edtIngredientUnit.setText(selectedIngredient.getUnit());
        });

        supplierDropDown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                reloadSupplierList();
        });
        supplierDropDown.setOnItemClickListener((parent, v, position, id) ->
                selectedSupplier = supplierList.get(position));

        btnAddTrans.setOnClickListener(v -> {
            if (radioImport.isChecked()) {
                handleImport(ingredientLotDAO);
            } else {
                handleExport(ingredientLotDAO, transactionDAO);
            }
        });
    }

    private void mapping(View view) {
        edtIngredientQuantity = view.findViewById(R.id.edtIngredientQuantity);
        edtIngredientPrice = view.findViewById(R.id.edtIngredientPrice);
        edtIngredientUnit = view.findViewById(R.id.edtIngredientUnit);
        edtTransactionDate = view.findViewById(R.id.edtTransactionDate);
        edtIngredientExDate = view.findViewById(R.id.edtIngredientExDate);
        edtNote = view.findViewById(R.id.edtNote);

        ingredientDropDown = view.findViewById(R.id.ingredientDropdownInTransaction);
        supplierDropDown = view.findViewById(R.id.supplierDropDown);
        tilSupplierDropDown = view.findViewById(R.id.tilSupplierDropDown);

        radioImport = view.findViewById(R.id.radioImport);
        radioExport = view.findViewById(R.id.radioExport);

        btnPickTransDate = view.findViewById(R.id.btnPickTransDate);
        btnPickExDate = view.findViewById(R.id.btnPickExDate);
        btnAddTrans = view.findViewById(R.id.btnAddTrans);
        btnClear = view.findViewById(R.id.btnClear);

        btnPickTransDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Chọn ngày giao dịch")
                    .build();

            datePicker.show(getParentFragmentManager(), "TRANS_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = sdf.format(new Date(selection));
                edtTransactionDate.setText(date);
            });
        });

        btnPickExDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Chọn ngày hết hạn")
                    .build();

            datePicker.show(getParentFragmentManager(), "EX_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = sdf.format(new Date(selection));
                edtIngredientExDate.setText(date);
            });
        });

        btnClear.setOnClickListener(v -> clearInputData());

        radioImport.setChecked(true);

        radioExport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnPickExDate.setEnabled(false);
                btnPickExDate.setAlpha(0.5f);

                supplierDropDown.setFocusable(false);
                supplierDropDown.setEnabled(false);
                supplierDropDown.setText("", false);
                tilSupplierDropDown.setEndIconOnClickListener(null);
                tilSupplierDropDown.setAlpha(0.5f);
            } else {
                btnPickExDate.setEnabled(true);
                btnPickExDate.setAlpha(1f);

                supplierDropDown.setFocusable(true);
                supplierDropDown.setEnabled(true);
                supplierDropDown.setAlpha(1f);
                tilSupplierDropDown.setEndIconOnClickListener(v -> supplierDropDown.showDropDown());
                tilSupplierDropDown.setAlpha(1f);
            }
        });

    }

    private void handleImport(IngredientLotDAO ingredientLotDAO) {
        TransactionInput transactionInput = extractTransactionInput();

        if (!validate(
                transactionInput.quantity,
                transactionInput.pricePerUnit,
                transactionInput.transDateText,
                transactionInput.exDateText))
            return;

        IngredientLot ingredientLot = IngredientLot.builder()
                .ingredientId(selectedIngredient.getId())
                .supplierId(selectedSupplier.getSupplierId())
                .pricePerUnit(transactionInput.pricePerUnit)
                .quantity(transactionInput.quantity)
                .importDate(Converters.toLocalDate(transactionInput.transDateText))
                .expirationDate(Converters.toLocalDate(transactionInput.exDateText))
                .isDeleted(false)
                .build();

        Transaction transaction = Transaction.builder()
                .transactionDate(Converters.toLocalDate(transactionInput.transDateText))
                .transactionType("Nhập")
                .quantity(transactionInput.quantity)
                .unit(transactionInput.unit)
                .note(transactionInput.note)
                .totalAmount(transactionInput.pricePerUnit * transactionInput.quantity)
                .build();

        boolean success = ingredientLotDAO.insertLotAndTransaction(ingredientLot, transaction);
        if (success) {
            float oldQuantity = selectedIngredient.getCurrentQuantity();
            selectedIngredient.setCurrentQuantity(oldQuantity + transactionInput.quantity);
            ingredientDAO.update(selectedIngredient);
            clearInputData();
            showToast("Thành công nhập nguyên liệu!");
        } else
            showToast("Có lỗi!");
    }

    private void handleExport(IngredientLotDAO ingredientLotDAO, TransactionDAO transactionDAO) {
        TransactionInput transactionInput = extractTransactionInput();

        if (!validate(
                transactionInput.quantity,
                transactionInput.pricePerUnit,
                transactionInput.transDateText,
                transactionInput.exDateText))
            return;

        List<Transaction> transactions = new ArrayList<>();
        float remaining = transactionInput.getQuantity();
        List<IngredientLot> availableLots = ingredientLotDAO.getAvailableLots(selectedIngredient.getId(), LocalDate.now());

        if (availableLots.isEmpty()) {
            showToast("Không đủ nguyên liệu!");
            ingredientDropDown.requestFocus();

        } else {
            for (IngredientLot lot : availableLots) {
                if (remaining <= 0)
                    break;

                float usedIngredient = Math.min(lot.getQuantity(), remaining);
                Transaction transaction = Transaction.builder()
                        .ingredientLotId(lot.getId())
                        .transactionDate(Converters.toLocalDate(transactionInput.transDateText))
                        .transactionType("Xuất")
                        .quantity(usedIngredient)
                        .unit(transactionInput.unit)
                        .note(transactionInput.note)
                        .totalAmount(usedIngredient * lot.getPricePerUnit())
                        .build();
                transactions.add(transaction);
                remaining -= usedIngredient;
            }
        }

        if (remaining > 0) {
            showToast("Không đủ nguyên liệu!");
            edtIngredientQuantity.requestFocus();
        } else {
            float totalUsed = 0f;
            for (Transaction transaction : transactions) {
                ingredientLotDAO.insertTransaction(transaction);
                for (IngredientLot lot : availableLots) {
                    if (lot.getId() == transaction.getIngredientLotId()) {
                        float remainQuantity = lot.getQuantity() - transaction.getQuantity();
                        lot.setQuantity(remainQuantity);
                        if (remainQuantity == 0f)
                            lot.setDeleted(true);
                        break;
                    }
                }
                totalUsed += transaction.getQuantity();
            }

            ingredientLotDAO.updateLots(availableLots);
            selectedIngredient.setCurrentQuantity(selectedIngredient.getCurrentQuantity() - totalUsed);
            ingredientDAO.update(selectedIngredient);
            clearInputData();
            showToast("Xuất hàng thành công");
        }

    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void clearInputData() {
        ingredientDropDown.setText("", false);
        supplierDropDown.setText("", false);
        edtIngredientQuantity.setText("");
        edtIngredientPrice.setText("");
        edtIngredientUnit.setText("");
        radioImport.setChecked(true);
        edtTransactionDate.setText("");
        edtIngredientExDate.setText("");
        edtNote.setText("");
    }

    private String extractDataFromEditText(TextInputEditText editText) {
        return Optional.ofNullable(editText.getText())
                .map(Editable::toString)
                .orElse("");
    }

    private TransactionInput extractTransactionInput() {
        float quantity = 0f;
        String quantityText = extractDataFromEditText(edtIngredientQuantity);
        if (!quantityText.isBlank()) {
            quantity = Float.parseFloat(quantityText);
        }

        double pricePerUnit = 0;
        String priceText = extractDataFromEditText(edtIngredientPrice);
        if (!priceText.isBlank()) {
            pricePerUnit = Double.parseDouble(priceText);
        }

        String unit = extractDataFromEditText(edtIngredientUnit);
        String transDateText = extractDataFromEditText(edtTransactionDate);
        String exDateText = extractDataFromEditText(edtIngredientExDate);
        String note = extractDataFromEditText(edtNote);

        return TransactionInput.builder()
                .quantity(quantity)
                .pricePerUnit(pricePerUnit)
                .unit(unit)
                .transDateText(transDateText)
                .exDateText(exDateText)
                .note(note)
                .build();
    }

    private boolean validate(float quantity, double price, String transDateText, String exDateText) {
        String ingredientName = Optional.ofNullable(ingredientDropDown.getText())
                .map(Editable::toString)
                .orElse("");
        String supplierName = Optional.ofNullable(supplierDropDown.getText())
                .map(Editable::toString)
                .orElse("");

        if (ingredientName.isBlank()) {
            ingredientDropDown.requestFocus();
            ingredientDropDown.setError("Vui lòng chọn nguyên liệu");
            return false;
        }

        if (radioImport.isChecked() && supplierName.isBlank()) {
            supplierDropDown.requestFocus();
            supplierDropDown.setError("Vui lòng chọn nhà cung cấp");
            return false;
        }

        if (quantity == 0f) {
            edtIngredientQuantity.requestFocus();
            edtIngredientQuantity.setError("Số lượng cần lớn hơn 0");
            return false;
        }

        if (price == 0) {
            edtIngredientPrice.requestFocus();
            edtIngredientPrice.setError("Giá cần lớn hơn 0");
            return false;
        }

        if (transDateText.isBlank()) {
            edtTransactionDate.requestFocus();
            edtTransactionDate.setError("Vui lòng chọn ngày giao dịch");
            return false;
        }

        if (radioImport.isChecked() && exDateText.isBlank()) {
            edtIngredientExDate.requestFocus();
            edtIngredientExDate.setError("Vui lòng chọn ngày hết hạn");
            return false;
        }

        return true;
    }

    public void loadData() {
        reloadIngredientList();
        reloadSupplierList();
    }

    public void reloadIngredientList() {
        ingredientList = ingredientDAO.getAll();
        ingredientAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ingredientList);
        ingredientDropDown.setAdapter(ingredientAdapter);
    }

    public void reloadSupplierList() {
        supplierList = supplierDAO.getAllSupplier();
        supplierAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                supplierList);
        supplierDropDown.setAdapter(supplierAdapter);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class TransactionInput {
        float quantity;
        double pricePerUnit;
        String unit;
        String transDateText;
        String exDateText;
        String note;
    }
}