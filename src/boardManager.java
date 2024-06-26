import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class boardManager {
    ArrayList<Board> Boards = new ArrayList<>();
    Scanner scan = new Scanner(System.in);
    int BoardNum = 0;

    public void addBoard(Board board) {
        this.Boards.add(board);
    }

    public void BoardFileRead(String filename) {
        BoardNum = CountingBoardNum(filename);
        try (Scanner file = new Scanner(new File(filename))){
            while(file.hasNextLine()){
                for(int i = 0; i < BoardNum; i++){
                    String totaldata = file.nextLine();
                    String[] boarddata = totaldata.split("\t");
                    String[][] Data = new String[BoardNum][boarddata.length];
                    if (boarddata.length == 7) {
                        for(int j = 0; j < 7; j++){
                            Data[i][j] = boarddata[j];
                        }
                        addBoard(new Board(Data[i][0], Integer.parseInt(Data[i][1]), Integer.parseInt(Data[i][2]), Data[i][3], Integer.parseInt(Data[i][4]), Data[i][5], Data[i][6]));
                    }else if(boarddata.length == 9){
                        for(int j = 0; j < 9; j++){
                            Data[i][j] = boarddata[j];
                        }
                        addBoard(new Board(Data[i][0], Integer.parseInt(Data[i][1]), Integer.parseInt(Data[i][2]), Data[i][3], Integer.parseInt(Data[i][4]), Data[i][5], Data[i][6], Data[i][7], Data[i][8]));
                    }else{
                        System.out.println("board.txt파일에 오류가 존재합니다.");
                    }
                }
            }

        }catch (FileNotFoundException e) {
            System.out.println("파일을 읽지 못했습니다.");
        }
    }

    public void addboard(String title, ArrayList<String> mtext, User user, int i) {
        try {
            Stack<Board> show = new Stack<>();
            ArrayList<Board> list = new ArrayList<>();
            String cmtext = "";
            String lID = "";
            for(String  str : mtext){
                if(str.contains("/")){
                    System.out.println("본문에 '/'가 포함되어 있으면 안 됩니다.");
                    return;
                }
            }
            for (String str : mtext) {
                cmtext = cmtext.concat(str);
                cmtext = cmtext.concat("/");

            }
            if (title.length() > 15) {
                System.out.println("제목은 15자 이하여야 합니다.");
                return;
            }
            if (cmtext.length() > 500) {
                System.out.println("본문은 500자 이하여야 합니다.");
                return;
            }
            for(Board b : Boards) {
                if(b.category == i) {
                    show.push(b);
                }
            }
            if(show.size()<10) {
                for(Board b : show) {
                    list.add(b);
                }
            }else {
                for(int k=0;k<10;k++) {
                    list.add(show.pop());

                }
            }
            for(Board b : list){
                if(b.head.equals(title)){
                    System.out.println("중복되는 제목은 사용할 수 없습니다.");
                    return;
                }
            }

            addBoard(new Board(user.userID, user.userGrade, i, lID, 0, title, cmtext));
            System.out.println("저장이 완료되었습니다.");
            saveboard();
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("오류가 있습니다. 메인 프롬프트로 돌아갑니다.");
        }

    }

    private int CountingBoardNum(String filename) {
        try (Scanner file = new Scanner(new File(filename))) {
            while (file.hasNextLine()) {
                BoardNum++;
                file.nextLine();
            }
            return BoardNum;
        } catch (FileNotFoundException e) {
            System.out.println("파일을 읽지 못했습니다.");
        }
        return BoardNum;
    }

    public void showboard(int i, User user) {
        Stack<Board> show = new Stack<>();
        for (Board b : Boards) {
            if (b.category == i) {
                show.push(b);
            }
        }
        if (show.isEmpty()) {
            System.out.println("게시물이 없습니다.");
            return;
        }
        if (show.size() < 10) {
            for (Board b : show) {
                System.out.println(" - "+b.head);
            }
        } else {
            for (int k = 0; k < 10; k++) {
                Board b = show.pop();
                System.out.println(" - "+b.head);
            }
        }

        System.out.print("조회할 게시물을 선택하세요 :");
        String s = scan.nextLine();
        search(i, s, user);
    }

    public int showboard(int i, int j) { // 관리자용
        Stack<Board> show = new Stack<>();
        for (Board b : Boards) {
            if (b.category == i) {
                show.push(b);
            }
        }
        if (show.isEmpty()) {
            if(j == 0) { // 게시글 삭제
                System.out.println("삭제할 게시글이 없습니다.");
                return 1;
            } else {
                System.out.println("댓글을 삭제할 게시글이 없습니다.");
                return 1;
            }
        }
        if (show.size() < 10) {
            if(j == 0){
                System.out.println("삭제할 게시글을 선택해주세요.");
                for (Board b : show) {
                    System.out.println(" - "+b.head);
                }
            } else{
                System.out.println("댓글을 삭제할 게시글을 선택해주세요.");
                for (Board b : show) {
                    System.out.println(" - "+b.head);
                }
            }
        } else {
            if(j == 0){
                System.out.println("삭제할 게시글을 선택해주세요.");
                for (int k = 0; k < 10; k++) {
                    Board b = show.pop();
                    System.out.println(" - "+b.head);
                }
            } else{
                System.out.println("댓글을 삭제할 게시글을 선택해주세요.");
                for (int k = 0; k < 10; k++) {
                    Board b = show.pop();
                    System.out.println(" - "+b.head);
                }
            }
        }

        return 0;
    }

    private void search(int i, String s, User user) {
        Stack<Board> show = new Stack<>();
        ArrayList<Board> list = new ArrayList<>();
        Board selectedboard = null;
        System.out.println("-".repeat(50));
        for(Board b : Boards) {
            if(b.category == i) {
                show.push(b);
            }
        }
        if(show.size()<10) {
            for(Board b : show) {
                list.add(b);
            }
        }else {
            for(int k=0;k<10;k++) {
                list.add(show.pop());
            }
        }
        for(Board b : list) {
            if(b.head.equals(s)){
                System.out.println(b);
                selectedboard = b;
                break;
            }
        }
        if(selectedboard == null) {
            System.out.println("입력하신 게시물이 없습니다.");
            return;
        }
        System.out.println("-".repeat(50));
        System.out.println("-댓글달기");
        System.out.println("-공감하기");
        System.out.println("-조회종료");
        System.out.print("SSH>");
        String st = scan.nextLine();

        int div;
        switch (st){
            case "댓글 달기","댓글달기","댓글" -> div = 1;
            case "공감하기", "공감 하기", "공감" -> div = 2;
            case "조회종료", "조회 종료", "종료" -> div = 0;
            default -> {
                System.out.println("잘못 입력하셨습니다. 종료합니다.");
                return;
            }
        }

        if(div == 0){
            return;
        }
        if (div == 2) {
            if(selectedboard.likeID.contains(user.userID)){
                System.out.println("이미 공감한 게시물 입니다.");
                return;
            }
            selectedboard.like++;
            selectedboard.likeID += user.userID;
            System.out.println(selectedboard);
            saveboard();
            return;

        }

        if(div == 1) {
            if (i == 1 && user.userGrade < selectedboard.grade) {
                System.out.println("댓글 입력 권한이 없습니다.");
                return;
            }
            System.out.println("댓글을 입력하세요 ('종료'를 입력하면 저장됩니다.)");
            System.out.println("댓글 입력>");
            ArrayList<String> mtext = new ArrayList<>();
            while (true) {
                String input = scan.nextLine();
                if (input.equals("종료")) {
                    break;
                }
                mtext.add(input);
            }
            cmt(mtext, user, selectedboard);
        }

    }

    public void search(int i, String s) { // 관리자용
        Stack<Board> show = new Stack<>();
        ArrayList<Board> list = new ArrayList<>();
        Board selectedboard = null;

        for(Board b : Boards) {
            if(b.category == i) {
                show.push(b);
            }
        }
        if(show.size()<10) {
            for(Board b : show) {
                list.add(b);
            }
        }else {
            for(int k=0;k<10;k++) {
                list.add(show.pop());
            }
        }
        for(Board b : list) {
            if(b.head.equals(s)){
                System.out.println(b);
                selectedboard = b;

                if(b.list.isEmpty() || b.idlist.isEmpty()){
                    System.out.println("삭제할 댓글이 없습니다.");
                    return;
                }
                break;
            }
        }
        if(selectedboard == null) {
            System.out.println("존재하지 않는 게시글입니다. 다시 시도하세요.");
            return;
        }

        try{
            System.out.println("삭제할 댓글의 순서를 입력해주세요.");
            System.out.print("SSH>");
            int num = Integer.parseInt(scan.nextLine());
            DeleteComment(num, selectedboard);
        } catch (NumberFormatException e){
            System.out.println("존재하지 않는 댓글입니다. 다시 시도하세요.");
        }
    }

    public void DeleteComment(int num, Board b){ // 관리자용
        try {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(b.comment.split("/")));
            ArrayList<String> idlist = new ArrayList<>(Arrays.asList(b.commentID.split("/")));
            list.remove(num - 1);
            idlist.remove(num - 1);
            b.comment = String.join("/", list);
            b.commentID = String.join("/", idlist);

//            if(list.size() == 1 || idlist.size() == 1){
//
//            }

            System.out.println("댓글이 삭제되었습니다.");
        }catch (IndexOutOfBoundsException e){
            System.out.println("해당하는 순서의 댓글이 없습니다.");
        }
    }

    private void cmt(ArrayList<String> ctext, User user, Board b) {
        String cmtext = "";
        boolean bl = false;
        for(String  str : ctext){
            if(str.contains("/")||str.contains("@")||str.contains("\t")){
                System.out.println("댓글에는 (/,@,tab) 이 포함되어 있으면 안 됩니다.");
                return;

            }
            if (!str.trim().isEmpty()) {
                bl = true;
                break;
            }


        }
        if(!bl) {
            System.out.println("댓글에 문자를 입력해야 합니다.");
            return;
        }
        for (String str : ctext) {
            cmtext = cmtext.concat(str);
            cmtext = cmtext.concat("@");

        }

        if(b.comment == null) {
            b.commentID = (user.userID + "/");
            b.comment = (cmtext + "/");
        } else {
            b.commentID += (user.userID + "/");
            b.comment += (cmtext + "/");
        }
        System.out.println("댓글 저장이 완료되었습니다.");
        saveboard();

    }

    public void myboard(int i, User login_user) {
        ArrayList<Board> show= new ArrayList<>();
        for(Board b : Boards) {
            if(b.ID.equals(login_user.userID)&& b.category == i) {
                show.add(b);
            }
        }

        if(show.isEmpty()) {
            System.out.println("삭제할 게시물이 없습니다.");
        }else {
            for(Board b : show) {
                System.out.println(" - "+b.head);
            }
            System.out.print("삭제할 게시물을 선택하세요 :");
            String s = scan.nextLine();
            delete(i, s, login_user);
            saveboard();
        }
    }

    public void delete(int i, String s, User login_user) {

        for(Board b : Boards) {
            if(b.ID.equals(login_user.userID)&&b.category==i&&b.head.equals(s)) {
                Boards.remove(b);
                System.out.println("게시물이 삭제되었습니다.");
                return;
            }
        }

        System.out.println("입력하신 게시물이 없습니다.");
    }

    public void delete(int i, String s) { // 관리자용

        for(Board b : Boards) {
            if(b.category==i && b.head.equals(s)) {
                Boards.remove(b);
                System.out.println("게시글이 삭제되었습니다.");
                return;
            }
        }

        System.out.println("입력하신 게시글이 없습니다.");
    }

    void saveboard() {
        String filename = "board.txt";
        try {
            FileWriter writer = new FileWriter(filename, false);

            for (Board b : Boards) {
                if(b.comment == null)
                    writer.write(b.ID + "\t" + b.grade + "\t" + b.category + "\t" + b.likeID+ "\t" + b.like + "\t" + b.head + "\t" + b.body +"\n");
                else
                    writer.write(b.ID + "\t" + b.grade + "\t" + b.category + "\t" + b.likeID+ "\t" + b.like + "\t" + b.head + "\t" + b.body + "\t" + b.commentID + "\t" + b.comment + "\n");
            }


            writer.close(); // 파일 닫기
        } catch (IOException e) {
            System.out.println("파일에 쓰기 오류가 발생했습니다: " + e.getMessage());
        }
    }

}