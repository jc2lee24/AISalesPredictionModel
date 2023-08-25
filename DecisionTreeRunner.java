import java.io.*;
import java.util.*;

public class DecisionTreeRunner implements Runnable{


    // public String SR_DATA = "C:\Users\justinlee\Desktop\Code\randomForestV2\data\SRs2016-2023.csv";
    // public String ACV_DATA = "C:\Users\justinlee\Desktop\Code\randomForestV2\data\ACV.csv";
    public String SR_DATA;
    public String ACV_DATA;

    public boolean running = false;


    public static String TEST_DATA = System.getProperty("user.home") + "\\Desktop\\Prediction_Program\\data\\testData.csv";
    public static String TRAIN_DATA = System.getProperty("user.home") + "\\Desktop\\Prediction_Program\\data\\trainData.csv";

    public String PREDICTION = System.getProperty("user.home") + "\\Desktop\\prediction.csv";

    public boolean start = false;

    public void getExcelLoc(String SR_DATA, String ACV_DATA){
        this.SR_DATA = SR_DATA;
        this.ACV_DATA = ACV_DATA;
    }


    public void main() throws IOException {
        
        start = false;


        BufferedWriter predictOut = new BufferedWriter(new FileWriter(PREDICTION));
        
        FileCombiner FC = new FileCombiner(SR_DATA, ACV_DATA);
        FC.combine(TEST_DATA, TRAIN_DATA);

        DecisionTree dt = new DecisionTree();

        Tripple<List<double[]>, List<Integer>, List<String>> trainData = getData(TRAIN_DATA);
        Tripple<List<double[]>, List<Integer>, List<String>> testData = getData(TEST_DATA);

        DataSet train = new DataSet(trainData.first, trainData.second, trainData.third);
        DataSet test = new DataSet(testData.first, testData.second, testData.third);

        dt.buildTree(train, 20, 50);

        predictOut.write("COMPANY,PREDICTED CHANGE (VIA SR)");
        predictOut.newLine();

        for(int i = 0; i < test.getSize(); i++){
            Instance sample = test.getInstance(i);
            int prediction = dt.predictLabel(sample.getFeatureVector());
            String realPredict;

            // <-50% 0
            //-50% - -35% 1
            //-35% - 25% 2
            //-25% - 15% 3
            //-15% - -10% 4
            //-10% - -5% 5
            //-5% - -3% 6
            //-%3 - +3% 7
            //+3% - +5% 8
            //+5% - + 10% 9
            //+10% - +15% 10
            //+15% - +25% 11
            //+25% - +35% 12
            //+35% - +50% 13
            //>50% 14


            if(prediction == 0) realPredict = "decrease more than 50%";

            else if(prediction == 1) realPredict = "decrease between 35% and 50%";
            
            else if(prediction == 2) realPredict = "decrease between 25% and 35%";

            else if(prediction == 3) realPredict = "decrease between 15% and 25%";

            else if(prediction == 4) realPredict = "decrease between 10% and 15%";

            else if(prediction == 5) realPredict = "decrease between 5% and 10%";

            else if(prediction == 6) realPredict = "decrease between 3% and 5%";
            
            else if(prediction == 7) realPredict = "between -3% and 3%";

            else if(prediction == 8) realPredict = "increase between 3% and 5%";
            
            else if(prediction == 9) realPredict = "increase between 5% and 10%";

            else if(prediction == 10) realPredict = "increase between 10% and 15%";
            
            else if(prediction == 11) realPredict = "increase between 15% and 25%";
            
            else if(prediction == 12) realPredict = "increase between 25% and 35%";

            else if(prediction == 13) realPredict = "increase between 35% and 50%";
            
            else {realPredict = "increase more than 50%";}

            String compName = sample.getCompName();

            predictOut.write(compName + "," + realPredict);
            predictOut.newLine();

        }

        running = false;


        predictOut.close();

    }
    private static Tripple<List<double[]>, List<Integer>, List<String>> getData(String path) throws IOException {
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            List<double[]> samples = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            List<String> compNames = new ArrayList<>();

            String input;
            while ((input = in.readLine()) != null) {


                String[] features = input.split(",");

                String compName = features[0];
                compNames.add(compName);
                double[] sample = new double[features.length - 1];
                for (int i = 1; i < features.length - 1; i++){
                    sample[i] = Double.parseDouble(features[i]);
                }
                samples.add(sample);

                labels.add(Integer.parseInt(features[features.length - 1]));
            }

            return new Tripple<>(samples, labels, compNames);
        }
    }

    public boolean isRunning(){
        return running;
    }

    public void start(){
        this.start = true;
        running = true;
    }

    @Override
    public void run(){
        try {
            this.main();
        } catch (IOException e) {
            System.out.println(e);
        }
    }



    private static class Tripple<K, V, Y> {
        K first;
        V second;
        Y third;

        public Tripple(K first, V second, Y third){
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

}
