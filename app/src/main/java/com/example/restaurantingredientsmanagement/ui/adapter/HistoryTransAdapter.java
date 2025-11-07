package com.example.restaurantingredientsmanagement.ui.adapter;

import com.example.restaurantingredientsmanagement.R;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantingredientsmanagement.data.dao.IngredientDAO;
import com.example.restaurantingredientsmanagement.data.dao.IngredientLotDAO;
import com.example.restaurantingredientsmanagement.data.dao.SupplierDAO;
import com.example.restaurantingredientsmanagement.data.dao.TransactionDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.IngredientLot;
import com.example.restaurantingredientsmanagement.model.Transaction;
import com.example.restaurantingredientsmanagement.utils.Converters;
import com.example.restaurantingredientsmanagement.utils.HistoryTransDiffCallback;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryTransAdapter extends RecyclerView.Adapter<HistoryTransAdapter.HistoryTransViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    private List<Transaction> transactionList = new ArrayList<>();
    private final OnItemClickListener clickListener;

    public HistoryTransAdapter(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public HistoryTransViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_transaction, parent, false);
        return new HistoryTransViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryTransViewHolder holder, int position) {
        holder.bind(transactionList.get(position), clickListener);

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateList(List<Transaction> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new HistoryTransDiffCallback(transactionList, newList));
        this.transactionList.clear();
        this.transactionList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class HistoryTransViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransDate, tvNote;
        MaterialCardView cardView;

        public HistoryTransViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransDate = itemView.findViewById(R.id.tvTransDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            cardView = itemView.findViewById(R.id.historyTransItemCard);
        }

        public void bind(Transaction transaction, OnItemClickListener listener) {
            AppDatabase appDatabase = AppDatabase.getInstance(itemView.getContext());
            SupplierDAO supplierDAO = appDatabase.createSupplierDAO();
            TransactionDAO transactionDAO = appDatabase.createTransactionDAO();
            IngredientLotDAO ingredientLotDAO = appDatabase.createIngredientLotDAO();
            IngredientDAO ingredientDAO = appDatabase.createIngredientDAO();

            tvTransDate.setText(Converters.fromLocalDate(transaction.getTransactionDate()));

            IngredientLot ingredientLot = ingredientLotDAO
                    .getById(transactionDAO.getIngredientLotIdByTransactionId(transaction.getTransactionId()));
            String transactionType = transaction.getTransactionType();
            String ingredientName = ingredientDAO.getNameById(ingredientLot.getIngredientId());
            if (transactionType.equals("Nhập")) {
                String supplierName = supplierDAO.getNameById(ingredientLot.getSupplierId());
                tvNote.setText(String.format(
                        Locale.getDefault(),
                        "%s %.2f %s %s từ %s",
                        transactionType, transaction.getQuantity(),
                        transaction.getUnit(),
                        ingredientName,
                        supplierName));
                tvNote.setTextColor(Color.parseColor("#4F7942"));
            } else {
                tvNote.setText(String.format(
                        Locale.getDefault(),
                        "%s %.2f %s %s",
                        transactionType,
                        transaction.getQuantity(),
                        transaction.getUnit(),
                        ingredientName));
                tvNote.setTextColor(Color.RED);
            }

            cardView.setOnClickListener(cv -> listener.onItemClick(transaction));
        }
    }
}
