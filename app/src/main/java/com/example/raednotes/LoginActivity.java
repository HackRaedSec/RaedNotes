package com.example.raednotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText, passworEditText ;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountTextViwe, mgotoforgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_text);
        passworEditText = findViewById(R.id.password_text);
        loginBtn = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        createAccountTextViwe = findViewById(R.id.create_account_button);
        mgotoforgotpassword = findViewById(R.id.gotoforgotpassword);


        loginBtn.setOnClickListener(v-> loginUser());
        createAccountTextViwe.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
        mgotoforgotpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent= new Intent(LoginActivity.this,forgotpassword.class);
                startActivity(intent);

            }
        });

    }
    void loginUser(){
        String email = emailEditText.getText().toString();
        String password = passworEditText.getText().toString();

        boolean isValidated = validateData(email, password);
        if (!isValidated){
            return;
        }

        loginAccountInFirebase(email,password);


    }
    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    //login Success
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        //go to main activity
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();

                    }else{
                        Utility.showToast(LoginActivity.this,"Email not verified");


                    }
                }else {
                    //login failed
                    Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
                }

            }
        });

    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);

        }

    }

    boolean validateData(String email,String password){
        //validate the input data of user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is Invalid");
            return false;

        }

        if (password.length()<6){
            passworEditText.setError("Password Length is Invalid");
            return false;

        }


        return true;

    }
}