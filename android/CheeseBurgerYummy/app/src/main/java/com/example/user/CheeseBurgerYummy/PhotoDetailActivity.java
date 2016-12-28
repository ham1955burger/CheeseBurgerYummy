package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.user.cheeseburgeryummy.Network.InterfaceAPI;
import com.example.user.cheeseburgeryummy.Network.ServiceGenerator;
import com.example.user.cheeseburgeryummy.Photo.PhotoBook;
import com.example.user.cheeseburgeryummy.Util.Utility;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import static com.facebook.internal.CallbackManagerImpl.RequestCodeOffset.Share;

/**
 * Created by user on 12/12/16.
 */

public class PhotoDetailActivity extends Activity {
    static final int REQUEST_TAKE_PHOTO = 0;
    static final int REQUEST_PHOTO_LIBRARY = 1;
    static final int REQUEST_IMAGE_CROP = 2;

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.ImageTextView) TextView imageTextView;
    @BindView(R.id.createdDateTextView) TextView createdDateTextView;
    @BindView(R.id.discriptionEditText) EditText descriptionEditText;
    @BindView(R.id.submitButton) Button submitButton;

    Entry entry;
    PhotoBook info;
    String userChoosenTask;
    File file;

    private String mCurrentPhotoPath;
    private Uri contentUri;

    private static CallbackManager fbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");

        Intent intent = getIntent();
        entry = (Entry) intent.getExtras().get("entry");

        fbManager = new CallbackManager.Factory().create();

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

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if(!path.exists()) {
            path.mkdirs();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            rotatePhoto();
            cropImage(contentUri);
        } else if (requestCode == REQUEST_PHOTO_LIBRARY && resultCode == RESULT_OK) {
            contentUri = null;
            contentUri = data.getData();
//            imageView.setImageURI(contentUri);
            cropImage(contentUri);
        } else if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras != null) {
                Bitmap bitmap = (Bitmap)extras.get("data");
                imageView.setImageBitmap(bitmap);

                // 임시파일 삭제
                if(mCurrentPhotoPath != null) {
                    File f = new File(mCurrentPhotoPath);
                    if(f.exists()) {
                        f.delete();
                    }
                    mCurrentPhotoPath = null;
                }
            } else {
                imageView.setImageURI(contentUri);
            }
        }



        /*
        switch(requestCode) {
            case (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK):
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();

                    if (selectedImage != null) {
                        imageView.setImageURI(selectedImage);
                    } else {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                }

                break;
            case REQUEST_TAKE_PHOTO:
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
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        dispatchTakePictureIntent();
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

    @OnClick(R.id.shareButton) void actionShare() {
        Log.d("222222", "333333333");

        Uri contentUrl = Uri.parse("https://developers.facebook.com");
        Uri imageUrl = Uri.parse("http://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Facebook_Headquarters_Menlo_Park.jpg/2880px-Facebook_Headquarters_Menlo_Park.jpg");

        ShareLinkContent shareContent = new ShareLinkContent.Builder()
                .setContentUrl(contentUrl)
                .setContentTitle(String.format("Android share test"))
                .setContentDescription(info.getDescription())
                .setImageUrl(imageUrl)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(PhotoDetailActivity.fbManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(PhotoDetailActivity.this, "공유되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.d("33333", "33333");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });

        shareDialog.show(shareContent, ShareDialog.Mode.FEED);
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
                // 임시파일 삭제
                if(mCurrentPhotoPath != null) {
                    File f = new File(mCurrentPhotoPath);
                    if(f.exists()) {
                        f.delete();
                    }
                    mCurrentPhotoPath = null;
                }
                contentUri = null;

                if (items[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /*
    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }
    */

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // fix) editing is not supported for this image
        pickPhoto.setType("image/*");
        pickPhoto.putExtra("crop", "true");
        pickPhoto.putExtra("return-data", true);
        pickPhoto.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickPhoto , REQUEST_PHOTO_LIBRARY);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //            ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                contentUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        file = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = file.getAbsolutePath(); //≥™¡ﬂø° Rotate«œ±‚ ¿ß«— ∆ƒ¿œ ∞Ê∑Œ.

        return file;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void cropImage(Uri contentUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    public void rotatePhoto() {
        ExifInterface exif;
        try {
            if(mCurrentPhotoPath == null) {
                mCurrentPhotoPath = contentUri.getPath();
            }
            exif = new ExifInterface(mCurrentPhotoPath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            if(exifDegree != 0) {
                Bitmap bitmap = getBitmap();
                Bitmap rotatePhoto = rotate(bitmap, exifDegree);
                saveBitmap(rotatePhoto);
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotate(Bitmap image, int degrees)
    {
        if(degrees != 0 && image != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float)image.getWidth(), (float)image.getHeight());

            try
            {
                Bitmap b = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);

                if(image != b)
                {
                    image.recycle();
                    image = b;
                }

                image = b;
            }
            catch(OutOfMemoryError ex)
            {
                ex.printStackTrace();
            }
        }
        return image;
    }

    public void saveBitmap(Bitmap bitmap) {
        File file = new File(mCurrentPhotoPath);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) ;
        try {
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inInputShareable = true;
        options.inDither=false;
        options.inTempStorage=new byte[32 * 1024];
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;

        File f = new File(mCurrentPhotoPath);

        FileInputStream fs=null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        Bitmap bm = null;

        try {
            if(fs!=null) bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }
}
