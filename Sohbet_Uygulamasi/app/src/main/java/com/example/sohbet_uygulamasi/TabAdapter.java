package com.example.sohbet_uygulamasi;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentPagerAdapter {


    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                IsteklerFragment isteklerFragment = new IsteklerFragment();
                return isteklerFragment;
            case 1:
                MesajlarFragment mesajlarFragment = new MesajlarFragment();
                return mesajlarFragment;
            case 2:
                ArkadaslarFragment arkadaslarFragment = new ArkadaslarFragment();
                return arkadaslarFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "İstekler";
            case 1:
                return "Mesajlar";
            case 2:
                return "Arkadaşlar";
            default:
                return null;
        }
    }
}
