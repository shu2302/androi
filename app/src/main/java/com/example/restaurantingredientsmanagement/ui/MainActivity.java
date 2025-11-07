package com.example.restaurantingredientsmanagement.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.restaurantingredientsmanagement.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    MaterialToolbar topToolBar;

    SupplierFragment supplierFragment;
    HomeFragment homeFragment;
    IngredienFragment ingredienFragment;
    ImageFragment imageFragment;

    TransactionFragment transactionFragment;

    NotifycationFragment notifycationFragment;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        topToolBar = findViewById(R.id.toolbar);
        supplierFragment = new SupplierFragment();
        homeFragment = new HomeFragment();
        ingredienFragment = new IngredienFragment();
        imageFragment = new ImageFragment();
        transactionFragment = new TransactionFragment();
        notifycationFragment = new NotifycationFragment();


        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment, "Home")
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, supplierFragment, "Supplier")
                .hide(supplierFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ingredienFragment, "Ingredient")
                .hide(ingredienFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, imageFragment, "Image")
                .hide(imageFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, transactionFragment, "Transaction")
                .hide(transactionFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, notifycationFragment, "Notification")
                .hide(notifycationFragment)
                .commit();

        activeFragment = homeFragment;

        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                transaction.hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;
            } else if (itemId == R.id.nav_supplier) {
                transaction.hide(activeFragment).show(supplierFragment).commit();
                activeFragment = supplierFragment;
                return true;
            } else if (itemId == R.id.nav_ingredient) {
                transaction.hide(activeFragment).show(ingredienFragment).commit();
                ingredienFragment.reloadData();
                activeFragment = ingredienFragment;
                return true;
            } else if (itemId == R.id.nav_image) {
                transaction.hide(activeFragment).show(imageFragment).commit();
                imageFragment.reloadData();
                activeFragment = imageFragment;
                return true;
            } else if (itemId == R.id.nav_import_export) {
                transaction.hide(activeFragment).show(transactionFragment).commit();
                activeFragment = transactionFragment;
                return true;
            }

            return false;
        });

        MenuItem btnOpenNotification = topToolBar.getMenu().findItem(R.id.btnOpenNotification);
        btnOpenNotification.setOnMenuItemClickListener(item -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            fragmentTransaction.hide(activeFragment).show(notifycationFragment).commit();
            notifycationFragment.reload();
            activeFragment = notifycationFragment;
            return true;
        });

    }
}