package com.example.repulo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    EditText emailEditText;
    EditText jelszoEditText;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        jelszoEditText = findViewById(R.id.jelszoEditText);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void belephet(){
        Intent szamlakActivity = new Intent(this, Jegyeim.class);
        startActivity(szamlakActivity);
    }

    public void belepes(View view) {
        String email = emailEditText.getText().toString();
        String jelszo = jelszoEditText.getText().toString();

        if (!email.contains("@") || !email.contains(".") || email.length() < 2 || jelszo.length() < 8){
            Toast.makeText(MainActivity.this, "Helytelen email cím vagy jelszó!", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, jelszo).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                belephet();
            }else {
                Toast.makeText(MainActivity.this, "Hibás email vagy jelszó!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void belepesGoogle(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                belephet();
            }else {
                Toast.makeText(MainActivity.this, "A belépés nem sikerült", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void regisztracio(View view) {
        Intent intent = new Intent(this, RegisztrcioActivity.class);
        intent.putExtra("email", emailEditText.getText().toString());
        intent.putExtra("jelszo", jelszoEditText.getText().toString());
        startActivity(intent);
    }

    public void adminBelepes(View view){
        String email = emailEditText.getText().toString();
        String jelszo = jelszoEditText.getText().toString();

        if (!email.contains("@") || !email.contains(".") || email.length() < 2 || jelszo.length() < 8){
            Toast.makeText(MainActivity.this, "Helytelen email cím vagy jelszó!", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, jelszo).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                CollectionReference reference = FirebaseFirestore.getInstance().collection("adminok");
                reference.whereEqualTo("userId", task.getResult().getUser().getUid()).get().addOnSuccessListener(eredmeny->{
                    if(eredmeny.size() == 0){
                        Toast.makeText(MainActivity.this, "Nem vagy Admin! hint: admin@admin.hu, adminjelszo", Toast.LENGTH_LONG).show();
                    } else{
                        Intent adminFeluletIntent = new Intent(this, AdminFelulet.class);
                        this.startActivity(adminFeluletIntent);
                    }
                });
            }else {
                Toast.makeText(MainActivity.this, "Hibás email vagy jelszó!", Toast.LENGTH_LONG).show();
            }
        });
    }
}