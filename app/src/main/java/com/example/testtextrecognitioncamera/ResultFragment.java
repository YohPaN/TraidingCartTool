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
        viewModel.returnCardJson().observe(requireActivity(), cardMutable -> {
            binding.cardName.setText(cardMutable.getName());
            binding.cardCode.setText(cardMutable.getId());
            binding.setPrice.setText(String.format("%s€", cardMutable.getCardSet().getSetPrice()));
            binding.setName.setText(cardMutable.getCardSet().getSetName());
            binding.setRarity.setText(cardMutable.getCardSet().getSetRarity());
        });

        binding.returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setCardFindState(false);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(ResultFragment.this);
                transaction.commit();
            }
        });
    }
}