package CampusConnect.Database.Models.Tutors;

public class TutorRequest
{
    public Tutor tutor;

    public TutorRequest(Tutor tutor)
    {
        this.tutor = tutor;
    }

    public Tutor getTutor() {return tutor;}
}
