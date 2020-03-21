package id.kuro.musique;

public class music {
    private long id;
    private String title;

    public music(long songID, String title){
        this.id = songID;
        this.title = title;
    }

    public long getId(){return id;}
    public String getTitle(){return title;}
}
