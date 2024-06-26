import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Integer.parseInt;

public class study_group {
    String groupName;
    int groupMax;
    String groupheadId; // user class 로 변경 필요.
    ArrayList<String> groupPeople = new ArrayList<>();
    int groupPassword;
    ArrayList<Integer> teamtimeavailable = new ArrayList<>();
    ArrayList<Board> groupboard = new ArrayList<>();

    public study_group(String groupname, int max, int password) {
        super();
        this.groupName = groupname;
        this.groupMax = max;
        this.groupPassword = password;
    }

    public study_group(String groupname, int max, int password, String header) {
        super();
        this.groupName = groupname;
        this.groupMax = max;
        this.groupPassword = password;
        this.groupheadId = header;

    }

    public study_group(String groupname, int max, int password, String header, ArrayList<String> people, String[] time, ArrayList<Board> board) {
        super();
        this.groupName = groupname;
        this.groupMax = max;
        this.groupPassword = password;
        this.groupheadId = header;
        this.groupPeople = people;
        if(time!=null){
            for (String str : time) {
                if (!str.isEmpty())
                    this.teamtimeavailable.add(parseInt(str));
            }
        }
        this.groupboard.addAll(board);
    }

    @Override
    public String toString() {
        String str = "";
        str += "그룹명 : " + this.groupName + "\n최대가입인원 : " + this.groupMax + "\n그룹장 아이디 : " + this.groupheadId +"\n그룹원 : "+this.groupPeople + "\n";
        str += "-".repeat(30);
        return str;
    }
}
