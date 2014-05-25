import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;

public class CarteAsservissement {

	public double x, y, t;

	ArduinoManager carte;

	FileWriter logFile;
	BufferedWriter log;

	public double v, w;

	CarteAsservissement(String port) {

		carte = new ArduinoManager();

		carte.initialize(port);

		try {
			logFile = new FileWriter("log.csv", false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log = new BufferedWriter(logFile);

		carte.addArduinoListener(new ArduinoListener() {

			@Override
			public void read(String str) {

				str = str.replaceAll("X", " ");
				str = str.replaceAll("Y", " ");
				str = str.replaceAll("T", " ");
				str = str.replaceAll("V", " ");
				str = str.replaceAll("W", " ");

				String[] strings = str.split(" ");
				try {

					x = Float.parseFloat(strings[1]);
					y = Float.parseFloat(strings[2]);
					t = Float.parseFloat(strings[3]);
					v = Float.parseFloat(strings[4]);
					w = Float.parseFloat(strings[5]);
					
					try {
						log.write(x + "," + y + "," + t + "," + v + "," + w + "\n");
						log.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (Exception e) {
				}

			}
		});
	}

	public void sendCommande(Commande commande, float f) {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.putShort((short) commande.getValue());

		buffer.putInt(Float.floatToIntBits(f));

		carte.sendBytes(buffer.array());
	}

	public void setAsservissementVitesse(float v, float w) {
		carte.send("V" + String.format(Locale.ENGLISH, "%.3f", v));
		carte.send("W" + String.format(Locale.ENGLISH, "%.3f", w));
		carte.send("O");
	}
	
	public void stop()
	{
		this.setAsservissementVitesse((float)0.0,  (float)0.0);
	}

	public void setAsservissementPosition(float x, float y, float theta) {
		sendCommande(Commande.ASSERVISSEMENT_POSITION_X, x);
		sendCommande(Commande.ASSERVISSEMENT_POSITION_Y, y);
		sendCommande(Commande.ASSERVISSEMENT_POSITION_THETA, theta);
		sendCommande(Commande.ASSERVISSEMENT_POSITION_OK, 0);
	}

	public void setPosition(float x, float y, float theta) {
		sendCommande(Commande.ODOMETRIE_X, x);
		sendCommande(Commande.ODOMETRIE_Y, y);
		sendCommande(Commande.ODOMETRIE_THETA, theta);
		sendCommande(Commande.ODOMETRIE_OK, 0);
	}

	public void setPIDGauche(boolean isGauche, float p, float i, float d) {
		if (isGauche) {
			sendCommande(Commande.PID1_K_P, p);
			sendCommande(Commande.PID1_K_I, i);
			sendCommande(Commande.PID1_K_D, d);
			sendCommande(Commande.PID1_OK, 0);
		} else {
			sendCommande(Commande.PID2_K_P, p);
			sendCommande(Commande.PID2_K_I, i);
			sendCommande(Commande.PID2_K_D, d);
			sendCommande(Commande.PID2_OK, 0);
		}
	}

	public void getPosition() {
		carte.send("P");
	}

	public void getVitesse() {
		sendCommande(Commande.PRINT_ODOMETRIE, 0);
	}

	public void enableMotors(boolean e) {
		if (e)
			sendCommande(Commande.ENABLE_MOTORS, 0);
		else
			sendCommande(Commande.DISABLE_MOTORS, 0);
	}

	public void arretUrgence() {
		sendCommande(Commande.ARRET_URGENCE, 0);
	}

	public void echo() {
		sendCommande(Commande.ECHO, 1.111111f);

	}

	
	public void reset()
	{
		carte.send("R");
	}
	public void finalize() {
		try {
			log.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
