
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class SignIn {
    public boolean signin(UserManager um, String Id_Password) {
        String[] userInfo = Id_Password.split("/");

        if (userInfo.length < 5) {
            return false;
        }

        String Name = userInfo[0].trim();
        String Id = userInfo[1].trim();
        String Password = userInfo[2].trim();
        String Number = userInfo[3].trim(); // 학번
        String Grade = userInfo[4].trim(); // 학년


        // 이름 확인
        if (Name.length() < 1) {
            System.out.println("이름의 길이가 짧습니다.");
            return false;
        }


        // 아이디 확인
        String[] a = Id.split(" ");
        if ((Id.length() < 6) || (Id.length() > 12)) {
            System.out.println("아이디의 길이가 잘못되었습니다.");
            return false;
        }
        if (containsHangul(Id)) {
            System.out.println("아이디에 한글을 포함할 수 없습니다.");
            return false;
        }
        if (a.length != 1) {
            System.out.println("아이디에 공백을 포함할 수 없습니다.");
            return false;
        }
        for (User user : um.Users) {
            if (user.userID.equals(Id)) {
                System.out.println("이미 존재하는 아이디입니다.");
                return false;
            }
        }

        // 비밀번호 확인
        String[] b = Password.split(" ");
        if ((Password.length() < 8) || (Password.length() > 16)) {
            System.out.println("비밀번호의 길이가 잘못되었습니다.");
            return false;
        }
        if (!containsEngNum(Password)) {
            System.out.println("비밀번호는 영어와 숫자로 만들어주세요.");
            return false;
        }
        if (containsHangul(Password)) {
            System.out.println("비밀번호에 한글을 포함할 수 없습니다.");
            return false;
        }
        if (b.length != 1) {
            System.out.println("비밀번호에 공백을 포함할 수 없습니다.");
            return false;
        }

        try {
            // 학번 확인
            if (!(Number.length() == 9)) {
                System.out.println("학번의 길이가 9가 아닙니다.");
                return false;
            } else if (Integer.parseInt(Number) < 0) {
                System.out.println("학번에 음수를 입력할 수 없습니다.");
                return false;
            }

            for (User user : um.Users) {
                if (user.userNumber == Integer.parseInt(Number)) {
                    System.out.println("이미 가입된 학번입니다.");
                    return false;
                }
            }

            // 학년 확인
            int GradeInt = Integer.parseInt(Grade);
            if ((GradeInt < 1) || (GradeInt > 4)) {
                System.out.println("학년을 잘못 입력하였습니다.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("숫자 입력란을 제대로 입력해주세요.");
            return false;
        }


        // if문에 다 안걸리면
        writeToFile("user.txt", Id_Password); // 유저파일에 추가
        System.out.println(Name + "님의 회원가입이 완료되었습니다.");
        return true;
    }

    public static void writeToFile(String filename, String userdata) {
        try {
            FileWriter writer = new FileWriter(filename, true); // 파일 열기, true는 append 모드
            String formattedData = userdata.replace("/", "\t"); // 슬래시를 탭으로 변경
            writer.write(formattedData + "\n"); // 포맷된 데이터 쓰기
            writer.close(); // 파일 닫기
        } catch (IOException e) {
            System.out.println("파일에 쓰기 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 문자열에 한글이 포함되어 있는지 체크
    static boolean containsHangul(String str) {
        return Pattern.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*", str);
    }

    // 영어와 숫자를 포함했는지 확인
    public static boolean containsEngNum(String str) {
        return Pattern.matches(".*[a-zA-Z].*[0-9].*", str);
    }
}