// importing libraries

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShapeDrawer extends JPanel {
    private List<List<Point2D.Float>> shapes;
    private List<Point2D.Float> normalizedPoints;
    float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
    float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
       

    public ShapeDrawer(String filePath, float margin_x, float margin_y, float magnification) {
        shapes = loadPointsFromFile(filePath, margin_x, margin_y, magnification);
    }

    private List<List<Point2D.Float>> loadPointsFromFile(String filePath, float margin_x, float margin_y, float magnification) {
        List<Point2D.Float> points = new ArrayList<>();
        List<List<Point2D.Float>> shapes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // read the 1st row -> number of shapes
            int numShapes = Integer.parseInt(br.readLine().trim());
            System.out.println("The number of shapes to be drawn: " + numShapes);

            // loop for the times of number of shapes
            for (int i = 0; i < numShapes; i++){

                // read the 2nd row -> number of points
                int numPoints = Integer.parseInt(br.readLine().trim());
                System.out.println("The number of point in shape " + (i+1) + ": " + numPoints);

                // 残りの行を読み取り座標リストに追加
                String line;
                for (int j = 0; j < numPoints; j++){
                // if the last row is empty, ignore it  
                    if((line = br.readLine()) != null) {
                        String[] coords = line.trim().split(" "); // split a raw with blank
                        
                        float x = (Float.parseFloat(coords[0]) + margin_x) * magnification;
                        if (j == 0 && x < minX) minX = x; // storing min and max value in shape 1
                        if (j == 0 && x > maxX) maxX = x;

                        float y = (Float.parseFloat(coords[1]) + margin_y) * magnification;
                        if (j == 0 && y < minY) minX = y;
                        if (j == 0 && y > maxY) maxY = y;

                        // add coordinate to the points list
                        Point2D.Float point = new Point2D.Float(x,y);
                        points.add(point);

                    }
                    else{ // when empty row
                        System.out.println("The lsat row in input is empty");
                    }
                }

                System.out.println("shape1 min x: " + minX + " max x: " + maxX);
                System.out.println("shape1 min y: " + minY + " max y: " + maxY);


                // add "points" to the parent list: "shapes"
                shapes.add(new ArrayList<>(points));
                // clear points
                points.clear();
                
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return shapes;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // アンチエイリアスを有効化（任意）
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 線の色と太さを設定
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));


        // draw lines (curves)
        for (int i = 0; i < shapes.size(); i++){
            int numPoints = shapes.get(i).size();

            for (int j = 0; j < numPoints; j++) {
                Point2D.Float p = shapes.get(i).get(j);
                Point2D.Float p_next = shapes.get(i).get((j + 1) % numPoints); // connect first and last point with line
                g2d.drawLine((int) p.x, (int) p.y, (int) p_next.x, (int) p_next.y);

                // compute the discrete curvature
                if(j > 0){
                    Point2D.Float p_before = shapes.get(i).get((j - 1) % numPoints);
                    double angle = calculateAngle(p_before, p, p_next);
                    System.out.println("Discrete curvature (theta) at (" + p.x + ", " + p.y + "): " + angle / Math.PI  + " (" + Math.toDegrees(angle) + " degree)");
                }
                else if (j == 0){
                    // set last point as p_before 
                    Point2D.Float p_before = shapes.get(i).get((numPoints-1) % numPoints);
                    double angle = calculateAngle(p_before, p, p_next);
                    System.out.println("Discrete curvature (theta) at (" + p.x + ", " + p.y + "): " + angle / Math.PI  + " (" + Math.toDegrees(angle) + " degree)");
                
                }
            }
        }
    }


    // function to compute discrete curvature at the point
    private double calculateAngle(Point2D.Float p_before, Point2D.Float p, Point2D.Float p_next) {
        // compute two vectors around a point
        float v1x = p.x - p_before.x;
        float v1y = p.y - p_before.y;
        float v2x = p_next.x - p.x;
        float v2y = p_next.y - p.y;

        // compute the inner product of two vectors
        float dotProduct = v1x * v2x + v1y * v2y;

        // get the size of two vectors
        double magnitude1 = Math.sqrt(v1x * v1x + v1y * v1y);
        double magnitude2 = Math.sqrt(v2x * v2x + v2y * v2y);

        double cosTheta = dotProduct / (magnitude1 * magnitude2);

        // return degree as radian
        return Math.acos(cosTheta);
    }


    // main function
    public static void main(String[] args) {
        // path of all vert files
        String dir = "./vert/";
        List<String> filenames = new ArrayList<String>(){
            {
                add("disk.vert");
                add("key.vert");
                add("riderr.vert");
                add("superior.vert");
            }
        };

        // disk.vert
        String filepath = dir + filenames.get(0);
        JFrame frame = new JFrame(filenames.get(0));
        System.out.println(filepath);
        ShapeDrawer panel = new ShapeDrawer(filepath,  1, 1, 100);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);
        // exit at last
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // key.vert
        filepath = dir + filenames.get(1);
        frame = new JFrame(filenames.get(1));
        System.out.println(filepath);
        panel = new ShapeDrawer(filepath,  2, 1, 3);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // riderr.vert
        filepath = dir + filenames.get(2);
        frame = new JFrame(filenames.get(2));
        System.out.println(filepath);
        panel = new ShapeDrawer(filepath,  10, 6, 20);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // superior.vert
        filepath = dir + filenames.get(3);
        frame = new JFrame(filenames.get(3));
        System.out.println(filepath);
        panel = new ShapeDrawer(filepath,  0, 1, 600);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);

    }
}
