import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.lang.Integer.parseInt;

public class studyTimeManager {
    String userID;
    String filename;
    Scanner scan = new Scanner(System.in);

    ArrayList<study_time> timelist = new ArrayList<>();

    int sumDayTime = 0;
    int weekTime = 0;
    ArrayList<Integer> week_sum_time = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0));

    String goal;
    int goal_day;
    int goal_week;

    class Time {
        String[] StartTime;
        String[] EndTime;

        Time(String[] st, String[] et) {
            this.StartTime = st;
            this.EndTime = et;
        }
    }

    ArrayList<Time> TL = new ArrayList<>();
    HashMap<String, Integer> subject = new HashMap<>();

    public void scanTimeFile(String filename) {
        this.filename = filename;
        try (Scanner file = new Scanner(new File(filename))) {
            while (file.hasNextLine()) {
                String str = file.nextLine();
                String[] result = str.split("\t");

                HashMap<String, Integer> hm = new HashMap<>();

                if(result[1].equals("0")||result[1].isEmpty()){
                    hm = null;
                }else {
                    String[] baseMap = result[1].split("&");
                    for (String s : baseMap) {
                        String[] entry = s.split("/");
                        hm.put(entry[0], parseInt(entry[1]));
                    }
                }
                this.addtime(new study_time(result[0], parseInt(result[2]), parseInt(result[3]), hm));
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일을 읽지 못했습니다.");
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("파일 형식이 잘못되었습니다.");
        }
    }

    void addtime(study_time studyTime) {
        for (study_time t : timelist) {
            if (t.userID.equals(studyTime.userID)) {
                timelist.remove(t);
                break;
            }
        }
        timelist.add(studyTime);
        savetime();
    }

    void savetime() {
        try {
            FileWriter writer = new FileWriter(filename, false);
            int i = 0;
            for (study_time t : timelist) {
                i++;
                writer.write(t.userID + "\t");
                StringJoiner joiner = new StringJoiner("&");

                if (t.subjectTime == null)
                    writer.write("0");
                else {
                    for (Map.Entry<String, Integer> entry : t.subjectTime.entrySet()) {
                        joiner.add(entry.getKey() + "/" + entry.getValue());
                    }
                    writer.write(joiner.toString());
                }
                writer.write("\t" + t.dayTime + "\t" + t.weekTime);

                if (i < timelist.size()) writer.write("\n");
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("파일 쓰기 오류");
        }
    }

    public String getStudyTimeById(String fileName, User login_user) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                // 각 줄을 탭으로 구분하여 배열로 변환
                String[] data = line.split("\t");
                // 첫 번째 항목(아이디)이 검색하려는 아이디와 일치하는지 확인
                if (data[0].equals(login_user.userID)) {
                    reader.close();
                    sumDayTime = parseInt(data[2]);
                    weekTime = parseInt(data[3]);

                    String[] baseMap = data[1].split("&");
                    if(!data[1].equals("0")){
                        for (String s : baseMap) {
                            String[] entry = s.split("/");
                            subject.put(entry[0], parseInt(entry[1]));
                        }
                    }

                    //System.out.println("아이디 불러오기 성공");
                    return "아이디 불러오기 성공";
                }
            }
            reader.close();
            // 파일 내에서 아이디를 찾지 못한 경우
            return "해당 아이디를 가진 사용자 정보가 없습니다.";
        } catch (IOException e) {
            return "파일을 찾을 수 없습니다.";
        }
    }

    // 오늘 요일 숫자로 받아오기 (0~6 월~일)
    public static int todayDay() {
        DayOfWeek day = todayDate().getDayOfWeek();
        return day.getValue() - 1;
    }

    // 오늘 날짜 받아오기
    public static LocalDate todayDate() {
        return LocalDate.now();
    }

    // 이번 주의 월요일 날짜 받아오기
    public static LocalDate monDate() {
        LocalDate thisMonday = todayDate();
        while (thisMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            thisMonday = thisMonday.minusDays(1);
        }
        return thisMonday;
    }

    // 이번 주의 일요일 날짜 받아오기
    public static LocalDate sunDate() {
        LocalDate thisSunday = todayDate();
        while (thisSunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            thisSunday = thisSunday.plusDays(1);
        }
        return thisSunday;
    }

    //일일 시간 측정
    public void measureTime(User login_user) {

        this.userID = login_user.userID;

        // 데이터를 불러왔을 시 불러온 데이터 초기화
        if(TL.isEmpty()){
            sumDayTime = 0;
            subject.clear();
        }

        int dayTime = 0;

        //사전 출력
        System.out.println("-".repeat(50));
        System.out.println("현재 공부 시간");
        if (!subject.isEmpty()) {
            for (Map.Entry<String, Integer> entry : subject.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                System.out.println(key + " | " + value / 60 + "시간 " + value % 60 + "분");
            }
        }
        System.out.println("총 공부 시간 : " + sumDayTime / 60 + "시간 " + sumDayTime % 60 + "분");
        System.out.println("-".repeat(50));

        System.out.println("시간과 분은 :, 시작과 끝은 -, 시간끼리의 구분은 /으로 입력해주세요.");
        System.out.print("시작 및 종료 시각> ");
        String str = scan.nextLine();


        String[] eachTime = str.split("/");  // 각 공부 구간으로 나눠짐
        ArrayList<Time> splitTime = new ArrayList<>();

        for (String t : eachTime) {
            String[] setime = t.split("-");  // 각 공부 구간에서 시작과 종료로 나눔 (setime : start, end time)
            if (setime.length == 2) {
                splitTime.add(new Time(setime[0].split(":"), setime[1].split(":")));  // 시작, 종료시간을 시와 분으로 나눠서 저장
            }
        }

        //모든 자료에 trim 진행
        for (Time T : splitTime) {
            for (int i = 0; i < T.StartTime.length; i++) {
                T.StartTime[i] = T.StartTime[i].trim();
            }
            for (int i = 0; i < T.EndTime.length; i++) {
                T.EndTime[i] = T.EndTime[i].trim();
            }
        }
        //입력에 대해 오류가 있는 지 판단
        for (int i = 0; i < splitTime.size(); i++) {
            Time t = splitTime.get(i);

            String[] st = t.StartTime;
            String[] et = t.EndTime;

            try {

                int st_hour = Integer.parseInt(st[0]);
                int st_min = Integer.parseInt(st[1]);

                int et_hour = Integer.parseInt(et[0]);
                int et_min = Integer.parseInt(et[1]);

                //0보다 작은 경우 리턴
                if (st_hour < 0 || st_min < 0 || et_hour < 0 || et_min < 0) {
                    System.out.println("올바른 형식으로 입력해주세요.");
                    System.out.println("-".repeat(50));
                    return;
                }

                //순서 맞는 지 확인
                if (i > 0) {
                    Time t2 = splitTime.get(i - 1);

                    String[] et2 = t2.EndTime;

                    int et2_hour = Integer.parseInt(et2[0]);
                    int et2_min = Integer.parseInt(et2[1]);

                    int judge = (st_hour * 60 + st_min) - (et2_hour * 60 + et2_min);

                    if (judge < 0) {
                        System.out.println("올바른 형식으로 입력해주세요.");
                        System.out.println("-".repeat(50));
                        return;
                    }
                }

                //기존 시간과 겹치는 지 확인
                for (Time T1 : TL) {
                    int t1_st = parseInt(T1.StartTime[0]) * 60 + parseInt(T1.StartTime[1]);
                    int t1_et = parseInt(T1.EndTime[0]) * 60 + parseInt(T1.EndTime[1]);
                    for (Time T2 : splitTime) {
                        int t2_st = parseInt(T2.StartTime[0]) * 60 + parseInt(T2.StartTime[1]);
                        int t2_et = parseInt(T2.EndTime[0]) * 60 + parseInt(T2.EndTime[1]);

                        if (((t1_st <= t2_st) && (t2_st <= t1_et)) || ((t2_st <= t1_st) && (t1_st <= t2_et))) {
                            System.out.println("겹치는 시간이 존재합니다. 시간을 초기화한 후 다시 진행해주세요.");
                            return;
                        }
                    }
                }

                //24시간보다 작은 경우 수행
                if (st_hour * 60 + st_min <= 1439 && et_hour * 60 + et_min <= 1440 && et_hour + et_min >= 1) {

                    int sum_min = (et_hour * 60 + et_min) - (st_hour * 60 + st_min);  // 공부시간 계산 (종료-시작)
                    if (sum_min > 0)
                        dayTime += sum_min;
                }
            } catch (NumberFormatException e) {
                System.out.println("올바른 형식으로 입력해주세요.");
                System.out.println("-".repeat(50));
                return;
            }
        }

        if (dayTime == 0) {
            System.out.println("올바른 형식으로 입력해주세요.");
            System.out.println("-".repeat(50));
            return;
        } else if (dayTime < 0) {
            System.out.println("올바른 형식으로 입력해주세요.");
            System.out.println("-".repeat(50));
            return;
        } else { // 만족하는 경우 결과값 출력
            sumDayTime += dayTime;
            System.out.println("총 공부 시간 : " + sumDayTime / 60 + "시간 " + sumDayTime % 60 + "분");
            System.out.println("-".repeat(50));
        }

        TL.addAll(splitTime);
        week_sum_time.set(todayDay(), sumDayTime);
        setweekTime();
        this.addtime(new study_time(this.userID, sumDayTime, weekTime, subject));
    }

    //과목 시간 측정
    public void measureSubjectTime(User login_user) {

        this.userID = login_user.userID;

        // 데이터를 불러왔을 시 불러온 데이터 초기화
        if(TL.isEmpty()){
            sumDayTime = 0;
            subject.clear();
        }

        int subjectTime = 0;

        //사전 출력
        System.out.println("-".repeat(50));
        System.out.println("현재 공부 시간");
        if (!subject.isEmpty()) {
            for (Map.Entry<String, Integer> entry : subject.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                System.out.println(key + " | " + value / 60 + "시간 " + value % 60 + "분");
            }
        }
        System.out.println("총 공부 시간 : " + sumDayTime / 60 + "시간 " + sumDayTime % 60 + "분");
        System.out.println("-".repeat(50));

        //입력 받기
        System.out.println("과목 이름을 적은 후 |로 구분짓고, 시간과 분은 :, 시작과 끝은 -, 시간끼리의 구분은 /으로 입력해주세요.");
        System.out.print("시작 및 종료 시각> ");
        String str = scan.nextLine();

        String[] input = str.split("\\|"); // 과목이름과 시간 나누기

        for(int i = 0; i < input.length; i++)
            input[i] = input[i].trim();

        String[] subjectName = input[0].split("[:/-]"); //의미규칙 확인
        if ((input.length != 2) || (subjectName.length != 1)) {
            System.out.println("올바른 형식으로 입력해주세요.");
            return;
        }

        String[] eachTime = input[1].split("/");  // 각 공부 구간으로 나눠짐
        ArrayList<Time> splitTime = new ArrayList<>();

        for (String t : eachTime) {
            String[] setime = t.split("-");  // 각 공부 구간에서 시작과 종료로 나눔 (setime : start, end time)
            if (setime.length == 2) {
                splitTime.add(new Time(setime[0].split(":"), setime[1].split(":")));  // 시작, 종료시간을 시와 분으로 나눠서 저장
            }
        }

        //모든 자료에 trim 진행
        for (Time T : splitTime) {
            for (int i = 0; i < T.StartTime.length; i++) {
                T.StartTime[i] = T.StartTime[i].trim();
            }
            for (int i = 0; i < T.EndTime.length; i++) {
                T.EndTime[i] = T.EndTime[i].trim();
            }
        }

        for (int i = 0; i < splitTime.size(); i++) {
            Time t = splitTime.get(i);

            String[] st = t.StartTime;
            String[] et = t.EndTime;

            try {

                int st_hour = Integer.parseInt(st[0]);
                int st_min = Integer.parseInt(st[1]);

                int et_hour = Integer.parseInt(et[0]);
                int et_min = Integer.parseInt(et[1]);

                //0보다 작은 경우 리턴
                if (st_hour < 0 || st_min < 0 || et_hour < 0 || et_min < 0) {
                    System.out.println("올바른 형식으로 입력해주세요.");
                    System.out.println("-".repeat(50));
                    return;
                }

                //순서 맞는 지 확인
                if (i > 0) {
                    Time t2 = splitTime.get(i - 1);

                    String[] et2 = t2.EndTime;

                    int et2_hour = Integer.parseInt(et2[0]);
                    int et2_min = Integer.parseInt(et2[1]);

                    int judge = (st_hour * 60 + st_min) - (et2_hour * 60 + et2_min);

                    if (judge < 0) {
                        System.out.println("올바른 형식으로 입력해주세요.");
                        System.out.println("-".repeat(50));
                        return;
                    }
                }

                //기존 시간과 겹치는 지 확인
                for (Time T1 : TL) {
                    int t1_st = parseInt(T1.StartTime[0]) * 60 + parseInt(T1.StartTime[1]);
                    int t1_et = parseInt(T1.EndTime[0]) * 60 + parseInt(T1.EndTime[1]);
                    for (Time T2 : splitTime) {
                        int t2_st = parseInt(T2.StartTime[0]) * 60 + parseInt(T2.StartTime[1]);
                        int t2_et = parseInt(T2.EndTime[0]) * 60 + parseInt(T2.EndTime[1]);

                        if (((t1_st <= t2_st) && (t2_st <= t1_et)) || ((t2_st <= t1_st) && (t1_st <= t2_et))) {
                            System.out.println("겹치는 시간이 존재합니다. 시간을 초기화한 후 다시 진행해주세요.");
                            System.out.println("-".repeat(50));
                            return;
                        }
                    }
                }

                //24시간보다 작은 경우 수행
                if (st_hour * 60 + st_min <= 1439 && et_hour * 60 + et_min <= 1440 && et_hour + et_min >= 1) {

                    int sum_min = (et_hour * 60 + et_min) - (st_hour * 60 + st_min);  // 공부시간 계산 (종료-시작)
                    if (sum_min > 0)
                        subjectTime += sum_min;
                }
            } catch (NumberFormatException e) {
                System.out.println("올바른 형식으로 입력해주세요.");
                System.out.println("-".repeat(50));
                return;
            }
        }

        if (subjectTime == 0) {
            System.out.println("올바른 형식으로 입력해주세요.");
            System.out.println("-".repeat(50));
            return;
        } else if (subjectTime < 0) {
            System.out.println("올바른 형식으로 입력해주세요.");
            System.out.println("-".repeat(50));
            return;
        } else {

            sumDayTime += subjectTime; //결과값 더하기

            //이미 등록한 과목인 지 확인
            if(subject.containsKey(subjectName[0])) {
                int a = subject.get(subjectName[0]);
                subject.replace(subjectName[0], a + subjectTime);
                System.out.println(subjectName[0] + " | " + subjectTime/60 + "시간 " + subjectTime % 60 + "분이 추가로 등록되었습니다.");
            }else {
                subject.put(subjectName[0], subjectTime);
                System.out.println(subjectName[0] + " | " + subjectTime/60 + "시간 " + subjectTime % 60 + "분이 등록되었습니다.");
            }

            System.out.println("총 공부 시간 : " + sumDayTime / 60 + "시간 " + sumDayTime % 60 + "분");
            System.out.println("-".repeat(50));
        }

        TL.addAll(splitTime);
        week_sum_time.set(todayDay(), sumDayTime);
        setweekTime();
        this.addtime(new study_time(this.userID, sumDayTime, weekTime, subject));
    }

    // 일주일 누적 공부시간 계산
    void setweekTime() {
        weekTime = 0;
        for (int t : week_sum_time) {
            weekTime += t;
        }
    }

    // 일일 목표 설정
    void setGoal_day(User login_user) {
        System.out.println("-".repeat(50));
        System.out.println(todayDate());
        System.out.print("공부 목표 시간> ");
        goal = scan.nextLine();  // 공부 목표 시간 받음

        if (login_user.user_Studying_Goal_day == 0) {  // 아직 목표 설정 안함
            boolean result = Pattern.matches("[1-9][0-9]?+시간$", goal);
            if (result) {  // 문법 형식에 부합 : 숫자+시간
                String[] t = goal.split("시간");
                goal_day = parseInt(t[0].trim());
                if (1 <= goal_day && goal_day <= 23) {  // 1시간-23시간 사이
                    login_user.user_Studying_Goal_day = goal_day;
                    System.out.println("-".repeat(50));
                    System.out.println(todayDate() + " 목표: " + login_user.user_Studying_Goal_day + "시간 설정 완료.");
                } else {
                    System.out.println("1-23사이의 시간을 입력하세요.");
                }
            } else {  // 문법 형식 X
                System.out.println("\"(숫자)시간\" 형식으로 입력하세요.");
                System.out.println("-".repeat(50));
            }
        } else {  // 이미 목표 설정함
            System.out.println("-".repeat(50));
            System.out.println("이미 설정하였습니다.");
            System.out.println(todayDate() + " 목표: " + login_user.user_Studying_Goal_day + "시간");
        }
    }


    // 주간 목표 설정
    void setGoal_week(User login_user) {
        System.out.println("-".repeat(50));
        System.out.println(monDate() + " ~ " + sunDate());
        System.out.print("공부 목표 시간> ");
        goal = scan.nextLine();  // 공부 목표 시간 받음

        if (login_user.user_Studying_Goal_week == 0) {  // 아직 목표 설정 안함
            boolean result = Pattern.matches("[1-9][0-9][0-9]?+시간$", goal);
            if (result) {  // 문법 형식에 부합 : 숫자+시간
                String[] t = goal.split("시간");
                goal_week = parseInt(t[0].trim());
                if (1 <= goal_week && goal_week <= 161) {  // 1시간-161시간 사이
                    login_user.user_Studying_Goal_week = goal_week;
                    System.out.println("-".repeat(50));
                    System.out.println(monDate() + " ~ " + sunDate() + " 목표: " + login_user.user_Studying_Goal_week + "시간 설정 완료.");
                } else {
                    System.out.println("1-161사이의 시간을 입력하세요.");
                }
            } else {  // 문법 형식 X
                System.out.println("\"(숫자)시간\" 형식으로 입력하세요.");
                System.out.println("-".repeat(50));
            }
        } else {  // 이미 목표 설정함
            System.out.println("-".repeat(50));
            System.out.println("이미 설정하였습니다.");
            System.out.println(monDate() + " ~ " + sunDate() + " 목표: " + login_user.user_Studying_Goal_week + "시간");
        }
    }


    // 일일 시간목표 확인
    public void compareDayStudy(User login_user) {
        System.out.println("-".repeat(50));
        System.out.println(todayDate());

        //목표 설정하지 않은 경우
        if (login_user.user_Studying_Goal_day == 0) {
            if (!subject.isEmpty())
                for (Map.Entry<String, Integer> entry : subject.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    System.out.println(key + " | " + value / 60 + "시간 " + value % 60 + "분");
                }
            System.out.println("일일 공부 시간 > " + sumDayTime / 60 + "시간 " + sumDayTime % 60 + "분");
            System.out.println("일일 목표 시간을 입력해주세요.");

            //목표 설정한 경우
        } else {
            if (!subject.isEmpty())
                for (Map.Entry<String, Integer> entry : subject.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    System.out.println(key + " | " + value / 60 + "시간 " + value % 60 + "분");
                }
            System.out.println("총 공부 시간 > " + sumDayTime / 60 + "시간 " + sumDayTime % 60 + "분");

            //목표 시간 달성한 경우
            if ((login_user.user_Studying_Goal_day * 60 - sumDayTime) <= 0)
                System.out.println("목표 시간을 달성했습니다.");
            else {  //목표 시간 달성하지 못한 경우 - 남은 시간 출력
                int left_time = login_user.user_Studying_Goal_day * 60 - sumDayTime;
                System.out.println("남은 시간 > " + left_time / 60 + "시간 " + left_time % 60 + "분");
            }
        }
        System.out.println("-".repeat(50));
    }


    // 주간 시간목표 확인
    void compareWeekStudy(User login_user) {
        System.out.println("-".repeat(50));
        System.out.println(monDate() + " ~ " + sunDate());

        System.out.println("주간 공부 시간> " + weekTime / 60 + "시간 " + weekTime % 60 + "분");

        if (login_user.user_Studying_Goal_week != 0) {  // 주간 목표시간 설정한 경우
            int requiredTIme = login_user.user_Studying_Goal_week * 60 - weekTime;
            if (requiredTIme <= 0) {
                System.out.println("목표 시간을 달성했습니다.");
            } else {
                System.out.println("남은 시간> " + requiredTIme / 60 + "시간 " + requiredTIme % 60 + "분");
            }
        } else {
            System.out.println("주간 목표 시간을 입력해주세요.");
        }
        System.out.println("-".repeat(50));
    }
}