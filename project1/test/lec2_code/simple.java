import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.Math;
import java.util.ArrayList;


public class simple {
    public static void main(String[] args) {
	JFrame f = new JFrame("Simple");

	int w = 800;
	int h = 500;
	
	DrawPanel dp = new DrawPanel(w, h);

	f.add(dp, BorderLayout.CENTER);
	f.pack();
	f.setSize(w, h);
	f.setVisible(true);
	
	f.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    System.exit(0);
		}
	    });
    }
}


class DrawPanel extends JPanel {
    ArrayList<Point> lines = new ArrayList<Point>();
    double h2, radius;
    int hh, w1, w2, w3, w4;

    
    public DrawPanel(int w, int h) {
        setBackground(Color.white);
        radius = 0.4*h;
        h2     = 0.5*h;
        w1 = w - (4*h)/10;
        w2 = w - (2*h)/10;
        w3 = w - h/10;
        w4 = w;
        hh = h;

	this.addMouseListener(new mouseDown());
    }


    private class mouseDown extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
	    Point p = e.getPoint();
	    lines.add(new Point(p));
	    repaint();
	}
    }


    public void paint(Graphics g) {
        double x, y, a, b, p, q;


        g.setColor(Color.blue);
        g.fillOval(xscale(-1.0), yscale(1.0),
		   (int)(2.0*radius), (int)(2.0*radius));

        g.setColor(Color.yellow);
        g.fillRect(w1, yscale(1.0), w2-w1, (int)(2.0*radius));

        g.setColor(Color.red);
        g.fillRect(w3, 0, w4-w3, hh);


        g.setColor(Color.black);
        int np = lines.size();
	if (np > 1) {
	    Point prev = lines.get(0);
	    
	    for (int i = 1; i < np; ++i) {
		double xstart = prev.getX();
		double ystart = prev.getY();

		Point curr = lines.get(i);
		double xend = curr.getX();
		double yend = curr.getY();

		g.drawLine((int)xstart, (int)ystart, (int)xend, (int)yend);

		prev = curr;
	    }
	}
    }

    public int xscale(double x) {
        return (int)(x*radius + h2);
    }

    public int yscale(double y) {
        return (int)(-y*radius + h2);
    }
}
