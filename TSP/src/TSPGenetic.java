import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TSPGenetic {

    private final int POPULATION_SIZE = 100;
    private final int ELITISM = 5;
    private final int MUTATION_RATE_PERCENT = 5;
    private final int ITERATIONS = 10_000;

    private final Random RANDOM = new Random();
    private final int RANDOM_RANGE = 800;

    private final Scanner scanner = new Scanner(System.in);

    private Point[] points;
    private int[][] distances;

    private final Supplier<PriorityQueue<int[]>> fixedPriorityQueueSupplier =
        () -> new PriorityQueue<>((a, b) -> fitness(b) - fitness(a));

    private PriorityQueue<int[]> population = fixedPriorityQueueSupplier.get();
    private int[] bestSoFar;

    private final int NumberOfPoints;

    public TSPGenetic() {
        // Input and initialization
        System.out.println("Enter N: ");
        NumberOfPoints = Integer.parseInt(scanner.nextLine());
        points = new Point[NumberOfPoints];
        distances = new int[NumberOfPoints][NumberOfPoints];

        // Random points
        for (int i = 0; i < NumberOfPoints; i++) {
            int x = RANDOM.nextInt(0, RANDOM_RANGE);
            int y = RANDOM.nextInt(0, RANDOM_RANGE);

            points[i] = new Point(x, y);
        }

//        points[0] = new Point(10, 10);
//        points[1] = new Point(30, 30);
//        points[2] = new Point(250, 520);
//        points[3] = new Point(103, 200);
//        points[4] = new Point(550, 410);
//        points[5] = new Point(310, 140);
//        points[6] = new Point(130, 10);
//        points[7] = new Point(10, 130);
//        points[8] = new Point(510, 720);
//        points[9] = new Point(440, 350);

        // Calculating distances
        for (int i = 0; i < NumberOfPoints; i++) {
            for (int j = 0; j < NumberOfPoints; j++) {
                distances[i][j] = calculateRelativeDistance(points[i], points[j]);
            }
        }
    }

    public void simulate() {

        // Generate random solutions
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(
                generateRandom(NumberOfPoints)
            );
        }

        bestSoFar = population.peek();
        System.out.println("First gen best distance: " + tourLength(bestSoFar));

        // Genetic iterations
        for (int k = 0; k < ITERATIONS; k++) {
            // Selection algorithm
            var populationList = new ArrayList<>(population);

            var fitnesses = populationList.stream()
                                          .mapToInt(this::fitness)
                                          .toArray();

            int sum = Arrays.stream(fitnesses).sum();

            // Integral Array
            for (int i = 0; i < fitnesses.length - 1; i++) {
                fitnesses[i + 1] += fitnesses[i];
            }

            List<int[]> children = new ArrayList<>();

            for (int i = 0; i < POPULATION_SIZE / 2; i++) {
                // Crossover
                int parent1 = RANDOM.nextInt(0, sum);
                int parent2 = RANDOM.nextInt(0, sum);

                int index1 = Arrays.binarySearch(fitnesses, parent1);
                int index2 = Arrays.binarySearch(fitnesses, parent2);

                if (index1 < 0)
                    index1 = -index1 - 1;

                if (index2 < 0)
                    index2 = -index2 - 1;

                crossOverAndMutate(populationList.get(index1), populationList.get(index2), children);
            }

            if (k == ITERATIONS / 4) {
                System.out.println("First quarter gen best distance: " + tourLength(bestSoFar));
            }

            if (k == ITERATIONS / 2) {
                System.out.println("Middle gen best distance: " + tourLength(bestSoFar));
            }

            if (k == 3 *ITERATIONS / 4) {
                System.out.println("Third quarter gen best distance: " + tourLength(bestSoFar));
            }

            List<int[]> elite = new ArrayList<>();

            for (int i = 0; i < ELITISM; i++) {
                elite.add(population.poll());
            }

            // Fixed length queue!
            population.clear();
            population.addAll(children);
            population.addAll(elite);

            bestSoFar = population.peek();
        }

        System.out.println("Last gen best distance: " + tourLength(bestSoFar));
    }

    public Point[] getPoints() {
        return points;
    }

    public int[] getRoute() {
        return bestSoFar;
    }

    private int calculateRelativeDistance(Point p1, Point p2) {
        int dx = p1.x() - p2.x();
        int dy = p1.y() - p2.y();

        return (int) Math.sqrt((dx * dx + dy * dy) + 0.0);
    }

    private int fitness(int[] solution) {
        int length = tourLength(solution);

        return Integer.MAX_VALUE / length;
    }

    private int tourLength(int[] solution) {
        int acc = 0;

        for (int i = 0; i < solution.length; i++) {
            int idx1 = solution[i];
            int idx2 = solution[(i + 1) % solution.length];

            acc += distances[idx1][idx2];
        }

        return acc;
    }

    private void mutate(int[] solution) {
        int r1 = RANDOM.nextInt(0, solution.length);
        int r2 = RANDOM.nextInt(0, solution.length);

        var tmp = solution[r1];
        solution[r1] = solution[r2];
        solution[r2] = tmp;
    }

    private void crossOverAndMutate(int[] solution1, int[] solution2, List<int[]> listToAppend) {
        final int length = solution1.length;

        final int r1 = RANDOM.nextInt(0, length);
        final int r2 = RANDOM.nextInt(0, length);

        int[] child1 = new int[length];
        int[] child2 = new int[length];

        boolean[] cache1 = new boolean[length];
        boolean[] cache2 = new boolean[length];

        // Copy genes
        for (int i = r1; i != (r2 + 1) % length; i = (i + 1) % length) {
            child1[i] = solution1[i];
            child2[i] = solution2[i];

            cache1[solution1[i]] = true;
            cache2[solution2[i]] = true;
        }

        // The rest of 2p crossover
        Func cross = (boolean[] cache, int[] parent, int[] child) -> {
            int i = (r2 + 1) % length;
            int j = i;
            int counter = 0;

            while (counter < length) {
                if (!cache[parent[i]]) {
                    cache[parent[i]] = true;

                    child[j] = parent[i];
                    j = (j + 1) % length;
                }

                i = (i + 1) % length;
                counter++;
            }
        };

        cross.apply(cache1, solution2, child1);
        cross.apply(cache2, solution1, child2);

        // Mutate
        if (RANDOM.nextInt(0, 101) < MUTATION_RATE_PERCENT)
            mutate(child1);

        if (RANDOM.nextInt(0, 101) < MUTATION_RATE_PERCENT)
            mutate(child2);

        listToAppend.add(child1);
        listToAppend.add(child2);
    }

    private int[] generateRandom(int length) {
        List<Integer> list = IntStream.range(0, length)
                                      .boxed()
                                      .collect(Collectors.toList());

        Collections.shuffle(list, RANDOM);

        return list.stream()
                   .mapToInt(Integer::intValue)
                   .toArray();
    }

    private interface Func {
        void apply(boolean[] cache, int[] parent, int[] child);
    }


}
