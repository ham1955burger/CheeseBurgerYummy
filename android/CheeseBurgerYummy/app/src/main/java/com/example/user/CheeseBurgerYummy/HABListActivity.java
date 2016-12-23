package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.cheeseburgeryummy.HAB.HABListAdapter;
import com.example.user.cheeseburgeryummy.HAB.HouseholdAccountBook;
import com.example.user.cheeseburgeryummy.Network.InterfaceAPI;
import com.example.user.cheeseburgeryummy.Network.ServiceGenerator;

import java.util.ArrayList;

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

public class HABListActivity extends Activity {
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.emptyTextView) TextView emptyTextView;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<HouseholdAccountBook> list = new ArrayList<HouseholdAccountBook>();
    private HABListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hab_list);
        ButterKnife.bind(this);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getList();
    }

    @OnClick(R.id.backButton) void actionBackButton(Button button) {
        finish();
    }

    @OnClick(R.id.submitButton) void actionAddButon(Button button) {
        Intent intent = new Intent(HABListActivity.this, HABDetailActivity.class);
        intent.putExtra("entry", Entry.ADD);
        startActivity(intent);
    }

    private void getList() {
        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

        Call<ArrayList<HouseholdAccountBook>> call = apiService.getHABList("tttoookkkeeennn");
        call.enqueue(new Callback<ArrayList<HouseholdAccountBook>>() {
            @Override
            public void onResponse(Call<ArrayList<HouseholdAccountBook>> call, Response<ArrayList<HouseholdAccountBook>> response) {
                list = response.body();

                if (list.size() == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.INVISIBLE);
                }
                adapter = new HABListAdapter(list);
                adapter.setInterfaceHABListAdapter(new HABListAdapter.InterfaceHABListAdapter() {
                    @Override
                    public void clickedItem(int position) {
                        Log.d(TAG, String.valueOf(position));
                        Intent intent = new Intent(HABListActivity.this, HABDetailActivity.class);
                        intent.putExtra("entry", Entry.EDIT);
                        intent.putExtra("info", list.get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void deletedItem(int position) {
                        deleteItem(position);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<ArrayList<HouseholdAccountBook>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void deleteItem(final int position) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(HABListActivity.this);
        alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

                        Call<Void> call = apiService.deleteHAB(list.get(position).getPk());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                getList();
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, t.toString());
                            }
                        });
                    }
                }).setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        HABListAdapter.ViewHolder viewHolder =  adapter.getItemViewType(position);
                        return;

                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }
}
