import java.util.Scanner;

public class Quad {
    static void handle(Scanner s) {
        System.out.println("Provide a b c");
        Double a, b, c;
        a = s.nextDouble();
        b = s.nextDouble();
        c = s.nextDouble();

        if (a.equals(0.0)) {
            System.out.println("a can not be zero");
            return;
        }

        Double delta = (b * b) - (4 * a * c);

        if (delta > 0) {
            Double x1 = (-b - Math.sqrt(delta)) / (2 * a);
            Double x2 = (-b + Math.sqrt(delta)) / (2 * a);
            System.out.printf("x1: %f, x2: %f", x1, x2);
        } else if (delta < 0) {
            System.out.println("No real roots");
        } else {
            Double x = (-b) / (2 * a);
            System.out.printf("x: %f", x);
        }
    }
}
