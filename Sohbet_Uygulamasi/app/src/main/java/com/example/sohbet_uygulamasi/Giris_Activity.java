package com.example.sohbet_uygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Giris_Activity extends AppCompatActivity {

    Toolbar giris_toolbar;
    public EditText eposta,sifre;
    Button buton;
    //FireBase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        giris_toolbar =(androidx.appcompat.widget.Toolbar) findViewById(R.id.actbargiris);
        setSupportActionBar(giris_toolbar);
        getSupportActionBar().setTitle("Giris Yap");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        sifre = (EditText) findViewById(R.id.txtplain_sifre_giris);
        eposta =(EditText) findViewById(R.id.txtplain_email_giris);
        buton=(Button) findViewById(R.id.btn_create_new_account);


        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                giris_Yap();

            }
        });
    }

    private void giris_Yap() {
        String email=eposta.getText().toString();
        String password = sifre.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"E mail Alanı Boş Olamaz",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Şifre Alanı Boş Olamaz",Toast.LENGTH_LONG).show();
        }
        else{
            buton.setEnabled(false);
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Giris_Activity.this,"Giriş Başarılı",Toast.LENGTH_LONG).show();
                        Intent mesaj = new Intent(Giris_Activity.this,Mesaj_Activity.class);
                        mesaj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mesaj);
                        finish();
                    }
                    else {
                        Toast.makeText(Giris_Activity.this,"Giriş Başarısız",Toast.LENGTH_LONG).show();
                        buton.setEnabled(true);
                    }
                }
            });
        }
    }
}