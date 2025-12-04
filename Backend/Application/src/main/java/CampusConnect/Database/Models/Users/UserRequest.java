package CampusConnect.Database.Models.Users;

public class UserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isTutor;
    private boolean isAdmin;
    private String bio;
    private String major;
    private String classification;
    private String contactInfo;


    public UserRequest(String firstName, String lastName, String username, String password, boolean isTutor, boolean isAdmin, String major, String classification, String bio, String contactInfo){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isTutor = isTutor;
        this.isAdmin = isAdmin;
        this.major = major;
        this.classification = classification;
        this.bio = bio;
        this.contactInfo = contactInfo;
    }
    public UserRequest() {}

    public String getfirstName() {return firstName; }

    public String getlastName() {return lastName; }

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

    public String getMajor() { return major; }

    public String getClassification() { return classification; }

    public String getBio() { return bio; }

    public String getContactInfo() { return contactInfo; }



}
