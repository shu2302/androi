package com.example.restaurantingredientsmanagement.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class BitMapBase64 {
    public static String bitMapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static Bitmap base64ToBitMap(String base64) {
        byte[] decodedString = Base64.getDecoder().decode(base64);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
