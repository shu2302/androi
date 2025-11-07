package com.example.restaurantingredientsmanagement.utils;

import java.text.DecimalFormat;

public class FormatDecimal {
    public static String formatToTwoDecimalPlaces(double number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }
}
