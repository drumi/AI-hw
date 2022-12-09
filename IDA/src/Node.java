public record Node<T>(Node<T> parent, T value, Direction moveUsed, int currentCost) {
}
