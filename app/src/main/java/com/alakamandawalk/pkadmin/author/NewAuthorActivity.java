package com.alakamandawalk.pkadmin.author;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.R;
import com.alakamandawalk.pkadmin.SettingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class NewAuthorActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri = null;

    String profileOrCoverImg;

    String authorId, authorName, authorPost , authorDescription, authorCoverImage , authorProfileImage;

    EditText authorIdEt, authorNameEt, authorPostEt, authorDescriptionEt;
    TextView titleTv, authorIdTv;
    ImageView authorCoverImg, authorProfileImg;
    Button addOrUpdateAuthorBtn;
    ImageButton backIb;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_author);

        SharedPreferences themePref = getSharedPreferences(SettingsActivity.THEME_PREFERENCE, MODE_PRIVATE);
        boolean isDarkMode = themePref.getBoolean(SettingsActivity.KEY_IS_NIGHT_MODE, false);

        if (isDarkMode){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Intent intent = getIntent();
        final String key = intent.getStringExtra("key");
        authorId = intent.getStringExtra("authorId");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        authorIdTv = findViewById(R.id.authorIdTv);
        titleTv = findViewById(R.id.titleTv);
        authorProfileImg = findViewById(R.id.authorProfileImg);
        authorCoverImg =  findViewById(R.id.authorCoverImg);
        addOrUpdateAuthorBtn = findViewById(R.id.addOrUpdateAuthorBtn);
        authorIdEt = findViewById(R.id.authorIdEt);
        authorNameEt = findViewById(R.id.authorNameEt);
        authorPostEt = findViewById(R.id.authorPostEt);
        authorDescriptionEt = findViewById(R.id.authorDescriptionEt);
        backIb = findViewById(R.id.backIb);

        pd = new ProgressDialog(this);


        authorProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
                profileOrCoverImg = "profileImage";
            }
        });

        authorCoverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
                profileOrCoverImg = "coverImage";
            }
        });

        if (key.equals("edit")){
            titleTv.setText("Edit author");
            addOrUpdateAuthorBtn.setText("update author");
            authorIdEt.setVisibility(View.GONE);
            loadAuthorData();
        }else {
            authorIdTv.setVisibility(View.GONE);
            titleTv.setText("New author");
            addOrUpdateAuthorBtn.setText("add author");
        }

        addOrUpdateAuthorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authorId = authorIdEt.getText().toString().trim();
                authorName = authorNameEt.getText().toString().trim();
                authorPost = authorPostEt.getText().toString().trim();
                authorDescription = authorDescriptionEt.getText().toString().trim();

                if(TextUtils.isEmpty(authorName)){
                    Toast.makeText(NewAuthorActivity.this, "author name is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(authorPost)){
                    Toast.makeText(NewAuthorActivity.this, "author post empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(authorDescription)){
                    Toast.makeText(NewAuthorActivity.this, "author description is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (key.equals("edit")){

                    updateAuthor();

                }else {

                    if(TextUtils.isEmpty(authorId)){
                        Toast.makeText(NewAuthorActivity.this, "author id is empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    uploadData();
                }
            }
        });

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateAuthor() {

        pd.setMessage("deleting cover image...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        Bitmap proBitmap = ((BitmapDrawable)authorProfileImg.getDrawable()).getBitmap();
        ByteArrayOutputStream proBaos = new ByteArrayOutputStream();
        proBitmap.compress(Bitmap.CompressFormat.PNG, 100, proBaos);
        final byte[] proPic = proBaos.toByteArray();

        Bitmap coverBitmap = ((BitmapDrawable)authorCoverImg.getDrawable()).getBitmap();
        ByteArrayOutputStream coverBaos = new ByteArrayOutputStream();
        coverBitmap.compress(Bitmap.CompressFormat.PNG, 100, coverBaos);
        final byte[] coverPic = coverBaos.toByteArray();

        final String coverPathAndName = "author/" + "coverImage_" + authorId;
        final String profilePathAndName = "author/" + "profileImage_" + authorId;

        StorageReference delCoverRef = FirebaseStorage.getInstance().getReferenceFromUrl(authorCoverImage);
        delCoverRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                pd.setMessage("deleting profile image...");

                StorageReference delProfileRef = FirebaseStorage.getInstance().getReferenceFromUrl(authorProfileImage);
                delProfileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        pd.setMessage("uploading cover image...");

                        StorageReference coverRef = FirebaseStorage.getInstance().getReference(coverPathAndName);
                        coverRef.putBytes(coverPic)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        final String coverUri = uriTask.getResult().toString();

                                        pd.setMessage("uploading profile image...");

                                        StorageReference proRef = FirebaseStorage.getInstance().getReference(profilePathAndName);
                                        proRef.putBytes(proPic)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                        while (!uriTask.isSuccessful());

                                                        String proUri = uriTask.getResult().toString();

                                                        pd.setMessage("uploading author data...");

                                                        if (uriTask.isSuccessful()){

                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("authorName", authorName);
                                                            hashMap.put("authorPost", authorPost);
                                                            hashMap.put("authorDescription", authorDescription);
                                                            hashMap.put("authorCoverImage", coverUri);
                                                            hashMap.put("authorProfileImage", proUri);

                                                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("author");
                                                            dbRef.child(authorId)
                                                                    .updateChildren(hashMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            pd.dismiss();
                                                                            Toast.makeText(NewAuthorActivity.this, "author updated!", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(NewAuthorActivity.this, AuthorProfileActivity.class);
                                                                            intent.putExtra("authorId", authorId);
                                                                            startActivity(intent);
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    pd.dismiss();
                                                                    Toast.makeText(NewAuthorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(NewAuthorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(NewAuthorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewAuthorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewAuthorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void loadAuthorData() {

        DatabaseReference authorRef = FirebaseDatabase.getInstance().getReference("author");
        Query query = authorRef.orderByChild("authorId").equalTo(authorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    authorName = ds.child("authorName").getValue().toString();
                    authorPost = ds.child("authorPost").getValue().toString();
                    authorDescription = ds.child("authorDescription").getValue().toString();
                    authorCoverImage = ds.child("authorCoverImage").getValue().toString();
                    authorProfileImage = ds.child("authorProfileImage").getValue().toString();

                    authorNameEt.setText(authorName);
                    authorPostEt.setText(authorPost);
                    authorDescriptionEt.setText(authorDescription);
                    authorIdTv.setText(authorId);

                    try {
                        Picasso.get()
                                .load(authorCoverImage)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.img_place_holder)
                                .into(authorCoverImg);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(authorCoverImg);
                    }

                    try {
                        Picasso.get()
                                .load(authorProfileImage)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.img_place_holder)
                                .into(authorProfileImg);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(authorProfileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewAuthorActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData() {

        pd.setMessage("uploading cover image...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        Bitmap proBitmap = ((BitmapDrawable)authorProfileImg.getDrawable()).getBitmap();
        ByteArrayOutputStream proBaos = new ByteArrayOutputStream();
        proBitmap.compress(Bitmap.CompressFormat.PNG, 100, proBaos);
        final byte[] proPic = proBaos.toByteArray();

        Bitmap coverBitmap = ((BitmapDrawable)authorCoverImg.getDrawable()).getBitmap();
        ByteArrayOutputStream coverBaos = new ByteArrayOutputStream();
        coverBitmap.compress(Bitmap.CompressFormat.PNG, 100, coverBaos);
        byte[] coverPic = coverBaos.toByteArray();

        String coverPathAndName = "author/" + "coverImage_" + authorId;
        final String profilePathAndName = "author/" + "profileImage_" + authorId;

        StorageReference coverRef = FirebaseStorage.getInstance().getReference(coverPathAndName);
        coverRef.putBytes(coverPic)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        final String coverUri = uriTask.getResult().toString();

                        pd.setMessage("uploading profile image...");

                        StorageReference proRef = FirebaseStorage.getInstance().getReference(profilePathAndName);
                        proRef.putBytes(proPic)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        String proUri = uriTask.getResult().toString();

                                        pd.setMessage("uploading author data...");

                                        if (uriTask.isSuccessful()){
                                            HashMap<Object, String> hashMap = new HashMap<>();
                                            hashMap.put("authorId", authorId);
                                            hashMap.put("authorName", authorName);
                                            hashMap.put("authorPost", authorPost);
                                            hashMap.put("authorDescription", authorDescription);
                                            hashMap.put("authorCoverImage", coverUri);
                                            hashMap.put("authorProfileImage", proUri);

                                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("author");
                                            dbRef.child(authorId).setValue(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            pd.dismiss();
                                                            Toast.makeText(NewAuthorActivity.this, "author added!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(NewAuthorActivity.this, AuthorProfileActivity.class);
                                                            intent.putExtra("authorId", authorId);
                                                            startActivity(intent);

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.dismiss();
                                                    Toast.makeText(NewAuthorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewAuthorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(NewAuthorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showImagePicDialog() {

        String options[] = {"camera", "gallery"};

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
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

                selectCoverOrProfilePhoto(image_uri);
            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE){

                selectCoverOrProfilePhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectCoverOrProfilePhoto(Uri image_uri) {

        if (profileOrCoverImg.equals("profileImage")){
            authorProfileImg.setImageURI(image_uri);
        }
        if (profileOrCoverImg.equals("coverImage")) {
            authorCoverImg.setImageURI(image_uri);
        }
    }

    @Override
    public void onBackPressed() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("cancel working and leave...");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewAuthorActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}
