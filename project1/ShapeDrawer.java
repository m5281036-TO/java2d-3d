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
            for (int j = 0; j < shapes.get(i).size(); j++) {
                Point2D.Float p1 = shapes.get(i).get(j);
                Point2D.Float p2 = shapes.get(i).get((j + 1) % shapes.get(i).size()); // connect first and last point with line
                g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
                

                // compute an angle and print
                // TODO: make it work
                if(j > 0){
                    // double angle = calculateAngle(p_before, p1, p2);
                    Point2D.Float p_before = shapes.get(i).get((j - 1) % shapes.get(i).size());
                    System.out.println ("before: " + p_before);
                    System.out.println ("p1: " + p1);
                    System.out.println ("after: " + p2);
                }
            }
        }
    }


    // function to compute the two lines
    // private double calculateAngle(Point2D.Float p1, Point2D.Float p2, Point2D.Float p3) {
    //     // ベクトルp1->p2とp2->p3を計算
    //     float v1x = p2.x - p1.x;
    //     float v1y = p2.y - p1.y;
    //     float v2x = p3.x - p2.x;
    //     float v2y = p3.y - p2.y;

    //     // ベクトルの内積を計算
    //     float dotProduct = v1x * v2x + v1y * v2y;

    //     // ベクトルの大きさを計算
    //     double magnitude1 = Math.sqrt(v1x * v1x + v1y * v1y);
    //     double magnitude2 = Math.sqrt(v2x * v2x + v2y * v2y);

    //     // cosθ = (内積) / (|v1| * |v2|)
    //     double cosTheta = dotProduct / (magnitude1 * magnitude2);

    //     // 角度をラジアンから度に変換して返す
    //     return Math.toDegrees(Math.acos(cosTheta));
    // }


    // main function
    public static void main(String[] args) {
        // TODO: 複数のファイルインプットに対応したJPanel
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

        // key.vert
        filepath = dir + filenames.get(1);
        frame = new JFrame(filenames.get(1));
        System.out.println(filepath);
        panel = new ShapeDrawer(filepath,  2, 2, 5);
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

        // exit at last
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
