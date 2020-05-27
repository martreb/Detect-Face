import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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

public class DetectFace {
    public static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }
    public static Mat bufferedImageToMat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_COLOR);
    }
    public static void main(String[] args) throws IOException {
        // Grabbed this from a tutorial. I need to make it real time now\
        // https://www.tutorialspoint.com/javaexamples/detect_face_in_image.htm

        //Loading the OpenCV core library  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Instantiating the CascadeClassifier 
        String xmlFile = "C:\\Users\\Bert\\Desktop\\Files\\Github\\Detect-Face\\lbpcascade_frontalface.xml"; // replace this with a url?
        CascadeClassifier classifier = new CascadeClassifier(xmlFile);

        JFrame frame = new JFrame(""); // create the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //what happens when the frame is closed

        JLabel label = new JLabel("");

        frame.getContentPane().add(label, BorderLayout.CENTER); // create the components, and put them in the frame

        frame.pack(); //size it
        frame.setSize(640, 480);

        frame.setLocationRelativeTo(null); //center it

        frame.setVisible(true); //show it

        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            System.exit(0);
        }

        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        while (true) {
            BufferedImage image = webcam.getImage();

            Mat src = bufferedImageToMat(image); //convert buffered image to mat

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

            //Showing the image     
            try {
                label.setIcon(new ImageIcon(matToBufferedImage(src)));
            } catch (NullPointerException e) {
                System.out.println("");
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}