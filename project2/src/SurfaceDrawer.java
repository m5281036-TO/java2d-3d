// importing libraries
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Surface3D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SurfaceDrawer extends JPanel {
    // init
    private List<Surface3D.Float> points;


    public SurfaceDrawer(String filepath){
        points = loadPointsFromFile(filepath);
    }


    // function to loasd points cloud from xyz file and store them in the "points"
    public void loadPointsFromFile (String filepath){
        //init
        String line;

        while((line = br.readLine()) != null) {
            String[] elements = line.trim().split(" "); // split a row with blank

            // store first 3 elements as coordinates
            float x = elements[0];
            float y = elements[1];
            float z = elements[2];
            Surface3D.Float point = new Surface3D.Float(x, y, z);
            
        }
    }



    public static void main(String args[]){

        //init
        String dir = "./data/xyz/";
        String filename = "bunny.xyz";
        String filepath = dir + filename;

        System.out.println(filepath);

        JFrame frame = new JFrame(filename);
        ShapeDrawer panel = new SurfaceDrawer(filepath);
    }
}

