import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TSPGenetic {

    private final int POPULATION_SIZE = 100;
    private final int ELITISM = 5;
    private final int MUTATION_RATE_PROMILS = 50;
    private final int ITERATIONS = 10_000;

    private final Random RANDOM = ThreadLocalRandom.current();
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

                // No need to self reproduce
                while (parent2 == parent1)
                    parent1 = RANDOM.nextInt(0, sum);

                int index1 = Arrays.binarySearch(fitnesses, parent1);
                int index2 = Arrays.binarySearch(fitnesses, parent2);

                if (index1 < 0)
                    index1 = -index1 - 1;

                if (index2 < 0)
                    index2 = -index2 - 1;

                crossOver(populationList.get(index1), populationList.get(index2), children);
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

            children.forEach(
                c -> mutationChance(c)
            );

            // Improvement?
            var betterElite = new ArrayList<int[]>();
            for (var e : elite) {
                int[] mut = new int[e.length];

                System.arraycopy(e, 0, mut, 0, mut.length);

                mutate(mut);

                if (fitness(mut) > fitness(e))
                    betterElite.add(mut);
                else
                    betterElite.add(e);
            }

            // Fixed length queue!
            population.clear();
            population.addAll(children);
            population.addAll(betterElite);
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

    private void mutationChance(int[] solution) {
            if (RANDOM.nextInt(0, 1001) < MUTATION_RATE_PROMILS) {
                mutate(solution);
            }
    }

    private void mutate(int[] solution) {
        mutate2(solution);
    }

    private void mutate1(int[] solution) {
        int r1 = RANDOM.nextInt(0, solution.length);
        int r2 = RANDOM.nextInt(0, solution.length);

        swap(solution, r1, r2);
    }

    private void mutate2(int[] solution) {
        int r1 = RANDOM.nextInt(0, solution.length);
        int r2 = RANDOM.nextInt(0, solution.length);

        if (r1 > r2) {
            var tmp = r1;
            r1 = r2;
            r2 = tmp;
        }
        for (int i = r1; i < r2 - i; i++) {
            swap(solution, i, r2 - i);
        }
    }

    private void swap(int[] arr, int idx1, int idx2) {
        var tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;
    }

    private void crossOver(int[] solution1, int[] solution2, List<int[]> listToAppend) {
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
