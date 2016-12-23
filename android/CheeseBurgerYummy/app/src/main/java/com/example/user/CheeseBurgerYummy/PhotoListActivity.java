package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cheeseburgeryummy.Network.InterfaceAPI;
import com.example.user.cheeseburgeryummy.Network.ServiceGenerator;
import com.example.user.cheeseburgeryummy.Photo.PhotoBook;
import com.example.user.cheeseburgeryummy.Photo.PhotoListAdapter;

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

public class PhotoListActivity extends Activity{
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.emptyTextView) TextView emptyTextView;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static Context context;
    private ArrayList<PhotoBook> photoList = new ArrayList<PhotoBook>();
    private PhotoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        ButterKnife.bind(this);
        context = this;
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new PhotoListAdapter.ItemDecorationAlbumColumns(10, 2));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPhotoList();
    }

    @OnClick(R.id.backButton)
    public void actionBackButton() {
        finish();
    }

    @OnClick(R.id.submitButton)
    public void actionAddButton() {
        Intent intent = new Intent(PhotoListActivity.this, PhotoDetailActivity.class);
        intent.putExtra("entry", Entry.ADD);
        startActivity(intent);
    }

    private void getPhotoList() {
        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

        Call<ArrayList<PhotoBook>> call = apiService.getPhotoList();
        call.enqueue(new Callback<ArrayList<PhotoBook>>() {
            @Override
            public void onResponse(Call<ArrayList<PhotoBook>> call, Response<ArrayList<PhotoBook>> response) {
                photoList = response.body();

                if (photoList.size() == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.INVISIBLE);
                }
                adapter = new PhotoListAdapter(photoList);

                adapter.setInterfacePhotoListAdapter(new PhotoListAdapter.InterfacePhotoListAdapter() {
                    @Override
                    public void clickedItem(PhotoBook photoDetail) {
                        Intent intent = new Intent(PhotoListActivity.this, PhotoDetailActivity.class);
                        intent.putExtra("entry", Entry.EDIT);
                        intent.putExtra("info", photoDetail);
                        startActivity(intent);
                    }
                    @Override
                    public void longClickedItem(int position) {
                        deletePhoto(position);
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<PhotoBook>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void deletePhoto(final int position) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(PhotoListActivity.this);
        alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("네",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

                    Call<Void> call = apiService.deletePhoto(photoList.get(position).getPk());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(PhotoListActivity.this, "Deleted photo!", Toast.LENGTH_SHORT).show();
                            getPhotoList();
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
                    return;

                }
            });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }
}
