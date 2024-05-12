package bence.varga.mozijegy.model;

public class Movie {
    private String name;
    private String info;
    private String genre;
    private final int imageRes;

    public Movie() {
        this.name = "";
        this.info = "";
        this.genre = "";
        this.imageRes = 0;

    }

    public Movie(String name, String info, String genre, int imageRes) {
        this.name = name;
        this.info = info;
        this.imageRes = imageRes;
        this.genre = genre;
    }
    public String getName() {return name;}

    public String getInfo() {return info;}

    public int getImageRes() {return imageRes;}
    public void setName(String name) {this.name = name;}

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setInfo(String info) {this.info = info;}
}
