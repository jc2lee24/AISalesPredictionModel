import java.io.IOException;

import javax.swing.JFrame;


public class Runner{

	public static void main(String[] args) throws Exception{
	
		JFrame fr = new JFrame("Prediction Model");
		Screen sc = new Screen();
		
		fr.add(sc);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.pack();
		fr.setVisible(true);

        try{
            sc.run();
        } catch(IOException e){
            System.out.println(e);
        }
	}

}
