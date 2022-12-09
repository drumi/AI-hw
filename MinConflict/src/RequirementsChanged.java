import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Violates interface contract
// Needs random restart, can get trapped in local maximum
// Pick random queen and MAXIMIZE conflicts
public class RequirementsChanged implements MinQueens {

    private static final Random random = ThreadLocalRandom.current();

    private final int[] queenVerticalPosition;
    private final int[] queensPerRow;
    private final int[] queensPerMainDiagonal;   // x + y
    private final int[] queensPerSecondDiagonal; // x - y + (n - 1)

    private final int n;

    public RequirementsChanged(int n) {
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

    public void solve() {
        while (!isSolvedNew()) {
            int randX = random.nextInt(n);
            int newY = getYWithMaxConflicts(randX);
            swap(randX, newY);
            printBoard();
        }

    }

    private int getYWithMinimalConflicts(int x) {
        int minConflict = Integer.MAX_VALUE;
        int minY = -1;
        final int oldY = queenVerticalPosition[x];

        for (int y = 0; y < n; y++) {
            int curConflicts = queensPerRow[y] +
                queensPerMainDiagonal[x + y] +
                queensPerSecondDiagonal[x - y + (n - 1)];

            if (oldY == y)
                curConflicts -= 3;

            if (curConflicts < minConflict || minConflict == curConflicts && random.nextBoolean()) {
                minConflict = curConflicts;
                minY = y;
            }
        }

        return minY;
    }

    private boolean isSolved() {
        for (int x = 0; x < n; x++)
            if (queensPerRow[x] > 1)
                return false;

        for (int d = 0; d < 2 * n - 1; d++)
            if (queensPerSecondDiagonal[d] > 1 || queensPerMainDiagonal[d] > 1)
                return false;

        return true;
    }

    private int getYWithMaxConflicts(int x) {
        int maxConflict = Integer.MIN_VALUE;
        int maxY = -1;

        final int oldY = queenVerticalPosition[x];

        for (int y = 0; y < n; y++) {
            int curConflicts = queensPerRow[y] +
                queensPerMainDiagonal[x + y] +
                queensPerSecondDiagonal[x - y + (n - 1)];

            if (oldY == y)
                curConflicts -= 3;

            if (curConflicts > maxConflict || maxConflict == curConflicts && random.nextBoolean()) {
                maxConflict = curConflicts;
                maxY = y;
            }
        }

        return maxY;
    }

    private boolean isSolvedNew() {
        int acc = 0;
        for (int x = 0; x < n; x++)
            if (queensPerRow[x] > 1)
                acc += (queensPerRow[x] - 1) * queensPerRow[x];

        for (int d = 0; d < 2 * n - 1; d++) {
            if (queensPerSecondDiagonal[d] > 1)
                acc += (queensPerSecondDiagonal[d] - 1) * queensPerSecondDiagonal[d];
            if (queensPerMainDiagonal[d] > 1)
                acc += (queensPerMainDiagonal[d] - 1) * queensPerMainDiagonal[d];
        }


        return acc == (n - 1) * n;
    }

    private void swap(int x, int newY) {
        int oldY = queenVerticalPosition[x];

        if (oldY == newY)
            return;

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