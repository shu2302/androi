package com.example.restaurantingredientsmanagement.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.restaurantingredientsmanagement.model.Transaction;

import java.util.List;

public class HistoryTransDiffCallback extends DiffUtil.Callback {
    private final List<Transaction> oldList;
    private final List<Transaction> newList;

    public HistoryTransDiffCallback(List<Transaction> oldList, List<Transaction> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getTransactionId() == newList.get(newItemPosition).getTransactionId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
