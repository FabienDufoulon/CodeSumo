import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Vector;

import javax.swing.*;

public class Panel extends JPanel implements ActionListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	double x,y,t;
	
	Rectangle rect;
	
	CarteAsservissement ca;
	Raspduino rasp;
	
	
	float v,w;

	Polygon trajectoire;
	
	Panel(Raspduino rasp)
	{
		super();

		this.rasp = rasp;
        
        setSize(900,600);
        rect = new Rectangle(0,0,20,30);
        
        this.ca = rasp.ca;
        
        new Timer(100,this).start();
        
        
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        this.addKeyListener(this);
        
        trajectoire = new Polygon();
	}
	
	public void paint(Graphics g)
	{ 
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.RED);
		
		if(rasp.commande!=null && rasp.commande.commande!=null)
		{
			for(double t=0;t<1.0;t+=0.01)
				g.drawLine(getX(rasp.commande.commande.Bx(t)), getY(rasp.commande.commande.By(t)), getX(rasp.commande.commande.Bx(t+0.01)), getY(rasp.commande.commande.By(t+0.01)));
		}
		rect.x = getX(x) - rect.width/2;
		rect.y = getY(y) - rect.height/2;
		g2d.rotate(-t, rect.x + rect.width/2, rect.y + rect.height/2);
		g2d.draw(rect);
		g2d.fill(rect);
	
		try {
			g2d.transform(g2d.getTransform().createInverse());
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		g.setColor(Color.BLUE);
		g2d.drawPolyline(trajectoire.xpoints,trajectoire.ypoints, trajectoire.npoints);
	}

	public int getX(double _x)
	{
		return (int) (_x*300.0 + this.getWidth()/2);
	}
	
	public int getY(double _y)
	{
		return (int) (-_y*300.0 + this.getHeight()/2);
	
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		x = ca.x;
		y = ca.y;
		t = ca.t;

		trajectoire.addPoint(getX(x), getY(y));
		if(trajectoire.npoints>10000)
			trajectoire = new Polygon();
		
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		rasp.stopFollowing();
		 
		v=0;
		w=0;
		switch(arg0.getKeyCode())
		{
		case KeyEvent.VK_LEFT:
			w = (float) -1.0;
			break;
		case KeyEvent.VK_RIGHT:
			w = (float) 1.0;
			break;
		case KeyEvent.VK_UP:
			v = (float) 0.3;
			break;
		case KeyEvent.VK_DOWN:
			v = (float) -0.3;
			break;
		case KeyEvent.VK_R:
			ca.reset();
			break;
		case KeyEvent.VK_T:
			rasp.followBezier();
			break;

		case KeyEvent.VK_P:
			rasp.commande.commande.KP += 5;
			System.out.println("----------------------------------------- KP = " + rasp.commande.commande.KP);
			break;
		case KeyEvent.VK_M:
			rasp.commande.commande.KP -= 5;
			System.out.println("----------------------------------------- KP = " + rasp.commande.commande.KP);
			break;

		case KeyEvent.VK_O:
			rasp.commande.commande.KV += 5;
			System.out.println("----------------------------------------- KV = " + rasp.commande.commande.KV);
			break;
		case KeyEvent.VK_L:
			rasp.commande.commande.KV -= 5;
			System.out.println("----------------------------------------- KV = " + rasp.commande.commande.KV);
			break;
			
		case KeyEvent.VK_C:
			this.trajectoire = new Polygon();
			break;
			
		}
		
		ca.setAsservissementVitesse(v, w);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

	
	
}
