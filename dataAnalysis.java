import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class DataAnalysis {
    
    Function<Double[], Double[]> scalingFunc;
    List<Double[]> scaledDataset;
    String acvFileLoc;
    int numFeatures;


    public DataAnalysis(String fileLoc){
        acvFileLoc = fileLoc;
    }

    public LinearRegressionFunction learn() throws IOException{
        BufferedReader acvIn = new BufferedReader(new InputStreamReader(new FileInputStream(acvFileLoc)));

        List<Double[]> dataset = new ArrayList<>();
        String acvInput = acvIn.readLine();
        acvInput = acvIn.readLine();

        numFeatures = acvInput.split("\",\"").length - 2;

        acvIn.mark(100000000);

        while((acvInput = acvIn.readLine()) != null){

            Double[] addData = new Double[numFeatures];
            for(int i = 2; i < numFeatures + 1; i++){
                String inString = acvInput.split("\",\"")[i];



                String outString = "";
                if(inString.length() >= 4){
                    String split[] = inString.split(",");
                    for(int j = 0; j < split.length; j++){
                        outString += split[j];
                    }
                }

                outString = outString.split("\"")[0];

                // System.out.println(i + " " + outString);

                if(outString.isEmpty() || outString.equals("")){
                    outString = "0";
                }

                double num = Double.parseDouble(outString);
                addData[i - 2] = num;
                
            }
            dataset.add(addData);
        }
        acvIn.reset();

        scalingFunc = FeaturesScaling.createFunction(dataset);

        scaledDataset = dataset.stream().map(scalingFunc).collect(Collectors.toList());

        List<Double> labels = new ArrayList<Double>();
        while((acvInput = acvIn.readLine()) != null){
            String inString = acvInput.split("\",\"")[(acvInput.split("\",\"").length) - 1];
            String split[] = inString.split(",");
            String outString = "";
            for(int i = 0; i < split.length; i++){
                outString += split[i];
            }

            if(outString.equals("\"")){
                outString = "0";
            }
            outString = outString.split("\"")[0];

            if(outString.isEmpty() || outString.equals("")){
                outString = "0";
            }            

            
            double addLabel = Double.parseDouble(outString);
            labels.add(addLabel);
        }

        double[] startThetas = new double[dataset.get(0).length - 1];
        LinearRegressionFunction targetFunction = new LinearRegressionFunction(startThetas);

        for(int i = 0; i < 10000; i++){
            targetFunction = LinearRegressionFunction.train(targetFunction, scaledDataset, labels, 0.1);
        }

        acvIn.close();

        return targetFunction;

    }

    public void predit(LinearRegressionFunction targetFunc, String path) throws IOException{
        BufferedWriter predictOut = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "\\Desktop\\prediction.csv"));
        BufferedReader data = new BufferedReader((new InputStreamReader(new FileInputStream(path))));

        List<Double[]> dataset = new ArrayList<>();
        List<String> compNames = new ArrayList<>();
        String dataString = data.readLine();
        dataString = data.readLine();

        while((dataString = data.readLine()) != null){
            Double[] addData = new Double[numFeatures];
            String splitData[] = dataString.split("\",\"");
            

            for(int i = 2; i < splitData.length; i++){
                String outData = "";
                String split[] = splitData[i].split(",");

                for(int j = 0; j < split.length; j++){
                    outData += split[j];
                }

                if(outData.equals("") || outData.equals("\"") || outData.isEmpty()){
                    outData = "0";
                }

                outData = outData.split("\"")[0];

                addData[i - 2] = Double.parseDouble(outData);
            }

            dataset.add(addData);
            compNames.add(splitData[1]);
        }

        for(int i = 0; i < dataset.size(); i++){
            scalingFunc = FeaturesScaling.createFunction(dataset);
            Double[] scaledFeatureVector = scalingFunc.apply(dataset.get(i));
            double predicted = targetFunc.apply(scaledFeatureVector);

            String split[] = compNames.get(i).split(",");
            String compName = "";

            for(int j = 0; j < split.length; j++){
                compName += split[j];
            }
            

            String write = compName + "," + predicted;
            predictOut.write(write);
            predictOut.newLine();
        }
        
        


        predictOut.close();
        data.close();

        if(Desktop.isDesktopSupported()){
            try{
                File predictFile = new File(System.getProperty("user.home") + "\\Desktop\\prediction.csv");
                Desktop.getDesktop().open(predictFile);
            } catch(IOException ex){
                System.out.println(ex);
            }
        }
    }
}
