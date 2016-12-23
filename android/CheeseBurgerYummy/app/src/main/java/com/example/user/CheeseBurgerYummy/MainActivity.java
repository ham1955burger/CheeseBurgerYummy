package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends Activity {
    @BindView(R.id.coverImageView) ImageView coverImageView;
    @BindView(R.id.welcomTextView) TextView welcomTextView;

    HashMap<String, Object> resultMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");

        Intent intent = getIntent();

        resultMap = (HashMap<String,Object>) intent.getExtras().getSerializable("resultMap");
        welcomTextView.setText(String.format("안녕하세요, %s님!", resultMap.get("name").toString()));
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

    @OnClick(R.id.inviteTextView) void actionFacebookInvite() {
        Log.d("11111", "2232sdjfoijoeifj");
    }

    public void setWelcomLabel() {

    }

}
