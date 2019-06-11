package com.example.apeye.ui.main.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apeye.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

public class NewPost extends AppCompatActivity {

    private static final String TAG = "NewPost";
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;
    private Uri CurrentImageURI = null;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.ask_community);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        newPostImage = findViewById(R.id.post_photo);
        newPostDesc = findViewById(R.id.post_desc);
        newPostBtn = findViewById(R.id.create_post);
        newPostProgress = findViewById(R.id.post_prog_bar);
        newPostProgress.setVisibility(View.INVISIBLE);
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BringImagePicker();
            }
        });
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(desc)){
                    final String randomName = UUID.randomUUID().toString();
                    final StorageReference ref = storageReference.child("Post_images").child(randomName + ".jpg");
                    newPostBtn.setEnabled(false);

                    if(isChanged) {


                        Bitmap compressedImageFile =null;
                        File newImageFile = new File(Objects.requireNonNull(CurrentImageURI.getPath()));
                        try {

                            compressedImageFile = new Compressor(NewPost.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Objects.requireNonNull(compressedImageFile).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();



                        newPostProgress.setVisibility(View.VISIBLE);
                        uploadTask = ref.putBytes(imageData);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ", e);
                                Toast.makeText(NewPost.this, "unsuccessful upload", Toast.LENGTH_LONG).show();
                            }
                        });

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }

                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String uri = Objects.requireNonNull(downloadUri).toString();
                                    update_data(uri, desc);

                                } else {
                                    Toast.makeText(NewPost.this, "unsuccessful Uri get", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        update_data("null",desc);
                    }

                }
                else{
                    Toast.makeText(NewPost.this, "Please Enter Description" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void update_data(String uri, String desc) {

        Uri uri1 = Uri.parse(uri);
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("image_url", uri1.toString());
        postMap.put("desc", desc);
        postMap.put("user_id", current_user_id);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd MMMM yyyy");
        String currentDateandTime = sdf.format(new Date());
        System.out.println(currentDateandTime);
        postMap.put("date", currentDateandTime);

        firebaseFirestore.collection("Posts").document().set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    newPostBtn.setEnabled(true);
                    newPostProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(NewPost.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                    // Intent mainIntent = new Intent(User_Information.this, MainActivity.class);
                    // startActivity(mainIntent);
                    finish();

                } else {

                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(NewPost.this, "(FIRE_STORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1).setInitialCropWindowPaddingRatio(0)
                .setMinCropWindowSize(0,0)
                .start(NewPost.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                CurrentImageURI = Objects.requireNonNull(result).getUri();
                newPostImage.setImageURI(CurrentImageURI);
                isChanged = true;

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
