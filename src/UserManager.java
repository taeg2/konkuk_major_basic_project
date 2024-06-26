import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserManager {
    String postname = "스마트 스터디 허브";
    ArrayList<User> Users = new ArrayList<>();//user들의 정보를 담고 있는 ArrayList UM의 메소드는 쓸필요 없고 foreach로 얘를 불러서 쓰면됨
    int USERNUM = 0;

    public ArrayList<User> getuserlist(){
        return Users;
    }

    public void addUser(User user) {
        this.Users.add(user);
    }

    public void userFileRead(String filename) {
        USERNUM = CountingUsernum(filename);
        try (Scanner file = new Scanner(new File(filename))) {
            while (file.hasNextLine()) {
                for (int i = 0; i < USERNUM; i++) {
                    String datafield = file.nextLine();
                    String[] personaldata = datafield.split("\t");
                    String[][] userData = new String[USERNUM][personaldata.length];

                    if (personaldata.length == 5) {//아직 목표시간을 입력하지 않은 상태
                        for (int j = 0; j < 5; j++) {
                            userData[i][j] = personaldata[j].trim();
                        }
                        addUser(new User(userData[i][0], userData[i][1], userData[i][2], Integer.parseInt(userData[i][3]), Integer.parseInt(userData[i][4])));
                    } else if (personaldata.length == 6) {
                        for (int j = 0; j < 6; j++) {
                            userData[i][j] = personaldata[j];
                        }
                        addUser(new User(userData[i][0], userData[i][1], userData[i][2], Integer.parseInt(userData[i][3]), Integer.parseInt(userData[i][4]), userData[i][5]));
                    } else if (personaldata.length == 7) {
                        for (int j = 0; j < 7; j++) {
                            userData[i][j] = personaldata[j];
                        }
                        addUser(new User(userData[i][0], userData[i][1], userData[i][2], Integer.parseInt(userData[i][3]), Integer.parseInt(userData[i][4]), Integer.parseInt(userData[i][5]), Integer.parseInt(userData[i][6])));
                    } else {
                        for (int j = 0; j < 8; j++) {//목표 시간이 입력 되어있는 상태
                            userData[i][j] = personaldata[j];
                        }
                        addUser(new User(userData[i][0], userData[i][1], userData[i][2], Integer.parseInt(userData[i][3]), Integer.parseInt(userData[i][4]), Integer.parseInt(userData[i][5]), Integer.parseInt(userData[i][6]), userData[i][7]));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일을 읽지 못했습니다.");
        }
    }

    public int CountingUsernum(String filename) {
        USERNUM = 0;
        try (Scanner file = new Scanner(new File(filename))) {
            while (file.hasNextLine()) {
                USERNUM++;
                file.nextLine();
            }
            return USERNUM;
        } catch (FileNotFoundException e) {
            System.out.println("파일을 읽지 못했습니다.");
        }
        return USERNUM;
    }



    @Override
    public String toString() {

        String str = "게시판명 : " + this.postname + "\n";
        str += "유저 수 : " + this.Users.size() + "\n";
        str += "------ 유저 리스트 -------\n";
        if (this.Users.size() == 0) {
            str += "등록된 유저가 없습니다.\n";
            return str;
        }

        for (User us : Users) {
            if (us != null) {
                str += us.toString() + "\n";
            } else {
                break;
            }
        }

        return str;
    }
}
