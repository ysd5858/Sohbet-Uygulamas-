package com.example.sohbet_uygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity {
    private String alinanKullaniciId,aktifKullaniciId,Aktif_Durum;
    private CircleImageView kullaniciProfilresmi;
    private TextView kullaniciProfilAdi,kullaniciProfilDurumu;
    private Button MesajGondermeTalebibuttonu,MesajDegerlendirmeTalebiButtonu;
    private DatabaseReference KullaniciYolu,SohbetTalebiYolu,SohbetlerYolu,BildirimYolu;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        alinanKullaniciId=getIntent().getExtras().get("tıklanan_kullanici_Id_goster").toString();

        kullaniciProfilresmi=findViewById(R.id.profil_resmi_ziyaret);
        kullaniciProfilAdi=findViewById(R.id.kullanici_adi_ziyaret);
        kullaniciProfilDurumu=findViewById(R.id.profil_durumu_ziyaret);
        MesajGondermeTalebibuttonu=findViewById(R.id.mesaj_gonderme_talebi_buttonu);
        MesajDegerlendirmeTalebiButtonu=findViewById(R.id.mesaj_degerlendirme_talebi_buttonu);
        KullaniciYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        SohbetTalebiYolu= FirebaseDatabase.getInstance().getReference().child("Sohbet Talebi");
        SohbetlerYolu= FirebaseDatabase.getInstance().getReference().child("Sohbetler");
        Aktif_Durum="yeni";
        firebaseAuth=FirebaseAuth.getInstance();
        aktifKullaniciId=firebaseAuth.getCurrentUser().getUid();

        KullaniciBilgisiAl();

    }

    private void KullaniciBilgisiAl() {
        KullaniciYolu.child(alinanKullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())&&(snapshot.hasChild("resim"))){

                    String kullaniciResmi= snapshot.child("resim").getValue().toString();
                    String kullaniciAdi= snapshot.child("ad").getValue().toString();
                    String kullaniciDurumu= snapshot.child("durum").getValue().toString();

                    Picasso.get().load(kullaniciResmi).placeholder(R.drawable.person).into(kullaniciProfilresmi);
                    kullaniciProfilAdi.setText(kullaniciAdi);
                    kullaniciProfilDurumu.setText(kullaniciDurumu);

                    chatTalepleriniYonet();
                }else
                {

                    String kullaniciAdi= snapshot.child("ad").getValue().toString();
                    String kullaniciDurumu= snapshot.child("durum").getValue().toString();

                    kullaniciProfilAdi.setText(kullaniciAdi);
                    kullaniciProfilDurumu.setText(kullaniciDurumu);

                    chatTalepleriniYonet();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatTalepleriniYonet() {

        SohbetTalebiYolu.child(aktifKullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(alinanKullaniciId))
                {
                    String talepTuru=snapshot.child(alinanKullaniciId).child("talep_turu").getValue().toString();
                    if (talepTuru.equals("gönderildi")){
                        Aktif_Durum="talep_gönderildi";
                        MesajGondermeTalebibuttonu.setText("Mesaj Talebi İptal");
                    }
                    else{
                        Aktif_Durum = "talep_alindi";
                        MesajGondermeTalebibuttonu.setText("Mesaj Talebi Kabul");
                        MesajDegerlendirmeTalebiButtonu.setVisibility(View.VISIBLE);
                        MesajDegerlendirmeTalebiButtonu.setEnabled(true);

                        MesajDegerlendirmeTalebiButtonu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MesajTalebiIptal();
                            }
                        });

                    }
                }else
                {
                    SohbetlerYolu.child(aktifKullaniciId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(alinanKullaniciId))
                            {
                                Aktif_Durum = "arkadaşlar";
                                MesajGondermeTalebibuttonu.setText("Bu sohbeti sil..");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (aktifKullaniciId.equals(alinanKullaniciId))
        {
            MesajGondermeTalebibuttonu.setVisibility(View.INVISIBLE);
        }
        else
        {
            MesajGondermeTalebibuttonu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MesajGondermeTalebibuttonu.setEnabled(false);
                    if (Aktif_Durum.equals("yeni"))
                    {
                        SohbetTalebiGonder();
                    }
                    if (Aktif_Durum.equals("talep_gönderildi"))
                    {
                        MesajTalebiIptal();
                    }
                    if (Aktif_Durum.equals("talep_alindi"))
                    {
                        MesajTalebiKabul();
                    }
                    if (Aktif_Durum.equals("arkadaşlar"))
                    {
                        OzelSohbetiSil();
                    }


                }
            });
        }
    }

    private void OzelSohbetiSil() {
        SohbetlerYolu.child(aktifKullaniciId).child(alinanKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    SohbetlerYolu.child(alinanKullaniciId).child(aktifKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                MesajGondermeTalebibuttonu.setEnabled(true);
                                Aktif_Durum = "yeni";
                                MesajGondermeTalebibuttonu.setText("Mesaj Talebi Gönder");

                                MesajDegerlendirmeTalebiButtonu.setVisibility(View.INVISIBLE);
                                MesajDegerlendirmeTalebiButtonu.setEnabled(false);
                            }

                        }
                    });
                }
            }
        });

    }

    private void MesajTalebiKabul() {
        SohbetlerYolu.child(aktifKullaniciId).child(alinanKullaniciId).child("Sohbetler").setValue("Kaydedildi").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    SohbetlerYolu.child(alinanKullaniciId).child(aktifKullaniciId).child("Sohbetler").setValue("Kaydedildi").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SohbetTalebiYolu.child(aktifKullaniciId).child(alinanKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            SohbetTalebiYolu.child(alinanKullaniciId).child(aktifKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    MesajGondermeTalebibuttonu.setEnabled(true);
                                                    Aktif_Durum = "arkadaşlar";
                                                    MesajGondermeTalebibuttonu.setText("Bu sohbeti sil");
                                                    MesajDegerlendirmeTalebiButtonu.setVisibility(View.INVISIBLE);
                                                    MesajDegerlendirmeTalebiButtonu.setEnabled(false);
                                                }
                                            });
                                        }

                                        }
                                });
                            }
                        }
                    });
                }
                }
        });

    }

    private void MesajTalebiIptal() {
        SohbetTalebiYolu.child(aktifKullaniciId).child(alinanKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    SohbetTalebiYolu.child(alinanKullaniciId).child(aktifKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                MesajGondermeTalebibuttonu.setEnabled(true);
                                Aktif_Durum = "yeni";
                                MesajGondermeTalebibuttonu.setText("Mesaj Talebi Gönder");

                                MesajDegerlendirmeTalebiButtonu.setVisibility(View.INVISIBLE);
                                MesajDegerlendirmeTalebiButtonu.setEnabled(false);
                            }

                        }
                    });
                }
            }
        });
    }

    private void SohbetTalebiGonder() {
        SohbetTalebiYolu.child(aktifKullaniciId).child(alinanKullaniciId).child("talep_turu").setValue("gönderildi").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    SohbetTalebiYolu.child(alinanKullaniciId).child(aktifKullaniciId).child("talep_turu")
                            .setValue("alındı").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                MesajGondermeTalebibuttonu.setEnabled(true);
                                Aktif_Durum="talep_gönderildi";
                                MesajGondermeTalebibuttonu.setText("Mesaj Talebi İptal");

                            }
                        }
                    });
                }
            }
        });

    }
}