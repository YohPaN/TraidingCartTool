package com.example.testtextrecognitioncamera;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DisplayService {

    private DisplayService() {}

    public static void displayDataCard(Card card, ResultViewModel viewModel, FragmentManager fragmentManager) {
        if(card != null) {
            viewModel.card(card);
            returnInformation(fragmentManager);
        } else {
            viewModel.setCardFindState(false);
        }
    }

    public static void returnInformation(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ResultFragment resultFragment = ResultFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container_view, resultFragment);
        fragmentTransaction.commit();
    }
}
