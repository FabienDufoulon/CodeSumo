/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.math.*;

import com.vividsolutions.jts.geom.*;

/**
 *
 * @author root
 */
public class TableDeJeu {
	
	//taille de la table :
	int tableWidth = 3000, tableHeight = 2000;
    
    private int posXInit, posYInit; //point en haut ﾃ�droite
    
    private int a;
    
    private int nbPoints;
    private Vertex[] point = new Vertex[151*101];
    //donne des points tous les 2cm
    private List<Point> obstacles = new ArrayList();
    private List<Polygon> polygons = new ArrayList();
    
    public TableDeJeu(String sens)
    {
        a = 1;
        if(sens == "gauche") 
            a = -1;
        
        posXInit = 62; //62
        posYInit = 250;  //250
        
    }
    
    public static double distancePoints(Vertex p1,Vertex p2){
    	return Math.sqrt(Math.pow(p1.getPoint().x-p2.getPoint().x, 2) + Math.pow(p1.getPoint().y-p2.getPoint().y, 2));
    }
    
    //////////////
    //initialisation
    //////////////
    public Vertex init() {
        // === Initialisation de la table ===
        // == Points ==
        
    	//maillage quadrillﾃｩ simple
    	nbPoints = 0;
    	for(int x = 0 ; x <= 150 ; x++) {
    		for(int y = 0 ; y <= 100 ; y++) {
    			
    			point[nbPoints] = new Vertex("("+x*tableWidth/150+","+y*tableHeight/100 + ")", 
    										 new Point(x*tableWidth/150, y*tableHeight/100));
    			nbPoints++;
    		}
    	}

        
        initEdges();
        initObstacles();
        
        return point[0];
    }
    
    public void initEdges() {
        // == Distances ==
    	for(int i = 0 ; i < nbPoints ; i++) {
    		List<Vertex> adjacentPoints = new ArrayList<Vertex>();
    		for(int j = 0 ; j < nbPoints ; j++) {
    				
    			/*for the while, 4 cardinal points with /900, insert /800 if want to work with 8 directions*/
    			if(Math.sqrt(Math.pow(point[i].getPoint().x-point[j].getPoint().x, 2) + Math.pow(point[i].getPoint().y-point[j].getPoint().y, 2)) < 10*tableWidth/800) {
    				adjacentPoints.add(point[j]);
    			}
    		}
    		
    		int sizeConstant = adjacentPoints.size();
    		point[i].adjacencies = new Edge[sizeConstant];
    		if(adjacentPoints.size() == 0) {
    			System.out.println("alert");
    		}   		
    		
    		/*for(Edge e : point[i].adjacencies) {
    			e = new Edge(adjacentPoints.get(0), 1);
    			adjacentPoints.remove(0);
    		}*/
    		
    		for (int k = 0; k < sizeConstant; k++){
    			point[i].adjacencies[k] = new Edge(adjacentPoints.get(0), distancePoints(point[i], adjacentPoints.get(0))); 
    			//makes weight depend on distance between points of the edge
    			adjacentPoints.remove(0);  			
    		}
    		
    	}

    }
    
    public void initObstacles(){
    	int largeurRobot = 200;
    	//en dessous du mammouth
    	int x = 400 - largeurRobot;
    	int y = 0;
    	while(x <= 1100+largeurRobot){
    		while(y <= 300+largeurRobot){
    			this.obstacles.add(new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 0;
    	}
    	
    	//en dessous de l'autre mammouth
    	x = 1900 - largeurRobot;
    	while(x <= 2600+largeurRobot){
    		while(y <= 300+largeurRobot){
    			this.obstacles.add( new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 0;
    	}
    	
    	//premier demi-cercle
    	x = 0;
    	y = 2000-250-largeurRobot;
    	while(x <= 250+largeurRobot){
    		while(y <= 2000){
    			this.obstacles.add( new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 2000-250-largeurRobot;
    	}

    	//deuxième demi-cercle
    	x = 3000 - 250 - largeurRobot;
    	y = 2000-250-largeurRobot;
    	while(x <= 3000){
    		while(y <= 2000){
    			this.obstacles.add( new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 2000-250-largeurRobot;
    	}
    	
    	//rond centre
    	x = 1500 - 300 - largeurRobot;
    	y = 1050-300-largeurRobot;
    	while(x <= 1500 + 300 + largeurRobot){
    		while(y <= 1050+300+largeurRobot){
    			this.obstacles.add( new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 1050-300-largeurRobot;
    	}    	
    	
    	//premier foyer
    	x = 900 - 160 - largeurRobot;
    	y = 1100 - 160 - largeurRobot;
    	while(x <= 900 + 160 + largeurRobot){
    		while(y <= 1100 + 160 + largeurRobot){
    			this.obstacles.add( new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 1100 - 160 - largeurRobot;
    	}  
    	
    	//deuxième foyer
    	x = 2100 - 160 - largeurRobot;
    	y = 1100 - 160 - largeurRobot;
    	while(x <= 2100 + 160 + largeurRobot){
    		while(y <= 1100 + 160 + largeurRobot){
    			this.obstacles.add( new Point(x, y));
    			y += 20;
    		}
    		x += 20;
    		y = 1100 - 160 - largeurRobot;
    	}  
    	
    	
    	
    	//Creation obstacles en JTS
    	GeometryFactory fact = new GeometryFactory();
    	
    	//semi - octogones
    	Coordinate[] coordinates = new Coordinate[] {
        		new Coordinate(400 - largeurRobot,0), new Coordinate(400 - largeurRobot,300), 
        		new Coordinate(400+(1100-400+2*largeurRobot)/4,300+largeurRobot), 
        		new Coordinate(400+3*(1100-400+2*largeurRobot)/4,300+largeurRobot), 
        		new Coordinate(1100+largeurRobot,300),new Coordinate(1100 + largeurRobot,0),
        		new Coordinate(400 - largeurRobot,0)
        	};    	
        LinearRing linear = fact.createLinearRing(coordinates);
        polygons.add(new Polygon(linear, null, fact));  

    	Coordinate[] coordinates2 = new Coordinate[] {
        		new Coordinate(1900 - largeurRobot,0), new Coordinate(1900 - largeurRobot,300), 
        		new Coordinate(1900+(2600-1900+2*largeurRobot)/4,300+largeurRobot), 
        		new Coordinate(1900+3*(2600-1900+2*largeurRobot)/4,300+largeurRobot), 
        		new Coordinate(2600+largeurRobot,300),new Coordinate(2600 + largeurRobot,0),
        		new Coordinate(1900 - largeurRobot,0)
        	};   
        LinearRing linear2 = fact.createLinearRing(coordinates2);
        polygons.add(new Polygon(linear2, null, fact));    

    	Coordinate[] coordinates3 = new Coordinate[] {
        		new Coordinate(0,2000), new Coordinate(250 + largeurRobot,2000), 
        		/*new Coordinate(250 + largeurRobot,250+largeurRobot),*/ new Coordinate(0,250+largeurRobot),
        		new Coordinate(0,2000)
        	};   
        LinearRing linear3 = fact.createLinearRing(coordinates3);
        polygons.add(new Polygon(linear3, null, fact));    

    	Coordinate[] coordinates4 = new Coordinate[] {
        		new Coordinate(2750-largeurRobot,2000), new Coordinate(3000,2000), 
        		/*new Coordinate(250 + largeurRobot,250+largeurRobot),*/ new Coordinate(3000,1750-largeurRobot),
        		new Coordinate(2750-largeurRobot,2000)
        	};    	
        LinearRing linear4 = fact.createLinearRing(coordinates4);
        polygons.add(new Polygon(linear4, null, fact));    
        
        //à refaire obstacles avec largeurRobot = 210;
    	Coordinate[] coordinates5 = new Coordinate[] {
        		new Coordinate(1100,650), new Coordinate(1900,650), new Coordinate(1900,1450), new Coordinate(1100,1450), new Coordinate(1100,650)
        	};    	
        LinearRing linear5 = fact.createLinearRing(coordinates5);
        polygons.add(new Polygon(linear5, null, fact));       
        
    	Coordinate[] coordinates6 = new Coordinate[] {
        		new Coordinate(640,840), new Coordinate(1160,840), new Coordinate(1160,1360), new Coordinate(640,1360), new Coordinate(640,840)
        	};    	
        LinearRing linear6 = fact.createLinearRing(coordinates6);
        polygons.add(new Polygon(linear6, null, fact));  
        
    	Coordinate[] coordinates7 = new Coordinate[] {
        		new Coordinate(1840,840), new Coordinate(1840,840), new Coordinate(2360,1360), new Coordinate(2360,1360), new Coordinate(1840,840)
        	};    	
        LinearRing linear7 = fact.createLinearRing(coordinates7);
        polygons.add(new Polygon(linear7, null, fact));  
        
    }
    
    public boolean validation(){
        for (Polygon poly : this.polygons){
        	if (true) return false;
        }
        
        return true;
    }
    
    public List<Vertex> path(Vertex i, Vertex f) {
        //Rﾃｩinitialistion des points
        
        for(int index = 0 ; index < point.length ; index++) {
            if(point[index] != null) {
                point[index].previous = null;
                point[index].minDistance = Double.POSITIVE_INFINITY;
                point[index].ignore = false;
            }
        }        
        
        Dijkstra.computePaths(i, this.obstacles);
        
	System.out.println("Distance to " + f + ": " + f.minDistance);
        List<Vertex> path = Dijkstra.getShortestPathTo(f);
	System.out.println("Path: " + path);
        
        if(Double.isInfinite(f.minDistance)) {
            path = null;
        }
        
        return path;
    } 
    
    public void recadrage(double angle, double ddistance) {
        posXInit = posXInit - (int)(ddistance*Math.cos(Math.toRadians(angle)));
        posYInit = posYInit - (int)(ddistance*Math.sin(Math.toRadians(angle)));
    }
    
    public void setBlocked(Vertex i, Vertex f)
    {
        for (Edge e : i.adjacencies)
        {
            if(e.target.equals(f))
            {
                e.blocked = true;
                System.out.println(i + " ==> " + f + " est bloquﾃｩ");
            }
        }
        
        for (Edge e : f.adjacencies)
        {
            if(e.target.equals(i))
            {
                e.blocked = true;
                System.out.println(f + " ==> " + i + " est bloquﾃｩ");
            }
        }
    }
    
    public Vertex[] getPosPoints()
    {
        return point;
    }
    
    public List<Point> getObstacles(){
    	return obstacles;
    }
    
    public int getXInit()
    {
        return posXInit;
    }
    
    public int getYInit()
    {
        return posYInit;
    }
    
    //Fonction qui convertit un List<Vertex> en List<Point> pour envoyer à la partie
    //s'occupant des trajectoires de Bézier
    public List<Point> convertPath(List<Vertex> path){
    	List<Point> path2 = new ArrayList<Point>(path.size()); 
		for (Vertex v : path) { 
		  path2.add(v.getPoint()); 
		}
    	return path2;
    }
    
//fonction qui enlève les points qui sont alignés pour faciliter Bézier.    
    public List<Point> convertIntoSegments(List<Point> path){
    	int i = 1;
		while (i < path.size()-1) { 
			if (aligne(path.get(i-1),path.get(i),path.get(i+1))){
				path.remove(i);
				i--;
			}
			i++;
		}
    	return path;
    }   

    //fonction qui utilise un déterminant pour déterminer si les points sont alignés
    public boolean aligne(Point p1, Point p2, Point p3){
    	Point v1 = new Point();
    	v1.setLocation(p2.getX()-p1.getX(),p2.getY()-p1.getY());
    	Point v2 = new Point();
    	v2.setLocation(p3.getX()-p2.getX(),p3.getY()-p2.getY());
    	if (Math.abs(v1.getX()*v2.getY() - v1.getY()*v2.getX()) <= 0.000001){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    public void test() {
        //Chemins 
        List<Vertex> path = this.path(point[1], point[665]);
        //this.setBlocked(point[3], point[4]);
        //System.out.println(aligne(new Point(0,0), new Point(2,2), new Point(10,10)));
    }
    
    public List<Vertex> getTest(int i) {
        //Chemins 
        return this.path(point[0], point[i]);
        //this.setBlocked(point[3], point[4]);
        
    }
     
    public List<Point> getTest2(int i) {
        //Chemins 
        return this.convertIntoSegments(this.convertPath(this.path(point[0], point[i])));
        //this.setBlocked(point[3], point[4]);
        
    }
    
    //renvoie le vertex correspondant au couple (x,y)
    public Vertex getVertex(int x,int y){
		return this.point[this.getIndex(x, y)];
    }

    //renvoie l'indice correspondant au couple (x,y)
    public int getIndex(int x,int y){
    	int x1 = x / 20;
    	int y1 = y / 20;
		return (x1 * 101 + y1);
    }
    
    public static void main(String[] args)
    {
        TableDeJeu tableDeJeu = new TableDeJeu("droite");
        tableDeJeu.init();
        //System.out.println(tableDeJeu.getIndex(100,120));
        //List<Point> pl = tableDeJeu.getTest2(534);
        
        new Fenetre(tableDeJeu);
    }
    
    
}

class Dijkstra
{
	
    public static void computePaths(Vertex source, List<Point> obstacles)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
	vertexQueue.add(source);

	while (!vertexQueue.isEmpty()) {
	    Vertex u = vertexQueue.poll();
	    	
	    if (u == null) break;
            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance && !e.blocked && (!obstacles.contains(v.getPoint()))) {
				    vertexQueue.remove(v);
		
				    v.minDistance = distanceThroughU ;
				    v.previous = u;
				    vertexQueue.add(v);
		                    
		                    v.ignore = e.ignore;
				}
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            //System.out.println(vertex);
            path.add(vertex);
        }

        Collections.reverse(path);
        return path;
    }
}


class Edge
{
    public final Vertex target;
    public final double weight;
    public final boolean ignore;
    public boolean blocked;
    
    public Edge(Vertex argTarget, double argWeight, boolean ignore) { 
        blocked = false;
        this.ignore = ignore;
        target = argTarget; 
        weight = argWeight; 
    }
    
    public Edge(Vertex argTarget, double argWeight) { 
        blocked = false;
        ignore = false;
        target = argTarget; 
        weight = argWeight; 
    }
}

class Fenetre extends JFrame {
  public Fenetre(TableDeJeu tdj){               
    this.setTitle("Graphe");
    this.setSize(800, 600); //800,600
    this.setLocationRelativeTo(null);              
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setContentPane(new Panneau(tdj));
 
    this.setVisible(true);
  }    
}

class Panneau extends JPanel {
    
    private TableDeJeu tableDeJeu;
    
    public Panneau(TableDeJeu tdj) {
        this.tableDeJeu = tdj;
    }
    
    
  public void paintComponent(Graphics g){
    //Dessin de la carte
    g.setColor(Color.black);
    double rapport1 = 800.0/3000.0; // 800 px sur l'ﾃｩcran == 3000 mm
    double rapport2 = 600.0/2000.0;
    
    //Rﾃｩcupﾃｩration de la position des points 
    Vertex[] point = this.tableDeJeu.getPosPoints();
    
    //bordures
    /*g.drawLine((int)(400*rapport), (int)(0*rapport), (int)(400*rapport), (int)(2000*rapport));
    g.drawLine((int)((3000-400)*rapport), (int)(0*rapport), (int)((3000-400)*rapport), (int)(2000*rapport));
    g.drawLine((int)((0)*rapport), (int)(2000*rapport), (int)((3000)*rapport), (int)(2000*rapport));
    
    g.drawLine((int)((0)*rapport), (int)(100*rapport), (int)((400)*rapport), (int)(100*rapport));
    g.drawLine((int)((0)*rapport), (int)(400*rapport), (int)((400)*rapport), (int)(400*rapport));
    g.drawLine((int)((0)*rapport), (int)(800*rapport), (int)((400)*rapport), (int)(800*rapport));
    g.drawLine((int)((0)*rapport), (int)(1200*rapport), (int)((400)*rapport), (int)(1200*rapport));
    g.drawLine((int)((0)*rapport), (int)(1600*rapport), (int)((400)*rapport), (int)(1600*rapport));
    g.drawLine((int)((0)*rapport), (int)(1900*rapport), (int)((400)*rapport), (int)(1900*rapport));
    
    g.drawLine((int)((3000)*rapport), (int)(100*rapport), (int)((3000-400)*rapport), (int)(100*rapport));
    g.drawLine((int)((3000)*rapport), (int)(400*rapport), (int)((3000-400)*rapport), (int)(400*rapport));
    g.drawLine((int)((3000)*rapport), (int)(800*rapport), (int)((3000-400)*rapport), (int)(800*rapport));
    g.drawLine((int)((3000)*rapport), (int)(1200*rapport), (int)((3000-400)*rapport), (int)(1200*rapport));
    g.drawLine((int)((3000)*rapport), (int)(1600*rapport), (int)((3000-400)*rapport), (int)(1600*rapport));
    g.drawLine((int)((3000)*rapport), (int)(1900*rapport), (int)((30006400)*rapport), (int)(1900*rapport));*/
    
    
    //affichage de la maille et des edges
    for(Vertex v : point)
    {
        if(v != null) {
            /*int x = (int)(800-((v.getPoint().getX() + tableDeJeu.getXInit())*rapport));
            int y = (int)(((v.getPoint().getY() + tableDeJeu.getYInit())*rapport));*/
        	int x = (int) (v.getPoint().getX()*rapport1);
        	int y = (int) (v.getPoint().getY()*rapport2);
            g.setColor(Color.blue);
            g.fillOval(x-2, y-2, 4, 4);
            
            g.setColor(Color.yellow);
            if(v.adjacencies != null){
                for (Edge e : v.adjacencies)
                {
                    /*g.drawLine(x, y, (int)(800-((e.target.getPoint().getX() + tableDeJeu.getXInit())*rapport)), (int)((e.target.getPoint().getY() + tableDeJeu.getYInit())*rapport));*/
                	g.drawLine(x, y, (int)(e.target.getPoint().getX() * rapport1), (int) (e.target.getPoint().getY() * rapport2));
                }
            }
        }
    }
    
    //dessin du chemin
    g.setColor(Color.red);
    System.out.println(System.currentTimeMillis());
    List<Vertex> path = this.tableDeJeu.getTest(this.tableDeJeu.getIndex(1500,20));
    System.out.println(System.currentTimeMillis());
    List<Point> path2 = this.tableDeJeu.getTest2(this.tableDeJeu.getIndex(1500,20));    
    Vertex vinit = path.get(0);
    for (Vertex v : path){
    	g.drawLine((int) (v.getPoint().getX()*rapport1), (int) (v.getPoint().getY()*rapport2), 
    				(int) (vinit.getPoint().getX()*rapport1), (int) (vinit.getPoint().getY()*rapport2));
    	vinit = v;
    }
    
   
    for (Point v : path2){
    	if (v != null){
	    	int x = (int) (v.getX()*rapport1);
	    	int y = (int) (v.getY()*rapport2);
	        g.setColor(Color.blue);
	        g.fillOval(x-4, y-4, 8, 8);
    	}
    }
    
    g.setColor(Color.darkGray);
    g.fillOval(-2, -2, 4, 4);    
    g.fillOval((int) (3000*rapport1)-1, (int) (2000*rapport2)-1, 2, 2);      
    
    for (Point v : this.tableDeJeu.getObstacles()){
    	if (v != null){
	    	int x = (int) (v.getX()*rapport1);
	    	int y = (int) (v.getY()*rapport2);
	        g.setColor(Color.green);
	        g.fillOval(x-4, y-4, 8, 8);
    	}
    }
    
  }              
}