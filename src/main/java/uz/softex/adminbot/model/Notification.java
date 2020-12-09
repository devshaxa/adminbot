package uz.softex.adminbot.model;

public class Notification {

    private Long id;
    private String type;
    private Object post;


    public Notification(String type,Object post) {
        this.type = type;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPost() {
        return post;
    }

    public void setPost(Object post) {
        this.post = post;
    }
}
