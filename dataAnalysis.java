// import jxl.Cell;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
// import jxl.format.Colour;
import jxl.write.*;
// import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import jxl.read.biff.BiffException;

import java.io.BufferedWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileWriter;
// import java.io.FileReader;
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileWriter;
import java.io.IOException;

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
    private Sheet comparisonSheet;
    private Sheet predictSheet;

    private excelCombiner ec;

    //create booleans to run in thread
    private boolean combine;
    private boolean createTrainData;
    private boolean createThetas;
    private boolean createBookedAmountLabels;
    private boolean createAcvLabels;
    private boolean scaleData;
    private boolean trainData;

    private List<Double[]> trainDataList;
    private double[] thetas;
    private List<Double> createBookedAmountsList;
    private List<Double> createAcvList;
    private List<Double[]> createScaleDataList;
    private LinearRegressionFunction trainedFunction;

    private List<Double[]> getTrainDataList;
    private double[] getThetas;
    private List<Double> getLabels;
    private List<Double[]> getScaleDataList;
    private double alpha;

    //constructor
    public dataAnalysis(){
        //instantiate workbooks and sheets
        try {
            //read workbooks
            winLossExcel = Workbook.getWorkbook(new File(WIN_LOSS));
            ACVPillarExcel = Workbook.getWorkbook(new File(ACV_PILLAR));
            allSRExcel = Workbook.getWorkbook(new File(ALL_SRS));
            analyzerBookingsExcel = Workbook.getWorkbook(new File(ANALYZER_BOOKINGS));

            //write workbooks
            comparisonExcel = Workbook.createWorkbook(new File(ACCURACY_TEST));
            predictExcel = Workbook.createWorkbook(new File(PREDICTIONS));

            //get sheets from workbooks
            winLossSheet = winLossExcel.getSheet(0);
            ACVPillarSheet = ACVPillarExcel.getSheet(0);
            allSRSheet = allSRExcel.getSheet(0);
            analyzerBookingsSheet = analyzerBookingsExcel.getSheet(0);

            //create sheets from writable workbooks
            comparisonSheet = comparisonExcel.createSheet("Compare", 0);
            predictSheet = predictExcel.createSheet("Predictions", 0);
            
            //create excel combiner object
            ec = new excelCombiner(winLossSheet, ACVPillarSheet, allSRSheet, analyzerBookingsSheet, comparisonSheet, predictSheet);    
            
            //create combined data excel and sheets
            combinedDataExcel = Workbook.getWorkbook(new File(COMBINED_DATA));
            combinedDataSheet = combinedDataExcel.getSheet(0);

            //instantiate booleans for threading
            boolean combine = false;
            boolean createTrainData = false;
            boolean createThetas = false;
            boolean createBookedAmountLabels = false;
            boolean createAcvLabels = false;
            boolean scaleData = false;
            boolean trainData = false;

        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    //calls combineData from excelCombiner.java
    public void combineFiles(){
        try{
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

    public void runCombineFiles(){
        combine = true;
    }


    //creates arraylist of array of data to feed to model
    public List<Double[]> createTrainData(){
        List<Double[]> dataset = new ArrayList<>();

        for(int i = 0; i < combinedDataSheet.getRows(); i++){
            Double[] addData = new Double[22];
            for(int j = 2; j < 25; j++){
                Cell readCell = combinedDataSheet.getCell(j, i);
                double num = Double.parseDouble(readCell.getContents());
                addData[j-2] = num;
            }

            dataset.add(addData);
        }

        return dataset;
    }

    public void runCreateTrainData(){
        createTrainData = true;
    }

    public List<Double[]> getTrainData(){
        return this.trainDataList;
    }


    //creates the original theta values (array of 0.0)
    public double[] createThetas(){
        int size = combinedDataSheet.getColumns() - 2;
        double[] thetas = new double[]{};

        for(int i = 0; i < size; i++){
            thetas[i] = 0.0;
        }

        return thetas;
    }
   
    public void runCreateThetas(){
        createThetas = true;
    }

    public double[] getThetas(){
        return thetas;
    }


    //creates array of booked amount values to compare to predictions
    public List<Double> createBookedAmountLabels(){
        List<Double> labels = new ArrayList<Double>();
        for(int i = 0; i < combinedDataSheet.getRows(); i++){
            Cell labelCell = combinedDataSheet.getCell(23, i);
            double addLabel = Double.parseDouble(labelCell.getContents());
            labels.add(addLabel); 
        }


        return labels;
    }

    public void runCreateBookedAmountLabels(){
        createBookedAmountLabels = true;
    }
    
    public List<Double> getBookedAmountLabels(){
        return createBookedAmountsList;
    }


    //creates array of ACV values to compare to predictions
    public List<Double> createAcvLabels(){
        List<Double> labels = new ArrayList<Double>();
        for(int i = 0; i < combinedDataSheet.getRows(); i++){
            Cell labelCell = combinedDataSheet.getCell(12, i);
            double addLabel = Double.parseDouble(labelCell.getContents());
            labels.add(addLabel); 
        }


        return labels;
    }

    public void runCreateAcvLables(){
        createAcvLabels = true;
    }
    
    public List<Double> getAcvLabels(){
        return createAcvList;
    }


    //scales the dataset
    public List<Double[]> scaleData(List<Double[]> dataset){
        Function<Double[], Double[]> scalingFunc;
        List<Double[]> scaledDataset;    

        scalingFunc = FeaturesScaling.createFunction(dataset);
        scaledDataset = dataset.stream().map(scalingFunc).collect(Collectors.toList());

        return scaledDataset;
    }

    public void runScaleData(){
        scaleData = true;
    }
    
    public List<Double[]> getScaleData(){
        return createScaleDataList;
    }


    //creates functions that the model will train
    public LinearRegressionFunction trainData(double[] thetas, List<Double> labels, List<Double[]> scaledDataset, double alpha) throws Exception{
        FileWriter fw;

        fw = new FileWriter(THETA_STORE);
        BufferedWriter bufferWrite = new BufferedWriter(fw);

        LinearRegressionFunction targetFunction =  new LinearRegressionFunction(thetas);

        for (int i = 0; i < 10000; i++) {
            targetFunction = LinearRegressionFunction.train(targetFunction, scaledDataset, labels, alpha);
        }

        String thetaWrite = targetFunction.thetasString();

        bufferWrite.write(thetaWrite);

        bufferWrite.close();

        return targetFunction;
    }

    public void runTrainData(){
        trainData = true;
    }
    
    public LinearRegressionFunction getTrainedFunction(){
        return trainedFunction;
    }

    //creates predictions
    public void predict() throws Exception{

    }


    public void getInfo(List<Double[]> getTrainDataList, double[] getThetas, List<Double> getLabels, List<Double[]> getScaleDataList, double alpha){
        this.getTrainDataList = getTrainDataList;
        this.getThetas = getThetas;
        this.getLabels = getLabels;
        this.getScaleDataList = getScaleDataList;
        this.alpha = alpha;
    }


    //for multithreading
    public void run(){
        while(true){
            
            //in case it crashes
            if(Thread.interrupted()){
                break;
            }
            
            //short timer - updates every 100 milliseconds
            try{
                Thread.sleep(100);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            if(combine){
                //returns nothing
                this.combineFiles();
                combine = false;
            }
            if(createTrainData){
                //returns arrayList "trainData"
                this.trainDataList = this.createTrainData();
                createTrainData = false;
            }
            if(createThetas){
                //returns arrayList "theatas"
                this.thetas = this.createThetas();
                createThetas = false;
            }
            if(createBookedAmountLabels){
                //returns arrayList of labels for booked amounts
                this.createBookedAmountsList = this.createBookedAmountLabels();
                createBookedAmountLabels = false;
            }
            if(createAcvLabels){
                //returns arrayList of labels for ACV
                this.createAcvList = this.createAcvLabels();
                createAcvLabels = false;
            }
            if(scaleData){
                //returns array of scaled data
                this.createScaleDataList = this.scaleData(getTrainDataList);
                scaleData = false;
            }
            if(trainData){
                //returns trained function 
                try{
                    this.trainedFunction = this.trainData(getThetas, getLabels, getScaleDataList, alpha);
                } catch(Exception e){
                    e.printStackTrace();
                }
                trainData = false;
            }
        }
    }
}
