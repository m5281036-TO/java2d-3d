import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public  class FileLoader extends JFrame{
    private List<Point3D> pointCloud;
    private List<Point3D> pointCloudOriginal;
    

        // ------ function to get point coordinates from the file ------
        private List<Point3D> getPointCloudFromFile(String filepath) {
            List<Point3D> points = new ArrayList<>();
            List<Point3D> norms = new ArrayList<>();
            String line;
    
            try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
                // read a line of the file
                while ((line = br.readLine()) != null) {
                    String[] coords = line.trim().split(" "); // split a raw with blank
    
                    // store first 3 numbers as coordinates (x, y, z)
                    float p_x = Float.parseFloat(coords[0]);
                    float p_y = Float.parseFloat(coords[1]);
                    float p_z = Float.parseFloat(coords[2]);
                    points.add(new Point3D(p_x, p_y, p_z));
    
                    // store next 3 numbers as norm
                    float norm_x = Float.parseFloat(coords[0]);
                    float norm_y = Float.parseFloat(coords[1]);
                    float norm_z = Float.parseFloat(coords[2]);
                    norms.add(new Point3D(norm_x, norm_y, norm_z));
                }
                
            } catch (IOException e) {
                // e.printStackTrace();
            }
    
            System.out.println("File read: " + filepath + "\nThe size of " + points.size() + " points");
    
            return points;
        }    
    
}
