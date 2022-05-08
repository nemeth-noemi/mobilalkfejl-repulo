package com.example.repulo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JaratRMU extends AppCompatActivity {

    private String RNU;
    private Jegy jegy;
    private boolean vevoModosit;

    private TextView vevoAzonositoText;
    private EditText honnan;
    private EditText hova;
    private RadioGroup jegyTipusGroup;
    private RadioButton fapadosRButton;
    private RadioButton elsoOsztalyRButton;
    private RadioButton businessRButton;
    private TextView indulasiIdo;
    private EditText vevoId;
    private EditText ferohely;
    private Button mentesGomb;

    private FirebaseFirestore firestore;
    private CollectionReference jegyekCollection;

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jarat_r_m_u);

        firestore = FirebaseFirestore.getInstance();
        jegyekCollection = firestore.collection("Jegyek");

        vevoAzonositoText = findViewById(R.id.vevoAzonositoText);
        honnan = findViewById(R.id.honnan);
        hova = findViewById(R.id.hova);
        jegyTipusGroup = findViewById(R.id.jegyTipusGroup);
        fapadosRButton = findViewById(R.id.fapadosRButton);
        elsoOsztalyRButton = findViewById(R.id.elsoOsztalyRButton);
        businessRButton = findViewById(R.id.businessRButton);
        indulasiIdo = findViewById(R.id.indulasiIdo);
        vevoId = findViewById(R.id.vevoId);
        ferohely = findViewById(R.id.ferohely);
        mentesGomb = findViewById(R.id.mentesGomb);

        RNU = this.getIntent().getStringExtra("RNU");
        jegy = (Jegy) this.getIntent().getSerializableExtra("jegy");
        vevoModosit = this.getIntent().getBooleanExtra("vevoModosit", false);

        if (RNU != null){
            if (RNU.equals("NEW")) {

                indulasiIdo.setOnClickListener(View -> {
                    showDateTimePicker(this);
                });

                vevoId.setEnabled(false);
                fapadosRButton.setChecked(true);

                mentesGomb.setOnClickListener(view -> {
                    if (honnan.getText().length() != 0 && hova.getText().length() != 0 && ferohely.getText().length() != 0) {
                        for (int i = 1; i <= Integer.parseInt(ferohely.getText().toString()); i++) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("ar", 10000);
                            data.put("honnan", honnan.getText().toString());
                            data.put("hova", hova.getText().toString());
                            data.put("indulasiIdo", indulasiIdo.getText().toString());
                            data.put("szekSzam", i);
                            data.put("megvett", false);
                            data.put("vevoId", "");
                            if (fapadosRButton.isChecked()) {
                                data.put("jegyTipus", "FAPADOS");
                            } else if (businessRButton.isChecked()) {
                                data.put("jegyTipus", "BUSINESS");
                            } else if (elsoOsztalyRButton.isChecked()) {
                                data.put("jegyTipus", "ELSO_OSZTALY");
                            }
                            jegyekCollection.add(data);
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Nem adtál meg minden adatot!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else if (jegy != null){
            if (vevoModosit){
                vevoId.setText(jegy.getVevoId());
                ferohely.setEnabled(false);
                honnan.setText(jegy.getHonnan());
                hova.setText(jegy.getHova());
                indulasiIdo.setText(jegy.getIndulasiIdoString());

                ferohely.setEnabled(false);
                honnan.setEnabled(false);
                hova.setEnabled(false);
                indulasiIdo.setEnabled(false);
                businessRButton.setEnabled(false);
                elsoOsztalyRButton.setEnabled(false);
                fapadosRButton.setEnabled(false);

                mentesGomb.setOnClickListener(view -> {
                    if (vevoId.getText().toString().equals(""))
                        jegyekCollection.document(jegy.getId()).update("megvett", false, "vevoId", "");
                    else
                        jegyekCollection.document(jegy.getId()).update("megvett", true, "vevoId", vevoId.getText().toString());
                    finish();
                });

            } else {

                indulasiIdo.setOnClickListener(View -> {
                    showDateTimePicker(this);
                });

                vevoId.setEnabled(false);
                ferohely.setEnabled(false);
                honnan.setText(jegy.getHonnan());
                hova.setText(jegy.getHova());
                indulasiIdo.setText(jegy.getIndulasiIdoString());
                switch (jegy.getJegyTipus()) {
                    case ELSO_OSZTALY:
                        elsoOsztalyRButton.setChecked(true);
                        break;
                    case BUSINESS:
                        businessRButton.setChecked(true);
                        break;
                    case FAPADOS:
                        fapadosRButton.setChecked(true);
                        break;
                }
                ferohely.setText(Integer.toString(jegy.getSzekSzam()));
                mentesGomb.setOnClickListener(view -> {
                    jegyekModositas(jegy);
                });
            }
        }

    }

    private void jegyekModositas(Jegy jegy){
        System.out.println("jegy: "+jegy.getId());
        List<Jegy> jegyek = new ArrayList<Jegy>();
        jegyekCollection
                .whereEqualTo("honnan", jegy.getHonnan())
                .whereEqualTo("hova", jegy.getHova())
                .whereEqualTo("indulasiIdo", jegy.getIndulasiIdoString())
                .whereEqualTo("jegyTipus", jegy.getJegyTipus().toString())
                .get().addOnSuccessListener(quaryJegyek -> {
            for (QueryDocumentSnapshot quaryJegy : quaryJegyek){
                String jegyTipusString;
                if(businessRButton.isChecked()){
                    jegyTipusString = "BUSINESS";
                } else if(fapadosRButton.isChecked()){
                    jegyTipusString = "FAPADOS";
                } else {
                    jegyTipusString = "ELSO_OSZTALY";
                }
                System.out.println("dsjglfdnbkjdgb"+quaryJegy.getId());
                jegyekCollection.document(quaryJegy.getId()).update("honnan", honnan.getText().toString(),"hova", hova.getText().toString(), "indulasiIdo", indulasiIdo.getText().toString(), "jegyTipus", jegyTipusString);
            }
            Toast.makeText(this, "Sikeres mentés!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDateTimePicker(Context context) {
        final Calendar currentDate = Calendar.getInstance();
        List<Integer> date = new ArrayList<Integer>();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.add(year);
                date.add(monthOfYear);
                date.add(dayOfMonth);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        LocalDateTime dateTime = LocalDateTime.of(date.get(0), date.get(1)+1, date.get(2), hourOfDay, minute);
                        indulasiIdo.setText(dateTime.toString());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }
}
