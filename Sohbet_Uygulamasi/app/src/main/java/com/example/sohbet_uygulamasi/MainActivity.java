package com.example.sohbet_uygulamasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button hesap_olustur;
    Button hesap_var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hesap_olustur =(Button) findViewById(R.id.btn_hesap_olustur);
        hesap_var =(Button) findViewById(R.id.btn_hesabim_var);

        hesap_var.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giris_gec = new Intent(MainActivity.this,Giris_Activity.class);
                startActivity(giris_gec);
                finish();
            }
        });
        hesap_olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kayit_gec = new Intent(MainActivity.this,Kayit_Activity.class);
                startActivity(kayit_gec);
                finish();
            }
        });
    }
}