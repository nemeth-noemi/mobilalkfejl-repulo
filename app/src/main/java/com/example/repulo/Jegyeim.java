package com.example.repulo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Jegyeim extends AppCompatActivity {

    private RecyclerView jegyekRecycleView;

    private List<Jegy> jegyek;
    private String userId;

    private int gridnumber = 1;
    private String jegyeimVagyJaratok = "jegyeim";

    public JegyItemFoglalasAdapter jegyItemFoglalasAdapter;
    public JegyItemAdapter jegyItemAdapter;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jegyeim);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }
        userId = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Jegyek");

        jegyekRecycleView = findViewById(R.id.jegyeimRecycleView);
        jegyekRecycleView.setLayoutManager(new GridLayoutManager(this, gridnumber));
        jegyek = new ArrayList<>();

        if (this.getIntent().getBooleanExtra("frissit", false)){
            this.finish();
        } else if(this.getIntent().getBooleanExtra("jaratReszletek", false)){
            jegyeimVagyJaratok = "jaratReszletek";
            jegyItemFoglalasAdapter = new JegyItemFoglalasAdapter(this, jegyek, userId);
            jegyekRecycleView.setAdapter(jegyItemFoglalasAdapter);
        } else{
            jegyItemAdapter = new JegyItemAdapter(this, jegyek);
            jegyekRecycleView.setAdapter(jegyItemAdapter);
        }

    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }

        super.onResume();
        if(jegyeimVagyJaratok.equals("jaratReszletek")){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Intent intent = this.getIntent();
            quaryjaratReszletek(intent.getStringExtra("honnan"), intent.getStringExtra("hova"), intent.getStringExtra("indulasiIdo"), intent.getStringExtra("jegyTipus"));
        } else if (jegyeimVagyJaratok.equals("jegyeim")){ // már megvett jegyeim
            quaryjegyeim();
        } else if(jegyeimVagyJaratok.equals("jaratok")) { // induló járatok
            quaryjaratok();
        }
        System.out.println("jegyek száma: "+jegyek.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void quaryjaratReszletek(String honnan, String hova, String indulasiIdo, String jegyTipus) {
        jegyek.clear();
        System.out.println("alma"+jegyTipus);
        collectionReference
                .whereEqualTo("megvett", false)
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
                jegyek.add(jegy);
            }
            jegyItemFoglalasAdapter.notifyDataSetChanged();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void quaryjaratok() {
        jegyek.clear();
        collectionReference.whereEqualTo("megvett", false).get().addOnSuccessListener(queryJegyek ->{
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

                int i = 0;
                while(i < jegyek.size()){
                    if(jegyek.get(i).getHova().equals(jegy.getHova()) && jegyek.get(i).getHonnan().equals(jegy.getHonnan()) && jegyek.get(i).getIndulasiIdo().equals(jegy.getIndulasiIdo()) && jegyek.get(i).getJegyTipus().equals(jegy.getJegyTipus())){
                        jegyek.get(i).setSzekSzam(jegyek.get(i).getSzekSzam() + 1);
                        break;
                    }
                    i++;
                }
                if(i == jegyek.size()){
                    jegy.setSzekSzam(1);
                    jegyek.add(jegy);
                }
            }
            jegyItemAdapter.notifyDataSetChanged();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void quaryjegyeim() {
        jegyek.clear();
        collectionReference.whereEqualTo("vevoId", userId).get().addOnSuccessListener(queryJegyek ->{
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
                jegyek.add(jegy);
            }
            jegyItemAdapter.notifyDataSetChanged();
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(jegyeimVagyJaratok.equals("jaratReszletek"))
            getMenuInflater().inflate(R.menu.user_menu_reszletek, menu);
        else
            getMenuInflater().inflate(R.menu.user_menu, menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                jegyeimVagyJaratok = "jaratok";
                finish();
                return true;
            case R.id.kijelentkezes:
                FirebaseAuth.getInstance().signOut();
                finish();
                return  true;
            case R.id.jegyeim:
                jegyeimVagyJaratok = "jegyeim";
                onResume();
                return true;
            case R.id.jaratok:
                jegyeimVagyJaratok = "jaratok";
                onResume();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}