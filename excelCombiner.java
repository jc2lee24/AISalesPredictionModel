import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class excelCombiner {

    private static final String COMBINED_DATA = "C:\\Users\\justinlee\\Desktop\\Code\\aiSalesPredictor2.0\\data\\combinedData.xls";

    private WritableWorkbook combinedDataExcel;

    private WritableSheet combinedDataSheet;

    //get sheets
    private Sheet winLossSheet;
    private Sheet ACVPillarSheet;
    private Sheet allSRSheet;
    private Sheet analyzerBookingsSheet;

    ArrayList<Integer> idList;





    public excelCombiner(Sheet winLossSheet, Sheet ACVPillarSheet, Sheet allSRSheet, Sheet analyzerBookingsSheet, Sheet comparisonSheet, Sheet predictSheet) throws IOException{

        combinedDataExcel= Workbook.createWorkbook(new File(COMBINED_DATA));

        combinedDataSheet = combinedDataExcel.createSheet("COMBINED DATA", 0);
    
        this.winLossSheet = winLossSheet;
        this.ACVPillarSheet = ACVPillarSheet;
        this.allSRSheet = allSRSheet;
        this.analyzerBookingsSheet = analyzerBookingsSheet;

        this.idList = new ArrayList<Integer>();

        
    }

    //whenever we write, we start at row 1
    //we save row 0 for headings
    public void combine() throws RowsExceededException, WriteException, IOException{

        try{            
            //add data from win-loss
            int col = 1;
            for(int c = 1; c < winLossSheet.getRows(); c++){
                boolean add = true;

                //check if it has an ID, and if it has already been added
                Cell idCell = winLossSheet.getCell(7, c);
                if(idCell.getContents().isEmpty()){
                    add = false;
                }
                if(add){
                    for(int i = 0; i < idList.size(); i++){
                        if(idList.get(i) == Integer.parseInt(idCell.getContents())){
                            add = false;
                        }
                    }
                }

                if(add){
                    //add id
                    int id = Integer.parseInt(idCell.getContents());
                    idList.add(id);
                    Number addCompID = new Number(0, col, id);

                    //add company name
                    String addCompNameString = winLossSheet.getCell(2, c).getContents();
                    Label addCompNameLab = new Label(1, col, addCompNameString);

                    //add annualized amount
                    double annualizedAmt = Double.parseDouble(winLossSheet.getCell(3, c).getContents());
                    Number addAnnualizedAmt = new Number(2, col, annualizedAmt);
                    
                    //add supports amount
                    double supportAmt;
                    if(!winLossSheet.getCell(27, c).getContents().isEmpty()){
                        supportAmt = Double.parseDouble(winLossSheet.getCell(27, c).getContents());
                    }
                    else{
                        supportAmt = 0;
                    }
                    Number addSupportAmt = new Number(3, col, supportAmt);

                    //add products amount
                    double productAmt = Double.parseDouble(winLossSheet.getCell(28, c).getContents());
                    Number addProductAmt = new Number(4, col, productAmt);

                    //add services amount
                    double servicesAmt = Double.parseDouble(winLossSheet.getCell(29, c).getContents());
                    Number addserviesAmt = new Number(5, col, servicesAmt);
                    

                    combinedDataSheet.addCell(addCompID);
                    combinedDataSheet.addCell(addCompNameLab);
                    combinedDataSheet.addCell(addAnnualizedAmt);
                    combinedDataSheet.addCell(addSupportAmt);
                    combinedDataSheet.addCell(addProductAmt);
                    combinedDataSheet.addCell(addserviesAmt);
                    

                    col++;
                    

                }
            }

            // now we have to check if it exist before we add more data
            //if it exist, then we can add it to that row
            //if if doesnt exist, the data is incomplete, and we probably can't add the data


            //now reading ACV Pillar
            for(int i = 2; i < ACVPillarSheet.getRows() - 3; i++){
                boolean add = false;
                int row = 0;
                int acvID = Integer.parseInt(ACVPillarSheet.getCell(0, i).getContents());

                //check if it is arrayList of IDs
                for(int j = 0; j < idList.size(); j++){
                    if(idList.get(j) == acvID){
                        add = true;
                        row = j + 1;
                        break;
                    }
                }

                if(add){
                    System.out.println("adding");

                    //get the ACV amounts for last 6 quarters

                    //convert pillar type to num
                    int pillarNum = 0;
                    String pillar = ACVPillarSheet.getCell(2, i).getContents();
                    if(pillar.equals("QUANTUM")){
                        pillarNum = 1;
                    } 
                    else if(pillar.equals("HARMONY")){
                        pillarNum = 2;
                    }
                    else if(pillar.equals("CLOUDGAURD")){
                        pillarNum = 3;
                    }
                    else{
                        pillarNum = 4;
                    }

                    Number addPillar = new Number(6, row, pillarNum);
                    combinedDataSheet.addCell(addPillar);

                    for(int j = 3; j < 9; j++){

                        //get ACV 12.21 data
                        Cell ACV = ACVPillarSheet.getCell(j, i);
                        double acvAmt;
                        String input;
                        //check if its empty
                        
                        if(ACV.getContents().isEmpty()){
                            //add 0 instead
                            acvAmt = 0;
                        }
                        else{
                            input = ACV.getContents();
                            String numString = String.valueOf(input);
                            acvAmt = Double.parseDouble(numString.replace(",", ""));                        
                        }

                        Number addAcvAmt = new Number(j + 4, row, acvAmt);

                        combinedDataSheet.addCell(addAcvAmt);
                    }
                }
            }


            //now adding SRs data
            for(int i = 1; i < allSRSheet.getRows(); i++){
                boolean add = false;
                int row = 0;
                if(!allSRSheet.getCell(2, i).getContents().isEmpty() || !allSRSheet.getCell(2, i).getContents().equals("")){
                    //System.out.println(allSRSheet.getCell(2, i).getContents());
                    int srsID = Integer.parseInt(allSRSheet.getCell(2, i).getContents());
                    //check if it is arrayList of IDs
                    for(int j = 0; j < idList.size(); j++){
                        if(idList.get(j) == srsID){
                            add = true;
                            row = j + 1;
                            break;
                        }
                    }                               
                }  
                
                if(add){
                    System.out.println("adding");
                    
                    double tickDur = Double.parseDouble(allSRSheet.getCell(5, i).getContents());
                    String severity = allSRSheet.getCell(8, i).getContents();

                    int sevInt = 0;
                    if(severity.equals("High")){
                        sevInt = 3;
                    }
                    else if(severity.equals("Medium")){
                        sevInt = 2;
                    }
                    else{
                        sevInt = 1;
                    }

                    Number tickDurAdd = new Number(13, row, tickDur);
                    Number severityAdd = new Number(14, row, sevInt);

                    combinedDataSheet.addCell(tickDurAdd);
                    combinedDataSheet.addCell(severityAdd);

                }
            }


            //now adding Analyzer Bookings 2021 data
            for(int i = 2; i < analyzerBookingsSheet.getRows() - 4; i++){
                boolean add = false;
                int row = 0;
                if(analyzerBookingsSheet.getCell(0, i).getContents().isEmpty()){
                    add = false;
                }
                else{
                    int analBookID = Integer.parseInt(analyzerBookingsSheet.getCell(0, i).getContents());

                    //check if it is arrayList of IDs
                    for(int j = 0; j < idList.size(); j++){
                        if(idList.get(j) == analBookID){
                            add = true;
                            row = j + 1;
                            break;
                        }
                    }    
                }
                
                
                if(add){
                    System.out.println("adding");
                    for(int j = 3; j < 12; j++){
                        Cell bookingCell = analyzerBookingsSheet.getCell(j, i);
                        
                        double addNum;
                        if(bookingCell.getContents().isEmpty()){
                            addNum = 0;
                        }
                        else{
                            addNum = Double.parseDouble(bookingCell.getContents());
                        }

                        Number addBooking = new Number(j + 12, row, addNum);
                        combinedDataSheet.addCell(addBooking);

                    }
                }
                
            }
                    
            this.addZero();
            
            combinedDataExcel.write();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (combinedDataExcel != null) {
                try {
                    combinedDataExcel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    public void addZero() throws RowsExceededException, WriteException{
        for(int c = 0; c < 24; c++){
            for(int r = 1; r < idList.size(); r++){
                Cell temp = combinedDataSheet.getCell(c, r);
                if(temp.getContents().isEmpty()){
                    Number zero = new Number(c, r, 0);
                    combinedDataSheet.addCell(zero);
                }
            }
        }
    }
}
