package hackdotslash.curlyenigma.resolve.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by monil20 on 4/8/18.
 */

public class Complaint {
    private String description;
    private String image;
    private String authorId, id;
    private User author;
    private double lat, lng;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public static Complaint fromJSON(JSONObject complaint){
        Complaint c = new Complaint();
        try {
            c.setId(complaint.getString("_id"));
            double lat = complaint.getDouble("lat");
            double lng = complaint.getDouble("lng");
            c.setLat(lat);
            c.setLng(lng);
            c.setDescription(complaint.getString("description"));
            User user = User.fromJSON(complaint.getJSONObject("author"));
            c.setImage(complaint.getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }
}
