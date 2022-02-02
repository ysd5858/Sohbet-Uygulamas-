package com.example.sohbet_uygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Mesaj_Activity extends AppCompatActivity {
    public Toolbar actionBar;
    public ViewPager viewPager;
    public TabLayout tabLayout;
    public TabAdapter tabAdapter;
    FirebaseAuth firebaseAuth;
    DatabaseReference kullanicilarReference;
    private String aktifKullaniciId;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser==null) {
            Intent main = new Intent(Mesaj_Activity.this, MainActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main);
            finish();
        }else
        {
            kullaniciDurumuGuncelle("çevrimiçi");
            KullanicininVarliginiDogrula();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null) {
            kullaniciDurumuGuncelle("çevrimdışı");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null) {
            kullaniciDurumuGuncelle("çevrimdışı");
        }
    }

    private void KullanicininVarliginiDogrula() {
        String mevcutKullaniciId = firebaseAuth.getCurrentUser().getUid();
        kullanicilarReference.child("Kullanicilar").child(mevcutKullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("ad").exists()))
                {
                    Toast.makeText(Mesaj_Activity.this, "kullanıcı doğrulandı", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent ayarlar = new Intent(Mesaj_Activity.this,AyarlarActivity.class);
                    ayarlar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(ayarlar);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void kullaniciDurumuGuncelle(String durum)
    {
        String kaydedilenAktifZaman, kaydedilenAktifTarih;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat aktifTarih = new SimpleDateFormat("MMM dd, yyyy");
        kaydedilenAktifTarih=aktifTarih.format(calendar.getTime());

        SimpleDateFormat aktifZaman = new SimpleDateFormat("hh:mm a");
        kaydedilenAktifZaman=aktifZaman.format(calendar.getTime());

        HashMap<String,Object> cevrimiciDurumuMap = new HashMap<>();
        cevrimiciDurumuMap.put("zaman",kaydedilenAktifZaman);
        cevrimiciDurumuMap.put("tarih",kaydedilenAktifTarih);
        cevrimiciDurumuMap.put("durum",durum);

        aktifKullaniciId=firebaseAuth.getCurrentUser().getUid();
        kullanicilarReference.child("Kullanicilar").child(aktifKullaniciId).child("kullaniciDurumu").updateChildren(cevrimiciDurumuMap);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);

        actionBar =(Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setTitle("MESAJ MENÜSÜ");

        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tabAdapter=new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        tabLayout=(TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        firebaseAuth=FirebaseAuth.getInstance();
        kullanicilarReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.cikis){
            kullaniciDurumuGuncelle("çevrimdışı");
            firebaseAuth.signOut();
            Intent giris =new Intent(Mesaj_Activity.this,Giris_Activity.class);
            startActivity(giris);
            finish();
        }
        if (item.getItemId()==R.id.ana_ayarlar_secenegi)
        {
            Intent ayar=new Intent(Mesaj_Activity.this,AyarlarActivity.class);
            startActivity(ayar);

        }
        if (item.getItemId()==R.id.ana_arkadas_bulma_secenegi){
            Intent arkadasBul=new Intent(Mesaj_Activity.this,ArkadasBulActivity.class);
            startActivity(arkadasBul);
        }

        return true;
    }
}