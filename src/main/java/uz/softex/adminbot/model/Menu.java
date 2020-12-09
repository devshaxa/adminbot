package uz.softex.adminbot.model;

public class Menu {
    private Long id;
    private String nameUz;
    private String nameRu;

    public Menu() {
    }

    public Menu(Long id, String nameUz, String nameRu) {
        this.id = id;
        this.nameUz = nameUz;
        this.nameRu = nameRu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameUz() {
        return nameUz;
    }

    public void setNameUz(String nameUz) {
        this.nameUz = nameUz;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }
}
