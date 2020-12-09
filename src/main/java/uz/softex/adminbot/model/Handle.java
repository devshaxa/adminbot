package uz.softex.adminbot.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "handle")
public class Handle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lang;
    private String menuName;
    private Integer step;

    @OneToOne(mappedBy="handle", cascade = CascadeType.MERGE)
    private Session session;

    public Handle() {
    }

    public Handle(String lang, String menuName, Integer step) {
        this.lang = lang;
        this.menuName = menuName;
        this.step = step;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
