import java.util.Scanner;

public class Main {

    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        int n = input.nextInt();
        MinQueens minQueens = new RequirementsChanged(n);

        var start = System.nanoTime();
        minQueens.solve();
        var end = System.nanoTime();

        // minQueens.printBoard();
        System.out.printf("Time ms: %d\n", (end - start) / 1_000_000);
    }

}
