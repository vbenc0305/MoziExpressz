package bence.varga.mozijegy.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import bence.varga.mozijegy.R;
import bence.varga.mozijegy.model.Jegy;
import bence.varga.mozijegy.model.Vetites;

public class IdopontAdapter extends RecyclerView.Adapter<IdopontAdapter.ViewHolder> {
    private ArrayList<Vetites> vetiteslista;
    private Context context;
    private int lastPos = -1;


    private final FirebaseFirestore mFirestore= FirebaseFirestore.getInstance();;
    private final CollectionReference mJegyek =mFirestore.collection("jegyek");
    private final CollectionReference mVetites =mFirestore.collection("screenings");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public IdopontAdapter(Context context, ArrayList<Vetites> vetitesData) {
        if (context == null || vetitesData == null) {
            throw new IllegalArgumentException("Context or vetitesData cannot be null");
        }
        this.context = context;
        this.vetiteslista = vetitesData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.vetites_movies, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IdopontAdapter.ViewHolder holder, int position) {
        Vetites currentvetites = vetiteslista.get(position);
        holder.bindTo(currentvetites);

        if(holder.getAdapterPosition()>lastPos){
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.sliderow);
            holder.itemView.startAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return vetiteslista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView vetitesideje;
        private TextView szekekszama;
        private TextView terem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vetitesideje = itemView.findViewById(R.id.vetitesideje);
            szekekszama = itemView.findViewById(R.id.szekekszama);
            terem = itemView.findViewById(R.id.terem);

            itemView.findViewById(R.id.foglalasbtn).setOnClickListener(v -> {
                if(user==null){
                    Toast.makeText(context, "Ehhez a funkcióhoz be kell jelentkezni", Toast.LENGTH_SHORT).show();

                }
                else{
                    mJegyek.add(new Jegy(
                            user.getUid(),
                            vetiteslista.get(getAdapterPosition()).getFilmNeve())
                    );
                    Toast.makeText(context, "Sikeres foglalás!", Toast.LENGTH_SHORT).show();
                    Vetites friss = vetiteslista.get(getAdapterPosition());
                    friss.setSzekek_szama(friss.getSzekek_szama()-1);
                    mVetites.document(vetiteslista.get(getAdapterPosition()).getFilmNeve()).update("szekek_szama",friss.getSzekek_szama());
                    notifyItemChanged(getAdapterPosition());



                }
            });
        }

        public void bindTo(@NonNull Vetites currentvetites) {
            if (currentvetites.getVetitesIdo() != null) {
                Date idohelper = currentvetites.getVetitesIdo().toDate();
                Log.d("IdopontAdapter", idohelper.toString());
                String helperVetites = "Vetítés ideje: "+ idohelper.getMonth() + "/" + idohelper.toString().split(" ")[2]+ " " + idohelper.getHours() + ":" + idohelper.toString().split(" ")[3].split(":")[1];
                vetitesideje.setText(helperVetites);
            }
            String helperSzekek = "Szabad székek száma: "+
                    currentvetites.getSzekek_szama();
            szekekszama.setText(helperSzekek);

            String helperTerem = "Terem: "+
                    currentvetites.getHelyszin();
            terem.setText(helperTerem);
        }

    }
}
