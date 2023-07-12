import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import jxl.read.biff.BiffException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.io.*;

public class dataAnalysis implements Runnable{

    //read file locations (input data)
    private static final String WIN_LOSS = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\2021_Q2023_Win_Loss.xls";
    private static final String ACV_PILLAR = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\ACV_Pillar_Dec21_March23.xls";
    private static final String ALL_SRS = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\All_SRs.xls";
    private static final String ANALYZER_BOOKINGS = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\Analyzer_Bookings_2021_Q12023.xls";

    private static final String COMBINED_DATA = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\combinedData.xls";

    
    //write file locations (write data, predictions)
    private static final String ACCURACY_TEST = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\Comparison.xls";
    private static final String PREDICTIONS = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\predictions.xls";

    //theta store file
    private static final String THETA_STORE = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\thetas.txt";

    //create reading workbooks (excels)
    private Workbook winLossExcel;
    private Workbook ACVPillarExcel;
    private Workbook allSRExcel;
    private Workbook analyzerBookingsExcel;

    private Workbook combinedDataExcel;

    //create writing workbooks (excels)
    private WritableWorkbook comparisonExcel;
    private WritableWorkbook predictExcel;

    //get sheets
    private Sheet winLossSheet;
    private Sheet ACVPillarSheet;
    private Sheet allSRSheet;
    private Sheet analyzerBookingsSheet;

    private Sheet combinedDataSheet;

    //create sheets
    private WritableSheet comparisonSheet;
    private WritableSheet predictSheet;
   

    //constructor
    public dataAnalysis(){
        //instantiate workbooks and sheets
        try {
            //write workbooks
            comparisonExcel = Workbook.createWorkbook(new File(ACCURACY_TEST));
            predictExcel = Workbook.createWorkbook(new File(PREDICTIONS));

            //create sheets from writable workbooks
            comparisonSheet = comparisonExcel.createSheet("Compare", 0);
            predictSheet = predictExcel.createSheet("Predictions", 0);
                        
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    //calls combineData from excelCombiner.java
    public void combineFiles(){
        try{
            //read workbooks
            winLossExcel = Workbook.getWorkbook(new File(WIN_LOSS));
            ACVPillarExcel = Workbook.getWorkbook(new File(ACV_PILLAR));
            allSRExcel = Workbook.getWorkbook(new File(ALL_SRS));
            analyzerBookingsExcel = Workbook.getWorkbook(new File(ANALYZER_BOOKINGS));

            //get sheets from workbooks
            winLossSheet = winLossExcel.getSheet(0);
            ACVPillarSheet = ACVPillarExcel.getSheet(0);
            allSRSheet = allSRExcel.getSheet(0);
            analyzerBookingsSheet = analyzerBookingsExcel.getSheet(0);
            
            excelCombiner ec = new excelCombiner(winLossSheet, ACVPillarSheet, allSRSheet, analyzerBookingsSheet, comparisonSheet, predictSheet);    

            ec.combine();

            combinedDataExcel = Workbook.getWorkbook(new File(COMBINED_DATA));
            combinedDataSheet = combinedDataExcel.getSheet(0);


        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch(BiffException e){
            e.printStackTrace();
        }
    }

    //if data already combined, instantiate excel and sheet
    public void getCombinedFiles(){
        try {
            combinedDataExcel = Workbook.getWorkbook(new File(COMBINED_DATA));
            combinedDataSheet = combinedDataExcel.getSheet(0);
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //creates arraylist of array of data to feed to model
    public ArrayList<Double[]> createTrainData(){
        ArrayList<Double[]> dataset = new ArrayList<>();

        for(int i = 1; i < combinedDataSheet.getRows(); i++){
            Double[] addData = new Double[6];
            for(int j = 6; j < 12; j++){
                Cell readCell = combinedDataSheet.getCell(j, i);
                double num = Double.parseDouble(readCell.getContents());
                addData[j-6] = num;
            }
            for(int j = 24; j < 23; j++){
                Cell readCell = combinedDataSheet.getCell(j, i);
                double num = Double.parseDouble(readCell.getContents());
                addData[j-10] = num;
            }

            dataset.add(addData);
        }

        // for(int i = 0; i < dataset.size(); i++){
        //     for(int j = 0; j < dataset.get(i).length; j++){
        //         System.out.print(dataset.get(i)[j] + " ");
        //     }
        //     System.out.println("\n");
        // }

        return dataset;
    }


    //creates the original theta values (array of 0.0)
    public double[] createThetas(){
        int size = 6;
        double[] thetas = new double[size];

        for(int i = 0; i < size; i++){
            thetas[i] = 0.0;
        }

        return thetas;
    }

    public double[] readThetas() throws IOException{
        FileReader fr = new FileReader(THETA_STORE);
        BufferedReader bufferRead = new BufferedReader(fr);

        int size = 6;


        double[] thetas = new double[size];
        
        String str = bufferRead.readLine();
        String[] split = str.split(" ");

        for(int i = 0; i < size; i++){
            //read file and add it to thetas[]
            thetas[i] = Double.parseDouble(split[i]);

        }

        bufferRead.close();

        return thetas;
    }


    //creates array of booked amount values to compare to predictions
    public ArrayList<Double> createBookedAmountLabels(){
        ArrayList<Double> labels = new ArrayList<Double>();
        for(int i = 1; i < combinedDataSheet.getRows(); i++){
            Cell labelCell = combinedDataSheet.getCell(23, i);
            double addLabel = Double.parseDouble(labelCell.getContents());
            labels.add(addLabel); 
        }


        return labels;
    }



    //creates array of ACV values to compare to predictions
    public ArrayList<Double> createAcvLabels(){
        ArrayList<Double> labels = new ArrayList<Double>();
        for(int i = 1; i < combinedDataSheet.getRows(); i++){
            Cell labelCell = combinedDataSheet.getCell(12, i);
            double addLabel = Double.parseDouble(labelCell.getContents());
            labels.add(addLabel); 
        }


        return labels;
    }



    //scales the dataset
    public ArrayList<Double[]> scaleData(ArrayList<Double[]> dataset){
        Function<Double[], Double[]> scalingFunc;
        ArrayList<Double[]> scaledDataset;    

        scalingFunc = FeaturesScaling.createFunction(dataset);
        scaledDataset = (ArrayList<Double[]>) dataset.stream().map(scalingFunc).collect(Collectors.toList());

        return scaledDataset;
    }


    //creates functions that the model will train
    public LinearRegressionFunction trainData(double[] thetas, List<Double> labels, List<Double[]> scaledDataset, double alpha, int acvOrBooked, double acvIntercept, double bookedIntercept) throws Exception{
        FileWriter fw;

        fw = new FileWriter(THETA_STORE);
        BufferedWriter bufferWrite = new BufferedWriter(fw);

        LinearRegressionFunction targetFunction =  new LinearRegressionFunction(thetas);

        for (int i = 0; i < 10000; i++) {
            targetFunction = LinearRegressionFunction.train(targetFunction, scaledDataset, labels, alpha, acvOrBooked, acvIntercept, bookedIntercept);

            // if(i % 100 == 0){
            //     System.out.println(i/100 + "% complete");
            // }
        }

        String thetaWrite = targetFunction.thetasString();

        bufferWrite.write(thetaWrite);

        bufferWrite.close();


        // System.out.println("trained");
        // System.out.println(thetas);

        return targetFunction;
    }


    //creates predictions
    public ArrayList<Double> predict(LinearRegressionFunction targetFunction, List<Double[]> dataset, int acvOrBooked, double intercept) throws Exception{
        ArrayList<Double> predictions = new ArrayList<Double>();

        for(int i = 0; i < dataset.size() - 1; i++){
            Function<Double[], Double[]> scalingFunc = FeaturesScaling.createFunction(dataset);
            Double[] scaledFeatureVector = scalingFunc.apply(dataset.get(i));
            double predicted = 0;
            if(acvOrBooked == 0){
                predicted = targetFunction.applyACV(scaledFeatureVector, intercept);   
            }
            else{
                predicted = targetFunction.applyBookedAmounts(scaledFeatureVector, intercept);
            }
            predictions.add(predicted);
        }

        return predictions;
    }

    
    //public void writePredictions(ArrayList<Double> acvPredict, ArrayList<Double> bookPredict, ArrayList<Double> acvActual, ArrayList<Double> bookedActual) throws WriteException, IOException{
    public void writePredictions(ArrayList<Double> acvPredict, ArrayList<Double> acvActual) throws WriteException, IOException{

        ArrayList<Double> acvAccuracies = new ArrayList<>();
        //ArrayList<Double> bookAccuracies = new ArrayList<>();

        for(int i = 0; i < acvPredict.size(); i++){
            Number predictCell = new Number(3, i + 1,  acvPredict.get(i));
            predictSheet.addCell(predictCell);

            // Number predictCell2 = new Number(6, i + 1, bookPredict.get(i));
            // predictSheet.addCell(predictCell2);
        }

        for(int i = 1; i < combinedDataSheet.getRows(); i++){
            Number id = new Number(0, i, Integer.parseInt(combinedDataSheet.getCell(0, i).getContents())); 
            Label customer = new Label(1, i, combinedDataSheet.getCell(1, i).getContents());

            Number realACV = new Number(4, i, Double.parseDouble(combinedDataSheet.getCell(12, i).getContents()));
            //Number realBooked = new Number(7, i, Double.parseDouble(combinedDataSheet.getCell(12, i).getContents()));

            predictSheet.addCell(id);
            predictSheet.addCell(customer);
            predictSheet.addCell(realACV);
            // predictSheet.addCell(realBooked);
        }
        
        double highestAccuracy = 0;

        for(int i = 0; i < acvActual.size() - 1; i++){
            double acvAccuracy = 0;
            //double bookedAccuracy = 0;
            if(Math.abs(acvActual.get(i)) < Math.abs(acvPredict.get(i))){
                acvAccuracy = acvActual.get(i) / acvPredict.get(i);
            }
            else{
                acvAccuracy = acvPredict.get(i) / acvActual.get(i);
            }
            if(acvAccuracy < 0){
                acvAccuracy = 0;
            }

            if(acvAccuracy > highestAccuracy){
                highestAccuracy = acvAccuracy;
            }

            acvAccuracies.add(acvAccuracy);

            Number addAcvAccuracy = new Number(5, i + 1, acvAccuracy);
            

            // if(bookedActual.get(i) < bookPredict.get(i)){
            //     bookedAccuracy = bookedActual.get(i) / bookPredict.get(i);
            // }
            // else{
            //     bookedAccuracy = bookPredict.get(i) / bookedActual.get(i);
            // }
            // if(bookedAccuracy < 0){
            //     bookedAccuracy = 0;
            // }

            // bookAccuracies.add(bookedAccuracy);
            
            // Number addBookedAccuracy = new Number(8, i + 1, bookedAccuracy);

            predictSheet.addCell(addAcvAccuracy);
            //predictSheet.addCell(addBookedAccuracy);
        }

        double totalAcvAccuracy = 0;
        for(int i = 0; i < acvAccuracies.size() - 1; i++){
            totalAcvAccuracy += acvAccuracies.get(i);
        }
        double avgAcvAccuracy = totalAcvAccuracy / acvAccuracies.size();


        // double totalBookAccuracy = 0;
        // for(int i = 0; i < bookAccuracies.size() - 1; i++){
        //     totalBookAccuracy += bookAccuracies.get(i);
        // }
        // double avgBookAccuracy = totalBookAccuracy / bookAccuracies.size();

        System.out.println("ACV accuracy: " + avgAcvAccuracy);
        // System.out.println("Booking accuracy: " + avgBookAccuracy);
        
        double totalPredict = 0;
        double totalAct = 0;
        double diff = 0;

        for(int i = 0; i < acvPredict.size(); i++){
            totalPredict += acvPredict.get(i);
        }
        
        for(int i = 0; i < acvActual.size(); i++){
            totalAct += acvAccuracies.size();
        }

        if(totalAct > totalPredict){
            System.out.println("overall too low");
            diff = totalAct - totalPredict;
        }

        else{
            System.out.println("overall too high");
            diff = totalPredict - totalAct;
        }

        double avg = diff / acvActual.size();

        System.out.println("average diff " + avg);

        System.out.println("best accuracy is " + highestAccuracy);


        predictExcel.write();
    }


    public void closeExcel() throws WriteException, IOException{
        predictExcel.close();
        comparisonExcel.close();
    }


    //for multithreading
    public void run(){
        while(true){
            
            //in case it crashes
            if(Thread.interrupted()){
                break;
            }
            
            //update timer - updates every 200 milliseconds
            try{
                Thread.sleep(200);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
          
        
        }
    }
}
