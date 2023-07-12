import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

import jxl.write.WriteException;

import java.awt.*;
import java.util.*;

public class Screen extends JPanel implements ActionListener{

    //create JButtons and JTextField
    private JButton start;
    private JButton oldData;
    private JButton newData;
    private JButton combineSheets;
    private JButton alreadyCombined;
    private JButton confirmLoc;

    private JButton close;

    private JTextField win_lossLoc;
    private JTextField acvPillarLoc;
    private JTextField allSRsLoc;
    private JTextField analyzerBookingLoc;
    private JTextField combinedDataLoc;

    private JTextField interceptIn;

    //create booleans to show/hide buttons and textfields
    private boolean showStart;
    private boolean showOldData;
    private boolean showNewData;
    private boolean showCombineSheets;
    private boolean showAlreadyCombined;
    private boolean showConfirmLoc;

    private boolean showWin_LossLoc;
    private boolean showAcvPillarLoc;
    private boolean showAllSrsLoc;
    private boolean showAnalyzerBookingLoc;
    private boolean showCombinedDataLoc;

    private boolean needToCombine;
    private boolean showErrorMessage;

    double intercept;
    
    //create object of dataAnalysis
    dataAnalysis DA;

    public Screen(){
        //initialize DA
        DA = new dataAnalysis();

        //create thread of DA
        Thread computationThread = new Thread(DA);
        computationThread.start();

        //jbuttons
        start = new  JButton();
        start.setFont(new Font("Arial", Font.BOLD, 20));
        start.setHorizontalAlignment(SwingConstants.CENTER);
        start.setBounds(279, 360, 200, 30);
        start.setText("START");
        this.add(start);
        start.addActionListener(this);

        oldData = new  JButton();
        oldData.setFont(new Font("Arial", Font.BOLD, 20));
        oldData.setHorizontalAlignment(SwingConstants.CENTER);
        oldData.setBounds(279, 360, 200, 30);
        oldData.setText("OLD DATA");
        this.add(oldData);
        oldData.addActionListener(this);

        newData = new  JButton();
        newData.setFont(new Font("Arial", Font.BOLD, 20));
        newData.setHorizontalAlignment(SwingConstants.CENTER);
        newData.setBounds(279, 310, 200, 30);
        newData.setText("NEW DATA");
        this.add(newData);
        newData.addActionListener(this);

        combineSheets = new  JButton();
        combineSheets.setFont(new Font("Arial", Font.BOLD, 16));
        combineSheets.setHorizontalAlignment(SwingConstants.CENTER);
        combineSheets.setBounds(279, 310, 200, 30);
        combineSheets.setText("COMBINE SHEETS");
        this.add(combineSheets);
        combineSheets.addActionListener(this);

        alreadyCombined = new JButton();
        alreadyCombined.setFont(new Font("Arial", Font.BOLD, 14));
        alreadyCombined.setHorizontalAlignment(SwingConstants.CENTER);
        alreadyCombined.setBounds(279, 360, 200, 30);
        alreadyCombined.setText("ALREADY COMBINED");
        this.add(alreadyCombined);
        alreadyCombined.addActionListener(this);

        confirmLoc = new JButton();
        confirmLoc.setFont(new Font("Arial", Font.BOLD, 16));
        confirmLoc.setHorizontalAlignment(SwingConstants.CENTER);
        confirmLoc.setBounds(279, 550, 200, 30);
        confirmLoc.setText("RUN");
        this.add(confirmLoc);
        confirmLoc.addActionListener(this);

        close = new JButton();
        close.setFont(new Font("Arial", Font.BOLD, 16));
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setBounds(279, 500, 200, 30);
        close.setText("CLOSE");
        this.add(close);
        close.addActionListener(this);


        //text fields
        win_lossLoc = new JTextField();
        win_lossLoc.setFont(new Font("Arial", Font.PLAIN, 20));
        win_lossLoc.setHorizontalAlignment(SwingConstants.CENTER);
        win_lossLoc.setBounds(200, 140, 400, 30);
        win_lossLoc.setText("win/loss file location");
        this.add(win_lossLoc);

        acvPillarLoc = new JTextField();
        acvPillarLoc.setFont(new Font("Arial", Font.PLAIN, 20));
        acvPillarLoc.setHorizontalAlignment(SwingConstants.CENTER);
        acvPillarLoc.setBounds(200, 190, 400, 30);
        acvPillarLoc.setText("ACV Pillar file location");
        this.add(acvPillarLoc);

        allSRsLoc = new JTextField();
        allSRsLoc.setFont(new Font("Arial", Font.PLAIN, 20));
        allSRsLoc.setHorizontalAlignment(SwingConstants.CENTER);
        allSRsLoc.setBounds(200, 240, 400, 30);
        allSRsLoc.setText("All SR's file location");
        this.add(allSRsLoc);

        analyzerBookingLoc = new JTextField();
        analyzerBookingLoc.setFont(new Font("Arial", Font.PLAIN, 20));
        analyzerBookingLoc.setHorizontalAlignment(SwingConstants.CENTER);
        analyzerBookingLoc.setBounds(200, 290, 400, 30);
        analyzerBookingLoc.setText("Analyzer Bookings file location");
        this.add(analyzerBookingLoc);

        combinedDataLoc = new JTextField();
        combinedDataLoc.setFont(new Font("Arial", Font.PLAIN, 20));
        combinedDataLoc.setHorizontalAlignment(SwingConstants.CENTER);
        combinedDataLoc.setBounds(200, 240, 400, 30);
        combinedDataLoc.setText("Excel file location");
        this.add(combinedDataLoc);

        interceptIn = new JTextField();
        interceptIn.setFont(new Font("Arial", Font.PLAIN, 20));
        interceptIn.setHorizontalAlignment(SwingConstants.CENTER);
        interceptIn.setBounds(200, 240, 400, 30);
        interceptIn.setText("");
        this.add(interceptIn);


        showStart = true;
        showOldData = false;
        showNewData = false;
        showCombineSheets = false;
        showAlreadyCombined = false;
        showConfirmLoc = false;

        showWin_LossLoc = false;
        showAcvPillarLoc = false;
        showAllSrsLoc = false;
        showAnalyzerBookingLoc = false;
        showCombinedDataLoc = false;

        showErrorMessage = false;

        setVisible(true);
        this.setLayout(null);
    }

    public Dimension getPreferredSize(){
        return new Dimension(800, 600);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //make buttons and text fields show/hide
        start.setVisible(showStart);
        oldData.setVisible(showOldData);
        newData.setVisible(showNewData);
        combineSheets.setVisible(showCombineSheets);
        alreadyCombined.setVisible(showAlreadyCombined);
        confirmLoc.setVisible(showConfirmLoc);

        win_lossLoc.setVisible(showWin_LossLoc);
        acvPillarLoc.setVisible(showAcvPillarLoc);
        allSRsLoc.setVisible(showAllSrsLoc);
        analyzerBookingLoc.setVisible(showAnalyzerBookingLoc);
        combinedDataLoc.setVisible(showCombinedDataLoc);

        close.setVisible(showStart);

        interceptIn.setVisible(showStart);

        if(showErrorMessage){
            g.drawString("ERROR: make sure the file is in .xls format", 20, 20);
        }
        
        repaint();
    }

    public void run(){
        showConfirmLoc = false;
        showWin_LossLoc = false;
        showAcvPillarLoc = false;
        showAllSrsLoc = false;
        showAnalyzerBookingLoc = false;
        showCombinedDataLoc = false;

        showErrorMessage = false;

        if(needToCombine){
            DA.combineFiles();
        }
        
        if(!needToCombine){
            DA.getCombinedFiles();
        }

        double[] thetas = DA.createThetas();
        System.out.println("Created default thetas");

        ArrayList<Double[]> trainData = DA.createTrainData();
        System.out.println("Created dataset from combinedData.xls");

        //if predicting bookedAmount
        // ArrayList<Double> bookedAmountLables = DA.createBookedAmountLabels(); 
        // System.out.println("Created booked amount labels");

        ArrayList<Double> acvLabels = DA.createAcvLabels();
        System.out.println("Created ACV Labels");

        ArrayList<Double[]> scaledData = DA.scaleData(trainData);
        System.out.println("Scaled data");

        try {
            LinearRegressionFunction acvTargetFunc = DA.trainData(thetas, acvLabels, scaledData, 0.1, 0, intercept, intercept);
            System.out.println("Created ACV target function");
            ArrayList<Double> acvPredictions = DA.predict(acvTargetFunc, scaledData, 0, intercept);
            System.out.println("Made ACV predictions");

            // LinearRegressionFunction bookedAmountTargetFunc = DA.trainData(thetas, bookedAmountLables, scaledData, 0.1, 1);
            // System.out.println("Created booked amount target function");
            // ArrayList<Double> bookedPredictions = DA.predict(bookedAmountTargetFunc, scaledData, 1);
            // System.out.println("Maded booked amount predictions");
            
            DA.writePredictions(acvPredictions, acvLabels);
            System.out.println("Wrote all predictions to excel");

            System.out.println("Wrote accuracies");

            System.out.println("Complete");

            showStart = true;

        } catch (Exception e1) {
            e1.printStackTrace();
        }

            //if predicting ACV
            //ArrayList<Double> acvLabels = DA.createAcvLabels();


    }
    


    public void actionPerformed(ActionEvent e){
        if(e.getSource() == start){
            showStart = false;

            
            // showNewData = true;
            // showOldData = true;
            intercept = Double.parseDouble(interceptIn.getText());

            System.out.println("\n\n");
            System.out.println("intercept: " + intercept);

            this.run();
        }
        
        else if(e.getSource() == newData){
            showNewData = false;
            showOldData = false;
            showCombineSheets = true;
            showAlreadyCombined = true;
        }
        
        else if(e.getSource() == oldData){
            showNewData = false;
            showOldData = false;
        }
        
        else if(e.getSource() == combineSheets){
            showCombineSheets = false;
            showAlreadyCombined = false;
            showConfirmLoc = true;
            showWin_LossLoc = true;
            showAcvPillarLoc = true;
            showAllSrsLoc = true;
            showAnalyzerBookingLoc = true;
            
            needToCombine = true;
        }
        
        else if(e.getSource() == alreadyCombined){
            showCombineSheets = false;
            showAlreadyCombined = false;
            showConfirmLoc = true;
            showCombinedDataLoc = true;

            needToCombine = false;
        }

        else if(e.getSource() == close){
            try {
                DA.closeExcel();
            } catch (WriteException | IOException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }
        
        else if(e.getSource() == confirmLoc){
            boolean correctFormat = true;
            
            //if at already combined state
            if(showCombinedDataLoc){
                String combinedDataLocString = combinedDataLoc.getText();
                int length = combinedDataLocString.length();
                String checkCombinedLoc = combinedDataLocString.substring(length - 3);
                if(!checkCombinedLoc.equals("xls")){
                    correctFormat = false;
                }
            }
            //if need to combine data
            else{
                String winLossLocString = win_lossLoc.getText();
                String checkWinLossLoc = winLossLocString.substring(winLossLocString.length() - 3);
                String acvPillarLocString = acvPillarLoc.getText();
                String checkAcvPillarLoc = acvPillarLocString.substring(acvPillarLocString.length() - 3);
                String allSRsLocString = allSRsLoc.getText();
                String checkAllSrsLoc = allSRsLocString.substring(allSRsLocString.length() - 3);
                String analyzerBookingLocString = analyzerBookingLoc.getText();
                String checkAnalyzerBookingLoc = analyzerBookingLocString.substring(analyzerBookingLocString.length() - 3);


                if(!checkWinLossLoc.equals("xls")){
                    correctFormat = false;
                }
                else if(!checkAcvPillarLoc.equals("xls")){
                    correctFormat = false;
                }
                else if(!checkAllSrsLoc.equals("xls")){
                    correctFormat = false;
                }
                else if(!checkAnalyzerBookingLoc.equals("xls")){
                    correctFormat = false;
                }
            }
            

            
            if(correctFormat){
                this.run();
            }

            else{
                showErrorMessage = true;
            }
        }
        
        
        repaint();
    }
}
