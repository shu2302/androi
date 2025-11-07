package com.example.restaurantingredientsmanagement.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.restaurantingredientsmanagement.data.dao.IngredientDAO;
import com.example.restaurantingredientsmanagement.data.dao.IngredientLotDAO;
import com.example.restaurantingredientsmanagement.data.dao.NotificationDAO;
import com.example.restaurantingredientsmanagement.data.dao.SupplierDAO;
import com.example.restaurantingredientsmanagement.data.dao.TransactionDAO;
import com.example.restaurantingredientsmanagement.model.Ingredient;
import com.example.restaurantingredientsmanagement.model.IngredientLot;
import com.example.restaurantingredientsmanagement.model.Notification;
import com.example.restaurantingredientsmanagement.model.Supplier;
import com.example.restaurantingredientsmanagement.model.Transaction;
import com.example.restaurantingredientsmanagement.utils.Converters;

@Database(entities = {Ingredient.class, Supplier.class, IngredientLot.class, Transaction.class, Notification.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract IngredientDAO createIngredientDAO();

    public abstract SupplierDAO createSupplierDAO();

    public abstract IngredientLotDAO createIngredientLotDAO();

    public abstract TransactionDAO createTransactionDAO();
    public abstract NotificationDAO createNotificationDAO();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "ingredient_management.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;
    }
}
