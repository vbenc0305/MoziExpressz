package bence.varga.mozijegy.view;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import bence.varga.mozijegy.R;
import bence.varga.mozijegy.model.Movie;

/**
 * A fő tevékenység az alkalmazásban, amely kezeli a bejelentkezést és a kijelentkezést.
 */
public class    MainActivity extends AppCompatActivity {

    private Button logoutButton;
    // Üzenetek
    private static final String LOGGED_IN_MESSAGE = "Üdvözöllek, %s!";
    private static final String LOG_TAG = "MainActivity";
    private static final String LOGGED_OUT_MESSAGE = "Sikeresen kijelentkeztél.";
    private RecyclerView recyclerView;
    private ArrayList<Movie> mItem;
    private MovieAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private int gridNumber = 1;
    private final int AB= R.id.aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_logged_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLogged), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutButton = findViewById(R.id.buttonLogout);
        if (user != null) {
            // Ha be van jelentkezve a felhasználó, üdvözlő üzenetet jelenítünk meg
            String userName = user.getDisplayName();
            Toast.makeText(this, String.format(LOGGED_IN_MESSAGE, userName), Toast.LENGTH_SHORT).show();
            initializeLogoutButton();
        } else {
            // Ha nincs bejelentkezve a felhasználó, a bejelentkezési gombot jelenítjük meg
            setContentView(R.layout.activity_main);
            Button loginButton = findViewById(R.id.buttonLogin);
            loginButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        }


        recyclerView = findViewById(R.id.movieRecycleView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItem = new ArrayList<>();

        mAdapter = new MovieAdapter(this, mItem);
        recyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("movies");

        queryData();
        initData();
    }


    private void queryData() {
       mItem.clear();

       mItems.orderBy("name").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for(QueryDocumentSnapshot docu: queryDocumentSnapshots){
              Movie movie =docu.toObject(Movie.class);
              mItem.add(movie);
           }

           if(mItem.isEmpty()){
               initData();
               queryData();
           }

           mAdapter.notifyDataSetChanged();

       });
    }

    private void initData() {
        // Ellenőrizzük, hogy a 'movies' gyűjtemény üres-e
        mItems.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Ha a lekérdezés sikeres, de nincs dokumentum
                if (task.getResult().isEmpty()) {
                    // Adatok feltöltése
                    uploadData();
                }
            } else {
                Log.e(LOG_TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
        }

    private void uploadData() {
        String[] movieInfo = getResources().getStringArray(R.array.movie_desc);
        String[] movieTitle = getResources().getStringArray(R.array.movieNames);
        String[] movieGenre = getResources().getStringArray(R.array.genre);

        TypedArray itemImgRes = getResources().obtainTypedArray(R.array.img);

        for (int i = 0; i < movieTitle.length; i++) {
            mItems.add(new Movie(
                    movieTitle[i],
                    movieInfo[i],
                    movieGenre[i],
                    itemImgRes.getResourceId(i, 0)));
            Log.e(LOG_TAG, "Movie added");
        }

        itemImgRes.recycle();
    }



    /**
     * A kijelentkezés gomb eseménykezelőjének inicializálása.
     */
    private void initializeLogoutButton() {
        if (logoutButton == null) {
            return;
        }
        logoutButton.setOnClickListener(v -> {
            // Kijelentkezési logika
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, LOGGED_OUT_MESSAGE, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("isLoggedIn", false);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filmlismenu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.aboutUs) {
            Log.d(LOG_TAG, "About us clicked");

        }
        if (item.getItemId() == R.id.jegyeim) {
            Log.d(LOG_TAG, "jegyeim clciked");
        }
        return super.onOptionsItemSelected(item);
    }
}
