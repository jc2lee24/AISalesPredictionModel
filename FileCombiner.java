import java.util.*;
import java.io.*;

public class FileCombiner {
    
    String srLoc;
    String acvLoc;

    public FileCombiner(String srLoc, String acvLoc){
        this.srLoc = srLoc;
        this.acvLoc = acvLoc;

    }


    public void combine(String testDataLoc, String trainDataLoc) throws IOException{

        BufferedReader srIn = new BufferedReader(new InputStreamReader(new FileInputStream(srLoc)));
        BufferedReader acvIn = new BufferedReader(new InputStreamReader(new FileInputStream(acvLoc)));

        BufferedWriter testOut = new BufferedWriter(new FileWriter(testDataLoc));
        BufferedWriter trainOut = new BufferedWriter(new FileWriter(trainDataLoc));

        String srInput; 
        srInput = srIn.readLine();
        srInput = srIn.readLine();

        srIn.mark(10000000);

        String acvInput;
        acvInput = acvIn.readLine();
        acvInput = acvIn.readLine();
        acvIn.mark(10000000);
        

        ArrayList<Integer> srHash = new ArrayList<>();
        ArrayList<Integer> acvHash = new ArrayList<>();

        ArrayList<Integer> sameHash = new ArrayList<>();

        //add all hashes to srHash
        while((srInput = srIn.readLine()) != null){
            String[] data = srInput.split(",");
            srHash.add(this.getHash(data[0]));
        }

        //add all hashes to acvHash
        while((acvInput = acvIn.readLine()) != null){
            String[] data = acvInput.split(",");
            acvHash.add(this.getHash(data[1]));
        }
        


        //find hashes that occur in both, add to sameHash
        for(int i = 0; i < srHash.size(); i++){
            int currHash = srHash.get(i);



            for(int j = 0; j < acvHash.size(); j++){
               
                //System.out.println(acvHash.get(j));
                if(acvHash.get(j) == currHash){
                    sameHash.add(currHash);
                    break;
                }
            }
        }

        srIn.reset();
        acvIn.reset();

        //combine the data
        for(int i = 0; i < sameHash.size(); i++){
            int currHash = sameHash.get(i);

            String addData = "";

            boolean srFound = false;
            while((srInput = srIn.readLine()) != null && !srFound){
                String[] tempData = srInput.split(",");
                if(currHash == this.getHash(tempData[0])){
                    
                    for(int j = 0; j < tempData.length - 3; j++){
                        addData += tempData[j] + ",";
                    }

                    // addData = srInput;

                    srIn.reset();
                    srFound = true;
                }
            }


            boolean acvFound = false;
            while((acvInput = acvIn.readLine()) != null && !acvFound){
                String[] tempData = acvInput.split("\",\"");
                if(currHash == this.getHash(tempData[1])){

                    int length = tempData.length - 1;
                    double percChange; 
                    double currSale = Double.parseDouble(tempData[length]);
                    double prevSale = Double.parseDouble(tempData[length]);



                    if(prevSale > currSale){
                        percChange = - (prevSale - currSale) / prevSale;
                    }
                    else if(prevSale == currSale){
                        percChange = 0;
                    }
                    else{
                        percChange = (currSale - prevSale) / prevSale;
                    }


                    int add;
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
                    if(percChange < -.5) add = 0;
                    
                    else if(percChange < -.35) add = 1;

                    else if(percChange < -.25) add = 2;

                    else if(percChange < -.15) add = 3;

                    else if(percChange < -.10) add = 4;

                    else if(percChange < -.05) add = 5;
                    
                    else if(percChange < -.03) add = 6;

                    else if(percChange < .03) add = 7;

                    else if(percChange < .05) add = 8;

                    else if(percChange < .10) add = 9;

                    else if(percChange < .15) add = 10;
                    
                    else if(percChange < .25) add = 11;

                    else if(percChange < .35) add = 12;
                    
                    else if(percChange < .5) add = 13;

                    else {add = 14;}

                    addData += add;

                    acvIn.reset();
                    acvFound = true;
                }
            }


            if(i%100 == 0){
                trainOut.write(addData);
                trainOut.newLine();
            } else {
                testOut.write(addData);
                testOut.newLine();
            }
        }


        srIn.close();
        acvIn.close();
        testOut.close();
        trainOut.close();
    }



    private int getHash(String input){
        String curr = input;

        int index = curr.indexOf("(");
        
        if(index > 3){
            curr = curr.substring(0, index);
        }

        if(curr.length() > 19){
            curr = curr.substring(0, 19);
        }


        // int hash = 0;
        // char[] charArray = curr.toCharArray();
        // for(int i = 0; i < charArray.length; i++){
        //     hash += Math.pow(charArray[i], i);
        // }

        // System.out.println(hash);

        return curr.hashCode();
    }

}
