
import java.util.ArrayList;
import flanagan.math.Polynomial;
import flanagan.complex.Complex;

public class CommandeBezier {
	public static double thetam, v_ref; // orientation et vitesse courantes du robot	
	private static double Mm[] = new double[2]; // position courante du robot
	
	// Points de controle de la coubre de Bezier
	private static double P0[] = new double[2];
	private static double P1[] = new double[2];
	private static double P2[] = new double[2];
	private static double P3[] = new double[2];
	
	// Parametres internes �la commande, fonction des valeurs precedentes
	public double t0;
	public static double y, c0, g0, theta0;
	
	// Parametres du PD (�tuner)
	public static int KP = 20;
	public static int KV = 10; // Action derivee
	
	
	public void set_points(double pP0[], double pP1[], double pP2[], double pP3[]) {
		CommandeBezier.P0[0] = pP0[0];
		CommandeBezier.P0[1] = pP0[1];
		CommandeBezier.P1[0] = pP1[0];
		CommandeBezier.P1[1] = pP1[1];
		CommandeBezier.P2[0] = pP2[0];
		CommandeBezier.P2[1] = pP2[1];
		CommandeBezier.P3[0] = pP3[0];
		CommandeBezier.P3[1] = pP3[1];
	}
	
	public void set_pos(double x, double y, double t) {
		CommandeBezier.Mm[0] = x;
		CommandeBezier.Mm[1] = y;
		CommandeBezier.thetam = t;
	}
	
	public void set_speed(double v) {
		CommandeBezier.v_ref = v;
	}
	
	/*
	 * Calcule la commande  a appliquer
	 */
	public double compute_wref() {
		// Etape 1: mettre  a jour (Mx,My,theta,v)
		// todo
		
		// Etape 2: en deduire t0
		t0 = find_t0(Mm[0], Mm[1]);
		
		// Etape 3: calculer les parametres
		compute_params();
		
		// Etape 4: calculer la commande w_ref
		double theta = thetam - theta0;

		double w0 = v_ref*Math.cos(theta) / (1 - c0*y);
		double w1 = y*(g0*Math.sin(theta) - KP*Math.cos(theta))* Math.cos(theta) / (1 - c0*y);
		double w2 = Math.sin(theta) * ( c0*Math.sin(theta) - KV*Math.cos(theta)*sign(w0) );
		double w3 = c0;

		double w_ref = w0*(w1 + w2 + w3);
		
		return -w_ref;
	}
	
	
	/*
	 * Calcule les parametres lire a  la courbe (y,c0,g0,theta0)
	 */
	private void compute_params() {
		// c0: courbure en t0
		c0 = courbure(t0);
		
		// g0: derivee de c0 par rapport a l'abscisse curviligne, en t0
		double dt0 = 0.001;
		double dc = courbure(t0+dt0) - courbure(t0);
		double ds = dt0 * norm(dx(t0+dt0/2), dy(t0+dt0/2));
		g0 = dc/ds;
		
		// theta0: angle par rapport  a l'axe x de la trajectoire, en t0
		theta0 = Math.atan2(dy(t0), dx(t0));
		
		// y: distance signee a la courbe
		y = dist(t0, Mm[0], Mm[1]);
		if ( (Bx(t0)-Mm[0])*dy(t0)-(By(t0)-Mm[1])*dx(t0) < 0 )
			y = -y;
	}
	
	
	/*
	 * Cherche le point le plus proche sur la courbe
	 */
	private double find_t0(double Mx, double My) {
		double x5 = P0[0]*P0[0] - 6*P2[1]*P3[1] - 6*P0[1]*P1[1] + P3[0]*P3[0] - 2*P0[0]*P3[0] - 6*P2[0]*P3[0] - 18*P1[1]*P2[1] + 6*P1[1]*P3[1] - 6*P0[0]*P1[0] + 6*P0[0]*P2[0] + P0[1]*P0[1] + 9*P1[0]*P1[0] + 6*P0[1]*P2[1] - 2*P0[1]*P3[1] + 9*P2[0]*P2[0] + 9*P1[1]*P1[1] - 18*P1[0]*P2[0] + 6*P1[0]*P3[0] + 9*P2[1]*P2[1] + P3[1]*P3[1];
		double x4 = -5*P0[0]*P0[0] - 10*P1[1]*P3[1] - 30*P1[0]*P1[0] + 5*P0[1]*P3[1] + 25*P0[0]*P1[0] - 20*P0[1]*P2[1] - 10*P1[0]*P3[0] - 5*P0[1]*P0[1] + 5*P2[1]*P3[1] + 5*P2[0]*P3[0] + 45*P1[0]*P2[0] - 30*P1[1]*P1[1] + 25*P0[1]*P1[1] + 5*P0[0]*P3[0] + 45*P1[1]*P2[1] - 20*P0[0]*P2[0] - 15*P2[0]*P2[0] - 15*P2[1]*P2[1];
		double x3 = 24*P0[1]*P2[1] - 40*P0[1]*P1[1] + 10*P0[0]*P0[0] + 36*P1[0]*P1[0] + 4*P1[0]*P3[0] + 24*P0[0]*P2[0] - 40*P0[0]*P1[0] - 4*P0[1]*P3[1] + 4*P1[1]*P3[1] - 36*P1[1]*P2[1] - 4*P0[0]*P3[0] + 6*P2[0]*P2[0] - 36*P1[0]*P2[0] + 36*P1[1]*P1[1] + 10*P0[1]*P0[1] + 6*P2[1]*P2[1];
		double x2 = -10*P0[1]*P0[1] + My*P0[1] - 3*My*P1[1] - 18*P1[1]*P1[1] - 3*Mx*P1[0] - My*P3[1] + P0[1]*P3[1] + 3*Mx*P2[0] - Mx*P3[0] - 18*P1[0]*P1[0] + P0[0]*P3[0] - 12*P0[1]*P2[1] + 9*P1[0]*P2[0] + 3*My*P2[1] - 10*P0[0]*P0[0] + 30*P0[1]*P1[1] - 12*P0[0]*P2[0] + 9*P1[1]*P2[1] + 30*P0[0]*P1[0] + Mx*P0[0];
		double x1 = 3*P1[1]*P1[1] + 3*P1[0]*P1[0] + 4*Mx*P1[0] + 2*P0[0]*P2[0] + 2*P0[1]*P2[1] - 2*Mx*P2[0] - 2*Mx*P0[0] - 10*P0[0]*P1[0] - 2*My*P0[1] - 10*P0[1]*P1[1] + 5*P0[1]*P0[1] - 2*My*P2[1] + 4*My*P1[1] + 5*P0[0]*P0[0];
		double x0 = My*P0[1] - P0[1]*P0[1] - P0[0]*P0[0] + P0[1]*P1[1] + Mx*P0[0] - Mx*P1[0] - My*P1[1] + P0[0]*P1[0];
		
		double coeff[] = {x0, x1, x2, x3, x4, x5};
		Polynomial poly = new Polynomial(coeff);
		
		Complex roots[] = poly.roots();
		ArrayList<Double> t0s = new ArrayList<Double>();
		
		// Recupere les zeros reels sur [0;1]
		for (int k=0; k<roots.length; k++) {
			if (roots[k].isReal() && roots[k].getReal() >= -0.5 && roots[k].getReal() <= 1.5) {
				t0s.add( roots[k].getReal() );
			}
		}
		
		if (t0s.size() == 1)
			return t0s.get(0);
		else if (t0s.size() > 1) {
			// on cherche le zero minimisant la distance
			double tmp = 1000;
			double t0 = 0;
			
			for (int k=0; k<t0s.size(); k++) {
				if ( dist(t0s.get(k), Mx, My) < tmp) {
					tmp = dist(t0s.get(k), Mx, My);
					t0 = t0s.get(k);
				}
			}
		
			return t0;
		}
		else
			return -1;

	}

	/*
	 * Fonctions diverses
	 */
	public double Bx(double t) {
		return P0[0]*(1-t)*(1-t)*(1-t) + 3*P1[0]*t*(1-t)*(1-t) + 3*P2[0]*t*t*(1-t) + P3[0]*t*t*t;
	}
	public double By(double t) {
		return P0[1]*(1-t)*(1-t)*(1-t) + 3*P1[1]*t*(1-t)*(1-t) + 3*P2[1]*t*t*(1-t) + P3[1]*t*t*t;
	}
	private double dx(double t) {
		return -3*P0[0]*(1-t)*(1-t) + 3*P1[0]*(1-t)*(1-t) - 6*P1[0]*t*(1-t) + 6*P2[0]*t*(1-t) - 3*P2[0]*t*t + 3*P3[0]*t*t;
	}
	private double dy(double t) {
		return -3*P0[1]*(1-t)*(1-t) + 3*P1[1]*(1-t)*(1-t) - 6*P1[1]*t*(1-t) + 6*P2[1]*t*(1-t) - 3*P2[1]*t*t + 3*P3[1]*t*t;
	}
	private double ddx(double t) {
		return 6*P0[0]*(1-t) - 12*P1[0]*(1-t) + 6*P1[0]*t + 6*P2[0]*(1-t) - 12*P2[0]*t + 6*P3[0]*t;
	}
	private double ddy(double t) {
		return 6*P0[1]*(1-t) - 12*P1[1]*(1-t) + 6*P1[1]*t + 6*P2[1]*(1-t) - 12*P2[1]*t + 6*P3[1]*t;
	}
	private double courbure(double t) {
		return (dx(t)*ddy(t) - dy(t)*ddx(t)) / Math.pow(dx(t)*dx(t) + dy(t)*dy(t), 1.5);
	}
	private double norm(double x, double y) {
		return Math.sqrt( x*x + y*y);
	}
	private double dist(double t, double Mx, double My) {
		double Px = P0[0]*(1-t)*(1-t)*(1-t) + 3*P1[0]*t*(1-t)*(1-t) + 3*P2[0]*t*t*(1-t) + P3[0]*t*t*t;
		double Py = P0[1]*(1-t)*(1-t)*(1-t) + 3*P1[1]*t*(1-t)*(1-t) + 3*P2[1]*t*t*(1-t) + P3[1]*t*t*t;
		return norm(Px-Mx, Py-My);
	}
	private double sign(double val) {
		if ( val < 0)
			return -1;
		else if (val > 0)
			return 1;
		else
			return 0;
	}
	
}
