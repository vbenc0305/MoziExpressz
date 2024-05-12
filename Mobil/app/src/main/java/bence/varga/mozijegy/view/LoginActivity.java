package bence.varga.mozijegy.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import bence.varga.mozijegy.R;


// A LoginActivity felelős a bejelentkezési felület megjelenítéséért és a bejelentkezési logika kezeléséért.
public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static final String PREF_KEY = Objects.requireNonNull(LoginActivity.class.getPackage()).toString();
    private static final int RC_SIGN_IN = 123;
    private static final int SECRET_KEY = 99;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailET;
    private EditText passwordET;
    private Button loginButtonET;
    private Button registerButtonET;
    private Button googleSignInButtonET;
    private FirebaseAuth Mauth;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initVariables();
        initListeners();
        Mauth = FirebaseAuth.getInstance();
       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
               .requestEmail()
               .build();

       mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch(ApiException e){
                Log.w(LOG_TAG, "Google sign in failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        Mauth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                goToHome();
            } else {
                Log.e(LOG_TAG, "Sikertelen bejelentkezés", task.getException());
                Toast.makeText(LoginActivity.this, "Hibás email vagy jelszó.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Nézetek inicializálása
    private void initViews() {
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Változók inicializálása
    private void initVariables() {
        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        loginButtonET = findViewById(R.id.LoginButton);
        registerButtonET = findViewById(R.id.RegisterButton);
        googleSignInButtonET = findViewById(R.id.google_sign_in_button);
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
    }

    // Eseménykezelők inicializálása
    private void initListeners() {
        loginButtonET.setOnClickListener(v -> loginLogic());
        registerButtonET.setOnClickListener(v -> registerLogic());
        googleSignInButtonET.setOnClickListener(v -> googleSignInLogic());
    }

    // Bejelentkezési logika
    private void loginLogic() {
        String userNameStr = emailET.getText().toString();
        String passwordStr = passwordET.getText().toString();

        Mauth.signInWithEmailAndPassword(userNameStr, passwordStr).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                goToHome();
            } else {
                Log.e(LOG_TAG, "Sikertelen bejelentkezés", task.getException());
                Toast.makeText(LoginActivity.this, "Hibás email vagy jelszó.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Regisztrációi logika
    private void registerLogic() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    // Google bejelentkezési logika
    private void googleSignInLogic() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    // Főképernyőre navigálás
    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("email", emailET.getText().toString());
        edit.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}