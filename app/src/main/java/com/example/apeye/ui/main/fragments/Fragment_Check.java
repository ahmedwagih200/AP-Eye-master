package com.example.apeye.ui.main.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apeye.R;
import com.example.apeye.adapters.RecyclerPlantKindAdapter;
import com.example.apeye.api.ApiService;
import com.example.apeye.api.RetrofitBuilder;
import com.example.apeye.model.ApiResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_Check extends Fragment implements Callback<ApiResponse> {
    private static final String TAG = "Fragment_Check";
    private static final int CAMERA_PIC_REQ = 1;
    private static final int CAMERA_PERMISSION_REQ = 2;
    private ImageView image;
    private TextView textPrediction;
    private ProgressBar progressBar;
    private byte[] imageBytes;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private ApiService apiService;
    private int[] mPlants;
    private ArrayList<String> mNames = new ArrayList<>();
    private RecyclerPlantKindAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check, container, false);
        image = view.findViewById(R.id.image);
        progressBar = view.findViewById(R.id.progressBar);
        apiService = RetrofitBuilder.createService(ApiService.class);
        Button captureButton = view.findViewById(R.id.btnCapture);
        Button checkButton = view.findViewById(R.id.btnCheck);
        Button checkOffline = view.findViewById(R.id.tfliteButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.plantsRecycler);
        recyclerView.setLayoutManager(layoutManager);
        initPlants();
        adapter = new RecyclerPlantKindAdapter(mPlants, mNames, getContext());
        recyclerView.setAdapter(adapter);


        textPrediction = view.findViewById(R.id.textPredection);
        textPrediction.setVisibility(View.GONE);
        checkOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });


        return view;

    }

    private void initPlants() {
        mPlants = new int[]{
                R.drawable.ic_apple,
                R.drawable.ic_cherries,
                R.drawable.ic_corn,
                R.drawable.ic_grapes,
                R.drawable.ic_peach,
                R.drawable.ic_pepper,
                R.drawable.ic_potatoes,
                R.drawable.ic_strawberry,
                R.drawable.ic_tomato
        };
        mNames.add("apple");
        mNames.add("cherry");
        mNames.add("corn");
        mNames.add("grape");
        mNames.add("peach");
        mNames.add("pepper");
        mNames.add("potato");
        mNames.add("strawberry");
        mNames.add("tomato");
    }


    private void uploadFile() {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
        } else {
            uploadImage();
        }
    }

    private void capture() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PIC_REQ);
        } else {
            requestPermission();
        }
    }

    private void uploadImage() {
        if (adapter.getSelected() == null) {
            showErrorDialog(1);
        } else {
            if (imageBytes != null) {
                progressBar.setVisibility(View.VISIBLE);
                final StorageReference imageReference = mStorageRef.child("image" + ".jpeg");
                mUploadTask = imageReference.putBytes(imageBytes).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uriPredict(uri,adapter.getSelected());
                                    }
                                });

                                //Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            } else {
                showErrorDialog(2);
            }
        }
    }

    private void showErrorDialog(int i) {
        if (i == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setMessage("Please pick the plant type first")
                    .setCancelable(true)
                    .setTitle("We are Sorry")
                    .setIcon(R.drawable.ic_confused);
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setMessage("Please Capture image to check")
                    .setCancelable(true)
                    .setTitle("We are Sorry")
                    .setIcon(R.drawable.ic_confused);
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    private void uriPredict(Uri uri,String type) {
        Call<ApiResponse> call = apiService.classify(uri.toString(), type);
        call.enqueue(this);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQ && resultCode == RESULT_OK) {
            assert data != null;
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            image.setImageBitmap(photo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert photo != null;

            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();

        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
        if (response.isSuccessful()) {
            assert response.body() != null;
            Log.e(TAG, "onResponse: " + response.body().getFlowers().get(0));
            textPrediction.setVisibility(View.VISIBLE);
            textPrediction.setText(response.body().getFlowers().get(0) + " with acc : " + response.body().getPred().get(0));
            Toast.makeText(getActivity(), response.body().getFlowers().get(0), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
        Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
        progressBar.setVisibility(View.GONE);
    }
}
