package com.example.restaurantingredientsmanagement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.restaurantingredientsmanagement.model.Notification;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface NotificationDAO {
    @Insert
    long insert(Notification notification);

    @Update
    int update(Notification notification);

    @Update
    void updateNotifies(List<Notification> notifications);

    @Query("update notifications set isDeleted=1 where notificationId=:notifyId")
    void softDelete(int notifyId);

    @Query("select * from notifications where isDeleted=0 and isResolved=0")
    List<Notification> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE ingredientId = :ingredientId" +
            " AND notifyType = :notifyType AND notifyDate=:notifyDate" +
            " LIMIT 1")
    Notification getExistingNotification(int ingredientId, String notifyType, LocalDate notifyDate);

}
