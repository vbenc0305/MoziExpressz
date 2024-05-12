package bence.varga.mozijegy.model;

public class Jegy {
    private String FelhId;
    private String MovieId;

    public Jegy(String felhId, String movieId) {
        FelhId = felhId;
        MovieId = movieId;
    }

    public Jegy() {
    }

    public String getFelhId() {
        return FelhId;
    }

    public void setFelhId(String felhId) {
        FelhId = felhId;
    }

    public String getMovieId() {
        return MovieId;
    }

    public void setMovieId(String movieId) {
        MovieId = movieId;
    }
}
