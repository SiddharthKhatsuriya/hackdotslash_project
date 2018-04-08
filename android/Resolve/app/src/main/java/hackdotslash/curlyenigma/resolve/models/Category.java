package hackdotslash.curlyenigma.resolve.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private String id;
    private String title;

    public Category() {
    }

    public Category(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Category fromJSON(JSONObject jsonObject){
        Category c = new Category();
        try {
            c.setId(jsonObject.getString("_id"));
            c.setTitle(jsonObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
