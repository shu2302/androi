package com.example.restaurantingredientsmanagement.utils;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class ImagePickerUtil {
    public static void openImageChooser(Fragment fragment, ActivityResultLauncher<Intent> imagePickerLauncher) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openImagePicker(fragment, imagePickerLauncher);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(fragment.requireContext(), "Quyền bị từ chối\n", Toast.LENGTH_SHORT).show();
            }
        };

        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Bạn cần cấp quyền để chọn ảnh.\nVào Cài đặt -> Quản lý quyền")
                .setPermissions(permissions)
                .check();
    }

    private static void openImagePicker(Fragment fragment, ActivityResultLauncher<Intent> imagePickerLauncher) {
        ImagePicker.with(fragment)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }
}
