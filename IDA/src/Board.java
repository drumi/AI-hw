public class Board {

    private final Position[] cache;
    private final int[][] tiles;
    private final int rowLength;

    public Board(int[][] tiles) {
        rowLength = tiles.length;
        this.tiles = copy(tiles, rowLength);
        cache = new Position[rowLength * rowLength];
        
        populateCache();
    }

    private Board(Board b) {
        rowLength = b.tiles.length;
        tiles = copy(b.tiles, rowLength);
        cache = new Position[rowLength * rowLength];

        System.arraycopy(b.cache, 0, this.cache, 0, rowLength * rowLength);
    }

    public final int getTile(int x, int y) {
        return tiles[y][x];
    }

    public final Position positionOf(int tile) {
        return cache[tile];
    }


    /**
     * @param dir The direction in which to generate the new board
     * @return New {@link Board} if move is possible otherwise returns <i>null<i/>
     */
    public Board move(Direction dir) {
        return switch (dir) {
            case UP -> moveUp();
            case RIGHT -> moveRight();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
        };
    }

    private Board moveDown() {
        Position zeroTilePosition = cache[0];

        if (zeroTilePosition.y() == 0)
            return null;

        Board toMove = new Board(this);
        int tileToSwapWith = tiles[zeroTilePosition.y() - 1][zeroTilePosition.x()];

        toMove.swap(0, tileToSwapWith);

        return toMove;
    }

    private Board moveUp() {
        Position zeroTilePosition = cache[0];

        if (zeroTilePosition.y() == rowLength - 1)
            return null;

        Board toMove = new Board(this);
        int tileToSwapWith = tiles[zeroTilePosition.y() + 1][zeroTilePosition.x()];

        toMove.swap(0, tileToSwapWith);

        return toMove;
    }

    private Board moveRight() {
        Position zeroTilePosition = cache[0];

        if (zeroTilePosition.x() == 0)
            return null;

        Board toMove = new Board(this);
        int tileToSwapWith = tiles[zeroTilePosition.y()][zeroTilePosition.x() - 1];

        toMove.swap(0, tileToSwapWith);

        return toMove;
    }

    private Board moveLeft() {
        Position zeroTilePosition = cache[0];

        if (zeroTilePosition.x() == rowLength - 1)
            return null;

        Board toMove = new Board(this);
        int tileToSwapWith = tiles[zeroTilePosition.y()][zeroTilePosition.x() + 1];

        toMove.swap(0, tileToSwapWith);

        return toMove;
    }

    public int getLength() {
        return rowLength;
    }
    
    private void populateCache() {
        for (int y = 0; y < rowLength; y++) {
            for (int x = 0; x < rowLength; x++) {
                int tile = tiles[y][x];
                cache[tile] = new Position(x, y);
            }
        }
    }

    private void swap(int tile1, int tile2) {

        Position p1 = cache[tile1];
        Position p2 = cache[tile2];

        // swap tiles
        var tmp = tiles[p1.y()][p1.x()];
        tiles[p1.y()][p1.x()] = tiles[p2.y()][p2.x()];
        tiles[p2.y()][p2.x()] = tmp;

        // update cache
        cache[tile1] = p2;
        cache[tile2] = p1;
    }

    private static int[][] copy(int[][] src, int length) {
        int[][] dest = new int[length][length];

        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, length);
        }

        return dest;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < rowLength; y++) {
            for (int x = 0; x < rowLength; x++) {
                sb.append(tiles[y][x]);
                sb.append(" ");
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}
