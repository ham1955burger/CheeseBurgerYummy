package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.cheeseburgeryummy.Network.ServiceGenerator;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.squareup.picasso.Picasso;

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
        setWelcomLabel(resultMap.get("name").toString());

        HashMap<String, Object> cover = (HashMap<String, Object>) resultMap.get("cover");
        setCoverImage(cover.get("source").toString());
    }

    // MARK: - Actions
    @OnClick(R.id.photoButton)
    public void actionPhotoButton(Button button) {
        Timber.i("clicked Photo Button");
        Intent intent = new Intent(MainActivity.this, PhotoListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.householdButton)
    public void actionHouseHoldButton(Button button) {
        Timber.i("clicked Household Button");
        Intent intent = new Intent(MainActivity.this, HABListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.inviteTextView) void actionFacebookInvite() {
        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://www.mydomain.com/myapplink";
        previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }

    public void setWelcomLabel(String name) {
        String welcomText = String.format("안녕하세요, %s님!", name);

        int nameStartIndex = welcomText.indexOf(name);
        int nameEndIndex = nameStartIndex + name.length();

        SpannableStringBuilder builder = new SpannableStringBuilder(welcomText);

        /*
        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        TypefaceSpan robotoRegularSpan = new CustomTypefaceSpan("", robotoRegular);
        TypefaceSpan robotoBoldSpan = new CustomTypefaceSpan("", robotoBold);
        */

        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorBasicText)), 0, welcomText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), nameStartIndex, nameEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomTextView.setText(builder);
    }

    public void setCoverImage(String imageUrl) {
        Picasso.with(MainActivity.this).load(imageUrl).into(coverImageView);
    }
}