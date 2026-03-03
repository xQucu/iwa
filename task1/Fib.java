import java.math.BigInteger;
import java.util.Scanner;

public class Fib {
    static void handle(Scanner s) {
        System.out.println("Which numer from fibonacci sequence to print?");

        try {
            int n = s.nextInt();

            if (n < 0) {
                System.out.println("Negative number is not allowed");
            } else if (n == 0) {
                System.out.println("The number is: 0");
            } else if (n == 1) {
                System.out.println("The number is: 1");
            } else {
                BigInteger a = new BigInteger("0");
                BigInteger b = new BigInteger("1");
                BigInteger c = new BigInteger("0");

                for (int i = 2; i <= n; i++) {
                    c = a.add(b);
                    a = b;
                    b = c;
                }

                System.out.printf("The number is: %s", c.toString());
            }
        } catch (Exception e) {
            System.out.println("Invalid input");
        }
    }
}
