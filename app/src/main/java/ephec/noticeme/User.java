package ephec.noticeme;


public class User {
    private int id;
    private int groupId;
    private String nom;
    private String prenom;
    private String mail;
    private String password;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getGroup() {
        return groupId;
    }

    public void setGroup(int group) {
        this.groupId = group;
    }

    public String getPassword() {
        return Connector.decrypt(password);
    }

    public void setPassword(String password) {
        this.password = Connector.encrypt(password);
    }
}
