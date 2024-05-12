package bence.varga.mozijegy.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import bence.varga.mozijegy.R;

// Itt lehetne például importálni
public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();

    private FirebaseAuth mAuth;

    private EditText usernameET;
    private EditText emailET;
    private EditText pwd;
    private EditText pwdAgain;
    private Button register;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mAuth = FirebaseAuth.getInstance();
        valtozoInit();
        registerLogic();
        cancelLogic();
    }

    private void init() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void valtozoInit() {
        register = findViewById(R.id.RegButton);
        cancel = findViewById(R.id.cancelButton);
        usernameET = findViewById(R.id.registerNameEditText);
        emailET = findViewById(R.id.registerEmailEditText);
        pwd = findViewById(R.id.registerPasswordEditText);
        pwdAgain = findViewById(R.id.registerPasswordAgainEditText);

    }

    private void registerLogic() {
        register.setOnClickListener(v -> {
            String username = usernameET.getText().toString();
            String email = emailET.getText().toString();
            String password = pwd.getText().toString();
            String passwordConfirm = pwdAgain.getText().toString();

            if (!password.equals(passwordConfirm)) {
                Log.e(LOG_TAG, "Nem egyezik a két jelszó");
                Toast.makeText(RegisterActivity.this, "A két jelszó nem egyezik meg", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Log.e(LOG_TAG, "Nem megfelelő e-mail formátum");
                Toast.makeText(RegisterActivity.this, "Nem megfelelő e-mail formátum", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Log.e(LOG_TAG, "A jelszó túl rövid");
                Toast.makeText(RegisterActivity.this, "A jelszó túl rövid (legalább 6 karakter kell)", Toast.LENGTH_SHORT).show();
                return;
            }
            //Ez a rész azért felelős, hogy megnézi, hogy van-e internetkapcsolat a Felhasználó telefonján, és ha nincs,
            //akkor ezt jelzi neki toast üzenetben.
            ConnectivityManager connectivityManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                // Nincs internetkapcsolat, jeleníts meg értesítést vagy Toast-ot
                Toast.makeText(this, "Nincs internetkapcsolat.", Toast.LENGTH_SHORT).show();
                return;
            }



            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    AuthResult authResult = task.getResult();
                    if (authResult != null) {
                        System.out.println(username);
                        FirebaseUser user = authResult.getUser(); // Regisztrált felhasználó lekérése
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username) // Felhasználó nevének beállítása
                                .build();

                        if (user != null) {
                            user.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Log.d(LOG_TAG, "Felhasználói adatok frissítve: név beállítva.");
                                } else {
                                    Log.e(LOG_TAG, "Hiba történt a felhasználói adatok frissítése közben", updateTask.getException());
                                }
                            });
                        }
                    }

                    Log.d(LOG_TAG, "Sikeres regisztráció");
                    Toast.makeText(RegisterActivity.this, "Sikeres regisztráció", Toast.LENGTH_SHORT).show();
                    goToLogin();
                } else {
                    Log.e(LOG_TAG, "Sikertelen regisztráció", task.getException());
                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private void goToLogin() {
        finish();
    }

    private void cancelLogic() {
        cancel.setOnClickListener(v -> finish());
    }
}
