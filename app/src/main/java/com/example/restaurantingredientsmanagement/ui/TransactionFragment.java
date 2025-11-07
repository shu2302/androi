package com.example.restaurantingredientsmanagement.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.restaurantingredientsmanagement.R;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends Fragment {

    MaterialButton btnHistoryTransaction, btnPerformTransaction;
    HistoryTransactionFragment historyTransactionFragment;
    ImportExportFragment importExportFragment;
    Fragment activeFragment;


    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnHistoryTransaction = view.findViewById(R.id.btnHistoryTransaction);
        btnPerformTransaction = view.findViewById(R.id.btnPerformTransaction);
        btnPerformTransaction.setBackgroundColor(Color.BLACK);
        btnPerformTransaction.setTextColor(Color.WHITE);

        historyTransactionFragment = new HistoryTransactionFragment();
        importExportFragment = new ImportExportFragment();

        getChildFragmentManager().beginTransaction()
                .add(R.id.fragment_container_trans, importExportFragment, "ImportExport")
                .commit();

        getChildFragmentManager().beginTransaction()
                .add(R.id.fragment_container_trans, historyTransactionFragment, "HistoryTrans")
                .hide(historyTransactionFragment)
                .commit();

        activeFragment = importExportFragment;

        btnPerformTransaction.setOnClickListener(v -> {
            btnPerformTransaction.setBackgroundColor(Color.BLACK);
            btnPerformTransaction.setTextColor(Color.WHITE);
            btnHistoryTransaction.setBackgroundColor(Color.WHITE);
            btnHistoryTransaction.setTextColor(Color.BLACK);

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.hide(activeFragment).show(importExportFragment).commit();
            importExportFragment.loadData();
            activeFragment = importExportFragment;
        });

        btnHistoryTransaction.setOnClickListener(v -> {
            btnHistoryTransaction.setBackgroundColor(Color.BLACK);
            btnHistoryTransaction.setTextColor(Color.WHITE);
            btnPerformTransaction.setBackgroundColor(Color.WHITE);
            btnPerformTransaction.setTextColor(Color.BLACK);

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.hide(activeFragment).show(historyTransactionFragment).commit();
            historyTransactionFragment.loadData();
            activeFragment = historyTransactionFragment;
        });
    }
}