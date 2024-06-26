public class User {
    String userName;
    String userID;
    String userPassword;
    int userGrade;
    int userNumber;
    int user_Studying_Goal_day;
    int user_Studying_Goal_week;
    String user_group;

    public User(String userName, String userID, String userPassword, int userNumber, int userGrade, int user_Studying_Goal_day, int user_Studying_Goal_week, String user_group) {
        this.userName = userName;
        this.userID = userID;
        this.userPassword = userPassword;
        this.userGrade = userGrade;
        this.userNumber = userNumber;
        this.user_Studying_Goal_day = user_Studying_Goal_day;
        this.user_Studying_Goal_week = user_Studying_Goal_week;
        this.user_group = user_group;
    }

    public User(String userName, String userID, String userPassword, int userNumber, int userGrade, String user_group) {
        this.userName = userName;
        this.userID = userID;
        this.userPassword = userPassword;
        this.userGrade = userGrade;
        this.userNumber = userNumber;
        this.user_Studying_Goal_day = 0;
        this.user_Studying_Goal_week = 0;
        this.user_group = user_group;
    }

    public User(String userName, String userID, String userPassword, int userNumber, int userGrade) {
        this.userName = userName;
        this.userID = userID;
        this.userPassword = userPassword;
        this.userGrade = userGrade;
        this.userNumber = userNumber;
        this.user_Studying_Goal_day = 0;
        this.user_Studying_Goal_week = 0;
        this.user_group = null;
    }

    public User(String userName, String userID, String userPassword, int userNumber, int userGrade, int user_Studying_Goal_day, int user_Studying_Goal_week) {
        this.userName = userName;
        this.userID = userID;
        this.userPassword = userPassword;
        this.userGrade = userGrade;
        this.userNumber = userNumber;
        this.user_Studying_Goal_day = user_Studying_Goal_day;
        this.user_Studying_Goal_week = user_Studying_Goal_week;
        this.user_group = null;
    }

    @Override
    public String toString() {
        String str = "=".repeat(20) + "\n";
        str += "유저 이름: " + this.userName + "\n";
        str += "유저 아이디: " + this.userID + "\n";
        str += "유저 비밀번호: " + this.userPassword + "\n";
        str += "유저 학년: " + this.userGrade + "\n";
        str += "유저 학번: " + this.userNumber + "\n";
        str += "유저 일일 목표 시간: " + this.user_Studying_Goal_day + "\n";
        str += "유저 일주일 목표 시간: " + this.user_Studying_Goal_week + "\n";
        if(this.user_group == null){
            str += "유저가 속한 그룹이  존재하지 않습니다." + "\n";
        }else{
            str += "유저가 속한 그룹: " + this.user_group + "\n";
        }
        str += "=".repeat(20) + "\n";

        return str;
    }

}
