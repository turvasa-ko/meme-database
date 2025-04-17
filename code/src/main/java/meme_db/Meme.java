package meme_db;


import java.util.Set;
import jakarta.persistence.*;


@Entity
public class Meme {

    @Id
    private String title;

    @Lob
    @Convert(converter = TagSetConverter.class)
    @Column(columnDefinition = "CLOB")
    private Set<String> tags;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] image;




    public Meme(String title, Set<String> tags, byte[] image) {
        this.title = title;
        this.tags = tags;
        this.image = image;
    }


    public String getTitle() {
        return title;
    }


    public Set<String> getTags() {
        return tags;
    }


    public byte[] getImage() {
        return image;
    }

}
