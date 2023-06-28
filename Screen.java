import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel implements ActionListener{
    
    public Screen(){


        setVisible(true);
        this.setLayout(null);
    }

    public Dimension getPreferredSize(){
        return new Dimension(800, 600);
    }

    public void paintComponent(Graphics g){
        super.paintComponents(g);   
    }

    @Override
    public void actionPerformed(ActionEvent e){

    }
}
