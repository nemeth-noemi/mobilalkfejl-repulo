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

import java.util.List;

public class JegyItemAdapter extends RecyclerView.Adapter<JegyItemAdapter.ViewHolder> {

    private Context context;
    private List<Jegy> jegyek;

    public JegyItemAdapter(Context context, List<Jegy> jegyek) {
        this.context = context;
        this.jegyek = jegyek;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.jegy_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(jegyek.get(position));
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
        void bindTo(Jegy jegy){
            if(!jegy.isMegvett()){
                szekSzamText.setText("Elérhető helyek:");
                jegyItemGomb.setText("Részletek");
                jegyItemGomb.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) jegyItemGomb.getLayoutParams();
                params.topMargin = 0;
                jegyItemGomb.setLayoutParams(params);

                jegyItemGomb.setOnClickListener(view ->{
                    Intent jegyeimIntent = new Intent(context, Jegyeim.class);
                    jegyeimIntent.putExtra("honnan", jegy.getHonnan());
                    jegyeimIntent.putExtra("hova", jegy.getHova());
                    jegyeimIntent.putExtra("indulasiIdo", jegy.getIndulasiIdoString());
                    jegyeimIntent.putExtra("jegyTipus", jegy.getJegyTipus().toString());
                    jegyeimIntent.putExtra("jaratReszletek", true);
                    context.startActivity(jegyeimIntent);
                });
            } else {
                szekSzamText.setText("Székszám:");
                jegyItemGomb.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) jegyItemGomb.getLayoutParams();
                params.topMargin = -80;
                jegyItemGomb.setLayoutParams(params);
            }

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