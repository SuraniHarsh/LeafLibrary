package tk.harshsurani.leaflibrary.Model;

public class ModelCategory {

    String id,category,uid;
    long timeStamp;

    //Constructor empty required for firebase
    public ModelCategory() {

    }

//        getter/Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCategory() {
        return category;
    }

    public String getUid() {
        return uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    //    Parameterize constructor

    public ModelCategory(String id, String category, String uid, long timeStamp) {
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timeStamp = timeStamp;

    }
}
