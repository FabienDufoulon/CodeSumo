
public class Robot {

    private Vertex vertexAct;
    private static final long ChallengeDuration = 90 * 1000; //1min30
    
    private static ArduinoManager arduinoServos;
    private static ArduinoManager arduinoCapteurs;
    private static CarteAsservissement carteAsservissement;
    
    private float x, y; 
    private float rot;
    private long timeStart; //en secondes
    
    //messages
    private boolean obstacle;
    private boolean deplacement;
    private boolean endAction;
	
	
	
    public Robot(ArduinoManager arduinoServosA, ArduinoManager arduinoCapteursA, CarteAsservissement carteAsservissementA, Vertex premierVertex) {

        vertexAct = premierVertex;

        //initialisation des variables;
        obstacle = false;
        endAction = false;
        deplacement = false;
        
        //Initialisation de la arduino
        arduinoServos = arduinoServosA;
        arduinoCapteurs = arduinoCapteursA;
        carteAsservissement = carteAsservissementA;
       		
		//Listener liee aux moteurs qui lit sur l'input des moteurs si on fait une rotation, un deplacement,
		// si on arrete de se deplacer et lit des donnees du "sonar"
        arduinoServos.addArduinoListener(new ArduinoListener() {
            @Override
            public void read(String msg) {
                //System.out.println("Received M : " + msg);
                if (msg.equals("F")) //fin de l'action
                {
                    System.out.println("fin de l'action !");
                    endAction = true;
                } 
            }
        });
		
		//Listener liee aux capteurs qui lit sur l'input des capteurs la presence ou l'absence d'un
		// obstacle ou aussi la fin d'une action.
        arduinoCapteurs.addArduinoListener(new ArduinoListener() {
            @Override
            public void read(String msg) {
                //System.out.println("Received C : " + msg);
                try {
                	obstacle = false;
                    String Scoordonnees[] = msg.split("cm");
                    int[] coordonnees = new int[4];
                    for (int i = 0; i < 4; i++){
                    	coordonnees[i] = Integer.parseInt(Scoordonnees[i]);
                    	if (coordonnees[i] < 15 && deplacement){
                    		obstacle = true;
                    	}
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });

        System.out.println("Start !");
        carteAsservissement.reset();

        this.start();
    }	
    
	//Ordonne les moteurs de s'activer
    public void start() {
        carteAsservissement.enableMotors(true); //active l'energie dans les moteurs
    }
    
    public void startTime() {
        timeStart = System.currentTimeMillis();

        try {
            Runtime.getRuntime().exec("aplay sound.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	//Ordonne les moteurs de se desactiver
    public void end() {
        carteAsservissement.enableMotors(false); ; // desactive l'energie dans les moteurs
    }
	
	//Ordonne les moteurs d'arreter le deplacement
    public void block() {
        carteAsservissement.stop(); //arret du deplacement
    }
    
    public Vertex getPosAct() {
        return vertexAct;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRot() {
        return rot;
    }
    
    //used at init
    public double setRot(float rot) {
        return this.rot = rot;
    }
    
    public void move(Vertex v, boolean ignoreObstacle) throws Exception {
        this.moveLin(v, ignoreObstacle);
    }
	
    
    
    public void moveLin(Vertex v, boolean ignoreObstacle) throws Exception {
    	while (!endAction){
    		Thread.sleep(200);
    	}
    	deplacement = true;    	
    	
    	System.out.println(System.currentTimeMillis() - timeStart >= ChallengeDuration);
    	
    	carteAsservissement.setAsservissementPosition(distance(this.vertexAct, v), angle(this.vertexAct, v) - rot);
    	
    	while (obstacle){
        	this.block();   		
    	}
    	
    	//envoyer deux fois à la chaine à la carte d'asservissement est ce mauvais?

    	carteAsservissement.setAsservissementPosition(distance(this.vertexAct, v), angle(this.vertexAct, v) - rot);  
    	
    	//à modifier... ici ne finit jamais la fonction jusqu'à la fin du jeu.
        while (!obstacle || ignoreObstacle ) {
            if (System.currentTimeMillis() - timeStart >= ChallengeDuration) {
            	carteAsservissement.stop(); // Si la limite de temps est depassee,
                throw new Exception("time"); // arreter les moteurs et renvoyer une exception "time"
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    	
       /* while (!obstacle || ignoreObstacle) {
			float l,t;
			l = (float) Math.sqrt(Math.pow(carteAsservissement.x - p.x, 2.0) + Math.pow(carteAsservissement.y - p.y, 2.0));
			t = -(float) (carteAsservissement.t - Math.atan2(p.y - carteAsservissement.y, p.x - carteAsservissement.x));
			
			if(l<0.01)
			{
				carteAsservissement.stop();
				System.out.println("POUEEEEET l = " + l);
				break;
			}
			else
				carteAsservissement.setAsservissementPosition(l, t);
		}
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	this.vertexAct = v;
    	deplacement = false;
    }
    
    public float distance(Vertex prev, Vertex next) {
    	return (float) Math.sqrt(Math.pow(next.getPoint().getX() - prev.getPoint().getX(), 2)
    					+ Math.pow(next.getPoint().getY() - prev.getPoint().getY(), 2));
    }
    
    public float angle(Vertex prev, Vertex next) {
    	return (float) ((next.getPoint().getX() - prev.getPoint().getX())/distance(prev, next));
    }
    
    public void faireAction(String action){
    	endAction = false;
    	switch (action){
    	//choix à faire entre actions bas level et actions haut level
    		case "lowerLift" :
    			arduinoServos.send("s");
    			break;

    		case "raiseLift" :
    			arduinoServos.send("t");
    			break;
    			
    		case "pumpOn" :
    			arduinoServos.send("p");
    			break;

    		case "pumpOff" :
    			arduinoServos.send("q");
    			break;
    			
    		case "valveOn" :
    			arduinoServos.send("e");
    			break;
    			
    		case "valveOff" :
    			arduinoServos.send("f");
    			break;
    			
    		case "rotateTigeBack" :
    			arduinoServos.send("c");
    			break;
    			
    		case "rotateTigeReady" :
    			arduinoServos.send("d");
    			break;
    			
    		case "rotateTigeVertBack" :
    			arduinoServos.send("w");
    			break;
    			
    		case "rotateTigeVertReady" :
    			arduinoServos.send("x");
    			break;
    		
    		case "powerLifter" :
    			arduinoServos.send("a");
    			break;  
    			
    		case "releaseLifter" :
    			arduinoServos.send("b");
    			break;    			
    	}
    	
    	while (!endAction){
    		try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}    		
    }
    
    /*
	//Fonction qui deplace le robot vers le Vertex v a travers une rotation initiale, un deplacement
	// rectiligne, et une rotation possible a la fin (avec des booleens si l'angle de rotation est deja 	// calcule et si on lui dit d'ignorer les obstacles sur le chemin)
    public void move(Vertex v, boolean ignoreObstacle, boolean angleDefini, String sens) throws Exception {
        endMove = false;
        rotation = false;
        deplacement = false;

        System.out.println(System.currentTimeMillis() - timeStart >= ChallengeDuration);

        //[X,Y,T][+,-][xxxx] X et Y a caster en int (pas de virgule) Theta en degres

        String X = "X" + adaptPos(v.getPoint().getX());
        System.out.println(X);
        arduinoMoteurs.send(X);

        String Y = "Y" + adaptPos(v.getPoint().getY());
        System.out.println(Y);
        arduinoMoteurs.send(Y);

        String ROT;
        if (angleDefini) {
            ROT = "T" + adaptRot(v.getRot());
        } else {
            double angle = Math.atan2(v.getPoint().getY() - vertexAct.getPoint().getY(), v.getPoint().getX() - vertexAct.getPoint().getX());
            ROT = "T" + adaptRot(Math.toDegrees(angle));
        }

        System.out.println(ROT);
        arduinoMoteurs.send(ROT);

        System.out.println("DT");
        arduinoMoteurs.send("DT"); //DT = Deplacement True : debut du deplacement


        while (!rotation) //On attend le debut de la rotation
        {
            if (System.currentTimeMillis() - timeStart >= ChallengeDuration) {
            	carteAsservissement.stop(); // Si la limite de temps est depassee,
                throw new Exception("time"); // arreter les moteurs et renvoyer une exception "time"
            }

            try {
                Thread.sleep(10);  
            } catch (Exception e) {
            }
        }
		
		//Le robot tourne pour preparer le deplacement
        System.out.println("Rotation du robot");

        while (!deplacement) //On attend la fin de la rotation
        {
            if (System.currentTimeMillis() - timeStart >= ChallengeDuration) {
            	carteAsservissement.stop(); // Si la limite de temps est depassee,
                throw new Exception("time"); // arreter les moteurs et renvoyer une exception "time"
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
		
		//Le robot se deplace
        rotation = false;

        while (!rotation && (!obstacle || ignoreObstacle)) {
            if (System.currentTimeMillis() - timeStart >= ChallengeDuration) {
            	carteAsservissement.stop(); // Si la limite de temps est depassee,
                throw new Exception("time"); // arreter les moteurs et renvoyer une exception "time"
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
		
		//Le robot arrete de se deplacer et tourne a nouveau, si jamais il y a une action a faire
        deplacement = false;

        if (obstacle && !ignoreObstacle) {
            System.out.println("Oh non, un obstacle");
            carteAsservissement.stop(); // Si on a un obstacle et qu'on ne l'ignore pas,
            throw new Exception("obstacle"); // arreter les moteurs et renvoyer une exception "obstacle"
            
            //à tester évasion et non simplement arrêt
        }

        System.out.println("Rotation du robot");

        while (!endMove) //On attend la fin du mouvement
        {
            if (System.currentTimeMillis() - timeStart >= ChallengeDuration) {
                carteAsservissement.stop(); // Si la limite de temps est depassee,
                throw new Exception("time"); // arreter les moteurs et renvoyer une exception "time"
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }

        rotation = false;
        /*
         try {
         Thread.sleep(500);
         } catch(Exception e) {}
         
        vertexAct = v;
    }*/
	
	
    public void retour() throws Exception {
        System.out.println("Retour");
        moveLin(vertexAct, true); //VertexAct fait reference au dernier vertex en memoire
		// On fait retour arriere au dernier vertex en memoire, en ignorant les obstacles et en 
		// recalculant l'angle de rotation.
    }
	
	
}
