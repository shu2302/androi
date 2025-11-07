package com.example.restaurantingredientsmanagement.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.restaurantingredientsmanagement.model.Ingredient;
import com.example.restaurantingredientsmanagement.model.Supplier;

import java.util.List;

@Dao
public interface IngredientDAO {
    @Insert
    long insert(Ingredient ingredient);
    @Update
    int update(Ingredient ingredient);
    @Delete
    void delete(Ingredient ingredient);
    @Query("SELECT * FROM ingredients WHERE isDeleted=0")
    List<Ingredient> getAll();
    @Query("select * from ingredients where name=:name and isDeleted=0")
    Ingredient existByName(String name);
    @Query("select name from ingredients where id=:id")
    String getNameById(int id);
    @Query("update ingredients set isDeleted=1 where id=:ingredientId")
    void softDelete(int ingredientId);
    @Query("select * from ingredients where name like :keyword and isDeleted=0")
    List<Ingredient> getByName(String keyword);
    @Query("select * from ingredients where currentQuantity<=:minQuantity and isDeleted=0")
    List<Ingredient> getByMinQuantity(float minQuantity);
}
