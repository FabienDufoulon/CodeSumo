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
	public Position p;
	
	TypeAsservissement asservType;
	
	Timer timer;

	boolean isRunning;

	public Raspduino() throws IOException {
		//ca = new CarteAsservissement("/dev/ttyAMA0");

		/*Panel panel = new Panel(this);
		new GUI(panel);*/
		
		/*MyThread thread = new MyThread(this){
		};
		
		thread.run();*/


		asservType = TypeAsservissement.NULL;
		
		while(true)
		{

			if (this.asservType == TypeAsservissement.ASSERV_TRAJECTOIRE) {
				commande.set_pos(ca.x, ca.y, ca.t);
				commande.set_speed(ca.v);


				if(commande.endTrajectoire())
				{
					ca.stop();
					stopFollowing();
				}
				else
				{
					float wr = (float) commande.compute_wref();
					float vr;
					vr = (float) (0.3 /(1+commande.commande.c0 * 0.35/2.0));
					
					ca.setAsservissementVitesse(vr, wr);
					
					System.out.println("Distance = " + commande.commande.y);
				}
				
				
				
			}
			else if(this.asservType == TypeAsservissement.ASSRV_POSITION)
			{
				float l,t;
				l = (float) Math.sqrt(Math.pow(ca.x - p.x, 2.0) + Math.pow(ca.y - p.y, 2.0));
				t = -(float) (ca.t - Math.atan2(p.y - ca.y, p.x - ca.x));
				
				if(l<0.01)
				{
					ca.stop();
					this.asservType = TypeAsservissement.NULL;
					System.out.println("POUEEEEET l = " + l);
				}
				else
					ca.setAsservissementPosition(l, t);
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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

		this.asservType = TypeAsservissement.ASSERV_TRAJECTOIRE;
		
		System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL XD");

	}

	public void stopFollowing() {
		this.asservType = TypeAsservissement.NULL;
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
				this.rasp.followBezier();
				break;

			case 'P': //P
				this.rasp.commande.commande.KP += 5;
				System.out.println("----------------------------------------- KP = " + rasp.commande.commande.KP);
				break;
			case 'M': //M
				this.rasp.commande.commande.KP -= 5;
				System.out.println("----------------------------------------- KP = " + rasp.commande.commande.KP);
				break;

			case 'O': //O
				this.rasp.commande.commande.KV += 5;
				System.out.println("----------------------------------------- KV = " + rasp.commande.commande.KV);
				break;
			case 'K': //K
				this.rasp.commande.commande.KV -= 5;
				System.out.println("----------------------------------------- KV = " + rasp.commande.commande.KV);
				break;
			}
			this.rasp.ca.setAsservissementVitesse(v, w);
	   }
	}
