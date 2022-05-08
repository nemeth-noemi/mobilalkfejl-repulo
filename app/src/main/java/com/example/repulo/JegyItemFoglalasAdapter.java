package com.example.repulo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JegyItemFoglalasAdapter extends RecyclerView.Adapter<JegyItemFoglalasAdapter.ViewHolder> {

    private Context context;
    private List<Jegy> jegyek;
    private String userId;

    public JegyItemFoglalasAdapter(Context context, List<Jegy> jegyek, String userId) {
        this.context = context;
        this.jegyek = jegyek;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JegyItemFoglalasAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.activity_jegy_item_foglalas, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(jegyek.get(position), userId);
    }

    @Override
    public int getItemCount() {
        return jegyek.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView honnan;
        private TextView hova;
        private TextView szekSzam;
        private TextView utazasiDatum;
        private TextView osztaly;
        private TextView szekSzamText;
        private Button jegyItemGomb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            honnan = itemView.findViewById(R.id.honnan);
            hova = itemView.findViewById(R.id.hova);
            szekSzam = itemView.findViewById(R.id.szekSzam);
            utazasiDatum = itemView.findViewById(R.id.utazasiDatum);
            osztaly = itemView.findViewById(R.id.osztaly);
            szekSzamText = itemView.findViewById(R.id.szekszamText);
            jegyItemGomb = itemView.findViewById(R.id.jegyItemGomb);

        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        void bindTo(Jegy jegy, String userId){
            jegyItemGomb.setOnClickListener(view -> {
                jegy.setVevoId(userId);
                Map<String, Object> data = new HashMap<>();
                data.put("Honnan", jegy.getHonnan());
                data.put("hova", jegy.getHova());

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Jegyek").document(jegy.getId());
                reference.update("vevoId", userId, "megvett", true).addOnCompleteListener(query -> {
                    if (query.isSuccessful()) {
                        Toast.makeText(context, "Sikeres foglalás!", Toast.LENGTH_LONG).show();
                        Intent jegyeimIntent = new Intent(context, Jegyeim.class);
                        jegyeimIntent.putExtra("frissit", true);
                        context.startActivity(jegyeimIntent);
                    } else {
                        Toast.makeText(context, "Sikertelen foglalás!", Toast.LENGTH_LONG).show();
                    }
                });
            });

            honnan.setText(jegy.getHonnan());
            hova.setText(jegy.getHova());
            szekSzam.setText(jegy.getSzekSzam()+"");
            utazasiDatum.setText(jegy.getIndulasiIdo());
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

    }
}