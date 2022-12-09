import exceptions.GameOverException;
import exceptions.SpaceAlreadyOccupiedException;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private int turn;
    private Player[] board;
    private WhoIsNext whoIsNext;

    private Player winner;

    public Board(WhoIsNext whoIsNext) {
        turn = 1;
        board = new Player[9];
        winner = null;
        this.whoIsNext = whoIsNext;
    }

    public Board(Board b) {
        this(b.whoIsNext);
        this.turn = b.turn;
        this.winner = b.winner;
        copyArray(b.board, this.board);
    }

    public Player getPlayerAt(int x, int y) {
        return board[x + y * 3];
    }

    private void setPlayerAt(int x, int y, Player player) {
        board[x + y * 3] = player;
    }

    public int getTurn() {
        return turn;
    }

    public Player getCurrentPlayer() {
        return whoIsNext.atTurn(turn);
    }

    public Board play(int x, int y) {
        Player current = getCurrentPlayer();
        Player playerAtSpace = getPlayerAt(x, y);

        if (playerAtSpace != null) {
            throw new SpaceAlreadyOccupiedException();
        }

        if (winner != null) {
            throw new GameOverException();
        }


        Board ret = new Board(this);
        ret.turn += 1;
        ret.setPlayerAt(x, y, current);
        ret.winner = ret.calculateWinner();

        return ret;
    }

    public Player getWinnerIfExsists() {
        if (winner == null)
            winner = calculateWinner();

        return winner;
    }

    public boolean isGameOver() {
        if (turn == 10)
            return true;

        return getWinnerIfExsists() != null;
    }

    private Player calculateWinner() {
        // Check rows
        for (int i = 0; i < 9; i += 3) {
            if (board[i] != null && board[i] == board[i + 1] && board[i + 1] == board[i + 2])
                return board[i];
        }

        // Check cols
        for (int i = 0; i < 3; i++) {
            if (board[i] != null && board[i] == board[i + 3] && board[i + 3] == board[i + 6])
                return board[i];
        }

        // Check main diagonal
        if (board[0] != null && board[0] == board[4] && board[4] == board[8])
            return board[0];

        // Check second diagonal
        if (board[2] != null && board[2] == board[4] && board[4] == board[6])
            return board[2];

        return null;
    }

    private void copyArray(Object[] src, Object[] dest) {
        System.arraycopy(src, 0, dest, 0, dest.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Player player = getPlayerAt(j, i);

                if (player != null)
                    sb.append(String.format("|%s", player));
                else {
                    sb.append("| ");
                }
            }

            sb.append("|\n");
        }

        return sb.toString();
    }

    public List<Board> generateChildren() {
        if (winner != null)
            throw new GameOverException();

        List<Board> children = new ArrayList<>();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Player player = this.getPlayerAt(x, y);

                if (player == null) {
                    children.add(this.play(x, y));
                }
            }
        }

        return children;
    }

}
