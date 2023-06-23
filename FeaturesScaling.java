import java.util.function.Function;
import java.util.*;

public class FeaturesScaling {

    public static Function<Double[], Double[]> createFunction(List<Double[]> data){
        int numFeatures = data.get(0).length;
        double[] averages = new double[numFeatures];        
        double[] maxValues = new double[numFeatures];
        double[] minValues = new double[numFeatures];

        for(Double[] instance: data){
            for(int i = 0; i < numFeatures; i++){
                double value = instance[i];
                averages[i] += value;
                maxValues[i] = Math.max(maxValues[i], value);
                minValues[i] = Math.min(minValues[i], value);
            }
        }

        return input -> {
            Double[] scaled = new Double[numFeatures];
            for(int i = 0; i < numFeatures; i++){
                double average = averages[i] / data.size();
                double max = maxValues[i];
                double min = minValues[i];
                double value = input[i];
                scaled[i] = (value - average) / (max - min);
            }

            return scaled;
        };

    }

}
