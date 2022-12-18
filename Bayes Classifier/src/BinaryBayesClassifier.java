import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BinaryBayesClassifier {

    private static final String MISSING_VALUE = "?";
    private static final String POSITIVE_VALUE = "y";
    private static final String NEGATIVE_VALUE= "n";

    private static final Random RANDOM = ThreadLocalRandom.current();

    private final int columns;
    private final int rows;

    private String[][] data;

    private final double[] probabilitesGivenFirst;
    private final double[] probabilitesNotGivenFirst;

    public BinaryBayesClassifier(String[][] data) {
        columns = data[0].length;
        rows = data.length;

        probabilitesGivenFirst = new double[columns];
        probabilitesNotGivenFirst = new double[columns];

        setData(data);
    }

    public void cleanData() {
        var positiveCount = new int[columns];
        var negativeCount = new int[columns];

        // Preprocessing
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (data[i][j].equals(POSITIVE_VALUE)) {
                    positiveCount[j]++;
                } else if (data[i][j].equals(NEGATIVE_VALUE)) {
                    negativeCount[j]++;
                }
            }
        }

        // Fill missing values
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (data[i][j].equals(MISSING_VALUE)) {
                    if (RANDOM.nextInt(positiveCount[j] + negativeCount[j]) < positiveCount[j]) {
                        data[i][j] = POSITIVE_VALUE;
                        positiveCount[j]++;
                    } else {
                        data[i][j] = NEGATIVE_VALUE;
                        negativeCount[j]++;
                    }
                }
            }
        }

        // Class probablility here
        probabilitesGivenFirst[0] = positiveCount[0] / (1. * rows);
        probabilitesNotGivenFirst[0] = 1- probabilitesGivenFirst[0];
    }

    public void train() {
        var positiveCountGivenFirstIsTrue = new int[columns];
        var negativeCountGivenFirstIsTrue = new int[columns];

        var positiveCountGivenFirstIsFalse = new int[columns];
        var negativeCountGivenFirstIsFalse = new int[columns];

        for (int i = 0; i < rows; i++) {
            if (data[i][0].equals(POSITIVE_VALUE)) {
                for (int j = 0; j < columns; j++) {
                    if (data[i][j].equals(POSITIVE_VALUE)) {
                        positiveCountGivenFirstIsTrue[j]++;
                    } else {
                        negativeCountGivenFirstIsTrue[j]++;
                    }
                }
            } else {
                for (int j = 0; j < columns; j++) {
                    if (data[i][j].equals(POSITIVE_VALUE)) {
                        positiveCountGivenFirstIsFalse[j]++;
                    } else {
                        negativeCountGivenFirstIsFalse[j]++;
                    }
                }
            }
        }

        // Rest here
        for (int i = 1; i < columns; i++) {
            probabilitesGivenFirst[i] = (1. * positiveCountGivenFirstIsTrue[i] /
                (positiveCountGivenFirstIsTrue[i] + negativeCountGivenFirstIsTrue[i]));

            probabilitesNotGivenFirst[i] = (1. * positiveCountGivenFirstIsFalse[i]) /
                (negativeCountGivenFirstIsFalse[i] + positiveCountGivenFirstIsFalse[i]);
        }
    }

    public boolean classify(String[] data) {
        double positiveProbability = Math.log(probabilitesGivenFirst[0]); // Class probability
        double negativeProbability = Math.log(probabilitesNotGivenFirst[0]); // Class probability

        // How much to be true
        for (int i = 1; i < data.length; i++) {
            if (data[i].equals(POSITIVE_VALUE)) {
                positiveProbability += Math.log(probabilitesGivenFirst[i]);
            } else if (data[i].equals(NEGATIVE_VALUE)){
                positiveProbability += Math.log(1 - probabilitesGivenFirst[i]);
            }
        }

        // How much to be false
        for (int i = 1; i < data.length; i++) {
            if (data[i].equals(POSITIVE_VALUE)) {
                negativeProbability += Math.log(probabilitesNotGivenFirst[i]);
            } else if (data[i].equals(NEGATIVE_VALUE)){
                negativeProbability += Math.log(1 - probabilitesNotGivenFirst[i]);
            }
        }

        return positiveProbability > (negativeProbability);
    }

    private void setData(String[][] d) {
        data = new String[rows][columns];

        for (int i = 0; i < d.length; i++) {
            System.arraycopy(d[i], 0, data[i], 0, columns);
        }
    }
}
