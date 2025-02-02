import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.Math;
import java.util.ArrayList;
import java.awt.image.MemoryImageSource;
import java.util.Random;

public class ProcScene extends JFrame {
	private static final long serialVersionUID = 1L;

	public ProcScene(int w, int h) {
		Random r = new Random(123456789);
		int pixels[] = new int[w * h];
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// get coordinates in [0,1]
				float xf = (float) x / w, yf = (float) y / h;
				// y goes down in Java 2D
				yf = 1.0f - yf;

				Color c;
				if (yf < terrain(xf + 0.3f)) {
					// fractal terrain in the dark
					c = new Color(0, 0, 0);
				} else if (yf > 0.7f) {
					// stars
					float t = r.nextFloat();
					if (t > 0.9999f)
						c = new Color(255, 255, 255);
					else
						c = skycolor(xf, yf);
				} else {
					// sky
					c = skycolor(xf, yf);
				}
				pixels[i++] = c.getRGB();
			}
		}
		img = createImage(new MemoryImageSource(w, h, pixels, 0, w));
	}

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, this);
	}

	public static void main(String[] args) {
		int w = 800;
		int h = 800;

		JFrame f = new ProcScene(w, h);

		f.pack();
		f.setSize(w, h);
		f.setVisible(true);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// Private helper functions
	private Color skycolor(float x, float y) {
		float h = Math.max(0.0f, 1.3f - y - (float) Math.pow(Math.abs(x - 0.5), 3.0));
		float r = (float) Math.pow(h, 5.0);
		float g = (float) Math.pow(h, 10.0);
		float b = 0.3f + (float) Math.pow(Math.max(0.0f, h - 0.1f), 10.0);

		return new Color(clamp(r), clamp(g), clamp(b));
	}

	// fractional part of x (x-floor(x))
	private float fract(float x) {
		return x - (float) Math.floor(x);
	}

	// linear interpolation between x0 and x1
	private float lerp(float x0, float x1, float t) {
		return x0 * (1.f - t) + t * x1;
	}

	private float hash(float n) {
		return fract((float) Math.sin(n) * 43758.5453123f);
	}

	private float smoothstep(float x) {
		return x * x * (3.0f - 2.0f * x);
	}

	// 1d noise
	private float noise(float x) {
		float xf = (float) Math.floor(x);
		float f = fract(x);
		float t = smoothstep(f);
		return 2.0f * lerp(hash(xf), hash(xf + 1.0f), t) - 1.0f;
	}

	// fractal terrain (fbm: 1d noise at multiple scales)
	private float terrain(float x) {
		float y = 0.f;
		for (int i = 0; i < 7; i++) {
			float s = (float) Math.pow(2.0, i);
			y = y + noise(s * x) / s;
		}
		return y * 0.5f * 0.3f + 0.36f;
	}

	private float clamp(float x) {
		return Math.max(Math.min(x, 1.0f), 0.f);
	}

	private Image img;
}
