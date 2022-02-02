package com.example.sohbet_uygulamasi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Model.Kisiler;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArkadaslarFragment extends Fragment {
    private View KisilerView;

    private RecyclerView kisilerListem;

    private DatabaseReference SohbetlerYolu,KullanıcılarYolu;
    private FirebaseAuth firebaseAuth;

    private String aktifKullaniciId;


    public ArkadaslarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        KisilerView= inflater.inflate(R.layout.fragment_arkadaslar, container, false);
        kisilerListem= KisilerView.findViewById(R.id.kisiler_listesi);
        kisilerListem.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseAuth=FirebaseAuth.getInstance();
        aktifKullaniciId=firebaseAuth.getCurrentUser().getUid();

        SohbetlerYolu= FirebaseDatabase.getInstance().getReference().child("Sohbetler").child(aktifKullaniciId);
        KullanıcılarYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");

        return KisilerView;

    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Kisiler> secenekler = new FirebaseRecyclerOptions.Builder<Kisiler>()
                .setQuery(SohbetlerYolu, Kisiler.class)
                .build();
        FirebaseRecyclerAdapter<Kisiler,KisilerViewHolder> adapter = new FirebaseRecyclerAdapter<Kisiler, KisilerViewHolder>(secenekler) {
            @NonNull
            @Override
            public KisilerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanici_gosterme_layout,parent,false);

                KisilerViewHolder viewHolder = new KisilerViewHolder(view);

                return  viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull KisilerViewHolder kisilerViewHolder, int i, @NonNull Kisiler kisiler) {
                String tıklananSatırKullaniciIdsi = getRef(i).getKey();
                KullanıcılarYolu.child(tıklananSatırKullaniciIdsi).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            if(snapshot.child("kullaniciDurumu").hasChild("durum"))
                            {
                                String durum = snapshot.child("kullaniciDurumu").child("durum").getValue().toString();
                                String tarih = snapshot.child("kullaniciDurumu").child("tarih").getValue().toString();
                                String zaman = snapshot.child("kullaniciDurumu").child("zaman").getValue().toString();

                                if (durum.equals("çevrimiçi"))
                                {
                                    kisilerViewHolder.cevrimIciIkonu.setVisibility(View.VISIBLE);
                                }

                                else if (durum.equals("çevrimdışı"))
                                {
                                    kisilerViewHolder.cevrimIciIkonu.setVisibility(View.INVISIBLE);
                                }
                            }

                            else
                            {
                                kisilerViewHolder.cevrimIciIkonu.setVisibility(View.INVISIBLE);
                            }

                            if (snapshot.hasChild("resim"))
                            {
                                String profilResmi = snapshot.child("resim").getValue().toString();
                                String kullaniciAdi = snapshot.child("ad").getValue().toString();
                                String kullaniciDurumu = snapshot.child("durum").getValue().toString();

                                kisilerViewHolder.kullaniciAdi.setText(kullaniciAdi);
                                kisilerViewHolder.kullaniciDurumu.setText(kullaniciDurumu);
                                Picasso.get().load(profilResmi).placeholder(R.drawable.person).into(kisilerViewHolder.profilResmi);
                            }

                            else
                            {
                                String kullaniciAdi = snapshot.child("ad").getValue().toString();
                                String kullaniciDurumu = snapshot.child("durum").getValue().toString();

                                kisilerViewHolder.kullaniciAdi.setText(kullaniciAdi);
                                kisilerViewHolder.kullaniciDurumu.setText(kullaniciDurumu);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        kisilerListem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class KisilerViewHolder extends RecyclerView.ViewHolder{

        TextView kullaniciAdi,kullaniciDurumu;
        CircleImageView profilResmi;
        ImageView cevrimIciIkonu;

        public KisilerViewHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciAdi=itemView.findViewById(R.id.kullanici_profil_adi);
            kullaniciDurumu=itemView.findViewById(R.id.kullanici_durumu);
            profilResmi=itemView.findViewById(R.id.kullanicilar_profil_resmi);
            cevrimIciIkonu=itemView.findViewById(R.id.kullanici_cevrimici_olma_durumu);
        }
    }

}