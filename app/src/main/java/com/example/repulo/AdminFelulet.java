package com.example.repulo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminFelulet extends AppCompatActivity {

    private RecyclerView jegyekRecycleView;
    private List<Jegy> jegyek;

    private int gridnumber = 1;
    private String jaratokVagyReszletek = "jaratok";

    JegyItemAdminAdapter jegyItemAdminAdapter;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_felulet);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Jegyek");

        jegyekRecycleView = findViewById(R.id.jegyeimRecycleView);
        jegyekRecycleView.setLayoutManager(new GridLayoutManager(this, gridnumber));
        jegyek = new ArrayList<>();



        if (this.getIntent().getBooleanExtra("frissit", false)){
            this.finish();
        }else if(this.getIntent().getBooleanExtra("jaratReszletek", false)){
            jaratokVagyReszletek = "reszletek";
            jegyItemAdminAdapter = new JegyItemAdminAdapter(this, jegyek, true);
            jegyekRecycleView.setAdapter(jegyItemAdminAdapter);
        }else {
            jegyItemAdminAdapter = new JegyItemAdminAdapter(this, jegyek, false);
            jegyekRecycleView.setAdapter(jegyItemAdminAdapter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }

        super.onResume();
        System.out.println(jaratokVagyReszletek);
        if(jaratokVagyReszletek.equals("jaratok")){
            quaryjaratok();
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Intent intent = this.getIntent();
            quaryjaratReszletek(intent.getStringExtra("honnan"), intent.getStringExtra("hova"), intent.getStringExtra("indulasiIdo"), intent.getStringExtra("jegyTipus"));

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void quaryjaratok() {
        jegyek.clear();
        collectionReference.get().addOnSuccessListener(queryJegyek ->{
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
            jegyItemAdminAdapter.notifyDataSetChanged();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void quaryjaratReszletek(String honnan, String hova, String indulasiIdo, String jegyTipus) {
        jegyek.clear();
        System.out.println("alma"+indulasiIdo);
        collectionReference
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
            System.out.println("jegyek száma: "+ jegyek.size());
            jegyItemAdminAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.admin_menu, menu);


        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                jaratokVagyReszletek = "jaratok";
                finish();
                return true;
            case R.id.kijelentkezes:
                FirebaseAuth.getInstance().signOut();
                finish();
                return  true;
            case R.id.ujJarat:
                Intent ujJarat = new Intent(this, JaratRMU.class);
                ujJarat.putExtra("RNU", "NEW");
                startActivity(ujJarat);
        }

        return super.onOptionsItemSelected(item);
    }

}