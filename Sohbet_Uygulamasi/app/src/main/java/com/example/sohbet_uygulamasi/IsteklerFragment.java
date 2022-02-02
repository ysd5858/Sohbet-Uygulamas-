package com.example.sohbet_uygulamasi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Model.Kisiler;
import de.hdodenhof.circleimageview.CircleImageView;

public class IsteklerFragment extends Fragment {

    private View TaleplerFragmentView;
    private RecyclerView taleplerListem;
    //firebase
    private DatabaseReference SohbetTalepleriYolu,KullanicilarYolu,SohbetlerYolu;
    private FirebaseAuth mYetki;
    private String aktifKullaniciId;

    public IsteklerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TaleplerFragmentView = inflater.inflate(R.layout.fragment_istekler, container, false);

        mYetki=FirebaseAuth.getInstance();
        aktifKullaniciId=mYetki.getCurrentUser().getUid();
        SohbetTalepleriYolu= FirebaseDatabase.getInstance().getReference().child("Sohbet Talebi");
        KullanicilarYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        SohbetlerYolu= FirebaseDatabase.getInstance().getReference().child("Sohbetler");


        taleplerListem= TaleplerFragmentView.findViewById(R.id.chat_talepleri_listesi);
        taleplerListem.setLayoutManager(new LinearLayoutManager(getContext()));

        return TaleplerFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Kisiler> secenekler = new FirebaseRecyclerOptions.Builder<Kisiler>()
                .setQuery(SohbetTalepleriYolu.child(aktifKullaniciId),Kisiler.class)
                .build();
        FirebaseRecyclerAdapter<Kisiler,TaleplerViewHolder> adapter = new FirebaseRecyclerAdapter<Kisiler, TaleplerViewHolder>(secenekler) {
            @NonNull
            @Override
            public TaleplerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kullanici_gosterme_layout,parent,false);

                TaleplerViewHolder holder = new TaleplerViewHolder(view);

                return holder;

            }

            @Override
            protected void onBindViewHolder(@NonNull TaleplerViewHolder taleplerViewHolder, int i, @NonNull Kisiler kisiler) {

                taleplerViewHolder.itemView.findViewById(R.id.talep_kabul_buttonu).setVisibility(View.VISIBLE);
                taleplerViewHolder.itemView.findViewById(R.id.talep_iptal_buttonu).setVisibility(View.VISIBLE);

                final String kullanici_id_listesi = getRef(i).getKey();

                DatabaseReference talepTuruAl = getRef(i).child("talep_turu").getRef();

                talepTuruAl.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String tur = snapshot.getValue().toString();

                            if (tur.equals("alındı")) {
                                KullanicilarYolu.child(kullanici_id_listesi).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("resim"))
                                        {
                                            final String talepProfilResmi = snapshot.child("resim").getValue().toString();
                                            Picasso.get().load(talepProfilResmi).into(taleplerViewHolder.profilResmi);
                                        }
                                        final String talepKullaniciAdi = snapshot.child("ad").getValue().toString();


                                        taleplerViewHolder.kullaniciAdi.setText(talepKullaniciAdi);
                                        taleplerViewHolder.kullaniciDurumu.setText("kullanıcı senle iletişim kurmak istiyor");


                                        taleplerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence secenekler[] = new CharSequence[]
                                                        {
                                                                "Kabul",
                                                                "İptal"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(talepKullaniciAdi+" Chat Talebi");

                                                builder.setItems(secenekler, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0)
                                                        {
                                                            SohbetlerYolu.child(aktifKullaniciId).child(kullanici_id_listesi).child("Sohbetler")
                                                                    .setValue("Kaydedildi").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        SohbetlerYolu.child(kullanici_id_listesi).child(aktifKullaniciId)
                                                                                .child("Sohbetler").setValue("Kaydedildi")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            SohbetTalepleriYolu.child(aktifKullaniciId).child(kullanici_id_listesi)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful())
                                                                                                            {
                                                                                                                SohbetTalepleriYolu.child(kullanici_id_listesi).child(aktifKullaniciId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                Toast.makeText(getContext(), "Sohbet kaydedildi..", Toast.LENGTH_LONG).show();

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
                                                        if(i==1){
                                                            SohbetTalepleriYolu.child(aktifKullaniciId).child(kullanici_id_listesi)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                SohbetTalepleriYolu.child(kullanici_id_listesi).child(aktifKullaniciId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                Toast.makeText(getContext(), "Sohbet silindi..", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        });
                                                                            }


                                                                            }
                                                                    });


                                                        }

                                                        }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        taleplerListem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }
        public static class TaleplerViewHolder extends RecyclerView.ViewHolder{

            TextView kullaniciAdi, kullaniciDurumu;
            CircleImageView profilResmi;
            Button KabulButtonu,IptalButtonu;

            public TaleplerViewHolder(@NonNull View itemView) {
                super(itemView);

                kullaniciAdi=itemView.findViewById(R.id.kullanici_profil_adi);
                kullaniciDurumu=itemView.findViewById(R.id.kullanici_durumu);
                profilResmi=itemView.findViewById(R.id.kullanicilar_profil_resmi);
                KabulButtonu=itemView.findViewById(R.id.talep_kabul_buttonu);
                IptalButtonu=itemView.findViewById(R.id.talep_iptal_buttonu);
            }

        }
}
