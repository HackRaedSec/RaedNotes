package com.example.raednotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText, passworEditText, confirmPasswordEditText ;
    Button creatAccountBtn;
    ProgressBar progressBar;
    TextView loginTextViwe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_text);
        passworEditText = findViewById(R.id.password_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_text);
        creatAccountBtn = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progress_bar);
        loginTextViwe = findViewById(R.id.login_button);


        creatAccountBtn.setOnClickListener(v-> createAccount());
        loginTextViwe.setOnClickListener(v-> finish());




    }
    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passworEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (!isValidated){
            return;
        }

        createAccountInFirebase(email,password);



    }

    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password) .addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()){
                            //Create Account is Done
                            Utility.showToast(CreateAccountActivity.this, "Succesfully Create Account, Check Email to verify!!!");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            //Failure
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());


                        }

                    }
                }
        );



    }
    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            creatAccountBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            creatAccountBtn.setVisibility(View.VISIBLE);

        }

    }

    boolean validateData(String email,String password, String confirmPassword){
        //validate the input data of user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is Invalid");
            return false;

        }

        if (password.length()<6){
            passworEditText.setError("Password Length is Invalid");
            return false;

        }

        if (!password.equals(confirmPassword)){
            passworEditText.setError("Password Not Matched");
            return false;

        }
        return true;

    }
}