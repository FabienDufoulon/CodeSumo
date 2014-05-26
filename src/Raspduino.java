import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.IOException;
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

	public Raspduino() throws IOException {
		ca = new CarteAsservissement("/dev/ttyS80");

		/*Panel panel = new Panel(this);
		new GUI(panel);*/
		
		MyThread thread = new MyThread(this){
		};

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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new Raspduino();
	}
}

class MyThread implements Runnable {
	Raspduino rasp;

	   public MyThread(Raspduino raspd) {
	      this.rasp = raspd;
	   }

	   public void run() {
	    	DataInputStream in = new DataInputStream(System.in);
	    	char ch = ' ';
			try {
				ch = (char) System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if (Character.isWhitespace(ch)) {
				System.out.println("");
	    	} 
	    	
			rasp.stopFollowing();
			 
			float v=0;
			float w=0;
	    	
			switch(ch)
			{
			case 'L': //left
				w = (float) -1.0;
				break;
			case 'R': //right
				w = (float) 1.0;
				break;
			case 'U': //up
				v = (float) 0.3;
				break;
			case 'D': //down
				v = (float) -0.3;
				break;
			case 'S': //Reset
				this.rasp.ca.reset();
				break;
			case 'T': //T
				rasp.followBezier();
				break;

			case 'P': //P
				rasp.commande.commande.KP += 5;
				System.out.println("----------------------------------------- KP = " + rasp.commande.commande.KP);
				break;
			case 'M': //M
				rasp.commande.commande.KP -= 5;
				System.out.println("----------------------------------------- KP = " + rasp.commande.commande.KP);
				break;

			case 'O': //O
				rasp.commande.commande.KV += 5;
				System.out.println("----------------------------------------- KV = " + rasp.commande.commande.KV);
				break;
			case 'K': //K
				rasp.commande.commande.KV -= 5;
				System.out.println("----------------------------------------- KV = " + rasp.commande.commande.KV);
				break;
			}
			this.rasp.ca.setAsservissementVitesse(v, w);
	   }
	}
