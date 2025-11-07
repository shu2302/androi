package com.example.restaurantingredientsmanagement.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.restaurantingredientsmanagement.model.Ingredient;

import java.util.List;

public class IngredientDiffCallback extends DiffUtil.Callback {
    private final List<Ingredient> oldList;
    private final List<Ingredient> newList;

    public IngredientDiffCallback(List<Ingredient> oldList, List<Ingredient> newList) {
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
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
