package code;

import java.sql.SQLException;

public class Tag {


    private String title;
    private int count;

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


    public int getCount() {
        return count;
    }



    public void increaseCount(Database database) throws SQLException {
        this.count++;
        database.increaseTagCount(this);
    }


    public void deincreaseCount(Database database) throws SQLException {
        if (count > 0) {
            this.count--;
            database.decreaseTagCount(this);
        }
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
