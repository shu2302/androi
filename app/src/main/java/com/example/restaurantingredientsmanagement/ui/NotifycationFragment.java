package com.example.restaurantingredientsmanagement.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.data.dao.IngredientDAO;
import com.example.restaurantingredientsmanagement.data.dao.IngredientLotDAO;
import com.example.restaurantingredientsmanagement.data.dao.NotificationDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.Ingredient;
import com.example.restaurantingredientsmanagement.model.IngredientLot;
import com.example.restaurantingredientsmanagement.model.Notification;
import com.example.restaurantingredientsmanagement.ui.adapter.NotifyAdapter;

import java.time.LocalDate;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotifycationFragment} factory method to
 * create an instance of this fragment.
 */
public class NotifycationFragment extends Fragment {

    ImageButton btnDelAll, btnReload;
    AppDatabase appDatabase;
    NotificationDAO notificationDAO;
    IngredientDAO ingredientDAO;
    IngredientLotDAO ingredientLotDAO;
    RecyclerView rvNotifies;
    NotifyAdapter notifyAdapter;

    List<Notification> notificationList;

    public NotifycationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifycation, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appDatabase = AppDatabase.getInstance(requireContext());
        notificationDAO = appDatabase.createNotificationDAO();
        ingredientDAO = appDatabase.createIngredientDAO();
        ingredientLotDAO = appDatabase.createIngredientLotDAO();

        rvNotifies = view.findViewById(R.id.notifyRecyclerView);
        rvNotifies.setLayoutManager(new LinearLayoutManager(requireContext()));
        notifyAdapter = new NotifyAdapter(notification -> showToast("Đã xử lý thông báo"));
        rvNotifies.setAdapter(notifyAdapter);
        loadNotifies();

        btnDelAll = view.findViewById(R.id.btnDelNotifies);
        btnReload = view.findViewById(R.id.btnReloadNotification);

        btnDelAll.setOnClickListener(v -> {
            if (notificationList.isEmpty())
                showToast("Không có thông báo nào để xóa!");
            else
                showDeleteConfirmationDialog();
        });

        btnReload.setOnClickListener(v -> loadNotifies());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                notificationDAO.softDelete(notificationList.get(position).getNotificationId());
                reload();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvNotifies);
    }

    public void loadNotifies() {
        LocalDate today = LocalDate.now();
        List<Ingredient> ingredientList = ingredientDAO.getByMinQuantity(10f);
        for (Ingredient ingredient : ingredientList) {
            String notifyType = (ingredient.getCurrentQuantity() == 0) ? "hết" : "sắp hết";
            Notification existing = notificationDAO.getExistingNotification(ingredient.getId(), notifyType, today);
            if (existing == null) {
                Notification notification = Notification.builder()
                        .ingredientId(ingredient.getId())
                        .notifyType(notifyType)
                        .notifyDate(today)
                        .isDeleted(false)
                        .isResolved(false)
                        .build();
                notificationDAO.insert(notification);
            }
        }

        LocalDate targetDate = today.plusDays(2);
        List<IngredientLot> ingredientLots = ingredientLotDAO.getExpiringOrExpiredLots(targetDate);
        for (IngredientLot lot : ingredientLots) {
            String notifyType = "sắp hết hạn";
            Notification existing = notificationDAO.getExistingNotification(lot.getIngredientId(), notifyType, today);
            if (existing == null) {
                Notification notification = Notification.builder()
                        .ingredientId(lot.getIngredientId())
                        .notifyType(notifyType)
                        .notifyDate(today)
                        .isDeleted(false)
                        .isResolved(false)
                        .build();
                notificationDAO.insert(notification);
            }
        }

        notificationList = notificationDAO.getAllNotifications();
        notifyAdapter.updateList(notificationList);
        if (!notificationList.isEmpty()) {
            showToast("Bạn có thông báo mới!");
        }
    }

    public void reload() {
        notificationList = notificationDAO.getAllNotifications();
        notifyAdapter.updateList(notificationList);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa?");
        builder.setMessage("Bạn có muốn xóa hết thông báo?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            notificationList.forEach(notification -> notification.setDeleted(true));
            notificationDAO.updateNotifies(notificationList);
            reload();
            showToast("Đã xóa hết tất cả thông báo!");
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}