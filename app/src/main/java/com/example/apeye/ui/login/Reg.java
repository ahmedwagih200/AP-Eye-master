package com.example.apeye.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apeye.R;
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

public class Reg extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_user_name;
    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth ;
    private ProgressBar progressBar;

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

        final Button reg_btn = findViewById(R.id.create_acc_button);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String email = reg_email_field.getText().toString();
                final String pass = reg_pass_field.getText().toString();
                final String userName =reg_user_name.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(userName)){
                    reg_btn.setEnabled(false);
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //API check

                                //data store
                                add_data(userName,email,pass);

                               // MainActivity.FirstRun = false;
                                //Intent intent = new Intent(Reg.this, UserInformation.class);
                               // intent.putExtra("comingFrom",1);
                               // startActivity(intent);
                               // finish();
                            }else {
                                reg_btn.setEnabled(true);
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(Reg.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void add_data(String user_name ,String email , String password){

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
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(Reg.this, "(FIRE_STORE Error) : " + error, Toast.LENGTH_LONG).show();
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
}

