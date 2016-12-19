package com.example.user.CheeseBurgerYummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.CheeseBurgerYummy.Network.InterfaceAPI;
import com.example.user.CheeseBurgerYummy.Network.ServiceGenerator;
import com.example.user.CheeseBurgerYummy.Photo.PhotoBook;
import com.example.user.CheeseBurgerYummy.Util.Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
/**
 * Created by user on 12/12/16.
 */

public class PhotoDetailActivity extends Activity {
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.ImageTextView) TextView imageTextView;
    @BindView(R.id.createdDateTextView) TextView createdDateTextView;
    @BindView(R.id.discriptionEditText) EditText descriptionEditText;
    @BindView(R.id.submitButton) Button submitButton;

    Entry entry;
    PhotoBook info;
    String userChoosenTask;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");

        Intent intent = getIntent();
        entry = (Entry) intent.getExtras().get("entry");

        if (entry == Entry.EDIT) {
            createdDateTextView.setVisibility(View.VISIBLE);
            submitButton.setText("EDIT");
            imageTextView.setVisibility(View.INVISIBLE);
            info = (PhotoBook) intent.getExtras().get("info");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Picasso.with(this).load(ServiceGenerator.BASE_URL + info.getImageThumbFile()).into(imageView);
            createdDateTextView.setText(format.format(info.getCreatedAt()));
            descriptionEditText.setText(info.getDescription());
        } else {
            createdDateTextView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    imageTextView.setVisibility(View.INVISIBLE);
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                    Log.d("11111111", selectedImage.getPath());
//                    String str = selectedImage.getPath(););
                    Log.d("11111111", getRealPathFromURI(selectedImage));

                    file = new File(getRealPathFromURI(selectedImage));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @OnClick(R.id.closeButton) void actionCloseButton() {
        finish();
    }

    @OnClick(R.id.imageView) void getPhotoImage() {
        selectImage();
    }

    @OnClick(R.id.submitButton) void submit() {
        if (entry == Entry.ADD) {
            postPhoto();
        } else {
            putPhoto();
        }

    }

    @OnClick(R.id.rootConstraintLayout) void hideKeyboard(ConstraintLayout constraintLayout) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(descriptionEditText.getWindowToken(), 0);
    }

    private void postPhoto() {
//        if (file == null) {
        if (null == imageView.getDrawable()) {
            Toast.makeText(this, "no attached image!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(descriptionEditText.getText().toString().trim())) {
            Toast.makeText(this, "description is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image_file", file.getName(), requestFile);
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionEditText.getText().toString());


        Call<Void> call = apiService.postPhoto(body, description);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("DDDDDDDDD", t.toString());
            }
        });
    }

    private void putPhoto() {
        if (file == null &&
                info.getDescription().equals(descriptionEditText.getText().toString())) {
            Toast.makeText(this, "no changed!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(PhotoDetailActivity.this);
        alert_confirm.setMessage("수정하시겠습니까?").setCancelable(false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InterfaceAPI apiService = ServiceGenerator.getClient().create(InterfaceAPI.class);
                        Map<String, RequestBody> data = new HashMap<>();

                        if (file != null) {
                            //image
                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                            data.put("image_file\"; filename=\"file.name\"", fileBody);
                        }

                        if (!info.getDescription().equals(descriptionEditText.getText().toString())) {
                            //description
                            RequestBody description =
                                    RequestBody.create(
                                            MediaType.parse("multipart/form-data"), descriptionEditText.getText().toString());
                            data.put("description", description);
                        }

                        Call<Void> call = apiService.putPhoto(info.getPk(), data);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(PhotoDetailActivity.this, "update ok!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("DDDDDD", t.toString());
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoDetailActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(PhotoDetailActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
