package com.example.repulo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JegyItemAdminAdapter extends RecyclerView.Adapter<JegyItemAdminAdapter.ViewHolder> {

    private Context context;
    private List<Jegy> jegyek;
    private boolean reszlet;

    public JegyItemAdminAdapter(Context context, List<Jegy> jegyek, boolean reszlet) {
        this.context = context;
        this.jegyek = jegyek;
        this.reszlet = reszlet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.jegy_item_admin, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(jegyek.get(position), reszlet);
    }

    @Override
    public int getItemCount() {
        return jegyek.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private List<Jegy> jegyek;
        private TextView honnan;
        private TextView hova;
        private TextView szekSzam;
        private TextView utazasiDatum;
        private TextView osztaly;
        private TextView szekSzamText;
        private TextView vevoAzonositoText;
        private TextView vevoAzonositoTextText;
        private Button jegyItemGomb;
        private Button jegyTorlesGomb;
        private Button jegyModositasGomb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            honnan = itemView.findViewById(R.id.honnan);
            hova = itemView.findViewById(R.id.hova);
            szekSzam = itemView.findViewById(R.id.szekSzam);
            utazasiDatum = itemView.findViewById(R.id.utazasiDatum);
            osztaly = itemView.findViewById(R.id.osztaly);
            szekSzamText = itemView.findViewById(R.id.szekszamText);
            jegyItemGomb = itemView.findViewById(R.id.jegyItemGomb);
            jegyTorlesGomb = itemView.findViewById(R.id.jegyTorlesGomb);
            vevoAzonositoText = itemView.findViewById(R.id.vevoAzonosito);
            vevoAzonositoTextText = itemView.findViewById(R.id.vevoAzonositoText);
            jegyModositasGomb = itemView.findViewById(R.id.jegyModositasGomb);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        void bindTo(Jegy jegy, boolean reszlet){

            jegyTorlesGomb.setOnClickListener(view ->{
                quaryjaratTorles(jegy.getHonnan(), jegy.getHova(), jegy.getIndulasiIdoString(), jegy.getJegyTipus().toString());
            });

            if(!reszlet){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vevoAzonositoText.getLayoutParams();
                params.topMargin = -80;
                vevoAzonositoText.setLayoutParams(params);
                vevoAzonositoText.setVisibility(View.INVISIBLE);
                params = (RelativeLayout.LayoutParams) vevoAzonositoTextText.getLayoutParams();
                params.topMargin = -80;
                vevoAzonositoTextText.setLayoutParams(params);
                vevoAzonositoTextText.setVisibility(View.INVISIBLE);
                szekSzamText.setText("Férőhelyek:");

                jegyModositasGomb.setOnClickListener(view -> {
                    Intent szamlaRNU = new Intent(context, JaratRMU.class);
                    szamlaRNU.putExtra("jegy", (Serializable) jegy);
                    context.startActivity(szamlaRNU);
                });

                jegyItemGomb.setOnClickListener(view ->{
                    Intent jegyeimIntent = new Intent(context, AdminFelulet.class);
                    jegyeimIntent.putExtra("honnan", jegy.getHonnan());
                    jegyeimIntent.putExtra("hova", jegy.getHova());
                    jegyeimIntent.putExtra("indulasiIdo", jegy.getIndulasiIdoString());
                    jegyeimIntent.putExtra("jegyTipus", jegy.getJegyTipus().toString());
                    jegyeimIntent.putExtra("jaratReszletek", true);
                    context.startActivity(jegyeimIntent);
                });

            } else{
                jegyTorlesGomb.setVisibility(View.INVISIBLE);
                jegyTorlesGomb.setVisibility(View.INVISIBLE);
                jegyModositasGomb.setVisibility(View.INVISIBLE);
                jegyItemGomb.setText("Vevő módosítás");

                jegyItemGomb.setOnClickListener(view -> {
                    Intent szamlaRNU = new Intent(context, JaratRMU.class);
                    szamlaRNU.putExtra("jegy", (Serializable) jegy);
                    szamlaRNU.putExtra("vevoModosit", true);
                    context.startActivity(szamlaRNU);
                });
            }

            honnan.setText(jegy.getHonnan());
            hova.setText(jegy.getHova());
            szekSzam.setText(jegy.getSzekSzam()+"");
            utazasiDatum.setText(jegy.getIndulasiIdo());
            vevoAzonositoText.setText(jegy.getVevoId());
            switch (jegy.getJegyTipus()){
                case FAPADOS:
                    osztaly.setText("Fapados");
                    break;
                case BUSINESS:
                    osztaly.setText("Business");
                    break;
                case ELSO_OSZTALY:
                    osztaly.setText("Első osztály");
                    break;
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void quaryjaratTorles(String honnan, String hova, String indulasiIdo, String jegyTipus) {
            System.out.println("alma"+indulasiIdo);
            FirebaseFirestore.getInstance().collection("Jegyek")
                    .whereEqualTo("honnan", honnan)
                    .whereEqualTo("hova", hova)
                    .whereEqualTo("indulasiIdo", indulasiIdo)
                    .whereEqualTo("jegyTipus", jegyTipus)
                    .get().addOnSuccessListener(queryJegyek ->{
                for (QueryDocumentSnapshot quaryJegy : queryJegyek){
                    Jegy jegy = new Jegy(
                            quaryJegy.getId(),
                            (String) quaryJegy.get("honnan"),
                            (String) quaryJegy.get("hova"),
                            Integer.parseInt(quaryJegy.get("szekSzam").toString()),
                            (String) quaryJegy.get("jegyTipus"),
                            LocalDateTime.parse((String) quaryJegy.get("indulasiIdo")),
                            Integer.parseInt(quaryJegy.get("ar").toString()),
                            (boolean) quaryJegy.get("megvett"),
                            (String) quaryJegy.get("vevoId")
                    );
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Jegyek").document(jegy.getId());
                    documentReference.delete().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            //notificationHandler.send("Sikeres törlés!");
                        }else{
                            //notificationHandler.send("Sikertelen törlés!");
                        }
                    });
                }
                Intent intent = new Intent(context, AdminFelulet.class);
                intent.putExtra("frissit", true);
                context.startActivity(intent);
            });
        }

    }
}