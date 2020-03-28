package com.gerielcastro.empresaexpofranchising;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroActivity extends AppCompatActivity {

    private Button scan, gravar;
    private TextView textView, data1,nomeEmpresa;
    final Activity activity = this;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef1;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        scan = (Button) findViewById(R.id.btnScan);
        gravar = (Button) findViewById(R.id.btnGravar);
        textView = (TextView) findViewById(R.id.scan_text);
        data1 = (TextView) findViewById(R.id.data_text);
        nomeEmpresa = (TextView) findViewById(R.id.empresa_text_nome);
        back = (ImageView) findViewById(R.id.arrow_back_formulario1);

        Bundle extra = getIntent().getExtras();
        String nome_empresa = extra.getString("NomeEmpresa");
        nomeEmpresa.setText(nome_empresa);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(CadastroActivity.this,HomeActivity.class);
              startActivity(intent);
              finish();
            }
        });

        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        Date data = new Date();
        String dataFormatada = formataData.format(data);
        data1.setText(dataFormatada);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                intentIntegrator.setOrientationLocked(Boolean.parseBoolean("portrait"));
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("CAMERA SCAN");
                intentIntegrator.setCameraId(0);
                intentIntegrator.initiateScan();
            }
        });

        mDatabaseRef1 = FirebaseDatabase.getInstance().getReference("Empresa");
        mDatabaseRef1.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        gravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView.getText().toString().isEmpty()) {
                    Toast.makeText(CadastroActivity.this, "Clica em Escanear Código", Toast.LENGTH_LONG).show();
                } else {

                    Empresa empresa = new Empresa();
                    empresa.setCode39(textView.getText().toString());
                    empresa.setData(data1.getText().toString());
                    empresa.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    empresa.setNomeEmpresa(nomeEmpresa.getText().toString());

                    String uploadId = mDatabaseRef1.push().getKey();
                    empresa.setmKey(uploadId);

                    mDatabaseRef1.child(uploadId).setValue(empresa).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(CadastroActivity.this, "Dados salvo com sucesso!", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroActivity.this, "Dados não salvo! OBS:. Problema com conexão", Toast.LENGTH_LONG).show();
                        }
                    });

                    Intent intent = new Intent(CadastroActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                alert(intentResult.getContents().toString());
                textView.setText(intentResult.getContents().toString());
            } else {
                alert("Scan cancelado");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void alert(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

}
