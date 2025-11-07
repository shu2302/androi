package com.example.restaurantingredientsmanagement.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.restaurantingredientsmanagement.model.Notification;
import com.example.restaurantingredientsmanagement.model.Transaction;

import java.util.List;

public class NotifyDiffCallback extends DiffUtil.Callback {
    private final List<Notification> oldList;
    private final List<Notification> newList;

    public NotifyDiffCallback(List<Notification> oldList, List<Notification> newList) {
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
        return oldList.get(oldItemPosition).getNotificationId() == newList.get(newItemPosition).getNotificationId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
