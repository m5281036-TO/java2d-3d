// run this program
// reference:
// https://docs.oracle.com/javase/8/docs/api/javax/swing/SwingUtilities.html


public class MainApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            SurfaceDrawer frame = new SurfaceDrawer();
            frame.setVisible(true);
        });
    }
}
