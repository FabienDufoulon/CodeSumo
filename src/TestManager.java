import java.io.DataInputStream;
import java.io.IOException;

public class TestManager {
    public static void main(String[] args) throws IOException
    {
        TableDeJeu tableDeJeu = new TableDeJeu();
        System.out.println("In process");
        tableDeJeu.init();
        System.out.println("done !");
        
        //tableDeJeu.test();
        
        //new Fenetre(tableDeJeu);
    	/*while (true){
	    	DataInputStream in=new DataInputStream(System.in);
	    	char ch= (char) System.in.read();
	    	if (Character.isWhitespace(ch)) {
				System.out.println("null char");
	    	} 
	    	else {System.out.println("Char : " +ch );  
	    			System.out.println(" "); }
    	}*/

    }
} 

/*QEI marchent. moteurs fonctionnent.
 * asserv : position
 *          vitesse
 *          reset odométrie (envoi de "R")
 *          enable motor (envoi de "E")
 *          disable (envoi de "D")
 *          break (envoi de "B")
 *          precision "P..."
 * java : asservissement en suivi de trajectoire et génération de trajectoire
 * 		  de djikstra : envoi d'une trajectoire discrète  (fait) (à gérer les obstacles aussi)
 * 		  de trajectoire : envoi d'une trajectoire continue (fonction de paramètre t entre 0 et 1 renvoyant un (x,y))
 * 							par interpolation de Bézier par morceau.
 * 		  de djikstra : envoi de validation ou non
 * 		  de trajectoire : envoi par protocole série de "X...Y...T..." à l'asserv
 * 		  gestion collision ?
 * 		  envoi de messages pour gestion actionneur
 * capteur collision distance de 10 cm
 * GPIO
 * 	bouton démarrage(en java?)
 * 	+ bouton pour couleur équipe
 * convertir List<Vertex> en List<Point> (fait)
 */
