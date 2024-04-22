import java.util.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWitdth = 360;
        int boardHeight = 640;
        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        frame.setSize(boardWitdth, boardHeight); 
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    
        Flappybirds flappybird = new Flappybirds();
        frame.add(flappybird); 
        frame.pack();
        flappybird.requestFocus();                                     
        frame.setVisible(true);
    }
}
