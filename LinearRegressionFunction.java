import java.util.*;
import java.util.function.Function;

public class LinearRegressionFunction {
   
    private final double[] thetaVector;

    LinearRegressionFunction(double[] thetaVector){
        this.thetaVector = Arrays.copyOf(thetaVector, thetaVector.length);
    }

    
    public Double applyACV(Double[] featureVector, double intercept){
        //best intercept is 2081.79 so far

        //first coefficient must be one for linReg
        assert featureVector[0] == 1.0;

        //linReg function
        double prediction = 0;
        for(int i = 0; i < thetaVector.length - 1; i++){
            // System.out.println(i);
            // System.out.println(featureVector[i]);
            prediction += thetaVector[i] * featureVector[i];
        }

        return prediction + intercept;
    }

    public Double applyBookedAmounts(Double[] featureVector, double intercept){
        //first coefficient must be one for linReg
        assert featureVector[0] == 1.0;

        //linReg function
        double prediction = 0;
        for(int i = 0; i < thetaVector.length; i++){
            // System.out.println(i);
            // System.out.println(featureVector[i]);
            prediction += thetaVector[i] * featureVector[i];
        }

        return prediction + intercept;
    }




    public double[] getThetas(){
        return Arrays.copyOf(thetaVector, thetaVector.length);
    }

    public String thetasString(){
        String thetas = "";
        for(int i= 0; i < thetaVector.length - 1; i ++){
            thetas += thetaVector[i];
            thetas += " ";
        }
        thetas += thetaVector[thetaVector.length - 1];

        return thetas;
    }

    public static double cost(Function<Double[], Double> targetFunction, List<Double[]> dataset, List<Double> labels){
            
        double sumSquaredErrors = 0;
        int m = dataset.size();

        for(int i = 0; i < m; i++){
            Double[] featureVector = dataset.get(i); //get input data from spreadsheet here
            double predicted = targetFunction.apply(featureVector);
            double label = labels.get(i); //get actually value from spreadsheet here

            double gap = predicted - label;

            sumSquaredErrors = Math.pow(gap, 2);
        }

        return (1.0/(2*m)) * sumSquaredErrors;
    }


    public static LinearRegressionFunction train(LinearRegressionFunction targetFunction, List<Double[]> dataset, List<Double> labels, double alpha, int acvOrBooked, double acvIntercept, double bookedIntercept){
        //acvOrBooked: 0 = acv, 1 = bookedAmount
        //dataset is what is fed in, labels are the true outcomes
        
        int m = dataset.size();
        double[] thetaVector = targetFunction.getThetas();
        double[] newThetaVector = new double[thetaVector.length];

        // compute the new theta of each element of the theta array
        for (int j = 0; j < thetaVector.length - 1; j++) {
            // summarize the error gap * feature
            double sumErrors = 0;
            for (int i = 0; i < m; i++) {
                Double[] featureVector = dataset.get(i);
                double error = 0;
                if(acvOrBooked == 0){
                    error = targetFunction.applyACV(featureVector, acvIntercept) - labels.get(i);
                }
                else{
                    error = targetFunction.applyBookedAmounts(featureVector, bookedIntercept) - labels.get(i);
                }
                sumErrors += error * featureVector[j];
            }

            // compute the new theta value
            double gradient = (1.0 / m) * sumErrors;
            newThetaVector[j] = thetaVector[j] - alpha * gradient;
        }

        return new LinearRegressionFunction(newThetaVector);
    }




}
