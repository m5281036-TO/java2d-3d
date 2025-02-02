// class for keyboard listener
// reference (Japanese): 
// https://qiita.com/derodero24/items/9ea025b92ac61edf0aa4 


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SurfaceDrawerKeyListener extends KeyAdapter {

    private SurfaceDrawer surfaceDrawer;

    private float MOVING_AMOUNT = 50; // amount of motion with one button click
    private float ROTATION_DEGREE = 15; // amount of rotation with one button click

    public SurfaceDrawerKeyListener(SurfaceDrawer surfaceDrawer) {
        this.surfaceDrawer = surfaceDrawer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // scaling object
        if (e.getKeyChar() == '=') {
            surfaceDrawer.scalePointCloud(1.2); // zoom in
            System.out.println("Zoom In x1.2");
        } else if (e.getKeyChar() == '-') {
            surfaceDrawer.scalePointCloud(0.85); // zoom out
            System.out.println("Zoom Out x0.85");
        }
        // moving object
        else if (e.getKeyChar() == 'a') {
            surfaceDrawer.movePointCloud("left", MOVING_AMOUNT); // left
            System.out.println("moving left");
        } else if (e.getKeyChar() == 'w') {
            surfaceDrawer.movePointCloud("above", MOVING_AMOUNT); // left
            System.out.println("moving above");
        } else if (e.getKeyChar() == 's') {
            surfaceDrawer.movePointCloud("below", MOVING_AMOUNT); // left
            System.out.println("moving below");
        } else if (e.getKeyChar() == 'd') {
            surfaceDrawer.movePointCloud("right", MOVING_AMOUNT); // left
            System.out.println("moving right");
        }
        // reset orientation
        else if (e.getKeyChar() == 'r') {
            surfaceDrawer.resetObject();
            System.out.println("Object orientation reset");
        }
        // rotateing object
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            surfaceDrawer.rotatePointCloudX(-ROTATION_DEGREE); // rotate positively in x-axis
                break;
            case KeyEvent.VK_DOWN:
            surfaceDrawer.rotatePointCloudX(ROTATION_DEGREE); // rotate negatively in x-axis
                break;
            case KeyEvent.VK_LEFT:
            surfaceDrawer.rotatePointCloudY(-ROTATION_DEGREE); // rotate positively in y-axis
                break;
            case KeyEvent.VK_RIGHT:
            surfaceDrawer.rotatePointCloudY(ROTATION_DEGREE); // rotate negatively in y-axis
                break;
        }
    }
}
