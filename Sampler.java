import java.util.Random;

public class Sampler {
    
    public static int[] randSample(int n, int m){
        int[] indexes = new int[n];

        for(int i = 0; i < n; i++){
            indexes[i] = i;
        }

        for(int i = 0; i < m; i++){
            int randomNum = new Random().nextInt(n - 1);
            // System.out.println(randomNum);

            int temp = indexes[i];
            indexes[i] = indexes[randomNum];
            indexes[randomNum] = temp;
        }

        int[] returnVal = new int[m];
        for(int i = 0; i < m; i++){
            returnVal[i] = indexes[i];
        }

        return returnVal;
    }


    public static int[] bootStrap(int n){
        int[] bootstrapIndex = new int[n];
        for(int i = 0; i < n; i++){
            bootstrapIndex[i] = new Random().nextInt(n);
        }

        return bootstrapIndex;
    }
}
