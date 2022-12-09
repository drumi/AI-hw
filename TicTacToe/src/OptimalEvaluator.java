import java.util.List;

public class OptimalEvaluator {

    private static final Player MAXIMIZER = Player.X;
    private static final Player MINIMIZER = Player.O;

    private Board bestMove;
    private int bestMoveScore;

    private int startingTurn;

    public Board getBestMove(Board board) {
        startingTurn = board.getTurn();
        bestMoveScore = board.getCurrentPlayer() == MAXIMIZER ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        eval(board, Integer.MIN_VALUE, Integer.MAX_VALUE, startingTurn);

        return bestMove;
    }

    private int eval(Board board, int alpha, int beta, int depth) {

        Player winner = board.getWinnerIfExsists();
        Player current = board.getCurrentPlayer();

        // Evaluate leaf
        if (winner != null) {
            return switch (winner) {
                case X -> 11 - board.getTurn();
                case O -> - (11 - board.getTurn());
            };
        }

        // Draw
        if (depth == 10) {
            return 0;
        }

        List<Board> children = board.generateChildren();

        if (current == MAXIMIZER) {
            for (Board child : children) {
                int eval = eval(child, alpha, beta, depth + 1);
                alpha = Math.max(alpha, eval);

                if (depth == startingTurn && eval > bestMoveScore) {
                    bestMove = child;
                    bestMoveScore = eval;
                }

                if (alpha >= beta) {
                    break;
                }
            }
            return alpha;
        } else {
            for (Board child : children) {
                int eval = eval(child, alpha, beta, depth + 1);
                beta = Math.min(beta, eval);

                if (depth == startingTurn && eval < bestMoveScore) {
                    bestMove = child;
                    bestMoveScore = eval;
                }

                if (alpha >= beta) {
                    break;
                }
            }

            return beta;
        }
    }
}
