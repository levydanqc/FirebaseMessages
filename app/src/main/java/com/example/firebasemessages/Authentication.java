package com.example.firebasemessages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        btLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.equals("")) {
                Toast.makeText(Authentication.this, "Le champs 'Email' est requis.",
                        Toast.LENGTH_SHORT).show();
            } else if (password.equals("")) {
                Toast.makeText(Authentication.this, "Le champs 'Mot de passe' est requis.",
                        Toast.LENGTH_SHORT).show();
            } else {
                LoginUser(email, password);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Authentication.this, MainActivity.class));
            finish();
        }
    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(Authentication.this, MainActivity.class));
                    } else {
                        Toast.makeText(Authentication.this, "Email ou mot de passe invalide.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


}