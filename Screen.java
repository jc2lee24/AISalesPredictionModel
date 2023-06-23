import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.*;
import jxl.write.Number;


import jxl.read.biff.BiffException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/* TO DO:
 *  -Save theta values to seperate file (DONE)
 *  -Relearn option (DONE)
 *      -If relearn, run as normal (DONE)
 *      -Else run with same theta values (saved values) (DONE)
 * 
 *  -Implement other data
 *      -Keep lin reg model?
 *      -Implement time-series?
 * 
 *  -Tweak lin reg equation (if keeping)
 * 
 *  -Add UI
 *      -Make easier for non-coders
 * 
 *  -Format Excel File (DONE)
 *      -Accuracy: (DONE)
 *          -Less than 1 is dark green (DONE)
 *          -Less than .95 is light green (DONE)
 *          -Less than .85 accuracy is yellow (DONE)
 *          -Less than .6 is red (DONE)
 *  
 * 
 *  -Export to executable .jar file
 *  -Make instructions?
 */

public class Screen extends JFrame implements ActionListener{

    private static final String EXCEL_FILE_LOCATION = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor\\data.xls";
    private static final String EXCEL_PREDICT_LOCATION = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor\\compareData.xls";
    private static final String WRITE_FILE = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor\\test.xls";
    private static final String REAL_FILE = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor\\realFuturePredictions.xls";
    private static final String THETA_STORE = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor\\thetas.txt";

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;


    public Screen(){
        super("Prediction Model");
        setSize(WIDTH, HEIGHT);

        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void run() throws Exception{

        Workbook data = null;
        Workbook compareData = null;
        WritableWorkbook write = null;
        WritableWorkbook predict = null;

        Scanner sc = new Scanner(System.in);


        List<Double[]> dataset = new ArrayList<>();


        try{

            compareData = Workbook.getWorkbook(new File(EXCEL_PREDICT_LOCATION));
            data = Workbook.getWorkbook(new File(EXCEL_FILE_LOCATION));
            write = Workbook.createWorkbook(new File(WRITE_FILE));
            predict = Workbook.createWorkbook(new File(REAL_FILE));

            Sheet sheet = data.getSheet(0);
            Sheet compareSheet = compareData.getSheet(0);
            WritableSheet writeSheet = write.createSheet("Comparisons", 0);
            WritableSheet predictions = predict.createSheet("Predictions", 0); 




            //create the dataset by pulling from the excel file
            for(int i = 0; i < sheet.getRows(); i++){
                Double[] addData = new Double[5];
                for(int j = 4; j < 9; j++){
                    Cell readCell = sheet.getCell(j, i);
                    double num = Double.parseDouble(readCell.getContents());
                    addData[j - 4] = num;
                }

                dataset.add(addData);
            }

            Function<Double[], Double[]> scalingFunc = FeaturesScaling.createFunction(dataset);
            List<Double[]>  scaledDataset  = dataset.stream().map(scalingFunc).collect(Collectors.toList());

            // create the labels
            List<Double> labels = new ArrayList<Double>();
            for(int i = 0; i < sheet.getRows(); i++){
                Cell readCell = sheet.getCell(9, i);
                double addLabel = Double.parseDouble(readCell.getContents());
                labels.add(addLabel);                
            }


            System.out.println("Re-learn algorithm? (y/n)");
            char yn = sc.next().charAt(0);

            JButton relearn = new JButton("Relearn");
            JButton reuse = new JButton("Don't learn");

            relearn.setLocation(yn, yn);
            relearn.setPreferredSize(null);

            reuse.setLocation(yn, yn);
            reuse.setPreferredSize(null);

            LinearRegressionFunction targetFunction;


            //if "y", relearn the algorithm
            if(yn == 'y'){
                FileWriter fw = new FileWriter(THETA_STORE);
                BufferedWriter bufferWrite = new BufferedWriter(fw);

                // create hypothesis function with initial thetas and train it with learning rate 0.1
                targetFunction =  new LinearRegressionFunction(new double[] { 0.0, 0.0, 0.0, 0.0, 0.0}); //it doesnt matter what the thetas start as
                for (int i = 0; i < 10000; i++) {
                    targetFunction = LinearRegressionFunction.train(targetFunction, scaledDataset, labels, 0.1);
                }

                String thetas = targetFunction.thetasString();

                bufferWrite.write(thetas);

                bufferWrite.close();
            }

            else{

                FileReader fr = new FileReader(THETA_STORE);
                BufferedReader bufferRead = new BufferedReader(fr);


                //get theta values from file
                double[] thetas = new double[5];

                String str = bufferRead.readLine();
                String[] split = str.split(" ");

                for(int i = 0; i < thetas.length; i++){
                    //read file and add it to thetas[]
                    thetas[i] = Double.parseDouble(split[i]);

                }

                targetFunction =  new LinearRegressionFunction(thetas);

                bufferRead.close();

            }


            // make a prediction of past sales for each quarter given
            // Double[] scaledFeatureVector = scalingFunc.apply(new Double[] {93088.48, 93088.48, 93088.48, 94300.69, 79395.64});

            // double predictedPrice = targetFunction.apply(scaledFeatureVector);

            ArrayList<Double> accuracies = new ArrayList<Double>();

            for(int i = 0; i < compareSheet.getRows(); i++){
                //get the last 5 quarter sales
                Double[] last5data = new Double[5];

                Cell compName = compareSheet.getCell(1, i);
                Label addComp = new Label(1, i + 1, compName.getContents());

                Cell compID = compareSheet.getCell(0, i);
                Number addID = new Number(0, i + 1, Double.parseDouble(compID.getContents()));

                writeSheet.addCell(addComp);
                writeSheet.addCell(addID);
                
                for(int j = 4; j < 9; j++){
                    last5data[j - 4] = Double.parseDouble(compareSheet.getCell(j, i).getContents());
                    Number tempAdd = new Number(j - 1, i + 1, last5data[j - 4]);
                    writeSheet.addCell(tempAdd);
                }

                //apply scaling
                Double[] scaledFeatureVector = scalingFunc.apply(last5data);
                //create preidction
                double predicted = targetFunction.apply(scaledFeatureVector);
                Number predictWrite = new Number(9, i + 1, predicted);
                writeSheet.addCell(predictWrite);
                //get % difference between prediction and actual
                double actualValue = Double.parseDouble(compareSheet.getCell(9, i).getContents());
                Number actualWrite = new Number (10, i + 1, actualValue);
                writeSheet.addCell(actualWrite);

                double accuracy = 0;
                double difference = 0;

                if(predicted > actualValue){
                    accuracy = actualValue/predicted;
                    difference = predicted - actualValue;
                }
                else{
                    accuracy = predicted/actualValue;
                    difference = actualValue - predicted;
                }

                WritableCellFormat format = new WritableCellFormat();

                if(accuracy < .5){
                    format.setBackground(Colour.RED);
                }
                else if(accuracy < .8){
                    format.setBackground(Colour.YELLOW);
                }
                else if(accuracy < .9){
                    format.setBackground(Colour.LIGHT_GREEN);
                }
                else{
                    format.setBackground(Colour.GREEN);
                }
                
                Number acc = new Number(12, i + 1, accuracy, format);
                writeSheet.addCell(acc);

                Number diff = new Number(13, i + 1, difference);
                writeSheet.addCell(diff);

                //add to array list
                accuracies.add(accuracy);
            }

            //find average of array list
            double total = 0;
            double best = 0;
            for(int i = 0; i < accuracies.size(); i++){
                total += accuracies.get(i);
                if(accuracies.get(i) > best){
                    best = accuracies.get(i);
                }
            }
            double percent = total/accuracies.size() * 100;

            //round percent
            double temp = Math.round(percent);
            percent = temp / 100;

            //print % accuracy
            System.out.println("Accurate to " + percent*100 + "% on average");
            System.out.println("Best guess is " + best*100 + "% accurate");


            write.write();

/*______________________________________________________________________________________________________*/
            //now predicting real future sales:


            for(int i = 0; i < compareSheet.getRows(); i++){
                //get the last 5 quarter sales
                Double[] last5data = new Double[5];

                Cell compName = compareSheet.getCell(1, i);
                Label addComp = new Label(1, i + 1, compName.getContents());

                Cell compID = compareSheet.getCell(0, i);
                Number addID = new Number(0, i + 1, Double.parseDouble(compID.getContents()));

                predictions.addCell(addComp);
                predictions.addCell(addID);
                
                for(int j = 5; j < 10; j++){
                    last5data[j - 5] = Double.parseDouble(compareSheet.getCell(j, i).getContents());
                    Number tempAdd = new Number(j - 1, i + 1, last5data[j - 5]);
                    predictions.addCell(tempAdd);
                }

                //apply scaling
                Double[] scaledFeatureVector = scalingFunc.apply(last5data);
                //create prediction
                double predicted = targetFunction.apply(scaledFeatureVector);
                Number predictWrite = new Number(10, i + 1, predicted);
                predictions.addCell(predictWrite);
                
            }

            predict.write();





        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }finally {

            if (data != null) {
                data.close();
            }

            if (write != null) {
                try {
                    write.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }

            if (predict != null) {
                try {
                    predict.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }



        }

        
    }

    public void actionPerformed(ActionEvent e){
            
    }
}
