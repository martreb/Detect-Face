// most of this is from my other project video-chat

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

public class DetectFaceSender extends javax.swing.JFrame 
{   
    public DetectFaceSender()  
    {
        initComponents(); // will set up the jframe and what not
    }
    
    public static BufferedImage matToBufferedImage(Mat matrix) throws IOException 
    {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }
    
    public static Mat bufferedImageToMat(BufferedImage image) throws IOException 
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_COLOR);
    }

    // this method is called on in the constructor above. just sets up the jframe
    private void initComponents() 
    {              
        JFrame frame = new JFrame(""); // create the frame
        // imageHolder = new javax.swing.JLabel(); // get a label for images
        JButton captureButton = new javax.swing.JButton(); // make a button
        JPanel buttonPanel = new JPanel(); // make a panel to stick the button to
        captureButton.setText("Capture"); // names the button
        captureButton.addActionListener(new java.awt.event.ActionListener() // makes the button able to do stuff
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) 
            {
                captureButtonActionPerformed(evt);
            }
        });
        
        buttonPanel.add(captureButton); // stick the button to the panel
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // what happens when the frame is closed
        // frame.getContentPane().add(imageHolder, BorderLayout.CENTER); // create the components, and put them in the frame
        frame.pack(); // size it
        frame.setSize(640, 520);
        frame.setLocationRelativeTo(null); // center it
        frame.setVisible(true); // show it
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH); // sticks the panel and button to the bottom of the frame
    }             
    
    private void captureButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {                                              
        Thread thread1 = new Thread(new VideoFeed()); // when the button is clicked it will make a new thread
        thread1.start(); // the new thread runs the run method in the VideoFeed class
    }                                             
    
    public static void main(String args[]) 
    {        
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            { 
                new DetectFaceSender().setVisible(true); // will run the sender class, and show it's stuff
            }
        });
    }
    
    public class VideoFeed implements Runnable
    {
        @Override
        public void run()
        {
            try 
            {
                LiveFeed(); // will run the method livefeed on it's own thread
            } 
            catch (IOException | InterruptedException ex) // catches both of those exceptions 
            {
                Logger.getLogger(DetectFaceSender.class.getName()).log(Level.SEVERE, null, ex); // whatever this is shows up when it doesnt work
            }
        }
    }
    
    // private javax.swing.JLabel imageHolder; // just so that the things in livefeed know what it is
    
    public void LiveFeed() throws IOException, InterruptedException
    {        
    	// Grabbed this from a tutorial. I need to make it real time now\
        // https://www.tutorialspoint.com/javaexamples/detect_face_in_image.htm

        //Loading the OpenCV core library  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Instantiating the CascadeClassifier 
        String xmlFile = "C:\\Users\\Bert\\Desktop\\Files\\Github\\Detect-Face\\lbpcascade_frontalface.xml"; // replace this with a url?
        CascadeClassifier classifier = new CascadeClassifier(xmlFile);

        Webcam webcam = null;
        DataOutputStream sender = null;
                
        String ipAddress = JOptionPane.showInputDialog("Enter your ip address"); // will ask for an ip address before doing anything

        try
        {
            // DataOutputStream sender is made into a BufferedOutputStream using a new socket on localhost on port 8080
            sender = new DataOutputStream(new BufferedOutputStream(new Socket(ipAddress, 8080).getOutputStream()));   
        }
        catch(IOException e)
        {
        	// doesnt do anything about it teehee. should probably change this though
        }
        
        try
        {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(640,480)); // maybe make this 320, 240 if it lags
            webcam.open();
        }
        catch(WebcamException e)
        {
            System.out.println("No webcam to use");
            System.exit(0);
        }
            
        while (true) // this boolean needs to change
        {
            BufferedImage frame = webcam.getImage(); // get frame from webcam
            
            Mat src = bufferedImageToMat(frame); //convert buffered image to mat
            
            //Detecting the face in the snap 
            MatOfRect faceDetections = new MatOfRect();
            classifier.detectMultiScale(src, faceDetections);
            System.out.println(String.format("Detecting %s faces", faceDetections.toArray().length)); // the % changes
            
            //Drawing boxes  
            for (Rect rect: faceDetections.toArray()) {
                Imgproc.rectangle(src, //where to draw the box 
                    new Point(rect.x, rect.y), //bottom left 
                    new Point(rect.x + rect.width, rect.y + rect.height), //top right  
                    new Scalar(0, 0, 255),
                    3); //RGB color 
            }

            try // if this stuff doesn't work, then the chat isn't working. I should add more catch things for different reasons for the chat to end
            {
            	try 
            	{
                    frame = matToBufferedImage(src);
                    // imageHolder.setIcon(new ImageIcon(frame)); // puts an image on the label
                } 
            	catch (NullPointerException e) 
            	{
                    System.out.println("");
                }
            	
            	int frameWidth = frame.getWidth();
	            int frameHeight = frame.getHeight();
	
	            sender.writeInt(frameWidth);
	            sender.writeInt(frameHeight); 
	
	            int[] pixelData = new int[frameWidth * frameHeight];
	            frame.getRGB(0, 0, frameWidth, frameHeight, pixelData, 0, frameWidth);
	            
	            for (int i = 0; i < pixelData.length; i++)
	            {
	                sender.writeInt(pixelData[i]);
	            }
            }
            catch(java.net.SocketException e) // add more of these with different messages
            {
            	System.out.println("Chat ended");
            	System.exit(0); // just end it, because it's not going to restart probably. Maybe change 
            					// this? like if the receiver started up again? do that later for sure actually
            }

                                        
            Thread.sleep(30);   //sleep for 30 milliseconds, then a new picture is taken
        }
    } 
} 
