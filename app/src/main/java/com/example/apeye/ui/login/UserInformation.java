package com.example.apeye.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apeye.R;
import com.example.apeye.ui.main.MainActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserInformation extends AppCompatActivity {

    private static final String TAG = "User_Information";
    public static boolean Data_check = true;
    private CircleImageView circleImage;
    private EditText userNameEditText;
    private String user_id;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private Button saveButton;
    private FirebaseFirestore firebaseFirestore;
    private Uri CurrentImageURI = null;
    private boolean isChanged = false;
    private int comingFrom; // 1 -> reg , 2 -> community

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user__information);
        Intent intent = getIntent();
        comingFrom = intent.getIntExtra("comingFrom",2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.user_info);
        if(comingFrom == 2)
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        MainActivity.FirstRun = false;

        progressBar = findViewById(R.id.progressBar);
        userNameEditText =findViewById(R.id.user_name);
        saveButton = findViewById(R.id.save_button);
        circleImage = findViewById(R.id.user_pic);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        progressBar.setVisibility(View.INVISIBLE);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(Objects.requireNonNull(task.getResult()).exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        CurrentImageURI = Uri.parse(image);
                        userNameEditText.setText(name);

                        Picasso.get().load(image).resize(100,100).placeholder(R.drawable.defualtuser).error(R.drawable.defualtuser).into(circleImage);
                    }
                    else{

                        // Toast.makeText(User_Information.this, "No data" , Toast.LENGTH_SHORT).show();
                        userNameEditText.setText(R.string.user);
                        Data_check = false;
                    }

                }else{

                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(UserInformation.this, "(FIRE_STORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }
            }
        });


        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BringImagePicker();

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                final String user_name = userNameEditText.getText().toString();

                if(!TextUtils.isEmpty(user_name)){

                    progressBar.setVisibility(View.VISIBLE);

                    final StorageReference ref = storageReference.child("profile_images").child(user_id + ".jpg");
                    saveButton.setEnabled(false);

                    if(isChanged){

                        Bitmap compressedImageFile =null;
                        File newImageFile = new File(Objects.requireNonNull(CurrentImageURI.getPath()));
                        try {

                            compressedImageFile = new Compressor(UserInformation.this)
                                    .setMaxHeight(250)
                                    .setMaxWidth(250)
                                    .setQuality(25)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Objects.requireNonNull(compressedImageFile).compress(Bitmap.CompressFormat.JPEG, 40, baos);
                        byte[] imageData = baos.toByteArray();

                        uploadTask = ref.putBytes(imageData);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ",e);
                                Toast.makeText(UserInformation.this, "unsuccessful upload" , Toast.LENGTH_LONG).show();
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
                                    update_data(uri,user_name);

                                } else {
                                    Toast.makeText(UserInformation.this, "unsuccessful Uri get" , Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }else{

                        update_data("null",user_name);
                    }
                }
                else {
                    Toast.makeText(UserInformation.this, "Please Enter Your Name" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void update_data(String uri, String user_name) {

        saveButton.setEnabled(false);
        Uri uri1 = Uri.parse(uri);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", uri1.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    saveButton.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    //Toast.makeText(User_Information.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(UserInformation.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(UserInformation.this, "(FIRE_STORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(UserInformation.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                CurrentImageURI = Objects.requireNonNull(result).getUri();
                circleImage.setImageURI(CurrentImageURI);
                isChanged = true;

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackForCommunity();

    }

    private void goBackForCommunity() {
        if(!Data_check)
        {
            update_data("null",userNameEditText.getText().toString());
            Data_check =true;
        }
        Intent mainIntent = new Intent(UserInformation.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(comingFrom == 1)
            getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_skip:
                goBackForCommunity();
                break;
            default:
                break;
        }
        return true;
    }
}
