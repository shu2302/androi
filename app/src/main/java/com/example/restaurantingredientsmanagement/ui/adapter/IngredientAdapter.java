package com.example.restaurantingredientsmanagement.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.model.Ingredient;
import com.example.restaurantingredientsmanagement.utils.BitMapBase64;
import com.example.restaurantingredientsmanagement.utils.FormatDecimal;
import com.example.restaurantingredientsmanagement.utils.IngredientDiffCallback;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
    }

    private List<Ingredient> ingredients = new ArrayList<>();

    private final OnItemClickListener listener;

    public IngredientAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateList(List<Ingredient> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new IngredientDiffCallback(this.ingredients, newList));
        this.ingredients.clear();
        this.ingredients.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUnit;
        ImageView imageView;
        MaterialCardView cardView;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvIngredientName);
            this.tvUnit = itemView.findViewById(R.id.tvUnitInItem);
            this.imageView = itemView.findViewById(R.id.ivImgInItem);
            this.cardView = itemView.findViewById(R.id.ingredientItemCard);
        }

        public void bind(Ingredient ingredient, OnItemClickListener listener) {
            tvName.setText(String.format(Locale.getDefault(), "%s có %s",
                    ingredient.getName(),
                    FormatDecimal.formatToTwoDecimalPlaces(ingredient.getCurrentQuantity())));
            tvUnit.setText(String.format("Đơn vị: %s", ingredient.getUnit()));
            String imgFromIngredient = ingredient.getImage();
            imageView.setImageBitmap(BitMapBase64.base64ToBitMap(imgFromIngredient));
            cardView.setOnClickListener(cv -> listener.onItemClick(ingredient));
        }
    }
}
