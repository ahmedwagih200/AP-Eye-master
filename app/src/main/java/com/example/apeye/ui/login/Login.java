package com.example.apeye.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
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

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;
    private EditText loginEmailText;
    private EditText loginPassText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText = findViewById(R.id.email_reg);
        loginPassText = findViewById(R.id.password_reg);
        Button loginBtn = findViewById(R.id.create_acc_button);
        Button regBtn = findViewById(R.id.buttonNewAcc);
        loginProgress = findViewById(R.id.progressBar_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.login);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendToComFrag();
                            }
                            else{
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(Login.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Reg.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        loginProgress.setVisibility(View.INVISIBLE);
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            sendToComFrag();
//        }
    }

    private void sendToComFrag() {
        MainActivity.FirstRun =false;
        startActivity(new Intent(Login.this, MainActivity.class));
        this.finish();

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Login.this, MainActivity.class));
        this.finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_gust:
                startActivity(new Intent(Login.this, MainActivity.class));
                this.finish();
                break;
                default:
                    break;
        }
        return true;
    }
}
