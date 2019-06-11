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

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Reg extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
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

        reg_email_field = findViewById(R.id.email_reg);
        reg_pass_field = findViewById(R.id.password_reg);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        final Button reg_btn = findViewById(R.id.create_acc_button);
        mAuth = FirebaseAuth.getInstance();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                reg_btn.setEnabled(false);
                                progressBar.setVisibility(View.INVISIBLE);
                                MainActivity.FirstRun = false;
                                Intent intent = new Intent(Reg.this, UserInformation.class);
                                intent.putExtra("comingFrom",1);
                                startActivity(intent);
                                finish();

                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(Reg.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
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

        switch (item.getItemId()){

            case android.R.id.home:
                startActivity(new Intent(Reg.this, MainActivity.class));
                this.finish();
                break;
            default:
                break;
        }
        return true;

    }
}
