/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.InputStream;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;
import java.util.Vector;

public class ArduinoManager implements SerialPortEventListener {
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyACM0", // Linux
			"/dev/ttyACM1", // Linux
			"/dev/ttyUSB0", // Linux
			"/dev/ttyUSB1", // Linux
			"COM0", // Windows
			"COM1", // Windows
			"COM2", // Windows
			"COM3", // Windows
			"COM4", // Windows
			"COM5", // Windows
			"COM6", // Windows
			"COM7", // Windows
			"COM8", // Windows
			"COM9", // Windows
			"COM10", // Windows
			};
	/** Buffered input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 57600;
        
        private static final char END_COM = '\n';
        
        //variables
        private Vector<ArduinoListener> listArduinoListener;
        private String message_tmp; 

	public boolean initialize(String port) {
		CommPortIdentifier portId = null;
		//Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
                
                listArduinoListener = new Vector<ArduinoListener>();
                
                message_tmp = "";
                
                try {
                    portId = CommPortIdentifier.getPortIdentifier(port);
                } catch(Exception e) {System.out.println(e);}

		// iterate through, looking for the port
                /*
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println("port : " + currPortId.getName());
                        if (currPortId.getName().equals(port)) {
                                System.out.println("3");
                                portId = currPortId;
                                System.out.println(port);
                                break;
                        }
                        
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					System.out.println(portName);
					break;
				}
			}
                        
		}*/
		
		
		

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return false;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			return true;
		} catch (Exception e) {
			System.err.println(e.toString());
			return false;
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			try {
				input.close();
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				
				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);
				
//				System.out.print(serialPort.getName() + " : " + chunk + " \"" + new String(chunk) + "\" " + "[");
//				
//				for(int i=0; i<available; i++)
//				{
//					System.out.print(chunk[i] + " ");
//				}
//				System.out.println("]");
				
				message_tmp = message_tmp + new String(chunk);

                                System.out.print(new String(chunk));
                                for(int i = 0 ; i < message_tmp.length() ; i++)
				{
                                        //System.out.print(message_tmp.charAt(i) + " ");
					if(message_tmp.charAt(i) == END_COM)
					{
                                                //  System.out.println("<= trouvé !");
						String str = message_tmp.substring(0, i); //i-1 car le retour chariot se note : 13 10
						//System.out.println("received = " + str);
						message_tmp = message_tmp.substring(i+1);
						this.updateArduinoListener(str);
                                                i = 0;
					}
				}
	
				// Displayed results are codepage dependent
			} catch (Exception e) { e.printStackTrace(); }
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	public void send(String message)
	{
            try
            {
                    System.out.println("sent = " + message);
                    output.write(message.getBytes());
                    output.write(END_COM);
                    output.flush();
            } catch (Exception e) {
                    System.err.println(e.toString());
            }
	}
	
	public void sendBytes(byte[] arg)
	{
        try
        {

			System.out.print( arg + " \"" + new String(arg) + "\" " + "[");
			
			for(int i=0; i<arg.length; i++)
			{
				System.out.print( arg[i] + " ");
			}
			System.out.println("] ---> " + serialPort.getName());
        	
                //System.out.println("sent = " + message);
                output.write(arg);
                output.flush();
        } catch (Exception e) {
                System.err.println(e.toString());
        }
		
	}
        
        public void addArduinoListener(ArduinoListener arduinoListener)
        {
            listArduinoListener.add(arduinoListener);
        }
        
        public void updateArduinoListener(String message)
        {
            for(int i = 0 ; i < listArduinoListener.size() ; i++)
            {
                listArduinoListener.get(i).read(message);
            }
        }
        
        public void removeArduinoListener(ArduinoListener arduinoListener)
        {
            listArduinoListener.remove(arduinoListener);
        }

}