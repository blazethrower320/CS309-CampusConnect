package CampusConnect.Database.Models.Users;

public class UserRequest {
    private String username;
    private String password;
    private boolean isTutor;
    private boolean isAdmin;
    private String


    public UserRequest(String username, String password, boolean isTutor, boolean isAdmin, ){
        this.username = username;
        this.password = password;
        this.isTutor = isTutor;
        this.isAdmin = isAdmin;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public boolean getisTutor() {
        return isTutor;
    }

    public boolean getisAdmin(){
        return isAdmin;
    }
}
