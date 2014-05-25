import java.util.Timer;
import java.util.TimerTask;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author root
 */
public class Raspduino {

	CarteAsservissement ca;

	public Trajectoire commande;

	Timer timer;

	boolean isRunning;

	public Raspduino() {
		ca = new CarteAsservissement("COM7");

		Panel panel = new Panel(this);
		new GUI(panel);

		timer = new Timer(true);

		isRunning = false;

		TimerTask tt = new TimerTask() {
			public void run() {
				if (isRunning) {
					commande.set_pos(ca.x, ca.y, ca.t);
					commande.set_speed(ca.v);


					if(commande.endTrajectoire())
					{
						ca.stop();
						stopFollowing();
					}
					else
					{
						ca.setAsservissementVitesse((float) 0.1,
							(float) commande.compute_wref());
						
						System.out.println("Distance = " + commande.commande.y);
					}
				}
			};
		};
		timer.scheduleAtFixedRate(tt, 0, 200);

	}

	public void followBezier() {
		double P0[] = { 0.0, 0.0 };
		double P1[] = { 0.5, 0.0 };
		double P2[] = { 0.0, 0.5 };
		double P3[] = { 0.5, 0.5 };

		double pP0[] = { 0.5, 0.5 };
		double pP1[] = { 1.0, 0.5 };
		double pP2[] = { 0.0, 0.2 };
		double pP3[] = { 0.0, 0.0 };

		commande = new Trajectoire();
		commande.addBezier(P0,P1,P2,P3);
		commande.addBezier(pP0,pP1,pP2,pP3);
		commande.set_speed(0.1);
		
		commande.setNextTrajectoire();

		this.stopFollowing();

		isRunning = true;
		
		System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL XD");

	}

	public void stopFollowing() {
		isRunning = false;
	} 

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new Raspduino();
	}
}
