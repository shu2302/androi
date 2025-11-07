package com.example.restaurantingredientsmanagement.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.restaurantingredientsmanagement.R;
import com.example.restaurantingredientsmanagement.data.dao.TransactionDAO;
import com.example.restaurantingredientsmanagement.data.db.AppDatabase;
import com.example.restaurantingredientsmanagement.model.Transaction;
import com.example.restaurantingredientsmanagement.ui.adapter.HistoryTransAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryTransactionFragment} factory method to
 * create an instance of this fragment.
 */
public class HistoryTransactionFragment extends Fragment {
    RecyclerView rvHistoryTrans;

    HistoryTransAdapter adapter;
    List<Transaction> historyTransList;
    AppDatabase appDatabase;
    TransactionDAO transactionDAO;

    public HistoryTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getInstance(requireContext());
        transactionDAO = appDatabase.createTransactionDAO();
        rvHistoryTrans = view.findViewById(R.id.transactionRecyclerView);
        rvHistoryTrans.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryTransAdapter(transaction -> showToast("Clicked"));
        rvHistoryTrans.setAdapter(adapter);
        loadData();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    public void loadData() {
        historyTransList = transactionDAO.getAllTransactions();
        adapter.updateList(historyTransList);
    }
}