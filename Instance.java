import java.util.Arrays;

public class Instance {
    
    private final double[] featureVector;
    private final double labelIndex;
    private final String compName;
    
    Instance(double[] featureVector, Integer integer, String compName){
        this.featureVector = featureVector;
        this.labelIndex = integer;
        this.compName = compName;

    }


    public double[] getFeatureVector() {
        return this.featureVector;
    }

    public Double getLabelIndex() {
        return labelIndex;
    }

    public String getCompName(){
        return compName;
    }

    
    @Override
    public String toString() {
        return "Instance{" + "featureVector=" + Arrays.toString(featureVector) + ", labelIndex=" + labelIndex + '}';
    }
}
