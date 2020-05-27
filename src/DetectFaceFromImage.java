import org.opencv.core.Core;   
import org.opencv.core.Mat; 
import org.opencv.core.MatOfRect; 
import org.opencv.core.Point; 
import org.opencv.core.Rect; 
import org.opencv.core.Scalar; 

import org.opencv.imgcodecs.Imgcodecs;  
import org.opencv.imgproc.Imgproc; 
import org.opencv.objdetect.CascadeClassifier;  
  
public class DetectFaceFromImage
{  
   public static void main (String[] args) 
   {     
      // Grabbed this from a tutorial. I need to make it real time now\
	  // https://www.tutorialspoint.com/javaexamples/detect_face_in_image.htm
	   
      //Loading the OpenCV core library  
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 
      
      //Instantiating the CascadeClassifier 
      String xmlFile = "C:\\Users\\Bert\\Desktop\\Files\\Github\\Detect-Face\\lbpcascade_frontalface.xml"; // replace this with a url?
      CascadeClassifier classifier = new CascadeClassifier(xmlFile);

      //Reading the Image from the file and storing it in to a Matrix object 
      String file = "C:\\Users\\Bert\\Pictures\\ProfilePictures\\botero.jpg"; // maybe could be a link, not sure
      Mat src = Imgcodecs.imread(file);  
      
      //Detecting the face in the snap 
      MatOfRect faceDetections = new MatOfRect(); 	
      classifier.detectMultiScale(src, faceDetections);  
      System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));  // the % changes
      
      //Drawing boxes  
      for (Rect rect : faceDetections.toArray()) 
      { 
         Imgproc.rectangle(src,       //where to draw the box 
         new Point(rect.x, rect.y),   //bottom left 
         new Point(rect.x + rect.width, rect.y + rect.height),  //top right  
         new Scalar(0, 0, 255), 
         3);    //RGB color 
      } 
      
      //Writing the image     
      Imgcodecs.imwrite("c:\\users\\bert\\Desktop\\output1.jpg", src); 
      
      System.out.println("Image Processed");  
   }     
}