package tk.harshsurani.leaflibrary.Model;

public class ModelPdf {
    //variables
    String uid, title, description, categoryId, url;
    long timestamp,id;
    //constructor for firebase
    public ModelPdf() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ModelPdf(String uid, String title, String description, String categoryId, String url, long timestamp, long id) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.id = id;
    }
}