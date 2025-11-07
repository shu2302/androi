package com.example.restaurantingredientsmanagement.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity(tableName = "ingredients")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    int id;
    String name;
    String image;
    String unit;
    float currentQuantity;
    boolean isLowStock;
    boolean isDeleted;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id && Float.compare(currentQuantity, that.currentQuantity) == 0 && isLowStock == that.isLowStock && isDeleted == that.isDeleted && Objects.equals(name, that.name) && Objects.equals(image, that.image) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, unit, currentQuantity, isLowStock, isDeleted);
    }

    @Override
    public String toString() {
        return name;
    }
}
