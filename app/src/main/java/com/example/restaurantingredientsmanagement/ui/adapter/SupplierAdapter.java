package com.example.restaurantingredientsmanagement.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.model.Supplier;
import com.example.restaurantingredientsmanagement.utils.SupplierDiffCallback;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder> {
    private List<Supplier> suppliers = new ArrayList<>();
    private final OnItemClickListener listener;

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supplier, parent, false);
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        holder.bind(suppliers.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return suppliers.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Supplier supplier);
    }

    public SupplierAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<Supplier> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SupplierDiffCallback(this.suppliers, newList));
        this.suppliers.clear();
        this.suppliers.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class SupplierViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContactInfo;
        MaterialCardView cardView;

        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvSupplierName);
            this.tvContactInfo = itemView.findViewById(R.id.tvSupplierContactInfo);
            this.cardView = itemView.findViewById(R.id.supplierItemCard);
        }

        public void bind(Supplier supplier, OnItemClickListener listener) {
            tvName.setText(String.format("Tên: %s", supplier.getName()));
            tvContactInfo.setText(String.format("Liên hệ: %s", supplier.getContactInfo()));
            cardView.setOnClickListener(cv -> listener.onItemClick(supplier));
        }
    }


}
