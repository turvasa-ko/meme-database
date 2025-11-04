package code.backend;

import org.json.JSONObject;

public class Tag {


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

}