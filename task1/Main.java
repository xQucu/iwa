import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        System.out.println("Type 1 to solve quadratic, type 2 to get nth fibonnaci sequence number.");
        int method = s.nextInt();
        if (method == 1) {
            Quad.handle(s);
        } else if (method == 2) {
            Fib.handle(s);
        } else {
            System.out.println("Incorrect input");
        }
        s.close();

    }
}
