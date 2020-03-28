package com.gerielcastro.empresaexpofranchising;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String usuario = " ";
    private TextView user, empresa2;
    private ListView listView;
    private DatabaseReference firebase, firebase2;
    private ValueEventListener mDBListener;
    private Adapter mAdapter;
    private List<Empresa> mUploads;
    private ProgressBar mProgressCircle;
    private ImageView back;
    private Empresa excluir;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        back = (ImageView) findViewById(R.id.arrow_back_sair);
        mProgressCircle = findViewById(R.id.progressBar2);
        listView = (ListView) findViewById(R.id.listarquivo);
        mUploads = new ArrayList<>();
        mAdapter = new Adapter(HomeActivity.this, (ArrayList<Empresa>) mUploads);
        listView.setAdapter(mAdapter);

        usuario = auth.getInstance().getCurrentUser().getEmail();

        user = (TextView) findViewById(R.id.usuario);
        empresa2 = (TextView) findViewById(R.id.empresa1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLoginScreen();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CadastroActivity.class);
                intent.putExtra("NomeEmpresa", empresa2.getText().toString());
                startActivity(intent);
            }
        });


        firebase = FirebaseDatabase.getInstance().getReference("Empresa");
        firebase.keepSynced(true);
        mDBListener = firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Empresa lista_empresa = postSnapshot.getValue(Empresa.class);

                    String chave = lista_empresa.getmKey();
                    String userEmail = lista_empresa.getEmail();

                    if (!chave.equals("teste")) {
                        if (usuario.equals("gerielshop@gmail.com")) {
                            mUploads.add(lista_empresa);
                        } else if (usuario.equals(userEmail)) {
                            mUploads.add(lista_empresa);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("error=" + databaseError.getMessage());
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

        firebase2 = FirebaseDatabase.getInstance().getReference("Usuario");

        mDBListener = firebase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String email = (String) postSnapshot.child("email").getValue();
                    String empresa = (String) postSnapshot.child("empresa").getValue();

                    if (usuario.equals(email)) {
                        user.setText(email);
                        empresa2.setText(empresa);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("error=" + databaseError.getMessage());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                excluir = mAdapter.getItem(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Empresa " + excluir.getNomeEmpresa());
                builder.setMessage("Você deseja excluir o Código - " + excluir.getCode39() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        firebase.child(excluir.getmKey()).removeValue();
                        Toast.makeText(HomeActivity.this, "Dados excluido com sucesso", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(HomeActivity.this, "Dados não excluido", Toast.LENGTH_LONG).show();
                    }
                });

                alerta = builder.create();
                alerta.show();
            }
        });
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        auth.getInstance().signOut();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(mDBListener);
    }
}
