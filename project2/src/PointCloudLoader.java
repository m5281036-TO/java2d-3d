// class to read .xyz file and returns point coordinates


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PointCloudLoader {

    public static List<Point3D> loadPointCloudFromFile(String filepath) {
        List<Point3D> points = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            while ((line = br.readLine()) != null) {
                String[] coords = line.trim().split(" "); // split line by space

                // Store first 3 numbers as coordinates (x, y, z)
                float p_x = Float.parseFloat(coords[0]);
                float p_y = Float.parseFloat(coords[1]);
                float p_z = Float.parseFloat(coords[2]);
                points.add(new Point3D(p_x, p_y, p_z));
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception as needed
        }

        return points;
    }

    public static String getFilePath() {
        String filepath = Config.FILE_DIR + MainApp.inputFileName + ".xyz";
        System.out.println(filepath);
        return filepath;
    }
}
