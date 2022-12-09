import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

public class IDA implements Search {

    private static final Direction[] directions = Direction.values();
    private final Heuristic heuristic;
    private final Supplier<Deque<Node<Board>>> dequeSupplier;

    public IDA(Heuristic heuristic, Supplier<Deque<Node<Board>>> dequeSupplier) {
        this.heuristic = heuristic;
        this.dequeSupplier = dequeSupplier;
    }

    @Override
    public List<Direction> solve(Board from, Board to) {
        return solve(from, to, heuristic.evaluate(from, to));
    }

    private List<Direction> solve(Board from, Board to, int maxCost) {
        Deque<Node<Board>> stack = dequeSupplier.get();

        // stores minimal value that exceeds maxCost to use for next call
        int minimalExceedingValue = Integer.MAX_VALUE;

        // adding root node
        stack.addFirst(new Node<>(null, from, null, 0));

        while (!stack.isEmpty()) {
            Node<Board> parent = stack.pollFirst();
            Board board = parent.value();

            for (Direction dir : directions) {
                if (parent.moveUsed() == dir.opposite())
                    continue;

                Board child = board.move(dir);

                if (child == null)
                    continue;

                int heuristicValue = heuristic.evaluate(child, to);
                int estimatedCost = parent.currentCost() + heuristicValue + 1;

                if (heuristicValue == 0) {
                    return generateMoveList(new Node<>(parent, child, dir, parent.currentCost() + 1));
                } else if (estimatedCost <= maxCost) {
                    stack.addFirst(new Node<>(parent, child, dir, parent.currentCost() + 1));
                } else if (estimatedCost < minimalExceedingValue) {
                    minimalExceedingValue = estimatedCost;
                }
            }
        }

        // No solution found. Repeating with bigger bound
        return solve(from, to, minimalExceedingValue);
    }

    private List<Direction> generateMoveList(Node<Board> endNode) {
        Deque<Direction> moves = new ArrayDeque<>();

        Node<Board> current = endNode;

        while (current.moveUsed() != null) {
            moves.addFirst(current.moveUsed());
            current = current.parent();
        }

        return new ArrayList<>(moves);
    }

}
