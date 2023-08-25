import java.util.ArrayList;
import java.util.List;

public class DataSet {
    
    private final List<Instance> data;

    private int numOfInstances;
    private int numOfFeatures;

    public DataSet(List<Instance> data){
        this.data = data;
        this.numOfInstances = data.size();
        this.numOfFeatures = data.get(0).getFeatureVector().length;
    }

    public DataSet(List<double[]> featureVectors, List<Integer> labels, List<String> compNames){
        data = new ArrayList<>();
        for(int i = 0; i < compNames.size(); i++){
            data.add(new Instance(featureVectors.get(i), labels.get(i), compNames.get(i)));
        }
        this.numOfInstances = data.size();
        this.numOfFeatures = data.get(0).getFeatureVector().length;
    }

    public int getNumOfInstance(){
        return numOfInstances;
    }

    public int getNumOfFeatures(){
        return numOfFeatures;
    }

    public List<Double> getLabels(){
        List<Double> labels = new ArrayList<Double>();
        for(Instance inst : data){
            labels.add(inst.getLabelIndex());
        }

        return labels;
    }

    public Instance getInstance(int i){
        return data.get(i);
    }

    public int getSize(){
        return data.size();
    }


    public List<Instance> getData(){
        return data;
    }



}
