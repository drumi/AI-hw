import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class Main {

    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        // Wire up dependencies
        Heuristic heuristic = new Manhattan();
        Supplier<Deque<Node<Board>>> supplier = ArrayDeque::new;
        Search ida = new IDA(heuristic, supplier);

        // Input board size and index of zero
        int n = input.nextInt() + 1;
        int indexOfZero = input.nextInt();
        int rowLength = Math.round((float) Math.sqrt(n));

        // Input initial state of the board
        int[][] initialTiles = new int[rowLength][rowLength];
        for (int y = 0; y < rowLength; y++) {
            for (int x = 0; x < rowLength; x++) {
                initialTiles[y][x] = input.nextInt();
            }
        }

        // Generate goal state
        int[][] goalTiles = new int[rowLength][rowLength];
        int idx = 0;
        int tile = 1;
        for (int y = 0; y < rowLength; y++) {
            for (int x = 0; x < rowLength; x++) {
                if (idx == indexOfZero)
                    goalTiles[y][x] = 0;
                else
                    goalTiles[y][x] = tile++;
            }
        }

        if (indexOfZero == -1)
            goalTiles[rowLength-1][rowLength-1] = 0;

        Board initial = new Board(initialTiles);
        Board goal = new Board(goalTiles);

        var start = System.nanoTime();
        List<Direction> moves = ida.solve(initial, goal);
        var end = System.nanoTime();

        var elapsedMs = (end - start) / 1_000_000;

        System.out.println(moves.size());

        for (var move : moves)
            System.out.println(move);

        System.out.printf("Time elapsed ms: %d", elapsedMs);
    }

}
