package com.alakamandawalk.pkadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class NewCategoryActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri = null;

    ImageButton backIb;
    ImageView categoryImgIv;
    Button publishCategoryBtn;
    EditText categoryNameEt, categoryIdEt;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        firebaseAuth = FirebaseAuth.getInstance();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        backIb = findViewById(R.id.backIb);
        categoryImgIv = findViewById(R.id.categoryImgIv);
        publishCategoryBtn = findViewById(R.id.publishCategoryBtn);
        categoryNameEt = findViewById(R.id.categoryNameEt);
        categoryIdEt = findViewById(R.id.categoryIdEt);
        pd = new ProgressDialog(this);

        categoryImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        publishCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categoryName = categoryNameEt.getText().toString().trim();
                String categoryId = categoryIdEt.getText().toString().trim();

                if(TextUtils.isEmpty(categoryName)){
                    Toast.makeText(NewCategoryActivity.this, "category name is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(categoryId)){
                    Toast.makeText(NewCategoryActivity.this, "category id is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadData(categoryId, categoryName);

            }
        });
    }

    private void uploadData(final String categoryId, final String categoryName) {

        pd.setMessage("adding new category...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        Bitmap bitmap = ((BitmapDrawable)categoryImgIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        final String newCategoryId = categoryId.toLowerCase().replaceAll("\\s+","");

        String filePathAndName = "category/" + "category_" + categoryId;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUrl = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("categoryId", newCategoryId);
                            hashMap.put("categoryName", categoryName);
                            hashMap.put("categoryImage", downloadUrl);

                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("category");
                            dbRef.child(newCategoryId).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            pd.dismiss();
                                            Toast.makeText(NewCategoryActivity.this, "category added!", Toast.LENGTH_SHORT).show();

                                            categoryIdEt.getText().clear();
                                            categoryNameEt.getText().clear();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(NewCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(NewCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showImagePicDialog() {

        String options[] = {"camera", "gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pic from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){

                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }

                }else if (which == 1){

                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE , "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION , "Temp Description");

        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{

                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{

                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(this, "please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri =data.getData();

                categoryImgIv.setImageURI(image_uri);

            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE){

                categoryImgIv.setImageURI(image_uri);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
