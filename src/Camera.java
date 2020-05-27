import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;

public class Camera
{
	public static void main(String[] args) 
	{
		JFrame frame = new JFrame("");  // create the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //what happens when the frame is closed
		
	    JLabel label = new JLabel("");
		
		frame.getContentPane().add(label, BorderLayout.CENTER); // create the components, and put them in the frame
	
		frame.pack(); //size it
		frame.setSize(640,480); 
		
		frame.setLocationRelativeTo(null); //center it
		
		frame.setVisible(true); //show it
		
		Webcam webcam = Webcam.getDefault();
        if (webcam != null) 
        {
            System.out.println("Webcam: " + webcam.getName());
        } 
        else 
        {
            System.out.println("No webcam detected");
            System.exit(0);
        }
        
        webcam.setViewSize(new Dimension(640,480));
        webcam.open();
        
        while(true) 
        {
            BufferedImage image = webcam.getImage(); 

            //int frameWidth = frame.getWidth();
            //int frameHeight = frame.getHeight();

            //int[] pixelData = new int[frameWidth * frameHeight];
            //frame.getRGB(0, 0, frameWidth, frameHeight, pixelData, 0, frameWidth);
            
            try 
            {
                label.setIcon(new ImageIcon(image));
            }
            catch(NullPointerException e)
            {
            	System.out.println(""); 
            }
            
            try 
            {
				Thread.sleep(30);
			} 
            catch (InterruptedException e) 
            {
				e.printStackTrace();
			}    
        }
	}
}
