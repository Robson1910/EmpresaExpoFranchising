package com.gerielcastro.empresaexpofranchising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogar;
    private EditText email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        email = (EditText) findViewById(R.id.Edit_text_email);
        password = (EditText) findViewById(R.id.Edit_text_password);
        btnLogar = (Button) findViewById(R.id.btnLogin);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean verificador = email.getText().toString().equals("");
                boolean verificador2 = password.getText().toString().equals("");

                if (!verificador && !verificador2) {
                    String edit_email = email.getText().toString().trim();
                    final String edit_password = password.getText().toString().trim();

                    auth.signInWithEmailAndPassword(edit_email, edit_password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {

                                        Toast.makeText(LoginActivity.this, "Sem Conexão com a Internet", Toast.LENGTH_LONG).show();
                                    } else {

                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Campo Vazio", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}