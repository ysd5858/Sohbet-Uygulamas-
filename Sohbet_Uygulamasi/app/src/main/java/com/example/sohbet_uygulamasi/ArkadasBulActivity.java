package com.example.sohbet_uygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Kisiler;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArkadasBulActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView ArkadasBulRecyclerListesi;
    private DatabaseReference KullaniciYolu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arkadas_bul);

        ArkadasBulRecyclerListesi=findViewById(R.id.arkadas_bul_recyler_listesi);
        ArkadasBulRecyclerListesi.setLayoutManager(new LinearLayoutManager(this));
        KullaniciYolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");

        mToolbar=findViewById(R.id.arkadas_bul_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Arkadaş Bul");

        }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Kisiler> secenekler =
                new FirebaseRecyclerOptions.Builder<Kisiler>()
                        .setQuery(KullaniciYolu, Kisiler.class)
                        .build();


        FirebaseRecyclerAdapter<Kisiler,ArkadasBulViewHolder>adapter=new FirebaseRecyclerAdapter<Kisiler, ArkadasBulViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull ArkadasBulViewHolder arkadasBulViewHolder, int i, @NonNull Kisiler kisiler) {
                arkadasBulViewHolder.kullaniciAdi.setText(kisiler.getAd());
                arkadasBulViewHolder.kullaniciDurumu.setText(kisiler.getDurum());
                Picasso.get().load(kisiler.getResim()).into(arkadasBulViewHolder.profilResmi);

                arkadasBulViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       final int a = arkadasBulViewHolder.getAdapterPosition();

                        String tıklanan_kullanici_Id_goster = getRef(a).getKey();

                        Intent profilAktivite = new Intent(ArkadasBulActivity.this,ProfilActivity.class);
                        profilAktivite.putExtra("tıklanan_kullanici_Id_goster",tıklanan_kullanici_Id_goster);
                        startActivity(profilAktivite);
                    }
                });


            }

            @NonNull
            @Override
            public ArkadasBulViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanici_gosterme_layout,parent,false);
                ArkadasBulViewHolder viewHolder = new ArkadasBulViewHolder(view);
                return viewHolder;

            }
        };
        ArkadasBulRecyclerListesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    public static class ArkadasBulViewHolder extends RecyclerView.ViewHolder{
        TextView kullaniciAdi,kullaniciDurumu;
        CircleImageView profilResmi;

        public ArkadasBulViewHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciAdi=itemView.findViewById(R.id.kullanici_profil_adi);
            kullaniciDurumu=itemView.findViewById(R.id.kullanici_durumu);
            profilResmi=itemView.findViewById(R.id.kullanicilar_profil_resmi);

        }
    }
}