package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.user.cheeseburgeryummy.HAB.HouseholdAccountBook;
import com.example.user.cheeseburgeryummy.Network.InterfaceAPI;
import com.example.user.cheeseburgeryummy.Network.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by user on 12/12/16.
 */

enum Entry {
    ADD,
    EDIT
}

public class HABDetailActivity extends Activity {
    private static final String TAG = HABDetailActivity.class.getSimpleName();

    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.receiveRadioButton) RadioButton receiveRadioButton;
    @BindView(R.id.payRadioButton) RadioButton payRadioButton;
    @BindView(R.id.dateEditText) EditText dateEditText;
    @BindView(R.id.priceEditText) EditText priceEditText;
    @BindView(R.id.categoryEditText) EditText categoryEditText;
    @BindView(R.id.memoEditText) EditText memoEditText;
    @BindView(R.id.submitButton) Button addButton;

    Entry entry;
    HouseholdAccountBook info;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hab_detail);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");

        Intent intent = getIntent();
        entry = (Entry) intent.getExtras().get("entry");

        if (entry == Entry.EDIT) {
            addButton.setText("EDIT");
            info = (HouseholdAccountBook) intent.getExtras().get("info");
            if (info.getState() == "receive") {
                // receive
                receiveRadioButton.setChecked(true);
                payRadioButton.setChecked(false);
            } else {
                // pay
                receiveRadioButton.setChecked(false);
                payRadioButton.setChecked(true);
            }

            dateEditText.setText(info.getDate());
            priceEditText.setText(String.valueOf(info.getPrice()));
            categoryEditText.setText(info.getCategory());
            memoEditText.setText(info.getMemo());
        }
    }

    @OnClick(R.id.closeButton) void actionClose(Button button) {
        finish();
    }

    @OnClick(R.id.submitButton) void actionSubmit(Button button) {
        if (entry == Entry.ADD) {
            post();
        } else {
            put();
        }
    }

    @OnClick(R.id.removeAllButton) void actionRemoveAll(Button button) {
        dateEditText.setText("");
        priceEditText.setText("");
        categoryEditText.setText("");
        memoEditText.setText("");
    }

    @OnClick(R.id.rootConstraintLayout) void hideKeyboard() {
        imm.hideSoftInputFromWindow(dateEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(priceEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(categoryEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(memoEditText.getWindowToken(), 0);
    }

    private void post() {
        if (TextUtils.isEmpty(dateEditText.getText().toString().trim())) {
            setFocusEditText(dateEditText);
            Toast.makeText(this, "date empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(priceEditText.getText().toString().trim())) {
            setFocusEditText(priceEditText);
            Toast.makeText(this, "price empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(categoryEditText.getText().toString().trim())) {
            setFocusEditText(categoryEditText);
            Toast.makeText(this, "category empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(memoEditText.getText().toString().trim())) {
            setFocusEditText(memoEditText);
            Toast.makeText(this, "memo empty!", Toast.LENGTH_SHORT).show();
            return;
        }


        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

        RadioButton checkedRadioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        Log.d(TAG, checkedRadioButton.getText().toString());
        Log.d(TAG, dateEditText.getText().toString());
        Log.d(TAG, priceEditText.getText().toString());
        Log.d(TAG, categoryEditText.getText().toString());
        Log.d(TAG, memoEditText.getText().toString());


        Call<Void> call = apiService.postHAB(
                checkedRadioButton.getText().toString().toLowerCase(),
                dateEditText.getText().toString(),
                Integer.parseInt(priceEditText.getText().toString()),
                categoryEditText.getText().toString(),
                memoEditText.getText().toString()
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, response.toString());
                Log.d(TAG, response.raw().message());
                Log.d(TAG, String.valueOf(response.message()));
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void put() {
        final RadioButton checkedRadioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        if (
                info.getState().equals(checkedRadioButton.getText().toString().toLowerCase()) &&
                info.getDate().equals(dateEditText.getText().toString()) &&
                String.valueOf(info.getPrice()).equals(priceEditText.getText().toString()) &&
                info.getCategory().equals(categoryEditText.getText().toString()) &&
                info.getMemo().equals(memoEditText.getText().toString())
           ) {
            Toast.makeText(this, "no changed!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(HABDetailActivity.this);
        alert_confirm.setMessage("수정하시겠습니까?").setCancelable(false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

                        Log.d(TAG, checkedRadioButton.getText().toString());
                        Log.d(TAG, dateEditText.getText().toString());
                        Log.d(TAG, priceEditText.getText().toString());
                        Log.d(TAG, categoryEditText.getText().toString());
                        Log.d(TAG, memoEditText.getText().toString());

                        Map<String, Object> data = new HashMap<>();

                        if (!info.getState().equals(checkedRadioButton.getText().toString().toLowerCase())) {
                            data.put("state", checkedRadioButton.getText().toString().toLowerCase());
                        }

                        if (!(info.getDate().equals(dateEditText.getText().toString()))) {
                            data.put("date", dateEditText.getText().toString());
                        }

                        if (!String.valueOf(info.getPrice()).equals(priceEditText.getText().toString())) {
                            data.put("price", priceEditText.getText().toString());
                        }

                        if (!info.getCategory().equals(categoryEditText.getText().toString())) {
                            data.put("category", categoryEditText.getText().toString());
                        }

                        if (!info.getMemo().equals(memoEditText.getText().toString())) {
                            data.put("memo", memoEditText.getText().toString());
                        }

                        /*
                        HABDataSend habDataSend = new HABDataSend();
                        habDataSend.setDate("2016-12-13");
                        */

                        Call<Void> call = apiService.putHAB(info.getPk(), data);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d(TAG, String.valueOf(response.code()));
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d(TAG, t.toString());
                            }
                        });
                    }
                }).setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void setFocusEditText(EditText editText) {
        editText.requestFocus();
        editText.setCursorVisible(true);
        imm.showSoftInput(editText, 0);
    }
}
