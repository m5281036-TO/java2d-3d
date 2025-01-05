// importing libraries
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ShapeDrawer extends JPanel {
    private List<List<Point2D.Float>> shapes;
       

    public ShapeDrawer(String filePath, float margin_x, float margin_y, float magnification) {
        shapes = loadPointsFromFile(filePath, margin_x, margin_y, magnification);
    }


    // load coordinates in a vert file as "points" buffer then store them in "shapes" variable
    private List<List<Point2D.Float>> loadPointsFromFile(String filePath, float margin_x, float margin_y, float magnification) {
        // initialization of lists
        List<Point2D.Float> points = new ArrayList<>();
        List<List<Point2D.Float>> shapes = new ArrayList<>();

        // read a vert file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // read the 1st row -> number of shapes
            int numShapes = Integer.parseInt(br.readLine().trim());
            System.out.println("The number of shapes to be drawn: " + numShapes);

            // read next rows (loop for the times of number of shapes)
            for (int i = 0; i < numShapes; i++){

                // read the 2nd row -> number of points
                int numPoints = Integer.parseInt(br.readLine().trim());
                System.out.println("The number of point in shape " + (i+1) + ": " + numPoints);

                // read the rest of the rows
                String line;
                for (int j = 0; j < numPoints; j++){
                // if the last row is empty, ignore it (last point is automatically connected to the first point)
                    if((line = br.readLine()) != null) {
                        String[] coords = line.trim().split(" "); // split a raw with blank
                        
                        float x = (Float.parseFloat(coords[0]) + margin_x) * magnification;
                        // if (j == 0 && x < minX) minX = x; // storing min and max value in shape 1
                        // if (j == 0 && x > maxX) maxX = x;

                        float y = (Float.parseFloat(coords[1]) + margin_y) * magnification;
                        // if (j == 0 && y < minY) minX = y;
                        // if (j == 0 && y > maxY) maxY = y;

                        // add coordinate to the points buffer
                        Point2D.Float point = new Point2D.Float(x,y);
                        points.add(point);

                    }
                }

                // add "points" to the parent list: "shapes"
                shapes.add(new ArrayList<>(points));
                // clear points (buffer)
                points.clear();
                
            }

        // error    
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return shapes;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < shapes.size(); i++){
            int numPoints = shapes.get(i).size();

            // draw main lines (curves)
            for (int j = 0; j < numPoints; j++) {
                // initialization of points
                Point2D.Float p_current = shapes.get(i).get(j);
                Point2D.Float p_next = shapes.get(i).get((j + 1) % numPoints); // connect first and last point with line
                Point2D.Float p_before;
                if(j == 0){
                    // set last point as p_before 
                    p_before = shapes.get(i).get((numPoints-1) % numPoints);

                }
                else{ // j > 0
                    p_before = shapes.get(i).get((j - 1) % numPoints);
                }

                // compute the discrete curvature
                double angle = calculateAngle(p_before, p_current, p_next);
                System.out.println("Discrete curvature (theta) at (" + p_current.x + ", " + p_current.y + "): " + angle / Math.PI  + " (" + Math.toDegrees(angle) + " degree)");
                                
                // draw a main line (curve)
                // set color and boldness
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine((int) p_current.x, (int) p_current.y, (int) p_next.x, (int) p_next.y);

                // draw a unit tangent to a curve
                // set color and boldness
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(1));
                Point2D.Float p_unit_tan = getUnitTangentPoint(p_before, p_current, p_next);
                g2d.drawLine((int) p_current.x, (int) p_current.y, (int) p_unit_tan.x, (int) p_unit_tan.y);

            }

        }

        // TODO: draw a norm
    }


    // function to compute discrete curvature at the point
    private double calculateAngle(Point2D.Float p_before, Point2D.Float p_current, Point2D.Float p_next) {
        // compute two vectors around a point
        float v1x = p_current.x - p_before.x;
        float v1y = p_current.y - p_before.y;
        float v2x = p_next.x - p_current.x;
        float v2y = p_next.y - p_current.y;

        // compute the inner product of two vectors
        float dotProduct = v1x * v2x + v1y * v2y;

        // get the size of two vectors
        double magnitude1 = Math.sqrt(v1x * v1x + v1y * v1y);
        double magnitude2 = Math.sqrt(v2x * v2x + v2y * v2y);

        double cosTheta = dotProduct / (magnitude1 * magnitude2);

        // return degree as radian
        return Math.acos(cosTheta);
    }


    // computation of a unit tangent
    private Point2D.Float getUnitTangentPoint (Point2D.Float p_before, Point2D.Float p_current, Point2D.Float p_next){
        float tangent_x = p_next.x - p_before.x;
        float tangent_y = p_next.y - p_before.y;

        float length = (float) Math.sqrt(tangent_x * tangent_x + tangent_y * tangent_y);
        float unit_tangent_x = tangent_x / length;
        float unit_tangent_y = tangent_y / length;
        Point2D.Float p_unit_tan = new Point2D.Float(p_current.x + unit_tangent_x, p_current.y + unit_tangent_y);

        return p_unit_tan;
    }


    private static void displayPanel (String filename, float margin_x, float margin_y, float magnification, boolean is_last_window){
        // init
        String dir = "./vert/";
        String filepath = dir + filename;

        System.out.println(filepath);

        // init of frame
        JFrame frame = new JFrame(filename);
        ShapeDrawer panel = new ShapeDrawer(filepath,  margin_x, margin_y, magnification);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);
        
        // exit at last
        if (is_last_window == true){
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

    }


    // main function
    public static void main(String[] args) {
        // path of all vert files
        List<String> filenames = new ArrayList<String>(){
            {
                add("disk.vert");
                add("key.vert");
                add("riderr.vert");
                add("superior.vert");
            }
        };

        // disk.vert
        displayPanel(filenames.get(0), 1, 1, 100, true);

        // key.vert
        displayPanel(filenames.get(1), 2, 1, 3, false);

        // riderr.vert
        displayPanel(filenames.get(2), 10, 6, 20, false);

        // superior.vert
        displayPanel(filenames.get(3), 0, 1, 600, false);
    }
}
