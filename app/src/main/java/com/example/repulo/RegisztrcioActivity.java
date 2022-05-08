package com.example.repulo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegisztrcioActivity extends AppCompatActivity {
    private static final String PREF_KEY = RegisztrcioActivity.class.getPackage().toString();

    EditText felhasznalonevEditText;
    EditText emailEditText;
    EditText jelszoEditText;
    EditText jelszoMegerositesEditText;
    EditText telefonszamEditText;
    Spinner GyerekekSzamaSpinner;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisztrcio);

        felhasznalonevEditText = findViewById(R.id.felhasznalonevEditText);
        emailEditText = findViewById(R.id.emailEditText);
        jelszoEditText = findViewById(R.id.jelszoEditText);
        jelszoMegerositesEditText = findViewById(R.id.jelszoMegerositeseEditText);
        telefonszamEditText = findViewById(R.id.telefonszamEditText);
        GyerekekSzamaSpinner = findViewById(R.id.GyerekekSzamaSpinner);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        emailEditText.setText(getIntent().getStringExtra("email"));
        jelszoEditText.setText(getIntent().getStringExtra("jelszo"));

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void regisztracio(View view) {
        String felhasznalonev = felhasznalonevEditText.getText().toString();
        String jelszo = jelszoEditText.getText().toString();
        String jelszoMegerosites = jelszoMegerositesEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String telefonszam = telefonszamEditText.getText().toString();
        String beosztas = GyerekekSzamaSpinner.getSelectedItem().toString();
        if (jelszo.length()<8){
            Toast.makeText(RegisztrcioActivity.this, "A jelszónak legalább 8 hosszúnak kell lennie!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!jelszo.equals(jelszoMegerosites)){
            Toast.makeText(RegisztrcioActivity.this, "A két jelszó nem egyezik", Toast.LENGTH_LONG).show();
            return;
        }
        if (!email.contains("@") || !email.contains(".") || email.length()<2){
            Toast.makeText(RegisztrcioActivity.this, "Helytelen email cím", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email,jelszo).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(RegisztrcioActivity.this, "Sikeres regisztráció!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(RegisztrcioActivity.this, "User was't created successfully: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}