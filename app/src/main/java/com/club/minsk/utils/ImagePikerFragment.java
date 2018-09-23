package com.club.minsk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Strings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ImagePikerFragment extends AppFragment {


    private int PICK_IMAGE_REQUEST = 23;
    private static final int READ_EXTERNAL_STORAGE_REQUEST = 35;
    private static final int CAMERA_REQUEST = 36;
    final int REQUEST_CODE_PHOTO = 1;


    public void openPickImageDialog() {
        CharSequence choose[] = new CharSequence[]{Strings.get(R.string.upload_from_gallery), Strings.get(R.string.upload_from_camera)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int choose) {
                if (choose == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
                    else
                        pickImages();
                }
                if (choose == 1) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                            (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else
                        createImage();
                }
            }
        });
        builder.show();
    }


    private void createImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);/*
        File potoFile = new File(directory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, potoFile);*/
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImages();
            }
        }
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                createImage();
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = App.getActiveActivity().getContentResolver().query(contentURI, null, null, null, null);
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

    void uploadPhotoUri(Uri selectedImage) {
        /*String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.   get(columnIndex);
                Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);

                if (selectedBitmap != null)
                    pickImage(selectedBitmap);

            } while (cursor.moveToNext());
            cursor.close();
        }*/
        pickImage(getRealPathFromURI(selectedImage));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri mImageUri = data.getData();
                uploadPhotoUri(mImageUri);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        uploadPhotoUri(uri);
                    }
                }

            }
        }
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && data != null) {

            Bundle bndl = data.getExtras();
            if (bndl != null) {
                Object obj = data.getExtras().get("data");
                if (obj instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) obj;
                    File file = storeImage(bitmap);
                    pickImage(file.getAbsolutePath());
                }
            }


        }
    }

    private File storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return pictureFile;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (Exception e) {
        }
        return pictureFile;
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + App.getActiveActivity().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public Bitmap convertBitmap(String bitmap, float max_size, int compress) {
        if (new File(bitmap).exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(bitmap);

            float width = bmp.getWidth();
            float height = bmp.getHeight();
            float ratio = width / height;
            float new_width = 0;
            float new_height = 0;
            if (ratio > 1) {
                new_width = Math.min(max_size, width);
                new_height = new_width / ratio;
            } else {
                new_height = Math.min(max_size, height);
                new_width = new_height * ratio;
            }

            bmp = Bitmap.createScaledBitmap(bmp, (int) new_width, (int) new_height, false);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, compress, bos);
            return BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));
        }
        return null;
    }

    public abstract void pickImage(String bitmap);
}
