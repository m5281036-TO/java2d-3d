import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SmoothCurve extends JPanel {
    private final List<Point2D.Float> points;

    public SmoothCurve(List<Point2D.Float> points) {
        this.points = points;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (points.size() < 4) {
            // 点が少なすぎる場合はスムーズな曲線を描画できない
            g2d.setColor(Color.RED);
            g2d.drawString("Need at least 4 points for smooth curve", 10, 20);
            return;
        }

        // Catmull-Romスプラインによる曲線描画
        g2d.setColor(Color.BLUE);
        drawCatmullRomSpline(g2d);
    }

    private void drawCatmullRomSpline(Graphics2D g2d) {
        for (int i = 1; i < points.size() - 2; i++) {
            Point2D.Float p0 = points.get(i - 1);
            Point2D.Float p1 = points.get(i);
            Point2D.Float p2 = points.get(i + 1);
            Point2D.Float p3 = points.get(i + 2);

            // 曲線を補間して描画
            for (float t = 0; t <= 1; t += 0.01) {
                Point2D.Float interpolatedPoint = catmullRomInterpolate(p0, p1, p2, p3, t);
                g2d.fillOval((int) interpolatedPoint.x, (int) interpolatedPoint.y, 2, 2);
            }
        }
    }

    private Point2D.Float catmullRomInterpolate(Point2D.Float p0, Point2D.Float p1, Point2D.Float p2, Point2D.Float p3, float t) {
        // Catmull-Romスプライン補間公式
        float t2 = t * t;
        float t3 = t2 * t;

        float x = 0.5f * (2 * p1.x + (-p0.x + p2.x) * t +
                          (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
                          (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);

        float y = 0.5f * (2 * p1.y + (-p0.y + p2.y) * t +
                          (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 +
                          (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3);

        return new Point2D.Float(x, y);
    }

    public static void main(String[] args) {
        // サンプルの点データ
        List<Point2D.Float> points = new ArrayList<>();
        points.add(new Point2D.Float(50, 200));
        points.add(new Point2D.Float(100, 100));
        points.add(new Point2D.Float(200, 300));
        points.add(new Point2D.Float(300, 150));
        points.add(new Point2D.Float(400, 250));

        // パネルを作成してフレームに追加
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smooth Curve using Discrete Curvature");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.add(new SmoothCurve(points));
            frame.setVisible(true);
        });
    }
}
