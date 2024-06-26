import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import static java.lang.Integer.parseInt;

public class StudyingTime {


    int dayStudyHour = 0;
    ArrayList<Integer> week_sum_time = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0));
    Scanner scan = new Scanner(System.in);
    String goal;
    int goal_day;
    int goal_week;

    public void measure() {
        for (int i = 0; i < 7; i++) {
            week_sum_time.add(0);
        }

        System.out.print("시작 및 종료 시각> ");
        String str = scan.nextLine();

        class time {
            String[] StartTime;
            String[] endTime;

            time(String[] st, String[] et) {
                this.StartTime = st;
                this.endTime = et;
            }
        }

        String[] eachTime = str.split("/");
        ArrayList<time> splitTime = new ArrayList<>();

        for (int i = 0; i < eachTime.length; i++) {
            String[] setime = eachTime[i].split("-");
            if (setime.length == 2) {
                splitTime.add(new time(setime[0].split(":"), setime[1].split(":")));
            }
        }

        for (int i = 0; i < splitTime.size(); i++) {
            String[] st = splitTime.get(i).StartTime;
            String[] et = splitTime.get(i).endTime;

            int st_hour = parseInt(st[0]);
            int st_min = parseInt(st[1]);

            int et_hour = parseInt(et[0]);
            int et_min = parseInt(et[1]);

            if (st_hour < 0 || st_min < 0 || et_hour < 0 || et_min < 0) {
                return;
            }

            if (st_hour * 60 + st_min <= 1439 && et_hour * 60 + et_min <= 1440 && et_hour + et_min >= 1) {

                int sum_min = (et_hour * 60 + et_min) - (st_hour * 60 + st_min);

                dayStudyHour += sum_min;
            }

        }
        week_sum_time.set(todayDay(), dayStudyHour);
        System.out.println("총 공부 시간 : " + dayStudyHour / 60 + "시간 " + dayStudyHour % 60 + "분");
    }

    String userID;
    LocalDate todayDate;
    String dayTime;
    String weekTime;

    ArrayList<StudyingTime> Times = new ArrayList<>();  // 공부 시간 리스트

    public StudyingTime(String userID) {
        this.userID = userID;
    }

    public StudyingTime(){

    }

    // 오늘 날짜 받아오기
    public static LocalDate todayDate() {
        LocalDate today = LocalDate.now();
        return today;
    }


    // 이번 주의 월요일 날짜 받아오기
    public static LocalDate monDate() {
        LocalDate thisMonday = todayDate();
        while (thisMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            thisMonday = thisMonday.minusDays(1);
        }
        return thisMonday;
    }

    public static LocalDate sunDate() {
        LocalDate thisSunday = todayDate();
        while (thisSunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            thisSunday = thisSunday.plusDays(1);
        }
        return thisSunday;
    }


    void setGoal_day(User login_user){
        System.out.println("-".repeat(50));
        System.out.println(todayDate());
        System.out.print("공부 목표 시간> ");
        goal = scan.nextLine();  // 공부 목표 시간 받음

        if(login_user.user_Studying_Goal_day == 0){  // 아직 목표 설정 안함
            boolean result = Pattern.matches("[1-9][0-9]?+시간$", goal);
            if(result){  // 문법 형식에 부합 : 숫자+시간
                String t[] = goal.split("시간");
                goal_day = parseInt(t[0]);
                if(1<=goal_day && goal_day<=23){  // 1시간-23시간 사이
                    login_user.user_Studying_Goal_day=goal_day;
                    System.out.println("-".repeat(50));
                    System.out.println(todayDate()+" 목표: "+login_user.user_Studying_Goal_day+"시간 설정 완료.");
                }else{
                    System.out.println("1-23사이의 시간을 입력하세요. + 안내 화면");
                }
            }else{  // 문법 형식 X
                System.out.println("형식에 맞춰 입력하세요. + 안내 화면");
            }
        }else {  // 이미 목표 설정함
            System.out.println("-".repeat(50));
            System.out.println("이미 설정하였습니다.");
            System.out.println(todayDate()+" 목표: "+login_user.user_Studying_Goal_day+"시간");
        }
    }


    void setGoal_week(User login_user){
        System.out.println("-".repeat(50));
        System.out.println(monDate()+"-"+sunDate());
        System.out.print("공부 목표 시간> ");
        goal = scan.nextLine();  // 공부 목표 시간 받음

        if(login_user.user_Studying_Goal_week == 0){  // 아직 목표 설정 안함
            boolean result = Pattern.matches("[1-9][0-9][0-9]?+시간$", goal);
            if(result){  // 문법 형식에 부합 : 숫자+시간
                String t[] = goal.split("시간");
                goal_week = parseInt(t[0]);
                if(1<=goal_week && goal_week<=161){  // 1시간-23시간 사이
                    login_user.user_Studying_Goal_week=goal_week;
                    System.out.println("-".repeat(50));
                    System.out.println(monDate()+"-"+sunDate()+" 목표: "+login_user.user_Studying_Goal_week+"시간 설정 완료.");
                }else{
                    System.out.println("1-161사이의 시간을 입력하세요. + 안내 화면");
                }
            }else{  // 문법 형식 X
                System.out.println("형식에 맞춰 입력하세요. + 안내 화면");
            }
        }else {  // 이미 목표 설정함
            System.out.println("-".repeat(50));
            System.out.println("이미 설정하였습니다.");
            System.out.println(monDate()+"-"+sunDate()+" 목표: "+login_user.user_Studying_Goal_week+"시간");
        }
    }

    public void compareDayStudy(){

        //목표 설정하지 않은 경우
        if (goal_day==0) {
            System.out.println("목표 시간 > 목표 시간을 입력해주세요.");
            System.out.println("현재 공부 시간 > " + dayStudyHour/60 + "시간 " + dayStudyHour%60 + "분");
            System.out.println("남은 시간 > 목표 시간을 입력해주세요.");

        //목표 설정한 경우
        }else {
            System.out.println("목표 공부 시간 > " + goal_day + "시간 ");
            System.out.println("현재 공부 시간 > " + dayStudyHour/60 + "시간 " + dayStudyHour%60 + "분");

            //목표 시간 달성한 경우
            if((goal_day*60 - dayStudyHour) <= 0)
                System.out.println("목표 시간을 달성했습니다.");
            else{  //목표 시간 달성하지 못한 경우 - 남은 시간 출력
                int left_time = goal_day*60 - dayStudyHour;
                System.out.println("남은 시간 > " + left_time/60 + "시간 " + left_time%60 + "분");
            }
        }
    }



    public static int todayDay() {
        DayOfWeek day = todayDate().getDayOfWeek();
        return day.getValue()-1;
    }
}