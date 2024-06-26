import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class groupManager {
    final int max = 100;
    String filename;
    static Scanner scan = new Scanner(System.in);
    ArrayList<study_group> grouplist = new ArrayList<>(max);
    study_group mygroup = null;
    User login_user;

    public void scanGroupFile(String filename) { //그룹 파일 리딩 메소드
        this.filename = filename;
        String[] people;
        String[] timearray = new String[0];
        try (Scanner file = new Scanner(new File(filename));) {
            while (file.hasNextLine()) {
                String str = file.nextLine();
                String[] result = str.split("\t");
                ArrayList<String> peoplelist = new ArrayList<>();
                ArrayList<Board> boardlist = new ArrayList<>();

                if (result.length >= 5) {
                    people = result[4].split(",");
                    Collections.addAll(peoplelist, people);
                }
                if (result.length >= 6) {
                    String time = result[5].replace("[", "").replace("]", "");
                    timearray = time.split(", ");
                }
                if (result.length >= 7) {
                    String[] board = result[6].split("&");
                    for (String b : board) {
                        String[] br = b.split("/");
                        if (br.length == 3)
                            boardlist.add(new Board(br[0], br[1], br[2]));
                    }
                }

                this.addgroup(new study_group(result[0], parseInt(result[1]), parseInt(result[2]), result[3], peoplelist, timearray, boardlist));
                timearray = null;

            }
        } catch (FileNotFoundException e) {
            System.out.println("파일을 읽지 못했습니다.");
        }

    }


    void findusergroup(User login_user) {
        for (study_group g : grouplist) {
            if (g.groupName.equals(login_user.user_group)){
                mygroup = g;
            }
        }
        this.login_user = login_user;
        System.out.println(mygroup);
        System.out.println(this.login_user);
    }

    boolean addgroup(study_group studyGroup) { //그룹 추가 메소드(파일입출력+사용자입력에 활용)
        boolean make = true;

        if (studyGroup.groupName.length() > 20) {
            studyGroup.groupName = studyGroup.groupName.substring(0, 20);
            System.out.println("그룹명은 20자이내로 제한됩니다.");
            return false;
        } else {
            for (study_group glist : grouplist) {
                if (glist.groupName.equals(studyGroup.groupName))
                    make = false;
            }
            if (make) {
                grouplist.add(studyGroup);
                savegroup();
                this.mygroup = studyGroup;
                System.out.println(studyGroup.groupName + " 그룹이 생성되었습니다!");
                return true;
            } else {
                System.out.println(studyGroup.groupName + "은(는) 이미 존재하는 그룹입니다. 다시 시도하세요.");
                return false;
            }
        }
    }

    void savegroup() {
        try {
            FileWriter writer = new FileWriter(filename, false);
            int i = 0;
            for (study_group g : grouplist) {
                i++;
                writer.write(g.groupName + "\t" + g.groupMax + "\t" + g.groupPassword + "\t" + g.groupheadId + "\t");
                for (String str : g.groupPeople) {
                    if (!str.equals(""))
                        writer.write(str + ",");
                }
                writer.write("\t");
                try {
                    writer.write(g.teamtimeavailable + "\t");
                    for (Board b : g.groupboard) {
                        writer.write(b.ID + "/" + b.head + "/" + b.body + "&");
                    }
                } catch (NullPointerException e) {
                    System.out.println("저장에 실패했습니다.");
                }

                if (i < grouplist.size()) writer.write("\n");
            }

            writer.close(); // 파일 닫기
        } catch (IOException e) {
            System.out.println("파일에 쓰기 오류가 발생했습니다: " + e.getMessage());
        }
    }

    void makegroup() { //그룹 생성 메소드
        System.out.println("[그룹명 인원수 비밀번호(숫자 4자)를 순서대로 입력하세요. ('/'으로 구분)]");
        System.out.print("그룹 생성> ");
        String input = scan.nextLine();
        System.out.println("-".repeat(50));
        String[] inputarr = input.split("/");

        if (!(login_user.user_group == null)) {
            System.out.println(login_user.user_group + ": 그룹에 속해 있다면 그룹을 만들 수 없습니다."); //사용자가 이미 그룹이 가입되어 있다면, 만들수 없도록 제제
            return;
        }
        try {
            for (int i = 0; i < inputarr.length; i++) {
                inputarr[i] = inputarr[i].trim();
            }
            if (inputarr[2].length() != 4) {
                System.out.println("비밀번호를 다시 입력해주세요.");
                return;
            }
            if (addgroup(new study_group(inputarr[0], parseInt(inputarr[1]), parseInt(inputarr[2]), login_user.userID)))
                this.login_user.user_group = inputarr[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("비어있는 값이 있습니다. 다시 입력하세요.");
        } catch (NumberFormatException e) {
            System.out.println("입력이 잘못되었습니다. 다시 입력하세요.");
        }

    }

    void searchgroup() {
        for (study_group g : grouplist)
            System.out.println(g);
    }

    void joingroup() { //그룹 입장 메소드
        if (login_user.user_group != null) {        //사용자가 이미 그룹이 가입되어 있다면, 만들수 없도록 제제
            System.out.println(login_user.user_group + " 그룹에 속해있습니다.");
            return;
        }

        System.out.println("[그룹명 비밀번호(숫자 4자)를 순서대로 입력하세요. ('/'으로 구분)]");
        System.out.print("그룹 입장> ");
        String input = scan.nextLine();
        System.out.println("-".repeat(50));
        String[] inputarr = input.split("/");

        for (int i = 0; i < inputarr.length; i++) {
            inputarr[i] = inputarr[i].trim();
        }
        try {
            boolean groupfound = false;
            for (study_group list : grouplist) {
                if (list.groupName.equals(inputarr[0])) {
                    groupfound = true;
                    if (list.groupPassword == parseInt(inputarr[1])) {
                        if (list.groupPeople.size() < list.groupMax - 1) {
                            System.out.println(list.groupName + " 그룹에 가입되었습니다!");
                            this.mygroup = list;
                            list.groupPeople.add(login_user.userID);
                            login_user.user_group = list.groupName;
                            savegroup();
                        } else {
                            System.out.println("그룹 인원이 초과하였습니다.");
                        }
                    } else {
                        System.out.println("비밀번호가 일치하지 않습니다. 다시 확인해 주세요.");
                    }
                }
            }
            if (!groupfound) {
                System.out.println("존재하지 않는 그룹입니다. 다시 확인해 주세요.");
            }
        } catch (NumberFormatException e) {
            System.out.println("비밀번호 형식이 잘못되었습니다.");
        }
    }

    void grouppostadd() {
        System.out.print("제목과 내용을 입력하세요.(/로 구분, 내용엔 Tab과 ,(comma), /(slash)를 사용할 수 없습니다.)\n제목/내용>");
        String input = scan.nextLine();

        String[] inputarr = input.split("/", 2);
        if (inputarr.length < 2) {
            System.out.println("제목과 내용을 정확히 구분하여 입력해주세요.");
            return;
        }

        String content = inputarr[1];
        if (content.contains("\t") || content.contains(",") || content.contains("/")) {
            System.out.println("게시물 내용엔 Tab과 ,(comma), /(slash)를 사용할 수 없습니다.");
            return;
        }

        boolean isUnique = true;
        for (Board b : mygroup.groupboard) {
            if (b.head.equals(inputarr[0])) {
                isUnique = false;
                break;
            }
        }

        if (isUnique) {
            mygroup.groupboard.add(new Board(login_user.userID, inputarr[0], content));
            System.out.println("등록되었습니다.");
        } else {
            System.out.println("중복된 제목의 학습자료가 등록되어 있습니다.");
        }
    }

    void grouppostdelete() {
        if(mygroup.groupboard.isEmpty()){
            System.out.println("-".repeat(50));
            System.out.println("삭제할 게시물이 없습니다.");
        } else {
            System.out.println("삭제할 게시물의 제목을 입력하세요.");
            System.out.println("-".repeat(50));
            for (Board b : mygroup.groupboard) {
                System.out.println("- " + b.head);
            }
            System.out.println("-".repeat(50));
            System.out.print("삭제> ");
            String input = scan.nextLine();
            System.out.println("-".repeat(50));
            Board del = null;
            for (Board b : mygroup.groupboard) {
                if (b.head.equals(input)) {
                    if (b.ID.equals(login_user.userID))
                        del = b;

                }
            }
            if (del != null) {
                mygroup.groupboard.remove(del);
                System.out.println(input + " 이(가) 삭제되었습니다.");
            } else {
                System.out.println("삭제에 실패했습니다.");
            }
        }
    }

    void grouppostsearch(String input) {
        System.out.println("-".repeat(50));
        boolean suc = false;
        for (Board b : mygroup.groupboard) {
            if (b.head.equals(input)) {
                System.out.println("제목> " + b.head);
                System.out.println("내용> " + b.body);
                suc = true;
                break;
            }
        }
        if (!suc)
            System.out.println("게시글 찾기에 실패했습니다. 다시 시도하세요.");
        System.out.println("-".repeat(50));
    }

    public void groupshowboard() {
        Stack<Board> tempStack = new Stack<>();
        Stack<Board> show = new Stack<>();
        for (Board b : mygroup.groupboard) {
            tempStack.push(b);
        }
        if (tempStack.isEmpty()) {
            System.out.println("게시물이 없습니다.");
            return;
        }
        int count = Math.min(tempStack.size(), 10);
        for (int j = 0; j < count; j++) {
            show.push(tempStack.pop());
        }
        while (!show.isEmpty()) {
            Board b = show.pop();
            System.out.println(b.head);
        }
        System.out.print("조회할 게시물을 선택하세요 : ");
        String s = scan.nextLine();
        grouppostsearch(s);
    }


    boolean teamtimeadd(String teamtime) {

        try {
            ArrayList<Integer> timeavailable = new ArrayList<>();

            int slashCount = teamtime.length() - teamtime.replace("/", "").length();
            if (slashCount != 4) {
                return false;
            }

            String[] day = teamtime.split("/");

            for (int i = 0; i < day.length; i++) {
                day[i] = day[i].trim();
            }

            for (int i = 0; i < day.length; i++) {
                if (!day[i].isEmpty()) {
                    String[] daytime = day[i].split(" ");
                    for (String dd : daytime) {
                        if (parseInt(dd) > 24 || parseInt(dd) < 0)
                            return false;
                        int timeValue = 0;
                        if (dd.length() == 1)
                            timeValue = parseInt((i + 1) + "0" + dd);
                        else if (dd.length() == 2)
                            timeValue = parseInt((i + 1) + dd);

                        if (!timeavailable.contains(timeValue)) {
                            timeavailable.add(timeValue);
                        }
                    }
                }
            }

            HashMap<Integer, List<Integer>> dayTime = new HashMap<>();
            for (Integer time : timeavailable) {
                int days = time / 100;
                int hour = time % 100;
                dayTime.putIfAbsent(days, new ArrayList<>());
                dayTime.get(days).add(hour);
            }

            String[] weeks = {"", "월", "화", "수", "목", "금"};
            for (int i = 1; i <= 5; i++) {
                if (dayTime.containsKey(i)) {
                    Collections.sort(dayTime.get(i));
                    System.out.print(weeks[i] + " : ");
                    List<Integer> times = dayTime.get(i);

                    for (int j = 0; j < times.size(); j++) {

                        if (Integer.toString(times.get(j)).length() == 1) {
                            System.out.print("0" + times.get(j) + "시");

                        } else if (Integer.toString(times.get(j)).length() == 2) {
                            System.out.print(times.get(j) + "시");
                        }
                        if (j < times.size() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.println();
                }
            }
            mygroup.teamtimeavailable.addAll(timeavailable);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("숫자를 잘못 입력하셨습니다.");
        }
        return false;
    }

    void teamtimesearch() {

        HashMap<Integer, Integer> frequency = new HashMap<>();
        for (int num : mygroup.teamtimeavailable) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }

        // 팀의 가능한 시간을 검색하는 코드
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(frequency.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        System.out.println("팀플 가능 시간:");
        String[] weeks = {"", "월", "화", "수", "목", "금"};

        for (Map.Entry<Integer, Integer> entry : list) {
            int key = entry.getKey();
            String week = weeks[key / 100]; //세자리 수 숫자로 표현되니까 100으로 나눠서 첫번째 값 알아내기
            int hour = key % 100;
            String output = String.format("%s %02d시: %d명", week, hour, entry.getValue());
            System.out.println(output);
        }

    }

    void teamtimedelete() {
        // 팀의 가능한 시간을 삭제하는 코드
        mygroup.teamtimeavailable.clear();
        System.out.println("모든 팀플 가능 시간이 삭제되었습니다.");
    }

    boolean setTimeTime(String teamtime) {
        try {
            ArrayList<Integer> timeavailable = new ArrayList<>();
            String[] day = teamtime.split("/");

            if (day.length != 5) {
                return false;
            }

            for (int i = 0; i < day.length; i++) {
                day[i] = day[i].trim();
            }

            for (int i = 0; i < day.length; i++) {
                if (!day[i].isEmpty()) {
                    String[] daytime = day[i].split(" ");
                    for (String dd : daytime) {
                        if (parseInt(dd) > 24 || parseInt(dd) < 0)
                            return false;
                        int timeValue = 0;
                        if (dd.length() == 1)
                            timeValue = parseInt((i + 1) + "0" + dd);
                        else if (dd.length() == 2)
                            timeValue = parseInt((i + 1) + dd);

                        if (!timeavailable.contains(timeValue)) {
                            timeavailable.add(timeValue);
                        }
                    }
                }
            }

            HashMap<Integer, List<Integer>> dayTime = new HashMap<>();
            for (Integer time : timeavailable) {
                int days = time / 100;
                int hour = time % 100;
                dayTime.putIfAbsent(days, new ArrayList<>());
                dayTime.get(days).add(hour);
            }

            String[] weeks = {"", "월", "화", "수", "목", "금"};
            for (int i = 1; i <= 5; i++) {
                if (dayTime.containsKey(i)) {
                    Collections.sort(dayTime.get(i));
                    System.out.print(weeks[i] + " : ");
                    List<Integer> times = dayTime.get(i);

                    for (int j = 0; j < times.size(); j++) {

                        if (Integer.toString(times.get(j)).length() == 1) {
                            System.out.print("0" + times.get(j) + "시");

                        } else if (Integer.toString(times.get(j)).length() == 2) {
                            System.out.print(times.get(j) + "시");
                        }
                        if (j < times.size() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.println();
                }
            }
            mygroup.teamtimeavailable.clear();
            mygroup.teamtimeavailable.addAll(timeavailable);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("숫자를 잘못 입력하셨습니다.");
        }
        return false;
    }

    public void adminpostdelete() {
        System.out.println("삭제할 게시물의 제목을 입력하세요.");
        System.out.println("-".repeat(50));
        if(mygroup.groupboard.size()==0){
            System.out.println("삭제할 게시물이 없습니다.");
            return;
        }
        for (Board b : mygroup.groupboard) {
            System.out.println("- " + b.head);
        }
        System.out.println("-".repeat(50));
        System.out.print("삭제> ");
        String input = scan.nextLine();
        System.out.println("-".repeat(50));
        Board del = null;
        for (Board b : mygroup.groupboard) {
            if (b.head.equals(input)) {
                del = b;
            }
        }
        if (del != null) {
            mygroup.groupboard.remove(del);
            System.out.println(input + " 이(가) 삭제되었습니다.");
        } else {
            System.out.println("삭제에 실패했습니다.");
        }
    }
    public void expelGroupMember(ArrayList<User> userlist) {
        try{
            if (mygroup.groupPeople.get(0).isEmpty()) {
                System.out.println("-".repeat(50));
                System.out.println("가입되어 있는 그룹원이 없습니다.");
            } else {
                System.out.println("-".repeat(50));
                for (String member : mygroup.groupPeople) {
                    System.out.println("- " + member);
                }
                System.out.println("-".repeat(50));
                System.out.print("그룹원 추방> ");
                String expelMemberName = scan.nextLine().trim();
                System.out.println("-".repeat(50));
                if(mygroup.groupPeople.contains(expelMemberName)){
                    System.out.print("정말 " + expelMemberName + "님을 추방하시겠습니까? (예/아니오)\n그룹원 추방>");
                    String confirm = scan.nextLine();
                    System.out.println("-".repeat(50));
                    if (confirm.equalsIgnoreCase("예")) {
                        if (mygroup.groupPeople.remove(expelMemberName)) {
                            System.out.println(expelMemberName + "님이 추방되었습니다.");
                            for (User ul : userlist) {
                                if (expelMemberName.equals(ul.userID)) {
                                    ul.user_group = "";
                                    break;
                                }
                            }
                            savegroup();
                        }
                    } else {
                        System.out.println("추방이 취소되었습니다.");
                    }
                }else {
                    System.out.println("해당 유저는 그룹에 존재하지 않습니다.");
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("-".repeat(50));
            System.out.println("가입되어 있는 그룹원이 없습니다.");
        }
    }

    public void deleteGroup(ArrayList<User> userlist, User login_user) {

        if (login_user.user_group == null) {
            System.out.println("삭제할 그룹이 없습니다.");
            return;
        }

        System.out.println("정말 " + this.mygroup.groupName + " 그룹을 삭제하시겠습니까?(예/아니요)");
        System.out.print("그룹 삭제>");
        String confirm = scan.nextLine();

        if (confirm.equals("예")) {
            Iterator<String> it = mygroup.groupPeople.iterator();
            while (it.hasNext()) {
                String us = it.next();
                for (User ul : userlist) {
                    if (us.equals(ul.userID)) {
                        ul.user_group = "";
                        it.remove();
                        break;
                    }
                }
            }

            login_user.user_group = null;
            grouplist.remove(mygroup);
            mygroup = null;
            System.out.println("그룹이 삭제되었습니다.");
        } else if (confirm.equals("아니요")) {
            System.out.println("그룹 삭제가 취소되었습니다.");
        } else {
            System.out.println("그룹 삭제가 취소되었습니다.");
        }

        savegroup();
    }
    boolean Check_groupHead(User login_user){
        for (study_group group : grouplist) {
            if (group.groupheadId.equals(login_user.userID)) {
                return true;
            }
        }
        return false;
    }
}