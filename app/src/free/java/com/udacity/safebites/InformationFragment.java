package com.udacity.safebites;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

public class InformationFragment extends Fragment {
    public InformationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_information, container, false);
        final ProgressBar ad_progress_bar = root.findViewById(R.id.ad_progress_bar);

        MobileAds.initialize(getContext(), Objects.requireNonNull(getContext()).getResources().getString(R.string.banner_ad_unit_id));
        final AdView mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                ad_progress_bar.setVisibility(View.GONE);
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }
}
