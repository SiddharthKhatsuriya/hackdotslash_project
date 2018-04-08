package hackdotslash.curlyenigma.resolve.models;

import org.json.JSONException;
import org.json.JSONObject; /**
 * Created by monil20 on 4/8/18.
 */

public class User {
    private String fname, lname, id;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static User fromJSON(JSONObject author) {
        User u = new User();
        try {
            String id = author.getString("_id");
            String fname = author.getString("fname");
            String lname = author.getString("lname");
            u.setId(id);
            u.setFname(fname);
            u.setLname(lname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }
}
