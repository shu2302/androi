package com.example.restaurantingredientsmanagement.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity(tableName = "ingredient_lot",
        foreignKeys = {
                @ForeignKey(
                        entity = Ingredient.class,
                        parentColumns = "id",
                        childColumns = "ingredientId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Supplier.class,
                        parentColumns = "supplierId",
                        childColumns = "supplierId",
                        onDelete = ForeignKey.CASCADE
                )},
        indices = {@Index("ingredientId"), @Index("supplierId")}
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientLot {
    @PrimaryKey(autoGenerate = true)
    long id;
    int ingredientId;
    int supplierId;
    double pricePerUnit;
    float quantity;
    LocalDate importDate;
    LocalDate expirationDate;
    boolean isDeleted;
}
