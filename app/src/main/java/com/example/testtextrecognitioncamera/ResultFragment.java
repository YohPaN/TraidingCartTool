package com.example.testtextrecognitioncamera;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testtextrecognitioncamera.databinding.FragmentResultBinding;

import org.json.JSONException;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;

    public static ResultFragment newInstance() {
        return new ResultFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ResultViewModel viewModel = new ViewModelProvider(requireActivity()).get(ResultViewModel.class);
        viewModel.returnCardJson().observe(requireActivity(), json -> {
            try {
                binding.cardName.setText(json.getString("name"));
                binding.cardCode.setText(json.getString("id"));
                binding.setPrice.setText(String.format("%s€", json.getJSONObject("card_sets").getString("set_price")));
                binding.setName.setText(json.getJSONObject("card_sets").getString("set_name"));
                binding.setRarity.setText(json.getJSONObject("card_sets").getString("set_rarity"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        binding.returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(ResultFragment.this);
                transaction.commit();
            }
        });
    }
}