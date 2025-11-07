package com.example.restaurantingredientsmanagement.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.data.dao.IngredientDAO;
import com.example.restaurantingredientsmanagement.data.dao.NotificationDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.Notification;
import com.example.restaurantingredientsmanagement.utils.Converters;
import com.example.restaurantingredientsmanagement.utils.NotifyDiffCallback;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyCationViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    private List<Notification> notificationList = new ArrayList<>();
    private final NotifyAdapter.OnItemClickListener clickListener;

    public NotifyAdapter(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotifyAdapter.NotifyCationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifycation, parent, false);
        return new NotifyCationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyCationViewHolder holder, int position) {
        holder.bind(notificationList.get(position), clickListener);

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateList(List<Notification> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NotifyDiffCallback(notificationList, newList));
        this.notificationList.clear();
        this.notificationList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class NotifyCationViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifyDate, tvContent;
        MaterialCardView cardView;

        public NotifyCationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifyDate = itemView.findViewById(R.id.tvNotifyDate);
            tvContent = itemView.findViewById(R.id.tvContent);
            cardView = itemView.findViewById(R.id.notifyItemCard);
        }

        public void bind(Notification notification, OnItemClickListener listener) {
            AppDatabase appDatabase = AppDatabase.getInstance(itemView.getContext());
            IngredientDAO ingredientDAO = appDatabase.createIngredientDAO();
            NotificationDAO notificationDAO = appDatabase.createNotificationDAO();

            tvNotifyDate.setText(Converters.fromLocalDate(notification.getNotifyDate()));
            String content = ingredientDAO.getNameById(notification.getIngredientId());
            if (notification.getNotifyType().equals("sắp hết hạn"))
                content = String.format("%s %s sử dụng", content, notification.getNotifyType());
            else
                content = String.format("Nguyên liệu %s %s", content, notification.getNotifyType());
            tvContent.setText(content);
            cardView.setOnClickListener(cv -> {
                if (!notification.isResolved()) {
                    notification.setResolved(true);
                    notificationDAO.update(notification);
                    cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                }
                listener.onItemClick(notification);
            });
        }
    }
}
