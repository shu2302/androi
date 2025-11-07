package com.example.restaurantingredientsmanagement.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.restaurantingredientsmanagement.model.Supplier;

import java.util.List;

public class SupplierDiffCallback extends DiffUtil.Callback {
    private final List<Supplier> oldList;
    private final List<Supplier> newlist;

    public SupplierDiffCallback(List<Supplier> oldList, List<Supplier> newlist) {
        this.oldList = oldList;
        this.newlist = newlist;
    }

    @Override
    public int getOldListSize() {
        return this.oldList.size();
    }

    @Override
    public int getNewListSize() {
        return this.newlist.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getSupplierId() == newlist.get(newItemPosition).getSupplierId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newlist.get(newItemPosition));
    }
}
