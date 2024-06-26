import java.util.HashMap;

public class study_time {
    String userID;
    int dayTime;
    int weekTime;
    HashMap<String, Integer> subjectTime;

    //public study_time(String userID, int dayTime, HashMap subjectTime, int weekTime){
    public study_time(String userID, int dayTime, int weekTime, HashMap<String, Integer> subjectTime){
        this.userID = userID;
        this.dayTime = dayTime;
        this.weekTime = weekTime;
        this.subjectTime = subjectTime;
    }
}