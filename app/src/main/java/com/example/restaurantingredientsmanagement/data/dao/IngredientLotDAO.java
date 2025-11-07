package com.example.restaurantingredientsmanagement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.restaurantingredientsmanagement.model.IngredientLot;
import com.example.restaurantingredientsmanagement.model.Transaction;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface IngredientLotDAO {
    @Insert
    long insert(IngredientLot lot);

    @Insert
    long insertTransaction(Transaction transaction);


    @androidx.room.Transaction
    default boolean insertLotAndTransaction(IngredientLot lot, Transaction transaction) {
        long lotId = insert(lot);
        if (lotId > 0) {
            transaction.setIngredientLotId(lotId);
            insertTransaction(transaction);
            return true;
        }
        return false;
    }

    @Update
    void updateLots(List<IngredientLot> lots);

    @Query("SELECT * FROM ingredient_lot WHERE id = :id")
    IngredientLot getById(long id);

    @Query("SELECT * FROM ingredient_lot " +
            "WHERE ingredientId = :ingredientId AND quantity > 0 AND expirationDate >= :today AND isDeleted = 0 " +
            "ORDER BY expirationDate ASC")
    List<IngredientLot> getAvailableLots(int ingredientId, LocalDate today);

    @Query("SELECT * FROM ingredient_lot WHERE isDeleted = 0 AND expirationDate <= :targetDate")
    List<IngredientLot> getExpiringOrExpiredLots(LocalDate targetDate);
}
