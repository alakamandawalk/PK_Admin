package com.alakamandawalk.pkadmin.playlist;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NewPlaylistActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri = null;

    ImageButton backIb;
    ImageView playlistImgIv;
    EditText playlistIdEt, playlistNameEt;
    Spinner categorySpinner;
    Button publishPlaylistBtn;
    ProgressDialog pd;

    ArrayList<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_playlist);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        backIb = findViewById(R.id.backIb);
        playlistImgIv = findViewById(R.id.playlistImgIv);
        playlistIdEt = findViewById(R.id.playlistIdEt);
        playlistNameEt = findViewById(R.id.playlistNameEt);
        categorySpinner = findViewById(R.id.categorySpinner);
        publishPlaylistBtn = findViewById(R.id.publishPlaylistBtn);
        pd = new ProgressDialog(this);

        playlistImgIv.setOnClickListener(new View.OnClickListener() {
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

        categoryList = new ArrayList<>();

        loadCategories();

        publishPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String playlistId = playlistIdEt.getText().toString().trim();
                String playlistName = playlistNameEt.getText().toString().trim();
                String playlistCategory = categorySpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(playlistId)){
                    Toast.makeText(NewPlaylistActivity.this, "playlist id is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(playlistName)){
                    Toast.makeText(NewPlaylistActivity.this, "playlist name is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadData(playlistId, playlistName, playlistCategory);

            }
        });
    }

    private void uploadData(String playlistId, final String playlistName, final String playlistCategory) {

        pd.setMessage("adding new playlist...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        Bitmap bitmap = ((BitmapDrawable)playlistImgIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        final String newPlaylistId = playlistId.toLowerCase().replaceAll("\\s+","");

        String filePathAndName = "playlist/" + "playlist_" + playlistId;

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
                            hashMap.put("playlistId", newPlaylistId);
                            hashMap.put("playlistName", playlistName);
                            hashMap.put("playlistImage", downloadUrl);
                            hashMap.put("playlistCategory", playlistCategory);

                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("playlist");
                            dbRef.child(newPlaylistId).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            pd.dismiss();
                                            Toast.makeText(NewPlaylistActivity.this, "playlist added!", Toast.LENGTH_SHORT).show();

                                            playlistIdEt.getText().clear();
                                            playlistNameEt.getText().clear();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(NewPlaylistActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(NewPlaylistActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadCategories() {

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String categoryId = ds.child("categoryId").getValue().toString();
                    categoryList.add(categoryId);
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(NewPlaylistActivity.this, R.layout.spinner_item, categoryList);
                    categorySpinner.setAdapter(categoryAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewPlaylistActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE , "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION , "Temp Description");

        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
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

                playlistImgIv.setImageURI(image_uri);

            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE){

                playlistImgIv.setImageURI(image_uri);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
