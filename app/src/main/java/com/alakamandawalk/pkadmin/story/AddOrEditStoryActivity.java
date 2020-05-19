package com.alakamandawalk.pkadmin.story;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alakamandawalk.pkadmin.DashboardActivity;
import com.alakamandawalk.pkadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.HashMap;

public class AddOrEditStoryActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri = null;

    String editStoryName, editStory, editStoryImage, editStorySearchTag;

    ImageView storyImgIv;
    TextInputEditText storyNameEt, newStoryEt, searchTagEt;
    TextView toolBarTitleTv;
    Button publishStoryBtn;
    ProgressDialog pd;
    ImageButton backIb;
    Spinner categorySpinner, playlistSpinner, authorSpinner;

    ArrayList<String> categoryList;
    ArrayList<String> playlist;
    ArrayList<String> authorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_story);

        Intent intent = getIntent();
        final String addOrEditKey = intent.getStringExtra("key");
        final String storyId = intent.getStringExtra("storyId");

        firebaseAuth = FirebaseAuth.getInstance();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storyImgIv = findViewById(R.id.storyImgIv);
        storyNameEt = findViewById(R.id.storyNameEt);
        searchTagEt = findViewById(R.id.searchTagEt);
        newStoryEt = findViewById(R.id.newStoryEt);
        toolBarTitleTv = findViewById(R.id.toolBarTitleTv);
        publishStoryBtn = findViewById(R.id.publishStoryBtn);
        backIb = findViewById(R.id.backIb);
        categorySpinner = findViewById(R.id.categorySpinner);
        playlistSpinner = findViewById(R.id.playlistSpinner);
        authorSpinner = findViewById(R.id.authorSpinner);

        pd = new ProgressDialog(this);

        storyImgIv.setOnClickListener(new View.OnClickListener() {
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
        playlist = new ArrayList<>();
        authorList = new ArrayList<>();

        loadCategories();
        loadAuthors();

        if (addOrEditKey.equals("edit")){

            toolBarTitleTv.setText("Edit Story");
            publishStoryBtn.setText("update story");
            loadStoryData(storyId);

        }else{

            toolBarTitleTv.setText("Add Story");
            publishStoryBtn.setText("publish story");
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                playlistSpinner.setAdapter(null);

                String playlistCategory = parent.getItemAtPosition(position).toString();
                loadPlaylists(playlistCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        publishStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String storyName = storyNameEt.getText().toString().trim();
                String searchTag = searchTagEt.getText().toString().trim();
                String story = newStoryEt.getText().toString().trim();
                String categoryId = categorySpinner.getSelectedItem().toString();
                String playlistId = playlistSpinner.getSelectedItem().toString();
                String authorId = authorSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(storyName) ||
                        TextUtils.isEmpty(searchTag) ||
                        TextUtils.isEmpty(story) ||
                        playlistSpinner.getSelectedItem() == null ||
                        categorySpinner.getSelectedItem() == null ||
                        authorSpinner.getSelectedItem() == null){
                    Toast.makeText(AddOrEditStoryActivity.this, "check again before publish!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (addOrEditKey.equals("edit")){
                    updateStory(storyName, story, categoryId, playlistId, storyId, searchTag, authorId);
                }else{
                    uploadData(storyName, story, categoryId, playlistId, searchTag, authorId);
                }

            }
        });
    }

    private void loadAuthors() {

        DatabaseReference authorRef = FirebaseDatabase.getInstance().getReference("author");
        authorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String authorId = ds.child("authorId").getValue().toString();
                    authorList.add(authorId);
                    ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(AddOrEditStoryActivity.this, R.layout.spinner_item, authorList);
                    authorSpinner.setAdapter(authorAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddOrEditStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStory(final String storyName, final String story, final String categoryId, final String playlistId, final String storyId, final String searchTag, final String authorId) {

        pd.setMessage("Updating...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        Bitmap bitmap = ((BitmapDrawable)storyImgIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(editStoryImage);
        storageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "story/" + "story_" + timeStamp;

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        storageReference.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        if (uriTask.isSuccessful()){

                                            String downloadUrl = uriTask.getResult().toString();

                                            HashMap<String, Object> hashMap = new HashMap<>();

                                            hashMap.put("storyName", storyName);
                                            hashMap.put("story", story);
                                            hashMap.put("storyImage", downloadUrl);
                                            hashMap.put("storyCategoryId", categoryId);
                                            hashMap.put("storyPlaylistId", playlistId);
                                            hashMap.put("storySearchTag", searchTag);
                                            hashMap.put("authorId", authorId);

                                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
                                            dbRef.child(storyId)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pd.dismiss();

                                                            Toast.makeText(AddOrEditStoryActivity.this, "story updated!", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(AddOrEditStoryActivity.this, DashboardActivity.class));
                                                            finish();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.dismiss();
                                                    Toast.makeText(AddOrEditStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddOrEditStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddOrEditStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

    }

    private void loadStoryData(String storyId) {

        pd.setMessage("Loading...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
        Query query = dbRef.orderByChild("storyId").equalTo(storyId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    editStoryName = ds.child("storyName").getValue().toString();
                    editStory = ds.child("story").getValue().toString();
                    editStoryImage = ds.child("storyImage").getValue().toString();
                    editStorySearchTag = ds.child("storySearchTag").getValue().toString();

                    storyNameEt.setText(editStoryName);
                    searchTagEt.setText(editStorySearchTag);
                    newStoryEt.setText(editStory);

                    try {
                        Picasso.get()
                                .load(editStoryImage)
                                .placeholder(R.drawable.img_place_holder)
                                .fit()
                                .centerCrop()
                                .into(storyImgIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_place_holder).into(storyImgIv);
                    }
                }

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(AddOrEditStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(AddOrEditStoryActivity.this, R.layout.spinner_item, categoryList);
                    categorySpinner.setAdapter(categoryAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddOrEditStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPlaylists(String playlistCategory) {

        DatabaseReference playlistRef = FirebaseDatabase.getInstance().getReference("playlist");
        Query query = playlistRef.orderByChild("playlistCategory").equalTo(playlistCategory);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playlist.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String playlistId = ds.child("playlistId").getValue().toString();
                    playlist.add(playlistId);
                    ArrayAdapter<String> playlistAdapter = new ArrayAdapter<>(AddOrEditStoryActivity.this, R.layout.spinner_item, playlist);
                    playlistSpinner.setAdapter(playlistAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddOrEditStoryActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(final String storyName, final String story, final String categoryId, final String playlistId, final String searchTag, final String authorId) {

        pd.setMessage("uploading new story...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        Bitmap bitmap = ((BitmapDrawable)storyImgIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "story/" + "story_" + timeStamp;

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
                            hashMap.put("storyId", timeStamp);
                            hashMap.put("storyName", storyName);
                            hashMap.put("story", story);
                            hashMap.put("storyDate", timeStamp);
                            hashMap.put("storyImage", downloadUrl);
                            hashMap.put("storyCategoryId", categoryId);
                            hashMap.put("storyPlaylistId", playlistId);
                            hashMap.put("storySearchTag", searchTag);
                            hashMap.put("authorId", authorId);

                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("story");
                            dbRef.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            pd.dismiss();
                                            Toast.makeText(AddOrEditStoryActivity.this, "story uploaded!", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(AddOrEditStoryActivity.this, DashboardActivity.class));
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(AddOrEditStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddOrEditStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                storyImgIv.setImageURI(image_uri);

            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE){

                storyImgIv.setImageURI(image_uri);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("cancel working and leave...");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddOrEditStoryActivity.super.onBackPressed();
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
