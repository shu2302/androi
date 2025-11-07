package com.example.restaurantingredientsmanagement.utils;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Converters {
    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return (date == null) ? null : date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @TypeConverter
    public static LocalDate toLocalDate(String dateString) {
        return (dateString == null) ? null : LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
