import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Screen extends JPanel implements ActionListener{
    
    DecisionTreeRunner dtr;
    Thread t1;

    private JButton acvPredict;
    private JButton srPredict;
    private JButton run;
    private JButton close;
    private JButton instructions;

    private JTextField acvDataLoc;
    private JTextField srDataLoc;

    private boolean showErrorMessage = false;

    private boolean showAcvPredict = true;
    private boolean showSrPredict = true;
    private boolean showRun = false;
    private boolean showClose = true;
    private boolean showInstructions = true;
    private boolean showAcvDataLoc = false;
    private boolean showSrDataLoc = false;

    private boolean linReg = false;
    private boolean decTree = false;

    private boolean showCompleteMessage = false;
    // private boolean showRunMessage = false;


    public Screen(){
        acvPredict = new JButton();
        acvPredict.setFont(new Font("Arial", Font.BOLD, 20));
        acvPredict.setHorizontalAlignment(SwingConstants.CENTER);
        acvPredict.setBounds(279, 280, 200, 30);
        acvPredict.setText("ACV Predictions");
        this.add(acvPredict);
        acvPredict.addActionListener(this);

        srPredict = new JButton();
        srPredict.setFont(new Font("Arial", Font.BOLD, 20));
        srPredict.setHorizontalAlignment(SwingConstants.CENTER);
        srPredict.setBounds(279, 320, 200, 30);
        srPredict.setText("SR Predictions");
        this.add(srPredict);
        srPredict.addActionListener(this);

        run = new  JButton();
        run.setFont(new Font("Arial", Font.BOLD, 20));
        run.setHorizontalAlignment(SwingConstants.CENTER);
        run.setBounds(279, 360, 200, 30);
        run.setText("RUN");
        this.add(run);
        run.addActionListener(this);

        close = new  JButton();
        close.setFont(new Font("Arial", Font.BOLD, 20));
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setBounds(279, 400, 200, 30);
        close.setText("EXIT");
        this.add(close);
        close.addActionListener(this);

        instructions = new  JButton();
        instructions.setFont(new Font("Arial", Font.BOLD, 20));
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        instructions.setBounds(279, 360, 200, 30);
        instructions.setText("Instructions");
        this.add(instructions);
        instructions.addActionListener(this);


        
    // SR_DATA = C:\Users\justinlee\Desktop\Code\randomForestV2\data\SRs2016-2023.csv;
    // ACV_DATA = C:\Users\justinlee\Desktop\Code\randomForestV2\data\ACV.csv;

        acvDataLoc = new JTextField();
        acvDataLoc.setFont(new Font("Arial", Font.PLAIN, 14));
        acvDataLoc.setHorizontalAlignment(SwingConstants.CENTER);
        acvDataLoc.setBounds(200, 190, 400, 30);
        acvDataLoc.setText("ACV DATA LOCATION HERE.csv");
        this.add(acvDataLoc);

        srDataLoc = new JTextField();
        srDataLoc.setFont(new Font("Arial", Font.PLAIN, 14));
        srDataLoc.setHorizontalAlignment(SwingConstants.CENTER);
        srDataLoc.setBounds(200, 230, 400, 30);
        srDataLoc.setText("SR DATA LOCATION HERE.csv");
        this.add(srDataLoc);


        dtr = new DecisionTreeRunner();

        t1 = new Thread(dtr);

        this.setLayout(null);

    }

    public Dimension getPreferredSize(){
        return new Dimension(800,600);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        acvPredict.setVisible(showAcvPredict);
        srPredict.setVisible(showSrPredict);
        run.setVisible(showRun);
        close.setVisible(showClose);
        instructions.setVisible(showInstructions);
        acvDataLoc.setVisible(showAcvDataLoc);
        srDataLoc.setVisible(showSrDataLoc);

        if(showErrorMessage){
            g.drawString("ERROR: make sure the file is in .csv format", 20, 20);
        }

        if(showCompleteMessage){
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("FINISHED CALCULATIONS", 260, 230);
            g.drawString("Written to predictions.csv, on desktop", 210, 260);
        }
        // if(showRunMessage){
        //     g.setFont(new Font("Arial", Font.BOLD, 20));
        //     g.drawString("RUNNING - DO NOT CLOSE", 260, 230);
        // }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == instructions){
            if(Desktop.isDesktopSupported()){
                try{
                    File instructionFile = new File(System.getProperty("user.home") + "\\Desktop\\Prediction_Program\\data\\instructions.pdf");
                    Desktop.getDesktop().open(instructionFile);
                } catch(IOException ex){
                    System.out.println(ex);
                }
            }
        }

        else if(e.getSource() == acvPredict){
            showInstructions = false;
            showAcvPredict = false;
            showSrPredict = false;
            showCompleteMessage = false;

            showAcvDataLoc = true;
            showRun = true;

            linReg = true;
        }

        else if(e.getSource() == srPredict){
            showInstructions = false;
            showAcvPredict = false;
            showSrPredict = false;
            showCompleteMessage = false;

            showAcvDataLoc = true;
            showSrDataLoc = true;
            showRun = true;

            decTree = true;
        }

        else if(e.getSource() == run){
            boolean correctFormat;
            String checkAcvLoc = acvDataLoc.getText().substring(acvDataLoc.getText().length() - 4);
            String checkSrLoc = srDataLoc.getText().substring(srDataLoc.getText().length() - 4);

            if(checkAcvLoc.equals(".csv") && checkSrLoc.equals(".csv")){
                correctFormat = true;
                showErrorMessage = false;
            }
            else{
                correctFormat = false;
                showErrorMessage = true;
            }

            if(correctFormat){
                if(decTree){

                    // showRunMessage = true;

                    dtr.getExcelLoc(srDataLoc.getText(), acvDataLoc.getText());
                    t1.run();

                    // showRunMessage = false;
                    showRun = false;
                    showAcvDataLoc = false;
                    showSrDataLoc = false;

                    showCompleteMessage = true;

                    if(Desktop.isDesktopSupported()){
                        try{
                            File predictFile = new File(System.getProperty("user.home") + "//Desktop//prediction.csv");
                            Desktop.getDesktop().open(predictFile);
                        } catch(IOException ex){
                            System.out.println(ex);
                        }
                    }

                    showAcvPredict = true;
                    showSrPredict = true;
                    showInstructions = true;
                    decTree = false;
                }

                else if(linReg){


                
                    // showRunMessage = true;


                    DataAnalysis DA = new DataAnalysis(acvDataLoc.getText());
                    try{
                        LinearRegressionFunction targetFunc = DA.learn();
                        DA.predit(targetFunc, acvDataLoc.getText());
                    } catch(IOException ex){
                        System.out.println(ex);
                    }
                    

                    // showRunMessage = false;

                    showAcvDataLoc = false;
                    showCompleteMessage = true;
                    showAcvPredict = true;
                    showSrPredict = true;
                    showInstructions = true;
                    linReg = false;
                }
            }
        }

        
        else if(e.getSource() == close){
            System.exit(0);
        }


        repaint();
    }

    public void open(){
        if(Desktop.isDesktopSupported()){
            try{
                File predictFile = new File(System.getProperty("user.home") + "//Desktop//prediction.csv");
                Desktop.getDesktop().open(predictFile);
            } catch(IOException ex){
                System.out.println(ex);
            }
        }
    }
}

