import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Performs worse
// Moving random maximum queen to random location
public class MinQueensMaxFirst implements MinQueens {

    private static final int NO_CONFLICT_X = -1;
    private static final Random random = ThreadLocalRandom.current();

    private final int[] queenVerticalPosition;
    private final int[] queensPerRow;
    private final int[] queensPerMainDiagonal;   // x + y
    private final int[] queensPerSecondDiagonal; // x - y + (n - 1)

    private final int n;

    public MinQueensMaxFirst(int n) {
        this.n = n;
        queenVerticalPosition = new int[n];
        queensPerRow = new int[n];
        queensPerMainDiagonal = new int[2 * n - 1];
        queensPerSecondDiagonal = new int[2 * n - 1];

        initQueens();
    }

    private void initQueens() {
        for (int x = 0; x < n; x++) {
            int y = random.nextInt(n);
            queenVerticalPosition[x] = y;
            queensPerRow[y]++;
            queensPerSecondDiagonal[(x - y) + (n - 1)]++;
            queensPerMainDiagonal[x + y]++;
        }
    }

    private int calculateConflictsForQueen(int queenX) {
        int y = queenVerticalPosition[queenX];
        return queensPerRow[y] - 1 +
            queensPerMainDiagonal[queenX + y] - 1 +
            queensPerSecondDiagonal[queenX - y + n - 1] - 1;
    }

    private int getRandomQueenXWithMaximumConflicts() {
        int maxConflicts = 0;
        int maxX = NO_CONFLICT_X;

        for (int x = 0; x < n; x++) {
            int conflicts = calculateConflictsForQueen(x);

            if (conflicts > maxConflicts || conflicts == maxConflicts && conflicts > 0 && random.nextBoolean()) {
                maxConflicts = conflicts;
                maxX = x;
            }
        }

        return maxX;
    }

    public void solve() {
        int maxX = getRandomQueenXWithMaximumConflicts();

        while (maxX != NO_CONFLICT_X) {
            int newY = random.nextInt(n);
            swap(maxX, newY);
            maxX = getRandomQueenXWithMaximumConflicts();
        }

    }

    private void swap(int x, int newY) {
        int oldY = queenVerticalPosition[x];
        queenVerticalPosition[x] = newY;

        queensPerRow[oldY]--;
        queensPerSecondDiagonal[x - oldY + n - 1]--;
        queensPerMainDiagonal[x + oldY]--;

        queensPerRow[newY]++;
        queensPerSecondDiagonal[x - newY + n - 1]++;
        queensPerMainDiagonal[x + newY]++;
    }

    public void printBoard() {
        boolean[][] queens = new boolean[n][n];

        for (int x = 0; x < n; x++) {
            int y = queenVerticalPosition[x];
            queens[y][x] = true;
        }

        System.out.print("\n");
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (queens[y][x]) {
                    System.out.print("Q ");
                } else {
                    System.out.print("* ");
                }
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }
}
