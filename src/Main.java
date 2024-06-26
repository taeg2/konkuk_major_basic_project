import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //무결성 검사 코드
        ArrayList<String> userLines = readfile("user.txt");
        ArrayList<String> studyTimeLines = readfile("study_time.txt");
        ArrayList<String> postLines = readfile("board.txt");
        ArrayList<String> groupLines = readfile("study_group.txt");

        {
            // 각 파일의 탭 개수를 확인
            if (checkTabCount(userLines, new int[]{0, 4, 6, 7, 8, 9})) {
                System.out.println("4파일을 읽지 못했습니다.");
                System.exit(-1); // 유효하지 않으면 프로그램 종료
            }

            if (checkTabCount(studyTimeLines, new int[]{0, 2, 3})) {
                System.out.println("5파일을 읽지 못했습니다.");
                System.exit(-1); // 유효하지 않으면 프로그램 종료
            }

            if (checkTabCount(postLines, new int[]{0, 4, 6, 8})) {
                System.out.println("6파일을 읽지 못했습니다.");
                System.exit(-1); // 유효하지 않으면 프로그램 종료
            }

            if (checkTabCount(groupLines, new int[]{0, 5, 6})) {
                System.out.println("7파일을 읽지 못했습니다.");
                System.exit(-1); // 유효하지 않으면 프로그램 종료
            }

            if(!checkEmptydataSeparator(studyTimeLines, new String[]{"/", "&"})){
                System.out.println("8파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if(!checkEmptydataSeparator(postLines, new String[]{"/", "@"})){
                System.out.println("9파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if (!checkEmptydataSeparator(groupLines, new String[]{"/", ",", "@"})) {
                System.out.println("9파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if (!checkConsecutiveEmptyLines(userLines)) {
                System.out.println("11파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if (!checkConsecutiveEmptyLines(studyTimeLines)) {
                System.out.println("12파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if (!checkConsecutiveEmptyLines(postLines)) {
                System.out.println("13파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if (!checkConsecutiveEmptyLines(groupLines)) {
                System.out.println("14파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if(!validateUsers(userLines)){
                System.out.println("15파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if (!validateStudyTime(userLines, studyTimeLines)) {
                System.out.println("16파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if(!validatePosts(userLines, postLines)){
                System.out.println("17파일을 읽지 못했습니다.");
                System.exit(-1);
            }

            if(!validateGroups(userLines, groupLines)){
                System.out.println("18파일을 읽지 못했습니다.");
                System.exit(-1);
            }
        }
        //무결성 검사 코드 여기까지

        Scanner scan = new Scanner(System.in);
        String menu;
        studyTimeManager studyTime = new studyTimeManager();
        try{
            studyTime.scanTimeFile("study_time.txt");
        }catch (NumberFormatException e){
            System.out.println("파일을 읽을 수 없습니다.");
            System.exit(-1);
        }

        groupManager group = new groupManager();
        try{
            group.scanGroupFile("study_group.txt");
        }catch (NumberFormatException e){
            System.out.println("파일을 읽을 수 없습니다.");
            System.exit(-1);
        }


        boardManager board = new boardManager();
        try{
            board.BoardFileRead("board.txt");
        }catch (NumberFormatException e){
            System.out.println("파일을 읽을 수 없습니다.");
            System.exit(-1);
        }

        UserManager um = new UserManager();
        try{
            um.userFileRead("user.txt");
        }catch (NumberFormatException e){
            System.out.println("파일을 읽을 수 없습니다.");
            System.exit(-1);
        }


        do {
            System.out.println("\n로그인 / 회원가입 중 원하는 작업을 입력하세요.");
            System.out.print("SSH>");
            menu = scan.nextLine();
            menu = menu.trim();

            switch (menu) {
                case "로그인":
                    System.out.println("-".repeat(50));
                    System.out.println("아이디와 비밀번호를 입력하세요.(/로 구분해주세요!)");
                    System.out.print("SSH>");
                    String Id_Password = scan.nextLine();
                    System.out.println("-".repeat(50));

                    User login_user = UserCheck(um, Id_Password);

                    if (login_user == null) {
                        System.out.println("로그인 실패");
                        System.out.println("-".repeat(50));
                        break;
                    } else {
                        group.findusergroup(login_user);
                        System.out.println("로그인 성공");
                        studyTime.getStudyTimeById("study_time.txt", login_user);

                        if (login_user.userGrade == 9 && login_user.userNumber == 999999999) {
                            do {
                                System.out.println("-".repeat(50));
                                System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                System.out.println(" - 게시글 삭제");
                                System.out.println(" - 댓글 삭제");
                                System.out.println(" - 프로그램 종료");
                                System.out.println("-".repeat(50));
                                System.out.print("SSH>");
                                menu = scan.nextLine().trim();

                                switch (menu) {
                                    case "게시글 삭제", "게시글삭제", "게시글", "게시" -> {
                                        System.out.println("-".repeat(50));

                                        if(board.showboard(0, 0) == 1){
                                            break;
                                        } else{
                                            System.out.println("-".repeat(50));
                                            System.out.print("SSH>");

                                            String postname = scan.nextLine();
                                            board.delete(0, postname);
                                            board.saveboard();
                                        }

                                    }

                                    case "댓글 삭제", "댓글삭제", "댓글", "댓" -> {
                                        System.out.println("-".repeat(50));

                                        if(board.showboard(0, 1) == 1){
                                            break;
                                        } else{
                                            System.out.println("-".repeat(50));
                                            System.out.print("SSH>");

                                            String postname = scan.nextLine();
                                            board.search(0, postname);
                                            board.saveboard();
                                        }

                                    }

                                    case "프로그램 종료", "종료" -> {
                                        System.out.println("프로그램을 종료하겠습니다.\n");
                                        System.exit(1);
                                    }
                                }
                            } while (!menu.equals("프로그램 종료\n"));
                            scan.close();
                        }
                    }


                    System.out.println("-".repeat(50));
                    System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                    System.out.println(" - 공부시간");
                    System.out.println(" - 게시판");
                    System.out.println(" - 그룹");
                    System.out.println(" - 프로그램 종료");
                    System.out.println("-".repeat(50));

                    // Inner loop
                    while (true) {
                        group.savegroup();
                        System.out.print("SSH>");
                        menu = scan.nextLine().trim();


                        switch (menu) {
                            case "3" -> {
                                for (User us : um.Users) {
                                    System.out.println(us);
                                }
                            }

                            case "공부시간", "공부 시간", "공부" -> {
                                System.out.println("-".repeat(50));
                                System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                System.out.println(" - 시간 측정");
                                System.out.println(" - 목표 설정");
                                System.out.println(" - 확인");
                                System.out.println("-".repeat(50));
                                System.out.print("공부 시간>");
                                menu = scan.nextLine().trim();

                                switch (menu) {
                                    case "시간측정","시간 측정","측정"->{
                                        System.out.println("-".repeat(50));
                                        System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                        System.out.println(" - 일일");
                                        System.out.println(" - 과목");
                                        System.out.println("-".repeat(50));
                                        System.out.print("시간 측정>");
                                        menu = scan.nextLine().trim();

                                        switch (menu){
                                            case "일일", "일" -> {
                                                studyTime.measureTime(login_user);
                                                guide();
                                            }
                                            case "과목", "과" -> {
                                                studyTime.measureSubjectTime(login_user);
                                                guide();
                                            }
                                            default -> {
                                                System.out.println("올바르지 않은 입력입니다.");
                                                guide();
                                            }
                                        }
                                    }

                                    case "목표설정", "목표 설정", "목표" -> {
                                        System.out.println("-".repeat(50));
                                        System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                        System.out.println(" - 일일");
                                        System.out.println(" - 주간");
                                        System.out.println("-".repeat(50));
                                        System.out.print("목표 설정>");
                                        menu = scan.nextLine().trim();

                                        switch (menu) {
                                            case "일일", "일" -> {
                                                studyTime.setGoal_day(login_user);
                                                guide();
                                            }
                                            case "주간", "주" -> {
                                                studyTime.setGoal_week(login_user);
                                                guide();
                                            }
                                            default -> {
                                                System.out.println("올바르지 않은 입력입니다.");
                                                guide();
                                            }
                                        }
                                        updateUser(um, login_user);
                                    }

                                    case "확인" -> {
                                        System.out.println("-".repeat(50));
                                        System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                        System.out.println(" - 일일");
                                        System.out.println(" - 주간");
                                        System.out.println("-".repeat(50));
                                        System.out.print("확인>");
                                        menu = scan.nextLine().trim();

                                        switch (menu) {
                                            case "일일", "일" -> {
                                                studyTime.compareDayStudy(login_user);
                                                guide();
                                            }
                                            case "주간", "주" -> {
                                                studyTime.compareWeekStudy(login_user);
                                                guide();
                                            }
                                            default -> {
                                                System.out.println("올바르지 않은 입력입니다.");
                                                guide();
                                            }
                                        }
                                    }
                                    default -> {
                                        System.out.println("올바르지 않은 입력입니다.");
                                        guide();
                                    }
                                }
                            }

                            case "게시판" -> {
                                System.out.println("-".repeat(50));
                                System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                System.out.println(" - 일반게시판");
                                System.out.println(" - 질문게시판");
                                System.out.println("-".repeat(50));
                                System.out.print("게시판>");
                                menu = scan.nextLine().trim();

                                switch (menu) {
                                    case "일반게시판", "일반", "일반 게시판" -> {
                                        System.out.println("-".repeat(50));
                                        System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                        System.out.println(" - 등록");
                                        System.out.println(" - 삭제");
                                        System.out.println(" - 조회");
                                        System.out.println("-".repeat(50));
                                        System.out.print("일반>");
                                        menu = scan.nextLine().trim();

                                        switch (menu) {
                                            case "등록" -> {
                                                System.out.println("_".repeat(50));
                                                System.out.println("제목을 입력하세요 :");
                                                String title = scan.nextLine();

                                                if(title.trim().isEmpty()){
                                                    System.out.println("제목에 문자를 입력해야 합니다.");
                                                    guide();
                                                    continue;
                                                }

                                                System.out.println("내용을 입력하세요('종료'를 입력하면 저장됩니다.) :");
                                                ArrayList<String> mtext = new ArrayList<>();
                                                boolean b = false;
                                                while (true) {
                                                    String input = scan.nextLine();
                                                    if (input.equals("종료")) {
                                                        break;
                                                    }
                                                    mtext.add(input);
                                                }

                                                for (String str : mtext) {
                                                    if (!str.trim().isEmpty()) {
                                                        b = true;
                                                        break;
                                                    }
                                                }
                                                if(!b) {
                                                    System.out.println("본문에 문자를 입력해야 합니다.");
                                                    guide();
                                                    continue;
                                                }
                                                board.addboard(title, mtext, login_user, 0);//0은 일반게시판이라는 것
                                                guide();
                                            }
                                            case "삭제" -> {
                                                System.out.println("_".repeat(50));
                                                board.myboard(0, login_user);//0은 일반게시판이라는 것
                                                guide();
                                            }
                                            case "조회" -> {
                                                System.out.println("_".repeat(50));
                                                board.showboard(0, login_user);
                                                guide();
                                            }
                                            default -> {
                                                System.out.println("올바르지 않은 입력입니다.");
                                                guide();
                                            }
                                        }
                                    }
                                    case "질문게시판", "질문", "질문 게시판" -> {
                                        System.out.println("-".repeat(50));
                                        System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                        System.out.println(" - 등록");
                                        System.out.println(" - 삭제");
                                        System.out.println(" - 조회");
                                        System.out.println("-".repeat(50));
                                        System.out.print("질문>");
                                        menu = scan.nextLine().trim();

                                        switch (menu) {
                                            case "등록" -> {
                                                System.out.println("_".repeat(50));
                                                System.out.println("제목을 입력하세요 :");
                                                String title = scan.nextLine();
                                                if(title.trim().isEmpty()){
                                                    System.out.println("제목에 문자를 입력해야 합니다.");
                                                    guide();
                                                    continue;
                                                }
                                                System.out.println("내용을 입력하세요('종료'를 입력하면 저장됩니다.) :");
                                                ArrayList<String> mtext = new ArrayList<>();
                                                boolean b = false;
                                                while (true) {
                                                    String input = scan.nextLine();
                                                    if (input.equals("종료")) {
                                                        break;
                                                    }
                                                    mtext.add(input);
                                                }
                                                for (String str : mtext) {
                                                    if (!str.trim().isEmpty()) {
                                                        b = true;
                                                        break;
                                                    }
                                                }
                                                if(!b) {
                                                    System.out.println("본문에 문자를 입력해야 합니다.");
                                                    guide();
                                                    continue;
                                                }
                                                board.addboard(title, mtext, login_user, 1);//1은 질문게시판이라는 것
                                                guide();
                                            }
                                            case "삭제" -> {
                                                System.out.println("_".repeat(50));
                                                board.myboard(1, login_user);//1은 일반게시판이라는 것
                                                guide();
                                            }
                                            case "조회" -> {
                                                System.out.println("_".repeat(50));
                                                board.showboard(1, login_user);
                                                guide();
                                            }
                                            default -> {
                                                System.out.println("올바르지 않은 입력입니다.");
                                                guide();
                                            }
                                        }
                                    }
                                    default -> {
                                        System.out.println("올바르지 않은 입력입니다.");
                                        guide();
                                    }
                                }
                            }

                            case "그룹" -> {
                                System.out.println("-".repeat(50));
                                System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                System.out.println(" - 그룹 생성");
                                System.out.println(" - 그룹 입장");
                                System.out.println(" - 학습 자료");
                                System.out.println(" - 팀플 가능 시간");
                                System.out.println(" - 그룹 조회");

                                if(group.Check_groupHead(login_user)){ // 그룹장이면
                                    System.out.println(" - 팀플 시간 조정");
                                    System.out.println(" - 자료 삭제");
                                    System.out.println(" - 그룹원 추방");
                                    System.out.println(" - 그룹 삭제");
                                }

                                System.out.println("-".repeat(50));
                                System.out.print("그룹>");
                                menu = scan.nextLine().trim();

                                switch (menu) {
                                    case "그룹 조회", "조회" -> {
                                        group.searchgroup();
                                        group.savegroup();
                                        guide();
                                    }

                                    case "그룹 생성", "생성" -> {
                                        System.out.println("그룹 생성 페이지입니다!");
                                        group.makegroup();
                                        updateUser(um, login_user);
                                        guide();
                                    }

                                    case "그룹 입장", "입장" -> {
                                        System.out.println("그룹 입장 페이지입니다!");
                                        group.joingroup();
                                        updateUser(um, login_user);
                                        guide();
                                    }

                                    case "학습 자료", "학습자료", "자료" -> {

                                        if (login_user.user_group == null) {
                                            System.out.println("그룹에 속해 있지 않습니다. 그룹을 가입하세요.");
                                            guide();
                                        }
                                        else {
                                            System.out.println("-".repeat(50));
                                            System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                            System.out.println(" - 등록");
                                            System.out.println(" - 삭제");
                                            System.out.println(" - 조회");
                                            System.out.println("-".repeat(50));
                                            System.out.print("학습 자료>");
                                            menu = scan.nextLine().trim();

                                            switch (menu) {
                                                case "등록" -> {
                                                    group.grouppostadd();
                                                    guide();
                                                }
                                                case "삭제" -> {
                                                    group.grouppostdelete();
                                                    guide();
                                                }
                                                case "조회" -> {
                                                    group.groupshowboard();
                                                    guide();
                                                }
                                                default -> {
                                                    System.out.println("올바르지 않은 입력입니다.");
                                                    guide();
                                                }
                                            }
                                        }
                                    }

                                    case "팀플가능시간", "팀플 가능 시간", "팀플", "팀플시간" -> {

                                        if (login_user.user_group == null)
                                            System.out.println("그룹에 속해 있지 않습니다. 그룹을 가입하세요.");
                                        else {
                                            System.out.println("-".repeat(50));
                                            System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
                                            System.out.println(" - 등록");
                                            System.out.println(" - 삭제");
                                            System.out.println(" - 조회");
                                            System.out.println("-".repeat(50));
                                            System.out.print("팀플 가능 시간>");
                                            menu = scan.nextLine().trim();

                                            switch (menu) {
                                                case "등록" -> {
                                                    System.out.println("-".repeat(50));
                                                    System.out.println("(시간은 '공백'으로 요일은 '/'로 구분)");
                                                    System.out.println("등록>");
                                                    String teamtime = scan.nextLine().trim();
                                                    try {
                                                        if (group.teamtimeadd(teamtime)) {
                                                            System.out.println("등록되었습니다.");
                                                        } else {
                                                            System.out.println("등록에 실패하였습니다.");
                                                        }
                                                    } catch (ArrayIndexOutOfBoundsException e) {
                                                        System.out.println("입력값이 잘못되었습니다.");
                                                    }
                                                    guide();
                                                }
                                                case "삭제" -> {
                                                    group.teamtimedelete();
                                                    guide();
                                                }
                                                case "조회" -> {
                                                    group.teamtimesearch();
                                                    guide();
                                                }
                                                default -> {
                                                    System.out.println("올바르지 않은 입력입니다.");
                                                    guide();
                                                }
                                            }
                                        }
                                    }


                                    case "팀플 시간 조정", "팀플시간조정", "시간조정", "조정" -> {
                                        System.out.println("-".repeat(50));
                                        System.out.println("(시간은 '공백'으로 요일은 '/'로 구분)");
                                        System.out.print("등록>");
                                        String teamtime = scan.nextLine().trim();

                                        try {
                                            if (group.setTimeTime(teamtime)) {
                                                System.out.println("등록되었습니다.");
                                            } else {
                                                System.out.println("등록에 실패하였습니다.");
                                            }
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            System.out.println("입력값이 잘못되었습니다.");
                                        }
                                        guide();

                                    }
                                    case "학습 자료 삭제", "학습자료삭제", "자료 삭제", "자료삭제" -> {
                                        group.adminpostdelete();
                                        guide();
                                    }

                                    case "그룹원 추방", "그룹원추방", "추방" -> {
                                        group.expelGroupMember(um.getuserlist());
                                        updateUser(um, login_user);
                                        guide();
                                    }

                                    case "그룹 삭제", "그룹삭제", "삭제" -> {
                                        group.deleteGroup(um.getuserlist(), login_user);
                                        updateUser(um, login_user);
                                        guide();
                                    }

                                    default -> {
                                        System.out.println("올바르지 않은 입력입니다.");
                                        guide();
                                    }
                                }
                            }
                            case "프로그램 종료", "종료" -> {
                                System.out.println("프로그램을 종료하겠습니다.");
                                group.savegroup();
                                board.saveboard();
                                updateUser(um, login_user);
                                System.exit(1);
                            }

                            default -> {
                                System.out.println("올바르지 않은 입력입니다.");
                                guide();
                            }
                        }
                    }
                case "회원 가입", "회원가입", "회원":
                    System.out.println("-".repeat(50));
                    System.out.println("이름, 아이디, 비밀번호, 학번, 학년을 입력하세요.(/로 구분해주세요!)");
                    System.out.print("SSH>");
                    Id_Password = scan.nextLine();
                    System.out.println("-".repeat(50));

                    UserManager userManager = new UserManager();
                    userManager.userFileRead("user.txt");
                    SignIn signIn = new SignIn();
                    boolean result = signIn.signin(userManager, Id_Password);

                    if (result) {
                        System.out.println("회원가입 성공\n");
                    } else {
                        System.out.println("회원가입 실패\n");
                    }
                    break;
                case "프로그램 종료":
                    System.out.println("프로그램을 종료하겠습니다.\n");
                    System.exit(1);
                default:
                    System.out.println("잘못 선택하셨습니다.\n");
                    break;
            }
        } while (!menu.equals("프로그램 종료\n"));

        // Close the scanner
        scan.close();
    }



    //무결성 검사 코드
    public static boolean validateUsers(ArrayList<String> userLines) {
        int adminCount = 0;
        for (String line : userLines) {
            String[] userInfo = line.split("\t");
            if (userInfo.length < 5 && userInfo.length != 0) {
                System.out.println("회원가입 조건을 만족하지 않는 데이터가 있습니다.");
                return false;
            }

            String name = userInfo[0].trim();
            String id = userInfo[1].trim();
            String password = userInfo[2].trim();
            String number = userInfo[3].trim(); // 학번
            String grade = userInfo[4].trim(); // 학년

            // 관리자 식별 코드 체크
            if (number.equals("999999999") && grade.equals("9")) {
                adminCount++;
            }

            // 회원 가입 조건 체크
            if (!checkName(name) || !checkId(id, userLines) || !checkPassword(password) || !checkNumber(number, userLines) || !checkGrade(grade)) {
                System.out.println("여기에서 걸림");
                return false;
            }
        }

        if (adminCount >= 2) {
            System.out.println("관리자 식별 코드를 가진 인원이 2명 이상입니다.");
            return false;
        }

        return true;
    }

    public static boolean validateStudyTime(ArrayList<String> userLines, ArrayList<String> studyTimeLines) {
        ArrayList<String> userIds = new ArrayList<>();
        for (String line : userLines) {
            String[] parts = line.split("\t");
            if (parts.length > 0) {
                userIds.add(parts[1]);
            }
        }

        for (String line : studyTimeLines) {
            String[] parts = line.split("\t");
            if (parts.length < 2) {
                return false;
            }

            String userId = parts[0];
            if (!userIds.contains(userId)) {
                return false;
            }

            // Check parts[3] for the required conditio
            if (parts.length > 3) {
                String[] subParts = parts[1].split("&");
                for (String subPart : subParts) {
                    String[] values = subPart.split("/");
                    if ((values.length)%2 == 1 || values.length == 0){
                        return false;
                    }
                    for (String value : values) {
                        if (value.isEmpty()) {
                            return false; // 분리된 값 중 하나가 null이거나 비어 있는 경우
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean validatePosts(ArrayList<String> userLines, ArrayList<String> postLines) {
        ArrayList<String> userIds = new ArrayList<>(); // 유저의 userId를 저장할 집합
        ArrayList<String> userGrades = new ArrayList<>(); // 유저의 학년을 저장할 리스트
        ArrayList<String> likeUserList = new ArrayList<>(); // 좋아요 누른 사람들 저장
        ArrayList<String> replyUserList = new ArrayList<>(); // 댓글 단 사람들 저장

        // postLines에서 좋아요 누른 사람들과 댓글 단 사람들 추출
        for (String line : postLines) {
            String[] parts = line.split("\t");
            if (!parts[4].equals("0")) { // 좋아요 누른 사람들 정보 확인
                likeUserList.add(parts[3]); // parts[3]이 좋아요를 누른 userId라고 가정
            }
            if (parts.length == 9) { // 댓글 단 사람들 정보 확인
                replyUserList.add(parts[7]); // parts[7]이 댓글을 단 userId라고 가정
            }
        }

        // userLines에서 유저 정보 추출
        for (String line : userLines) {
            String[] parts = line.split("\t");
            if (parts.length > 4) { // 라인이 충분한 필드를 가지고 있는지 확인
                userIds.add(parts[1]); // parts[1]이 userId라고 가정
                userGrades.add(parts[4]); // parts[4]가 유저의 학년이라고 가정
            }
        }

        // 좋아요를 누른 사람들의 ID가 유저 리스트에 있는지 확인
        for (String likeUserId : likeUserList) {
            if(likeUserId.isEmpty()){
                break;
            }
            String[] likeUserIds = likeUserId.split("/");
            for (String id : likeUserIds) {
                if (!userIds.contains(id)) {
                    System.out.println("좋아요를 누른 ID " + id + "가 user.txt에 없습니다.");
                    System.exit(-1); // 프로그램 종료
                }
            }
        }

        // 댓글을 단 사람들의 ID가 유저 리스트에 있는지 확인
        for (String replyUserId : replyUserList) {
            String[] replyUserIds = replyUserId.split("/");
            for (String id : replyUserIds) {
                if (!userIds.contains(id)) {
                    System.out.println("댓글을 단 ID " + id + "가 user.txt에 없습니다.");
                    System.exit(-1); // 프로그램 종료
                }
            }
        }

        // postLines에서 포스트 검증
        for (String line : postLines) {
            String[] parts = line.split("\t");
            if (parts.length < 2) {
                System.out.println(1);
                return false; // 포스트 형식이 올바르지 않음
            }

            String userId = parts[0];
            String postGrade = parts[1];

            // userGrades에서 해당 userId의 학년 찾기
            String userGrade = null;
            int index = new ArrayList<>(userIds).indexOf(userId);
            if (index != -1 && index < userGrades.size()) {
                userGrade = userGrades.get(index);
            } else {
                System.out.println(2);
                return false; // userId가 userIds 집합에 없음
            }

            // 포스트의 학년(postGrade)과 유저의 학년(userGrade) 비교
            if (!postGrade.equals(userGrade)) {
                System.out.println(3);
                return false; // 학년이 일치하지 않음
            }
        }

        System.out.println(likeUserList);
        System.out.println(countLikes(postLines));
        // 좋아요 누른 아이디 수와 하트의 개수가 맞지 않을 경우 검증
        if (likeUserList.size() != countLikes(postLines)) {
            System.out.println(4);
            return false; // 좋아요 누른 아이디 수와 하트의 개수가 다름
        }

        // 댓글을 단 아이디 수와 댓글의 개수가 맞지 않을 경우 검증
        if (replyUserList.size() != countReplies(postLines)) {
            System.out.println(5);
            return false; // 댓글을 단 아이디 수와 댓글의 개수가 다름
        }

        return true; // 모든 검증 조건을 만족함
    }

    public static int countLikes(ArrayList<String> postLines) {
        int count = 0;
        for (String line : postLines) {
            String[] parts = line.split("\t");
            if (parts.length >= 7) {
                String[] names = parts[3].split("/");
                for (String name : names) {
                    if (!name.trim().isEmpty()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static int countReplies(ArrayList<String> postLines) {
        int count = 0;
        for (String line : postLines) {
            String[] parts = line.split("\t");
            if (parts.length >= 9) {
                String[] replies = parts[7].split("/");
                for (String reply : replies) {
                    if (!reply.trim().isEmpty()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static boolean validateGroups(ArrayList<String> userLines, ArrayList<String> groupLines) {
        Set<String> userIds = new HashSet<>();

        // userLines에서 userIds 집합 생성
        for (String line : userLines) {
            String[] parts = line.split("\t");
            if (parts.length > 1) {
                userIds.add(parts[1]); // parts[1]이 userId라고 가정
            }
        }

        // groupLines에서 그룹 검증
        for (String line : groupLines) {
            String[] parts = line.split("\t");
            if (parts.length < 5) {
                System.out.println("그룹 라인의 형식이 올바르지 않습니다.");
                System.exit(-1); // 프로그램 종료
            }

            String userId = parts[3]; // 그룹 리더의 userId라고 가정
            if (!userIds.contains(userId)) {
                System.out.println("그룹 리더 ID " + userId + "가 user.txt에 없습니다.");
                System.exit(-1); // 프로그램 종료
            }

            // 그룹원 ID가 포함된 부분 처리
            String members = parts[4]; // 그룹원 정보가 parts[4]에 있다고 가정
            if (members != null && !members.isEmpty()) {
                String[] memberIds = members.split(",");
                for (String memberId : memberIds) {
                    memberId = memberId.trim(); // 공백 제거
                    if (!userIds.contains(memberId)) {
                        System.out.println("그룹원 ID " + memberId + "가 user.txt에 없습니다.");
                        System.exit(-1); // 프로그램 종료
                    }
                }
            }
        }

        return true; // 모든 검증 조건을 만족함
    }

    public static boolean checkName(String name) {
        return !name.isEmpty();
    }

    public static boolean checkId(String id, ArrayList<String> userLines) {
        if (id.length() < 6 || id.length() > 12 || SignIn.containsHangul(id) || id.contains(" ")) {
            return false;
        }

        int count = 0;
        for (String line : userLines) {
            String[] userInfo = line.split("\t");
            if (userInfo[1].trim().equals(id)) {
                count++;
            }
        }

        return count <= 1;
    }

    public static boolean checkPassword(String password) {
        return password.length() >= 8 && password.length() <= 16 && SignIn.containsEngNum(password) && !SignIn.containsHangul(password) && !password.contains(" ");
    }

    public static boolean checkNumber(String number, ArrayList<String> userLines) {
        if (number.length() != 9 || Integer.parseInt(number) < 0) {
            System.out.println("1여기에서 걸림");
            return false;
        }

        int count = 0;
        for (String line : userLines) {
            String[] userInfo = line.split("\t");
            if (userInfo[3].trim().equals(number)) {
                count++;
            }
        }
        return count <= 1;
    }

    public static boolean checkGrade(String grade) {
        try {
            int gradeInt = Integer.parseInt(grade);
            return (gradeInt >= 1 && gradeInt <= 4)||gradeInt==9;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkEmptydataSeparator(ArrayList<String> data, String[] separators) {
        for (String line : data) {
            for (String separator : separators) {
                String[] parts = line.split(separator);
                for (String part : parts) {
                    if (part.trim().isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean checkConsecutiveEmptyLines(ArrayList<String> data) {
        for (int i = 0; i < data.size() - 1; i++) {
            if (data.get(i).trim().isEmpty() && data.get(i + 1).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkTabCount(ArrayList<String> data, int[] allowedCounts) {
        for (String line : data) {
            String[] parts = line.split("\t");

            int tabCount = line.length() - line.replace("\t", "").length(); // 탭 개수 계산
            boolean isAllowed = false;


            for (int count : allowedCounts) {
                if (tabCount == count) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                return true; // 허용된 탭 개수가 아닌 경우 true 반환
            }
        }
        return false; // 모든 라인이 허용된 탭 개수를 가지면 false 반환
    }






    public static ArrayList<String> readfile(String filename) {
        try {
            return new ArrayList<>(Files.readAllLines(Paths.get(filename)));
        } catch (IOException e) {
            System.out.println("파일을 읽지 못했습니다.");
            System.exit(-1);
            return null;
        }
    }
    //여기까지





    public static void updateUser(UserManager um, User loginUser) {
        String filePath = "user.txt";
        try {
            FileWriter filename = new FileWriter(filePath, false);
            filename.close();
        } catch (IOException e) {
            System.out.println("파일이 존재하지 않습니다.");
        }

        try {
            FileWriter filename = new FileWriter(filePath, true); //파일 열기
            for (User us : um.Users) {
                if (us.user_group == null) {
                    filename.write(us.userName + "\t" + us.userID + "\t" + us.userPassword + "\t" + us.userNumber + "\t" + us.userGrade + "\t" + us.user_Studying_Goal_day + "\t" + us.user_Studying_Goal_week + "\t" + "\t" + "\n");
                } else {
                    filename.write(us.userName + "\t" + us.userID + "\t" + us.userPassword + "\t" + us.userNumber + "\t" + us.userGrade + "\t" + us.user_Studying_Goal_day + "\t" + us.user_Studying_Goal_week + "\t" + us.user_group + "\n");
                }
            }
            filename.close(); // 파일 닫기
        } catch (IOException e) {
            System.out.println("파일에 쓰기 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private static User UserCheck(UserManager um, String Id_Password) {
        int check = -1;
        String[] login = Id_Password.split("/");

        if(login.length < 2){
            System.out.println("아이디와 비밀번호를 모두 입력해주세요.");
            return null;
        }

        String Id = login[0].trim();
        String Password = login[1].trim();

        for (User user : um.Users) {
            if (user.userID.equals(Id)) {
                if (user.userPassword.equals(Password)) {
                    if((user.userGrade == 9) && (user.userNumber == 999999999)){
                        System.out.println("관리자로 로그인이 완료 되었습니다.");
                        return user;
                    } else {
                        System.out.println("로그인이 완료 되었습니다.");
                        return user;
                    }
                } else {
                    check = 1;
                }
            }
        }

        if (check == 1) {
            System.out.println("비밀번호가 올바르지 않습니다. 다시 시도하세요.");
        } else if(SignIn.containsHangul(Id) || SignIn.containsHangul(Password)){
            System.out.println("잘못된 입력입니다. 다시 시도하세요.");
        }else {
            System.out.println("존재하지 않는 아이디입니다. 다시 시도하세요.");
        }
        return null;
    }

    public static void guide() {
        System.out.println("-".repeat(50));
        System.out.println("아래의 메뉴 중 한 가지를 입력하세요.");
        System.out.println(" - 공부시간");
        System.out.println(" - 게시판");
        System.out.println(" - 그룹");
        System.out.println(" - 프로그램 종료");
        System.out.println("-".repeat(50));
    }
}