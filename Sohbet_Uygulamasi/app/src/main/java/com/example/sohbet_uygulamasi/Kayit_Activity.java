package com.example.sohbet_uygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Kayit_Activity extends AppCompatActivity {
    Toolbar kayit_toolbar;
    public EditText sifre,eposta;
    public Button buton;
    FirebaseAuth firebaseAuth;
    DatabaseReference kok_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);

        kayit_toolbar =(androidx.appcompat.widget.Toolbar) findViewById(R.id.actbarkayit);
        setSupportActionBar(kayit_toolbar);
        getSupportActionBar().setTitle("Kayıt Ol");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        sifre = (EditText) findViewById(R.id.txtplain_sifre_kayit);
        eposta =(EditText) findViewById(R.id.txtplain_email_kayit);
        buton=(Button) findViewById(R.id.btn_create_new_account_kayit);
        kok_reference= FirebaseDatabase.getInstance().getReference();

        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                yeni_Hesap_Olustur();

            }
        });
    }

    private void yeni_Hesap_Olustur() {
        String email=eposta.getText().toString();
        String password = sifre.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"E mail Alanı Boş Olamaz",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Şifre Alanı Boş Olamaz",Toast.LENGTH_LONG).show();
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String mesajGonderenId=firebaseAuth.getCurrentUser().getUid();
                        kok_reference.child("Kullanicilar").child(mesajGonderenId).setValue("");
                        Toast.makeText(Kayit_Activity.this,"Hesabınız Başarılı Bir Şekilde Oluşturuldu",Toast.LENGTH_LONG).show();
                        Intent mesaja_git = new Intent(Kayit_Activity.this,Mesaj_Activity.class);
                        mesaja_git.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mesaja_git);
                        finish();
                    }
                    else {
                        Toast.makeText(Kayit_Activity.this,"Bir Hata Oluştu",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}