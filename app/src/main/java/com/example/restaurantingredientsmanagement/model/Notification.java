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

@Entity(tableName = "notifications",
        foreignKeys = @ForeignKey(
                entity = Ingredient.class,
                parentColumns = "id",
                childColumns = "ingredientId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("ingredientId"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @PrimaryKey(autoGenerate = true)
    int notificationId;
    int ingredientId;
    String notifyType;
    LocalDate notifyDate;
    boolean isResolved;
    boolean isDeleted;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return notificationId == that.notificationId && ingredientId == that.ingredientId && isResolved == that.isResolved && isDeleted == that.isDeleted && Objects.equals(notifyType, that.notifyType) && Objects.equals(notifyDate, that.notifyDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, ingredientId, notifyType, notifyDate, isResolved, isDeleted);
    }
}
