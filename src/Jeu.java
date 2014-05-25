
public class Jeu {

	public static void main(String[] args)
    {
        TableDeJeu tableDeJeu = new TableDeJeu("droite");
        tableDeJeu.init();
        
        //Robot robot = new Robot(new ArduinoManager(), new ArduinoManager(), tableDeJeu.getVertex(40, 80));
        
        // premier feu cote mur
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(3000, 800));
        }else{
        	// robot.moveto(tableDeJeu.getVertex(0, 800));        	
        }
        
        //premier arbre
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(3000, 1300));     
        }else{
        	// robot.moveto(tableDeJeu.getVertex(0, 1300));               	
        }
        
        //deuxieme feu
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(2600, 1100)); 
        }else{
        	// robot.moveto(tableDeJeu.getVertex(400, 1100));             	
        }
        
        //foyer
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(2100, 1100));     	
        }else{
        	// robot.moveto(tableDeJeu.getVertex(900, 1100));         	
        }
        
        //troisieme feu
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(2100, 1600));   
        }else{
        	// robot.moveto(tableDeJeu.getVertex(900, 1600));           	
        }
        
        //deuxieme arbre
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(2300, 2000));   
        }else{
        	// robot.moveto(tableDeJeu.getVertex(700, 2000));           	
        }
        
        //quatrieme feu
        if (/*couleur robot jaune*/ true){
        	// robot.moveto(tableDeJeu.getVertex(1700, 2000));          	
        }else{
        	// robot.moveto(tableDeJeu.getVertex(1300, 2000));  
        }
        
        new Fenetre(tableDeJeu);
    }
}
