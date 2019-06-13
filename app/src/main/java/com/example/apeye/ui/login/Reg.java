package com.example.apeye.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apeye.R;
import com.example.apeye.api.ApiService;
import com.example.apeye.api.RetrofitBuilder;
import com.example.apeye.model.ApiSignUpResponse;
import com.example.apeye.ui.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reg extends AppCompatActivity implements Callback<ApiSignUpResponse> {
    private static final String TAG = "Reg";
    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_user_name;
    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth ;
    private ProgressBar progressBar;
    private ApiService apiService;
    private  String email,pass,userName;
    private Button reg_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.signup);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        reg_email_field = findViewById(R.id.email_reg);
        reg_user_name = findViewById(R.id.user_name_reg);
        reg_pass_field = findViewById(R.id.password_reg);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        apiService = RetrofitBuilder.createService(ApiService.class);

        reg_btn = findViewById(R.id.create_acc_button);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                email = reg_email_field.getText().toString();
                pass = reg_pass_field.getText().toString();
                userName =reg_user_name.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(userName)){
                    reg_btn.setEnabled(false);
                    if(isValidEmail(email)){
                        signUpInServer(email,userName,pass);
                    }else{
                        //TODO -> USE AWESOME VALIDATION
                        toastErrorMessage("Bad Email Form");
                    }

                }else{
                    //TODO -> USE AWESOME VALIDATION
                    toastErrorMessage("Can't SIGNUP with empty field(s).");
                }

            }
        });

    }
    private void signUpInServer(String email,String userName , String password) {
        Call<ApiSignUpResponse> call = apiService.signUp(email,userName,password);
        call.enqueue(this);
    }

    private void addData(String user_name ,String email , String password){

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("email", email);
        userMap.put("password", password);

        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    MainActivity.FirstRun =false;
                    Intent mainIntent = new Intent(Reg.this, MainActivity.class);
                    //mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();

                } else {
                    toastErrorMessage(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Reg.this, Login.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(Reg.this, MainActivity.class));
            this.finish();
        }
        return true;

    }

    @Override
    public void onResponse(Call<ApiSignUpResponse> call, Response<ApiSignUpResponse> response) {
        if(response.isSuccessful()){
            Log.e(TAG, "onResponse: "+response.body().getResult() );
            assert response.body() != null;
            if(response.body().getResult().equals("True")){
                signUpInFirebase(userName,email,pass);
            }else{
                toastErrorMessage(response.body().getResult());
            }
        }
        //progressBar.setVisibility(View.INVISIBLE);
    }

    private void signUpInFirebase(final String userName,final String email,final String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //data store
                    addData(userName,email,pass);
                    // MainActivity.FirstRun = false;
                    //Intent intent = new Intent(Reg.this, UserInformation.class);
                    // intent.putExtra("comingFrom",1);
                    // startActivity(intent);
                    // finish();
                }else {
                    toastErrorMessage(Objects.requireNonNull(task.getException()).getMessage());
                    
                }
            }

            
        });
    }

    @Override
    public void onFailure(Call<ApiSignUpResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: ."+t.getLocalizedMessage());
        toastErrorMessage(t.getLocalizedMessage());
       // progressBar.setVisibility(View.INVISIBLE);
    }
    private void toastErrorMessage(String errorMessage) {
        reg_btn.setEnabled(true);
        Toast.makeText(Reg.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
    }
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

