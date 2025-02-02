import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


public class SurfaceDrawer extends JFrame {

    private List<Point3D> pointCloud;
    private List<Point3D> pointCloudOriginal;
    private String filepath;
    private float MOVING_AMOUNT = 50; // amount of motion with one button click
    private float ROTATION_DEGREE = 15; // amount of rotation with one button click
    private double rotationX = 0;
    private double rotationY = 0;



    // ------ main instance ------
    public SurfaceDrawer() {
        setTitle("3D Point Cloud Visualization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        filepath = getFilepath();
        pointCloud = getPointCloudFromFile(filepath);
        pointCloudOriginal = getPointCloudFromFile(filepath); // copy original
        scalePointCloud(100);
        add(new Canvas3D());
        addKeyListener(new SurfaceDrawerKeyListener(this));
        }


    // ------ function to specify xyz file path ------
    private String getFilepath() {
        String dir = "../data/xyz/";
        String filename = "bunny.xyz";
        String filepath = dir + filename;
        return filepath;
    }

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


    // reference for scaling and orientation (Japanese): 
    // http://www.maroon.dti.ne.jp/koten-kairo/works/Java3D/transform2.html (basic transformation method of objects)
    // ------ function to scale the shape ------
    public void scalePointCloud(double scaleFactor) {
        for (int i = 0; i < pointCloud.size(); i++) {
            Point3D point = pointCloud.get(i);
            pointCloud.set(i, new Point3D(
                point.getX() * scaleFactor,
                point.getY() * scaleFactor,
                point.getZ() * scaleFactor
            ));
        }
        repaint();
    }

    // ------ function to move the shape ------
    public void movePointCloud(String direction, float movingAmount) {
        for (int i = 0; i < pointCloud.size(); i++) {
            Point3D point = pointCloud.get(i);
            if (direction == "left") {
                pointCloud.set(i, new Point3D(
                    point.getX() + movingAmount,
                    point.getY(),
                    point.getZ()
                ));
            } else if (direction == "above") {
                pointCloud.set(i, new Point3D(
                    point.getX(),
                    point.getY() - movingAmount,
                    point.getZ()
                ));
            } else if (direction == "below") {
                pointCloud.set(i, new Point3D(
                    point.getX(),
                    point.getY() + movingAmount,
                    point.getZ()
                ));
            } else if (direction == "right") {
                pointCloud.set(i, new Point3D(
                    point.getX() - movingAmount,
                    point.getY(),
                    point.getZ()
                ));
            } 
        }
        repaint();
    }

    // ------ functions to rotate the shape ------
    public void rotatePointCloudX(double angle) {
        rotationX += Math.toRadians(angle);
        repaint();
    }

    public void rotatePointCloudY(double angle) {
        rotationY += Math.toRadians(angle);
        repaint();
    }


    // ------ function to reset object orientation ------
    // TODO: set default object size as an approprimate size
    public void resetObject(){
        for (int i = 0; i < pointCloud.size(); i++) {
            Point3D original_point = pointCloudOriginal.get(i);
            pointCloud.set(i, new Point3D(
                original_point.getX(),
                original_point.getY(),
                original_point.getZ()
            ));
        }
        repaint();
    }




    private class Canvas3D extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // set background color
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // scaling and offset
            int width = getWidth();
            int height = getHeight();
            double scale = 2.0;
            int offsetX = width / 2;
            int offsetY = height / 2;

            // set plot color
            g2d.setColor(Color.BLUE);

            // controlling the display of the points
            for (Point3D point : pointCloud) {
                double p_x = point.getX();
                double p_y = point.getY();
                double p_z = point.getZ();

                // roatate in x axis
                double new_Y = p_y * Math.cos(rotationX) - p_z * Math.sin(rotationX);
                double new_Z = p_y * Math.sin(rotationX) + p_z * Math.cos(rotationX);
                p_y = new_Y;
                p_z = new_Z;

                // roatate in y axis
                double new_X = p_x * Math.cos(rotationY) + p_z * Math.sin(rotationY);
                p_z = -p_x * Math.sin(rotationY) + p_z * Math.cos(rotationY);
                p_x = new_X;

                // project 3D objects into 2D display
                int screenX = (int) (p_x / scale + offsetX);
                int screenY = (int) (-p_y / scale + offsetY);
                g2d.fillOval(screenX, screenY, 4, 4);
            }
        }
    }


    // ------ class to call point cloud data ------
    private static class Point3D {
        private final double x;
        private final double y;
        private final double z;

        public Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }
        public double getZ() {
            return z;
        }
    }


    // ------ main function ------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SurfaceDrawer frame = new SurfaceDrawer();
            frame.setVisible(true);
        });
    }
}
