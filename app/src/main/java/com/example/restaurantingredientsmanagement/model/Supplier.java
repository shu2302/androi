package com.example.restaurantingredientsmanagement.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Locale;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity(tableName = "suppliers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    int supplierId;
    String name;
    String contactInfo;
    String address;
    boolean isDeleted;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return supplierId == supplier.supplierId && isDeleted == supplier.isDeleted && Objects.equals(name, supplier.name) && Objects.equals(contactInfo, supplier.contactInfo) && Objects.equals(address, supplier.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, name, contactInfo, address, isDeleted);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%d - %s", supplierId, name);
    }
}
