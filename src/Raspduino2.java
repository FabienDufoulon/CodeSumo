
public class Raspduino2 {
    private TableDeJeu tableDeJeu;
    
    private Robot robot;
    
    private ArduinoManager manager0;
    private ArduinoManager manager1;
    private ArduinoManager servos;
    private ArduinoManager capteurs;
    private CarteAsservissement ca;
    
    String couleur;
    //Actions

    
    private boolean start;
    
    public Raspduino2(String couleur)
    {        
        System.out.println("version 1.0");
        
        this.couleur = couleur;
        //start = false;
        
        manager0 = new ArduinoManager();
        manager1 = new ArduinoManager();
        
        
        manager0.initialize("/dev/ttyACM0");
        manager1.initialize("/dev/ttyACM1");
        ca = new CarteAsservissement("/dev/ttyAMA0");
        
        try {
            Thread.sleep(3000); //temps necessaire a l'initialisation
        } catch(Exception e) {}
        
        
        manager0.addArduinoListener(new ArduinoListener() {
            
            @Override
            public void read(String msg) {
                //System.out.println("manager0 : " + msg);
                if(msg.compareTo("Moteurs") == 0)
                {
                    servos = manager0;
                    System.out.println("Moteurs trouve !");
                }
                else if(msg.compareTo("Capteurs") == 0)
                {
                    capteurs = manager0;
                    System.out.println("Capteurs trouve !");
                }
            }
        });
        
        manager1.addArduinoListener(new ArduinoListener() {

            @Override
            public void read(String msg) {
                //System.out.println("manager1 : " + msg);
                if(msg.compareTo("Moteurs") == 0)
                {
                    servos = manager1;
                    System.out.println("Moteurs trouve !");
                }
                else if(msg.compareTo("Capteurs") == 0)
                {
                    capteurs = manager1;
                    System.out.println("Capteurs trouve!");
                }
            }
        });
        
        manager0.send("h");
        manager1.send("h");
        
        tableDeJeu = new TableDeJeu(); 
        tableDeJeu.init();
        
        while(servos == null || capteurs == null) {
            try {
                Thread.sleep(10);
            } catch(Exception e) {}
        }
        
        System.out.println("Go !");
        
        if ( couleur == "jaune"){
        	robot = new Robot(servos, capteurs, ca, tableDeJeu.getVertex(40, 50)); //à revoir positions
        } else{
        	robot = new Robot(servos, capteurs, ca, tableDeJeu.getVertex(2960, 50));
        }
        /*
       capteurs.addArduinoListener(new ArduinoListener() {

            @Override
            public void read(String msg) {
                //System.out.println("manager1 : " + msg);
                if(msg.compareTo("START") == 0)
                {
                    start = true;
                }
            }
        });
       
       System.out.println("Waiting....");
       
       while(!start) {
            try {
                Thread.sleep(10);
            } catch(Exception e) {}
        }*/
        
        try {
                robot.startTime();
                //actions du robot :
                robot.start();
                
                
        } catch(Exception e) { 
            System.out.println("Time's up !");
        }
       /*
       try {
                square.start();
            
        } catch(Exception e) { 
            System.out.println("Time's up !");
        }*/
        
        robot.end();
    }
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Raspduino2(args[0]);
    }
}
