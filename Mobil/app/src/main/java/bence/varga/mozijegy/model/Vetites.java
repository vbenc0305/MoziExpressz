package bence.varga.mozijegy.model;

import com.google.firebase.Timestamp;

public class Vetites {
    private String filmNeve;
    private String id;
    private int szekek_szama;
    private String helyszin;

    public String getHelyszin() {
        return helyszin;
    }

    public void setHelyszin(String terem) {
        this.helyszin = terem;
    }

    private Timestamp vetitesIdo;

    public Vetites() {
    }

    public Vetites(String filmNeve, String helyszin, int szekek_szama, Timestamp vetitesIdo) {
        this.filmNeve = filmNeve;
        this.helyszin = helyszin;
        this.szekek_szama = szekek_szama;
        this.vetitesIdo = vetitesIdo;
    }

    public String getFilmNeve() {
        return filmNeve;
    }

    public void setFilmNeve(String filmNeve) {
        this.filmNeve = filmNeve;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSzekek_szama() {
        return szekek_szama;
    }

    public void setSzekek_szama(int szekek_szama) {
        this.szekek_szama = szekek_szama;
    }

    public Timestamp getVetitesIdo() {
        return vetitesIdo;
    }

    public void setVetitesIdo(Timestamp vetitesIdo) {
        this.vetitesIdo = vetitesIdo;
    }
}
