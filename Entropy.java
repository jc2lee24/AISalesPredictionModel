import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entropy {

    /*
     * calculate the entropy of response variables. 
     * H(X) = -\sum_{i=1}^{d} p(x_{i})log2(p(x_{i}))
     *
     */
    public static double getEntropy(List<Double> labels) {


        double size = labels.size();
        // System.out.println(size);

        if(size == 0){
            return 0;
        }


        Map<Double, Integer> countMap = new HashMap<>();

        for (int i = 0; i < labels.size(); i++) {
            if (!countMap.containsKey(labels.get(i))) {
                // System.out.println("Label: " + labels.get(i));

                countMap.put(labels.get(i), 1);

                // System.out.println("Map: (" + labels.get(i) + ", " + countMap.get(labels.get(i)) + ")");

            } else {
                int currCnt = countMap.get(labels.get(i));
                countMap.put(labels.get(i), currCnt + 1);
            }

            // System.out.println(countMap.get(labels.get(i)));
        }

        double entropy = 0;

        for (Double label : countMap.keySet()) {
            // System.out.println(countMap.get(label));

            double p = (double) countMap.get(label) / size;

            entropy += p * log(p, 2);


        }

        // System.out.println(entropy * -1);

        return -1 * entropy;
    }

    private static double log(double x, int base) {
        return Math.log(x) / Math.log(base);
    }
}
