import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final WhoIsNext NEXT = turn -> turn % 2 == 1 ? Player.X : Player.O;

    private static final OptimalEvaluator evaluator = new OptimalEvaluator();

    public static void main(String[] args) {

        Board gameBoard = new Board(NEXT);

        // Set up game
        System.out.println("Computer(1) or Player(2) first: ");
        int choice = scanner.nextInt();

        // if PC is first
        if (choice == 1)
            gameBoard = computerMove(gameBoard);
        else
            System.out.println(gameBoard);

        // Game loop
        while (true) {
            if (gameBoard.isGameOver())
                break;

            gameBoard = playerMove(gameBoard);

            if (gameBoard.isGameOver())
                break;
            gameBoard = computerMove(gameBoard);
        }

        // Game end output
        if (gameBoard.getWinnerIfExsists() == null) {
            System.out.println("Draw");
        } else {
            System.out.printf("%s wins! %n", gameBoard.getWinnerIfExsists());
        }
    }

    private static Board playerMove(Board gameBoard) {
        System.out.println("Enter move (x, y) x, y in [1,3]: ");

        int x = scanner.nextInt();
        int y = scanner.nextInt();

        gameBoard = gameBoard.play(x - 1, y - 1);

        System.out.println("You play: ");
        System.out.println(gameBoard);

        return gameBoard;
    }

    private static Board computerMove(Board gameBoard) {
        gameBoard = evaluator.getBestMove(gameBoard);

        System.out.println("Computer plays: ");
        System.out.println(gameBoard);

        return gameBoard;
    }
}
