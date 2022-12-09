public enum Player {
    X("X"),
    O("O");

    String letter;

    Player(String letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return letter;
    }
}
