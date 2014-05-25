/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class Robot {

    private Vertex vertexAct;
    private static final long ChallengeDuration = 90 * 1000; //1min30
    private static ArduinoManager arduinoMoteurs;
    private static ArduinoManager arduinoCapteurs;
    private double x, y; 
    private double rot;
    private int valSonar;
    private long timeStart; //en secondes
    //messages
    private boolean endMove;
    private boolean obstacle;
    private boolean rotation;
    private boolean deplacement;
    private boolean endAction;
    private int distanceSonar;

    public Robot(ArduinoManager arduinoMoteurs, ArduinoManager arduinoCapteurs, Vertex premierVertex) {

        vertexAct = premierVertex;

        //initialisation des variables;
        endMove = false;
        obstacle = false;
        rotation = false;
        deplacement = false;
        endAction = false;

        //Initialisation de la arduino
        this.arduinoMoteurs = arduinoMoteurs;
        this.arduinoCapteurs = arduinoCapteurs;

        distanceSonar = 70;
		
		//Listener liee aux moteurs qui lit sur l'input des moteurs si on fait une rotation, un deplacement,
		// si on arrete de se deplacer et lit des donnees du "sonar"
        arduinoMoteurs.addArduinoListener(new ArduinoListener() {
            @Override
            public void read(String msg) {
                //System.out.println("Received M : " + msg);

                if (msg.equals("FinDeplacement")) //fin du mouvement
                {
                    System.out.println("fin de mouvement !");
                    endMove = true;
                } else if (msg.equals("rotation")) {
                    System.out.println("rotation !");
                    rotation = true;
                } else if (msg.equals("deplacement")) {
                    System.out.println("deplacement !");
                    deplacement = true;
                } else if (msg.charAt(0) == 'S') {
                    System.out.println("msg = " + msg);
                    System.out.println("update du sonar");
                    System.out.println(msg.substring(1));
                    valSonar = Integer.parseInt(msg.substring(1));
                    System.out.println("valSonar = " + valSonar);
                } else if (msg.length() > 12) {
                    try {
                        String coordonnees[] = msg.split("\t");
                        x = Double.parseDouble(coordonnees[0]);
                        y = Double.parseDouble(coordonnees[1]);
                        rot = Double.parseDouble(coordonnees[2]) * 360 / 3.14;
                        //System.out.println("X = " + x + ", Y = " + y + ", rot = " + rot);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        });
		
		//Listener liee aux capteurs qui lit sur l'input des capteurs la presence ou l'absence d'un
		// obstacle ou aussi la fin d'une action.
        arduinoCapteurs.addArduinoListener(new ArduinoListener() {
            @Override
            public void read(String msg) {
                //System.out.println("Received C : " + msg);
				//Robot.arduinoCapteurs.send("0") : Accuse de Reception
                if (msg.equals("OT")) //obstacle true
                {
                    Robot.arduinoCapteurs.send("O");
                    System.out.println("obstacle = true");
                    obstacle = true;
                } else if (msg.equals("OF")) //obstacle false
                {
                    Robot.arduinoCapteurs.send("O");
                    System.out.println("obstacle = false");
                    obstacle = false;
                } else if (msg.equals("finAction")) {
                    Robot.arduinoCapteurs.send("O");
                    System.out.println("fin de l'action");
                    endAction = true;
                }
            }
        });

        System.out.println("Start !");
        arduinoMoteurs.send("Z");
		//arduinoMoteurs.send("Z") : Reinitialisation

        this.start();
    }

	//Ordonne les moteurs de s'activer
    public void start() {
        arduinoMoteurs.send("ET"); //ET = Enable True : active l'energie dans les moteurs
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
        arduinoMoteurs.send("EF"); //EF = Enable False : desactive l'energie dans les moteurs
    }
	
	//Ordonne les moteurs d'arreter le deplacement
    public void block() {
        arduinoMoteurs.send("DF"); //DF = Deplacement False : arret du deplacement
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
    /*
     public void move(Vertex v) throws Exception {
     this.move(v, false, true);
     }
     */

    public void move(Vertex v, boolean ignoreObstacle, boolean angleDefini) throws Exception {
        this.move(v, ignoreObstacle, angleDefini, "");
    }
	
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
                arduinoMoteurs.send("DF"); // DF = Deplacement False : Si la limite de temps est depassee,
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
                arduinoMoteurs.send("DF"); // DF = Deplacement False : Si la limite de temps est depassee,
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
                arduinoMoteurs.send("DF"); // DF = Deplacement False : Si la limite de temps est depassee,
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
            arduinoMoteurs.send("DF"); // DF = Deplacement False : Si on a un obstacle et qu'on ne l'ignore pas,
            throw new Exception("obstacle"); // arreter les moteurs et renvoyer une exception "obstacle"
        }

        System.out.println("Rotation du robot");

        while (!endMove) //On attend la fin du mouvement
        {
            if (System.currentTimeMillis() - timeStart >= ChallengeDuration) {
                arduinoMoteurs.send("DF"); // DF = Deplacement False : Si la limite de temps est depassee,
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
         */
        vertexAct = v;
    }
	
	
    public void retour() throws Exception {
        System.out.println("Retour");
        move(vertexAct, true, false, ""); //VertexAct fait reference au dernier vertex en memoire
		// On fait retour arriere au dernier vertex en memoire, en ignorant les obstacles et en 
		// recalculant l'angle de rotation.
    }
	
	//Fonction qui prend une position "flottante", et qui retourne une chaine de caractere composee
	// du signe et de 4 chiffres. (ex : "+0010" decimal)
    private String adaptPos(double pos) {
        int intPos = (int) pos;
        String X = String.format("%04d", Math.abs(intPos)); // Fonction qui formate la chaine de caractere
															// pour qu'elle ait 4 chiffres
        if (pos >= 0) {
            X = "+" + X;
        } else {
            X = "-" + X;
        }

        return X;
    }

	//Fonction qui prend une angle "flottant", et qui retourne une chaine de caractere composee
	// du signe et de 4 chiffres, plus precisemment 3 chiffres qui denote la partie entiere de l'angle
	// et 1 chiffre pour le premier chiffre apres la virgule (ex : "+3251" degres)
    private String adaptRot(double rot) {
        int intRotEntier = (int) rot;
        int intRotDecimale = (int) ((rot % 1) * 10); // prend le premier chiffre apres la virgule
        String stringRotEntier = String.format("%03d", Math.abs(intRotEntier)); 
		// Fonction qui formate la chaine de caractere pour qu'elle ait 3 chiffres
        String stringRotDecimale = Integer.toString(Math.abs(intRotDecimale));
		// Fonction qui change le premier chiffre apres la virgule a une chaine de caractere
        String stringRot;

        if (rot >= 0) {
            stringRot = "+" + stringRotEntier + stringRotDecimale;
        } else {
            stringRot = "-" + stringRotEntier + stringRotDecimale;
        }

        return stringRot;
    }
	
	//Fonction qui beep le sonar pour qu'il lui renvoie la distance a travers le listener et qui la renvoie
    public int getDist() {
        valSonar = Integer.MAX_VALUE;
        arduinoMoteurs.send("S");

        while (valSonar == Integer.MAX_VALUE) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }

        System.out.println("getDist() = " + valSonar);

        return valSonar;
    }
	
	//Fonction qui envoie les coordonnees x et y aux moteurs , plus precisemment des chaines de caractere
	// avec "PX" ou "PY" avec la chaine de position concatenee (ex : PX+0010)
    public void setPos(int x, int y) {
        System.out.println("PX" + this.adaptPos(x));
        arduinoMoteurs.send("PX" + this.adaptPos(x));
        System.out.println("PY" + this.adaptPos(y));
        arduinoMoteurs.send("PY" + this.adaptPos(y));
    }

	//Fonction qui envoie l'angle de rotation aux moteurs, plus precisemment des chaines de caractere
	// avec "PX" ou "PY" avec la chaine de l'angle concatenee (ex : PT+3212)
    public void setRot(double rot) {
        System.out.println("PT" + this.adaptRot(rot));
        arduinoMoteurs.send("PT" + this.adaptRot(rot));	//Mort aux PTs !! (Fafnir -> pour Borrits)
    }

    public int getDistanceSonar() {
        return distanceSonar;
    }
}
