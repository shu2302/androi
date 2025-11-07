package com.example.restaurantingredientsmanagement.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.restaurantingredientsmanagement.model.Supplier;

import java.util.List;

@Dao
public interface SupplierDAO {
    @Insert
    long insert(Supplier supplier);

    @Update
    int update(Supplier supplier);

    @Delete
    int delete(Supplier supplier);

    @Query("SELECT * FROM suppliers where isDeleted = 0")
    List<Supplier> getAllSupplier();

    @Query("select * from suppliers where name=:name and isDeleted=0")
    Supplier existByName(String name);

    @Query("update suppliers set isDeleted=1 where supplierId=:supplierId")
    void softDelete(int supplierId);

    @Query("select * from suppliers where name like :keyword and isDeleted=0")
    List<Supplier> getByName(String keyword);

    @Query("select name from suppliers where supplierId=:id")
    String getNameById(int id);
}
