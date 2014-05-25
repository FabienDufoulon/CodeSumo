import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


public class Trajectoire {

	CommandeBezier commande;
	
	Queue<Bezier> tablBezier;
	
	Trajectoire()
	{
		this.tablBezier = new LinkedList<Bezier>();
		
		commande = new CommandeBezier(); 
	}
	
	void addBezier(double pP0[], double pP1[], double pP2[], double pP3[]) 
	{
		Bezier b = new Bezier();
		b.toString();
		
		b.P0[0] = pP0[0];
		b.P0[1] = pP0[1];
		b.P1[0] = pP1[0];
		b.P1[1] = pP1[1];
		b.P2[0] = pP2[0];
		b.P2[1] = pP2[1];
		b.P3[0] = pP3[0];
		b.P3[1] = pP3[1];
		
		this.tablBezier.add(b);
	}
	
	public void setNextTrajectoire()
	{
		Bezier b = this.tablBezier.poll();
		commande.set_points(b.P0, b.P1, b.P2, b.P3);
	}

	public void set_pos(double x, double y, double t) {
		commande.set_pos(x, y, t);
	}
	
	public void set_speed(double v) {
		commande.set_speed(v);
	}
	

	public double compute_wref() {
		
		if(commande.t0>1.0)
		{
			this.setNextTrajectoire();
		}
		
		return commande.compute_wref();
	
	}

	public boolean endTrajectoire() {
		
		return this.tablBezier.size() == 0 && this.commande.t0 > 1.0;
	}
}
