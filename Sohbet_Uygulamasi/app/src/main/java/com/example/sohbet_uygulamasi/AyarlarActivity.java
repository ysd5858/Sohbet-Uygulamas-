package com.example.sohbet_uygulamasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AyarlarActivity extends AppCompatActivity {

    public Button hesapAyarlariniGuncelleme;
    public EditText kullaniciAdi,kullaniciDurumu;
    public CircleImageView kullaniciProfilResmi;
    private Toolbar ayarlarToolbar;
    String mevcutKullaniciID;
    //Firebase
    public FirebaseAuth firebaseAuth;
    DatabaseReference veriYolu;
    private StorageReference KullaniciProfilResimleriYolu;
    private StorageTask yuklemeGorevi;
    //Yukleniyor
    private ProgressDialog yukleniyorBar;
    //Resim Seçme
    private static final int GaleriSecme = 1;
    //Uri
    Uri resimUri;
    String myUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);

        hesapAyarlariniGuncelleme=findViewById(R.id.ayarları_guncelleme_butonu);
        kullaniciAdi=findViewById(R.id.kullanici_adi_ayarla);
        kullaniciDurumu=findViewById(R.id.profil_durumu_ayarla);
        kullaniciProfilResmi=findViewById(R.id.kisi_resmi);

        firebaseAuth=FirebaseAuth.getInstance();
        mevcutKullaniciID=firebaseAuth.getCurrentUser().getUid();

        veriYolu = FirebaseDatabase.getInstance().getReference();
        KullaniciProfilResimleriYolu= FirebaseStorage.getInstance().getReference().child("Profil Resimleri");

        yukleniyorBar=new ProgressDialog(this);

        ayarlarToolbar = findViewById(R.id.ayarlar_toolbar);
        setSupportActionBar(ayarlarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Hesap Ayarları");

        hesapAyarlariniGuncelleme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AyarlariGuncelle();
            }
        });
        kullaniciAdi.setVisibility(View.INVISIBLE);
        KullaniciBilgisiAl();
        kullaniciProfilResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(AyarlarActivity.this);
            }
        });
    }
    private String dosyaUzantisiAl(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            resimUri = result.getUri();
            kullaniciProfilResmi.setImageURI(resimUri);

        }
        else
        {
            Toast.makeText(this, "Resim Seçilemedi'", Toast.LENGTH_LONG).show();
        }


    }

    private void KullaniciBilgisiAl() {
        veriYolu.child("Kullanicilar").child(mevcutKullaniciID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())&&(snapshot.hasChild("ad"))&&(snapshot.exists())&&(snapshot.hasChild("resim"))){
                    String kullaniciAdiAl = snapshot.child("ad").getValue().toString();
                    String kullaniciDurumuAl = snapshot.child("durum").getValue().toString();
                    String kullaniciResmiAl = snapshot.child("resim").getValue().toString();

                    kullaniciAdi.setText(kullaniciAdiAl);
                    kullaniciDurumu.setText(kullaniciDurumuAl);
                    Picasso.get().load(kullaniciResmiAl).into(kullaniciProfilResmi);

                }else if ((snapshot.exists())&&(snapshot.hasChild("ad"))){
                    String kullaniciAdiAl = snapshot.child("ad").getValue().toString();
                    String kullaniciDurumuAl = snapshot.child("durum").getValue().toString();


                    kullaniciAdi.setText(kullaniciAdiAl);
                    kullaniciDurumu.setText(kullaniciDurumuAl);

                }else{
                    kullaniciAdi.setVisibility(View.VISIBLE);
                    Toast.makeText(AyarlarActivity.this, "Lütfen Profil verilerinizi ayarlayın!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AyarlariGuncelle() {
        String kullaniciAdiniAl = kullaniciAdi.getText().toString();
        String kullaniciDurumunuAl = kullaniciDurumu.getText().toString();

        if (TextUtils.isEmpty(kullaniciAdiniAl))
        {
            Toast.makeText(this, "Ad boş olamaz!", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(kullaniciDurumunuAl))
        {
            Toast.makeText(this, "Durum boş olamaz!", Toast.LENGTH_LONG).show();
        }
        else{
            bilgileriYukle();
        }

    }

    private void bilgileriYukle() {
        yukleniyorBar.setTitle("Bilgi Aktarma");
        yukleniyorBar.setMessage("Lütfen bekleyin");
        yukleniyorBar.setCanceledOnTouchOutside(false);
        yukleniyorBar.show();

        if (resimUri ==null)
        {
            DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");

            String gonderiId = veriYolu.push().getKey();

            String kullaniciAdiAl = kullaniciAdi.getText().toString();
            String kullaniciDurumuAl = kullaniciDurumu.getText().toString();

            HashMap<String,Object> profilHaritasi = new HashMap<>();
            profilHaritasi.put("uid",gonderiId);
            profilHaritasi.put("ad",kullaniciAdiAl);
            profilHaritasi.put("durum",kullaniciDurumuAl);

            veriYolu.child(mevcutKullaniciID).updateChildren(profilHaritasi);

            yukleniyorBar.dismiss();

        }else{
            final StorageReference resimYolu = KullaniciProfilResimleriYolu.child(mevcutKullaniciID+"."+dosyaUzantisiAl(resimUri));
            yuklemeGorevi =resimYolu.putFile(resimUri);
            yuklemeGorevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return resimYolu.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri indirmeUrisi=task.getResult();
                        myUri=indirmeUrisi.toString();
                        veriYolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
                        String gonderiId = veriYolu.push().getKey();

                        String kullaniciAdiAl = kullaniciAdi.getText().toString();
                        String kullaniciDurumuAl = kullaniciDurumu.getText().toString();

                        HashMap<String,Object> profilHaritasi = new HashMap<>();
                        profilHaritasi.put("uid",gonderiId);
                        profilHaritasi.put("ad",kullaniciAdiAl);
                        profilHaritasi.put("durum",kullaniciDurumuAl);
                        profilHaritasi.put("resim",myUri);

                        veriYolu.child(mevcutKullaniciID).updateChildren(profilHaritasi);
                        yukleniyorBar.dismiss();
                    }
                    else
                    {
//Başarısızsa
                        String hata = task.getException().toString();
                        Toast.makeText(AyarlarActivity.this, "Hata: "+hata, Toast.LENGTH_LONG).show();
                        yukleniyorBar.dismiss();
                        Intent mesajaDon=new Intent(AyarlarActivity.this,Mesaj_Activity.class);
                        startActivity(mesajaDon);
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AyarlarActivity.this, "Hata: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    yukleniyorBar.dismiss();
                }
            });
        }
        }

}