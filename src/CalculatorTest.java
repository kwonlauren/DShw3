import java.io.*;
import java.util.Stack;

public class CalculatorTest {
    public static void main(String args[]) {
        try{
            String in = "((-2+3)*7  )+  8";
            System.out.println(isValid(in));
        } catch (Exception e){
            System.out.println("ERROR");
        }

        /*
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String input = br.readLine();
                if (input.compareTo("q") == 0)
                    break;

                command(input);
            } catch (Exception e) {
                System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
            }
        }

         */
    }

    private static void command(String input) throws Exception{

    }

    private static String isValid(String infix) throws Exception{
        //valid하지 않으면 Exception, valid하면 공백제거, ~로 바꾼 결과 리턴.
        String result = "";
        boolean op_prev = true;
        boolean num_prev = false;
        Stack<Character> stk = new Stack<Character>();
        for(int i=0; i<infix.length(); i++){
            char ch = infix.charAt(i);
            if(Character.isDigit(ch)) {
                if(num_prev) throw new Exception();
                else{
                    num_prev = true;
                    op_prev = false;
                    result = result + ch;
                }
            }
            else if(isOperator(ch)){
                if(op_prev){
                    if(ch=='-') ch='~'; //unary -
                    else if(ch != '(') throw new Exception(); //연산자 두번 연속 나오므로 잘못된 입력
                }
                if(ch=='(') stk.push(ch);
                else if(ch==')'){
                    if(stk.isEmpty()) throw new Exception();//(가 없는데 )가 나왔으므로
                    else stk.pop();
                }
                op_prev = true;
                if(ch==')') op_prev = false;
                num_prev = false;
                result = result + ch;
            }else if(!(ch==' ') && !(ch=='\t')) throw new Exception();//숫자, 연산자, 공백 외 다른 문자

        }
        if (!stk.isEmpty()) throw new Exception();//stk에 (가 남아있으면

        return result;
    }

    private static String infix_to_postfix(String infix) {
        //올바른 입력만 들어온다고 가정, unary-는 ~로 변환되어서 들어옴.

        Stack<Character> stk = new Stack<>();
        long num = 0;
        boolean op_prev = false; //직전의 공백 아닌 문자가 operator였는지
        boolean neg = false; //unary-가 나오면 true가 돼서 다음 숫자를 음수로 처리
        String postfix = "";

        for (int i = 0; i < infix.length(); i++) {
            char ch = infix.charAt(i);

            if (Character.isDigit(ch)) {
                int char_num = ch - '0';
                if (num == 0) {//숫자의 첫 자리(MSD)
                    num = char_num;
                } else {//숫자의 첫자리가 아 경우
                    num = num * 10 + char_num;
                }
                if (i == infix.length() - 1 || !Character.isDigit(infix.charAt(i + 1))) {
                    //숫자의 마지막 자리(LSD) (수식의 마지막이거나, 다음 자리가 숫자가 아니면)
                    postfix = postfix.concat(Long.toString(num) + ' ');
                    num = 0;
                }
            }else if (isOperator(ch)) {
                if(ch=='^' || ch == '~') {
                    stk.push(ch);
                }else if (ch == '(') {
                    stk.push(ch);
                }else if (ch == ')') {
                    while (stk.peek() != '(') {
                        postfix = postfix.concat(String.valueOf(stk.pop()) + " ");
                    }
                    stk.pop(); //pop (
                }else{
                    while(!stk.isEmpty() && preced(stk.peek()) >= preced(ch)){
                        postfix = postfix.concat(String.valueOf(stk.pop())+" ");
                    }
                    stk.push(ch);
                }
            }//공백일 경우 아무것도 안하기
        }

        while(!stk.isEmpty()){
            postfix = postfix.concat(String.valueOf(stk.pop())+" ");
        }
        return postfix.strip();//마지막의 공백 하나 제거해주기
    }

        private static int preced(char ch){
            if (ch == '+' || ch == '-') return 1;
            else if (ch == '*' || ch == '/') return 2;
            else if (ch == '~') return 3;
            else if (ch == '^') return 4;
            else return 0;
        }

        private static boolean isOperator( char ch){
            return ch=='('||ch==')'||ch == '~' || ch == '^' || ch == '-' || ch == '+' || ch == '*' || ch == '/' || ch == '%';
        }

}
