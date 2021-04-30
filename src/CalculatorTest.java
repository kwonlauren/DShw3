import java.util.Stack;
import java.io.*;

public class CalculatorTest {
    public static void main(String args[]) {
        /*
        try{
            String in = "((-2+3)*7  )+  8";
            System.out.println(isValid(in));
        } catch (Exception e){
            System.out.println("ERROR");
        }
       */
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

    }

    public static void command(String input) throws Exception{
        try{
            input = isValid(input);
            String postfix = infix_to_postfix(input);
            long result = postfixEval(postfix);
            System.out.println(postfix);
            System.out.println(result);

        } catch(Exception e){
            System.out.println("ERROR");
        }
    }

    private static String isValid(String infix) throws Exception{
        //valid하지 않으면 Exception, valid하면 공백제거, ~로 바꾼 결과 리턴.
        String result = "";
        boolean op_prev = true;
        boolean num_prev = false;//직전 자리가 숫자였는지
        boolean num_blank_prev = false;//숫자 공백이 나온 경우 --> 다음에 숫자 나오면 에러
        Stack<Character> stk = new Stack<Character>();
        for(int i=0; i<infix.length(); i++){
            char ch = infix.charAt(i);
            if(Character.isDigit(ch)) {
                if(num_blank_prev) throw new Exception();
                else{
                    num_prev = true;
                    op_prev = false;
                    result = result + ch;
                }
            }else if(ch=='(' || ch==')'){//num_prev, op_prev 변경하지 않음.
                if(ch=='(') {
                    stk.push(ch);
                    op_prev=true;
                }
                else{
                    if(op_prev) throw new Exception();
                    if(stk.isEmpty()) throw new Exception();//(가 없는데 )가 나왔으므로
                    else stk.pop();
                }
                if(num_prev) num_blank_prev = true;
                result = result + ch;
            }else if(ch==' '|| ch=='\t'){
                if(num_prev) num_blank_prev = true;
            }else if(isOperator(ch)){//()외의 연산자인 경우
                if(op_prev){
                    if(ch=='-') ch='~';
                    else throw new Exception();
                }
                else{
                    op_prev = true;
                    num_prev = false;
                    num_blank_prev = false;
                }
                result = result + ch;
            }else throw new Exception();//숫자, 연산자, 공백 외 다른 문자
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
                    while(!stk.isEmpty() && precede(stk.peek()) >= precede(ch)){
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

    private static int precede(char ch){
        if (ch == '+' || ch == '-') return 1;
        else if (ch == '*' || ch == '/'||ch=='%') return 2;
        else if (ch == '~') return 3;
        else if (ch == '^') return 4;
        else return 0;
    }

    private static boolean isOperator( char ch){
        return ch=='('||ch==')'||ch == '~' || ch == '^' || ch == '-' || ch == '+' || ch == '*' || ch == '/' || ch == '%';
    }

    private static long postfixEval(String postfix) throws Exception{
        //교재의 PostfixEval을 참고함.
        Stack<Long> stk = new Stack<>();
        boolean num_prev = false;
        long A, B;
        for(int i=0; i<postfix.length(); i++){
            char ch = postfix.charAt(i);
            if(Character.isDigit(ch)){
                if(num_prev){
                    long tmp = stk.pop();
                    tmp = 10*tmp+(ch-'0');
                    stk.push(tmp);
                }else stk.push((long) (ch-'0'));
                num_prev = true;
            }else if(isOperator(ch)){
                if(ch=='~'){
                    long tmp = stk.pop();
                    stk.push(-tmp);
                }else{
                    A = stk.pop();
                    B = stk.pop();
                    long val = operation(A,B,ch);
                    stk.push(val);
                }
                num_prev=false;
            }else num_prev=false; //ch가 공백
        }
       return stk.pop();
    }

    private static long operation(long a, long b, char ch) throws Exception{
        long val=0;
        switch(ch){
            case '+': val = b+a; break;
            case '-': val = b-a; break;
            case '*': val = b*a; break;
            case '/': {
                val = b/a;
                if(a==0) throw new Exception();
                break;
            }
            case '%': {
                if(a==0) throw new Exception();
                val = b % a;
                break;
            }
            case '^': {
                if(b==0 && a<0) throw new Exception();
                val = (long)Math.pow(b,a);
                break;
            }
        }
        return val;
    }
}
