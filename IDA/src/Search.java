import java.util.List;

public interface Search {

    /**
     * @param from Starting board state
     * @param to Ending board state
     * @return A {@link List} with the moves needed to be performed;
     */
    List<Direction> solve(Board from, Board to);

}
