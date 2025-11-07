package com.example.restaurantingredientsmanagement.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity(tableName = "transactions",
        foreignKeys = @ForeignKey(
                entity = IngredientLot.class,
                parentColumns = "id",
                childColumns = "ingredientLotId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("ingredientLotId")
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    int transactionId;
    long ingredientLotId;
    LocalDate transactionDate;
    String transactionType;
    float quantity;
    String unit;
    String note;
    double totalAmount;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId && ingredientLotId == that.ingredientLotId && Float.compare(quantity, that.quantity) == 0 && Double.compare(totalAmount, that.totalAmount) == 0 && Objects.equals(transactionDate, that.transactionDate) && Objects.equals(transactionType, that.transactionType) && Objects.equals(unit, that.unit) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, ingredientLotId, transactionDate, transactionType, quantity, unit, note, totalAmount);
    }
}
