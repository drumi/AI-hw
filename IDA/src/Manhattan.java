public class Manhattan implements Heuristic {

    @Override
    public int evaluate(Board b1, Board b2) {
        int accumulator = 0;
        int length = b1.getLength();

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                int tile = b1.getTile(x, y);
                Position goal = b2.positionOf(tile);

                accumulator += Math.abs(x - goal.x());
                accumulator += Math.abs(y - goal.y());
            }
        }

        return accumulator;
    }

}
