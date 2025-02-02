import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;


public class SubdivisionCurve extends JFrame{
    JFrame graph_frame;
    DrawPanel draw1;
    Curve curve;
    JCheckBox check;


    public SubdivisionCurve(){
	curve = new Curve();
	draw1 = new DrawPanel();
	draw1.setCurve(curve);
	draw1.setMain(this);
    
	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(draw1, BorderLayout.CENTER);
	this.setSize(800,500);
    
	JPanel p1 = new JPanel();
	p1.setLayout(new FlowLayout());

	JButton clear = new JButton("Clear");
	clear.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    draw1.picked = -1;
		    curve.clear();
		    draw1.updateCurve();
		}
	    });
	p1.add(clear);
    
	this.getContentPane().add(p1, BorderLayout.NORTH);
	this.setVisible(true);
    }


    public static void main(String[] arg){
	SubdivisionCurve main = new SubdivisionCurve();
	main.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
		    System.exit(0);
		}
	    });
    }
}


class DrawPanel extends JPanel{

    // Check for Mouse event
    // if close to a control point, then select it
    // otherwise add a new control point to the control polygon
    private class Picker extends MouseAdapter{
	int hit_range = 10;
	public void mousePressed(MouseEvent e){
	    Point p = e.getPoint();
	    picked = curve.checkClosePoint(p, hit_range);
	    if(picked < 0){
		curve.appendCP(e.getPoint());
		curve.subdivide(subdivideLevel);
	    }
	    repaint();
	}
    }
  
    // if a control point is picked, 
    // then update its position
    private class Mover extends MouseMotionAdapter{
	public void mouseDragged(MouseEvent e){
	    if(picked < 0)
		return;
	    curve.moveCP(e.getPoint(), picked);
	    curve.subdivide(subdivideLevel);
	    repaint();
	}
    }
  
    
    // Subdivision curve to be plotted
    Curve curve;

    // subdivision level for the curve
    int subdivideLevel = 6;

    // Radius for the control points
    int radius = 5;

    // index of the selected control point
    int picked;

    // reference to the main frame for update
    SubdivisionCurve main;
  

    DrawPanel(){
	this.setBackground(Color.white);
	this.addMouseListener(new Picker());
	this.addMouseMotionListener(new Mover());
    }

    void setCurve(Curve curve){
	this.curve = curve;
	picked = -1;
    }
    
    void setMain(SubdivisionCurve main){
	this.main = main;
    }
    
    public void paint(Graphics g){
	// Repaint the background
	g.setColor(Color.white);
	g.fillRect(0, 0, this.getSize().width, this.getSize().height);
    
	if(curve == null)
	    return;


	// Draw the control points, control polygon 
	// and the subdivision curve
	Graphics2D g2 = (Graphics2D)g;
	g2.setColor(Color.black);
	g2.setStroke(new BasicStroke(3.0f));
    
	int cn = curve.control_P.size();


	// draw the control polygon
	if(cn != 0){
	    int[] x = new int[cn];
	    int[] y = new int[cn];
	    for(int i=0; i<cn; i++){
		Point p = curve.control_P.get(i);
		x[i] = p.x;
		y[i] = p.y;
	    }
	    g2.setColor(Color.lightGray);
	    g2.drawPolygon(x, y, cn);
	}
    
	g2.setColor(Color.black);
	int pn = curve.point_N;


	// draw the subdivision curve
	if(pn != 0){
	    int[] x = new int[pn];
	    int[] y = new int[pn];
	    for(int i=0; i<pn; i++){
		x[i] = (int)curve.point[i][0];
		y[i] = (int)curve.point[i][1];
	    }
	    g.drawPolygon(x, y, pn);
	}


	// draw the control points
	for(int i=0; i<cn; i++){
	    Point p = curve.control_P.get(i);
	    if(i == picked){
		g2.setColor(Color.red);
		g2.drawOval(p.x-2*radius, p.y-2*radius, 4*radius, 4*radius);
	    }
	    else{
		g2.setColor(Color.blue);
		g2.fillOval(p.x-radius, p.y-radius, 2*radius, 2*radius);
	    }
	}
    }


    public void update(Graphics g){
	g.setColor(Color.white);
	g.fillRect(0, 0, this.getSize().width, this.getSize().height);
	super.update(g);
    }
  

    void updateCurve(){
	curve.subdivide(subdivideLevel);
	repaint();
    }
}


class Curve{
    //Vector control_P;
    ArrayList<Point> control_P;
    double[][] point;
    int point_N;
  
    Curve(){
	point_N = 0;
	control_P = new ArrayList<Point>();
    }
  
    void appendCP(Point p){
	control_P.add(p);
    }
  
    void moveCP(Point p, int i){
	Point p1 = control_P.get(i);
	p1.x = p.x;
	p1.y = p.y;
    }
  
  
    int checkClosePoint(Point p, int r){
	int index= -1;
	int min = 100000000;
	for(int i=0; i<control_P.size(); i++){
	    Point p1 = control_P.get(i);
	    int dist = (int)Math.sqrt((p.x-p1.x)*(p.x-p1.x)+(p.y-p1.y)*(p.y-p1.y));
	    if(dist < min){
		min = dist;
		index = i;
	    }
	}
	if(min > r)
	    return -1;
	else
	    return index;
    }


    /*
      4-point subdivision rule.
      See e.g.
      https://www.tu-chemnitz.de/informatik/GDV/sonstig/Vortraege_Exkursionswoche/2009/class3sub_beamer.pdf
     */  
    void subdivide(int n){
	if(control_P.size() == 0)
	    return;

	int cp_N = control_P.size();
	int step = (int)Math.pow(2,n);
	point_N = step*cp_N;
	point = new double[point_N][2];
    
	for(int i=0; i<cp_N; i++){
	    int index = i*step;
	    Point p = control_P.get(i);
	    point[index][0] = (double)p.x;
	    point[index][1] = (double)p.y;
	}
    
	for(int i=0; i<n; i++){
	    for(int j=0; j<point_N; j+=step){
		int i1 = (j+point_N-step)%point_N;
		int i2 = j;
		int i3 = (j+step)%point_N;
		int i4 = (j+2*step)%point_N;
        
		int odd = j + step/2;
		point[odd][0] = 0.0625*(-point[i1][0] + 9.0*point[i2][0]
					+ 9.0*point[i3][0] - point[i4][0]);
		point[odd][1] = 0.0625*(-point[i1][1] + 9.0*point[i2][1]
					+ 9.0*point[i3][1] - point[i4][1]);
	    }
	    step /= 2;
	}
    }
  
    void clear(){
	control_P = new ArrayList<Point>();
	point_N = 0;
    }
    
    double dist(double[] p1, double[] p2){
	return Math.sqrt((p1[0]-p2[0])*(p1[0]-p2[0]) + 
			 (p1[1]-p2[1])*(p1[1]-p2[1]));
    }
}

