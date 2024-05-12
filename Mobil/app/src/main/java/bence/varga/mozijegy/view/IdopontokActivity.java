package bence.varga.mozijegy.view;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import bence.varga.mozijegy.R;
import bence.varga.mozijegy.model.Vetites;

public class IdopontokActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Vetites> mVetitesek;
    private IdopontAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mVetites;
    private String selectedMovieName;
    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_idopontok);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedMovieName = getIntent().getStringExtra("movie_name");

        recyclerView = findViewById(R.id.vetitesRecycleView);

        recyclerView.setLayoutManager(new GridLayoutManager(this,gridNumber));
        mVetitesek = new ArrayList<>();

        mFirestore = FirebaseFirestore.getInstance();
        mVetites = mFirestore.collection("screenings");

        mAdapter = new IdopontAdapter(this, mVetitesek);
        recyclerView.setAdapter(mAdapter);



        queryData();


    }

    private void queryData() {
        mVetitesek.clear();
        // A Firebase lekérdezés során csak azokat a vetítéseket kérjük le, amelyek a kiválasztott filmhez tartoznak
        mVetites.orderBy("vetitesIdo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot docu: queryDocumentSnapshots){
                        Vetites vetites = docu.toObject(Vetites.class);
                        Log.e("IdopontokActivity", selectedMovieName +" "+ vetites.getFilmNeve());
                        if(selectedMovieName.equals(vetites.getFilmNeve())) {
                            mVetitesek.add(vetites);
                            /*
                            try {
                                uploadData();
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }*/
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Kezeljük a lekérdezés során fellépő hibákat
                    Log.e("IdopontokActivity", "Hiba a vetítések lekérdezése közben", e);
                });
    }
/*
    private void uploadData() throws ParseException {
        String[] idoPontFilmNeve = getResources().getStringArray(R.array.vetit_movies);
        String[] idoPontHelyszin = getResources().getStringArray(R.array.vetit_helyszin);
        String[] idoPontvetitesIdopont = getResources().getStringArray(R.array.vetit_ido);
        int szekszam = 40;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        
        for (int i  =0; i<idoPontHelyszin.length;i++){
            Date parsedDate = dateFormat.parse(idoPontvetitesIdopont[i]);

            mVetites.add(new Vetites(
                    idoPontFilmNeve[i],
                    idoPontHelyszin[i],
                    szekszam,
                    new Timestamp(parsedDate)));
            Log.e("IdopontokActivity", "Időpont hozzáadva");
        }
    }*/

}