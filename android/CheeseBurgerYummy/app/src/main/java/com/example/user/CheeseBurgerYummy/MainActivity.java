package com.example.user.CheeseBurgerYummy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");
    }

    // MARK: - Actions
    @OnClick(R.id.photoButton)
    public void actionPhotoButton(Button button) {
        Timber.i("clicked Photo Button");
        Log.d("AAAAA", "AAAAAA");
        Intent intent = new Intent(MainActivity.this, PhotoListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.householdButton)
    public void actionHouseHoldButton(Button button) {
        Timber.i("clicked Household Button");
        Log.d("BBBBB", "BBBBBB");
        Intent intent = new Intent(MainActivity.this, HABListActivity.class);
        startActivity(intent);
    }

}
