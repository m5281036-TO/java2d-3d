// importing libraries

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class ShapeDrawer extends JPanel {
    private List<List<Point2D.Float>> shapes;

    public ShapeDrawer(String filePath) {
        shapes = loadPointsFromFile(filePath);
    }

    private List<List<Point2D.Float>> loadPointsFromFile(String filePath) {
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
                    line = br.readLine();
                    String[] coords = line.trim().split(" ");
                    float x = Float.parseFloat(coords[0]);
                    float y = Float.parseFloat(coords[1]);

                    // add coordinate to the points list
                    Point2D.Float point = new Point2D.Float(x,y);
                    points.add(point);
                }
                
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

        // 
        for (int i = 0; i < shapes.size(); i++){

            for (int j = 0; j < shapes.get(i).size(); j++) {
                Point2D.Float p1 = shapes.get(i).get(j);
                Point2D.Float p2 = shapes.get(i).get((j + 1) % shapes.get(i).size()); // 最後の点を最初の点と接続
                g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
            }
        }
    }


    // main function
    public static void main(String[] args) {
        // path of all vert files
        String dir = "./vert/";
        List<String> filenames = new ArrayList<String>(){
            {
                add("disk.vert");
                add("key.vert");
                // sadd("rider.vert");
                // add("superior.vert");
            }
        };

        for (int i=0; i<filepaths.size(); i++){
            String filepath = dir + filenames(i);

            // creating window
            JFrame frame = new JFrame("Shape Drawer");

            // drowing shape from vert file
            ShapeDrawer panel = new ShapeDrawer(filepath);
            System.out.println("done: " + filepath);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setVisible(true);
        }
    }
}
