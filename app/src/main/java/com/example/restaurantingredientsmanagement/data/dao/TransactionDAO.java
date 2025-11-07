package com.example.restaurantingredientsmanagement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.restaurantingredientsmanagement.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDAO {
    @Query("SELECT * FROM transactions WHERE transactionId = :id")
    Transaction getById(int id);

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC, transactionId DESC")
    List<Transaction> getAllTransactions();

    @Query("select ingredientLotId from transactions where transactionId=:id ")
    long getIngredientLotIdByTransactionId(int id);
}
