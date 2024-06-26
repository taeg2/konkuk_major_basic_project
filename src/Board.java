import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    String ID;
    int grade;
    String head;
    String body;
    String comment;
    int category; //0이면 일반게시판, 1이면 질문게시판.
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String>  idlist = new ArrayList<>();
    int like = 0;
    String likeID = "^";
    String commentID;

    public Board(String ID, int grade, int category, String likeID, int like, String head, String body) {
        this.ID = ID;
        this.grade = grade;
        this.category = category;
        this.likeID = likeID;
        this.like = like;
        this.head = head;
        this.body = body;
    }
    public Board(String ID, int grade, int category, String likeID, int like, String head, String body, String commentID, String comment) {
        this.ID = ID;
        this.grade = grade;
        this.category = category;
        this.likeID = likeID;
        this.like = like;
        this.head = head;
        this.body = body;
        this.commentID = commentID;
        this.comment = comment;
    }
    public Board(String ID,String head, String body){
        this.ID = ID;
        this.head = head;
        this.body = body;
    }
    @Override
    public String toString() {
        String str = "제목> "+this.head+"\n내용> ";
        String[] bodys = this.body.split("/");
        for(String s : bodys){
            str += "\n";
            str += s;
        }

        str += "\n공감수>";
        str += like;
        if(this.comment == null) {
            return str;
        }


        list = new ArrayList<>(Arrays.asList(comment.split("/")));


        idlist = new ArrayList<>(Arrays.asList(commentID.split("/")));


        if(list.isEmpty()) {
            return str;
        }
        str += "\n댓글>";
        for(int i=0; i< list.size(); i++) {
            str += "\n" + idlist.get(i) + "-";
            String cmstr = list.get(i);
            String[] cmts = cmstr.split("@");
            for(String s : cmts){
                str += "\n";
                str += s;
            }

        }

        return str;
    }
}