package code.backend;

import java.sql.SQLException;

import org.json.JSONObject;

import code.backend.Database;

public class Tag implements Comparable<Tag> {


    private String title;
    private Integer count;

    private static final String ERROR_MESSAGE = " - TAG: ";


    
    public Tag(String title, int count) {
        setTitle(title);
        setCount(count);
    }


    private void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new NullPointerException(ERROR_MESSAGE+"Title mustn't be null or empty");
        }

        this.title = title;
    }


    private void setCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE+"Count must be positive integer");
        }

        this.count = count;
    }


    public String getTitle() {
        return title;
    }


    public Integer getCount() {
        return count;
    }



    public JSONObject toJSONString() {
        JSONObject tagJSON = new JSONObject();
        tagJSON.put("title", title);
        tagJSON.put("count", count);

        return tagJSON;
    }



    @Override
    public String toString() {
        return title;
    }


    
    
    @Override
    public int compareTo(Tag other) {
        int titleComparasion = title.compareTo(other.getTitle());
        int countComparasion = count.compareTo(other.getCount());

        if (titleComparasion != 0) return titleComparasion;
        if (countComparasion != 0) return countComparasion;

        return 0;
    }
    

    @Override
    public boolean equals(Object other) {

        if (this == other) return true;
        if (other == null) return false;
        if (this.getClass() != other.getClass()) return false;

        Tag otherTag = (Tag) other;
        return title.equals(otherTag.getTitle());
    }


    @Override
    public int hashCode() {
        return title.hashCode();
    }

}