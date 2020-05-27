// a lot of this code comes from my other project. Video-Chat. this one is actually
// basically the same, because I dont have to do any special stuff with the images.

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class DetectFaceReceiver extends javax.swing.JFrame 
{
    public DetectFaceReceiver() 
    {
        initComponents(); // this method makes a frame to use, and all of it's components
    }
                       
    private void initComponents() 
    {
        JFrame frame = new JFrame(""); // create the frame
        receiverImageHolder = new javax.swing.JLabel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // what happens when the frame is closed
        frame.getContentPane().add(receiverImageHolder, BorderLayout.CENTER); // create the components, and put them in the frame
        frame.pack(); // size it
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null); // center it
        frame.setVisible(true); // show it
    }              
    
    private static javax.swing.JLabel receiverImageHolder; // this is here so main knows what this variable is

    public static void main(String args[]) throws IOException 
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            {
                new DetectFaceReceiver().setVisible(true);
            }
        });
        
        ServerSocket server = new ServerSocket(8080);
        Socket socket = server.accept();
        
        try (DataInputStream rcv = new DataInputStream(new BufferedInputStream(socket.getInputStream())))
        {
            while (true)
            {
                int frameWidth = rcv.readInt();
                int frameHeight = rcv.readInt();

                int[] pixelData = new int[frameWidth * frameHeight];

                for (int i = 0; i < pixelData.length; i++)
                {
                    pixelData[i] = rcv.readInt();
                }

                BufferedImage frame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB);
                frame.setRGB(0, 0, frameWidth, frameHeight, pixelData, 0, frameWidth);
                
                receiverImageHolder.setIcon(new ImageIcon(frame));   
            }
        }
        catch(Exception e)
        {
            System.out.println("Chat ended on sending side.");
            server.close();
            socket.close();
            System.exit(0);
        }
                
    }
    
}
