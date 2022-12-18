import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String[][] data = getAllData();

        final int rows = data.length;
        final int cols = data[0].length;

        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equals("republican"))
                data[i][0] = "y";
            else if (data[i][0].equals("democrat"))
                data[i][0] = "n";
        }

        String[][] trainingData = new String[rows - rows / 10][];
        String[][] validationData = new String[rows / 10][];

        List<Double> accuracies = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            int v = 0;
            int k = 0;
            for (int j = 0; j < rows; j++) {
                if (j / (rows / 10) == i) {
                    validationData[v++] = data[j];
                } else {
                    trainingData[k++] = data[j];
                }
            }

            int correctClassifications = 0;
            int incorrectClasifications = 0;

            var bs = new BinaryBayesClassifier(trainingData);
            bs.cleanData();
            bs.train();

            for (var vd : validationData) {
                boolean isRepublicant = bs.classify(vd);

                if (vd[0].equals("y")) {
                    if (isRepublicant)
                        correctClassifications++;
                    else
                        incorrectClasifications++;
                } else if (vd[0].equals("n")){
                    if (!isRepublicant)
                        correctClassifications++;
                    else
                        incorrectClasifications++;
                }
            }

            double accuracy = 1. * correctClassifications/(correctClassifications + incorrectClasifications);
            accuracies.add(accuracy);

            System.out.printf(
                "Correct %d, Incorrect: %d Accuracy: %f %n",
                correctClassifications,
                incorrectClasifications,
                accuracy
            );
        }

        System.out.println(
            "Average accuracy: " + accuracies.stream()
                                             .mapToDouble(Double::doubleValue).average().getAsDouble()
        );
    }

    private static String[][] getAllData() {
        var dataScanner = new Scanner(
            Objects.requireNonNull(
                BinaryBayesClassifier.class.getResourceAsStream("/house-votes-84.data")
            )
        );

        List<String[]> lst = new ArrayList<>();

        while (dataScanner.hasNext()) {
            var row = dataScanner.next();
            lst.add(row.split(","));
        }


        return lst.toArray(String[][]::new);
    }
}
