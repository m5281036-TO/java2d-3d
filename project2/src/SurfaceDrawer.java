import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class SurfaceDrawer extends JFrame {

    private List<Point3D> pointCloud;
    private String filepath;
    private float MOVING_AMOUNT = 50;


    // ------ main instance ------
    public SurfaceDrawer() {
        setTitle("3D Point Cloud Visualization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        filepath = getFilepath();
        pointCloud = getPointCloudFromFile(filepath);
        scalePointCloud(100);
        add(new Canvas3D());

        // keyboard listener
        // reference for on-button action
        // https://qiita.com/derodero24/items/9ea025b92ac61edf0aa4
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // scaling shapes
                if (e.getKeyChar() == '=') {
                    scalePointCloud(1.2); // zoom in
                    System.out.println("Zoom In x1.2");
                } else if (e.getKeyChar() == '-') {
                    scalePointCloud(0.85); // zoom out
                    System.out.println("Zoom Out x0.85");
                }
                // moving shapes
                else if (e.getKeyChar() == 'a') {
                    movePointCloud("l", MOVING_AMOUNT); // left
                    System.out.println("moving left");
                } else if (e.getKeyChar() == 'w') {
                    movePointCloud("a", MOVING_AMOUNT); // above
                    System.out.println("moving avobe");
                } else if (e.getKeyChar() == 's') {
                    movePointCloud("b", MOVING_AMOUNT); // below
                    System.out.println("moving below");
                } else if (e.getKeyChar() == 'd') {
                    movePointCloud("r", MOVING_AMOUNT); // right
                    System.out.println("moving right");   
                }
            }
        });
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
    // http://www.maroon.dti.ne.jp/koten-kairo/works/Java3D/transform2.html (basic transformation method)

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
    public void movePointCloud(String direction, double movingAmount) {
        for (int i = 0; i < pointCloud.size(); i++) {
            Point3D point = pointCloud.get(i);
            if (direction == "l") { //left
                pointCloud.set(i, new Point3D(
                    point.getX() + movingAmount,
                    point.getY(),
                    point.getZ()
                ));
            } else if (direction == "a") { //above
                pointCloud.set(i, new Point3D(
                    point.getX(),
                    point.getY() - movingAmount,
                    point.getZ()
                ));
            } else if (direction == "b") { //below
                pointCloud.set(i, new Point3D(
                    point.getX(),
                    point.getY() + movingAmount,
                    point.getZ()
                ));
            } else if (direction == "r") { //right
                pointCloud.set(i, new Point3D(
                    point.getX() - movingAmount,
                    point.getY(),
                    point.getZ()
                ));
            } 
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

            g2d.setColor(Color.BLUE);

            for (Point3D point : pointCloud) {
                // 3D座標を2Dに投影
                int screenX = (int) (point.getX() / scale + offsetX);
                int screenY = (int) (-point.getY() / scale + offsetY);
                g2d.fillOval(screenX, screenY, 4, 4);
            }
        }
    }


    // ------ class to store point cloud data ------
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
