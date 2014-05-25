
public enum Commande {

    ENABLE_MOTORS(0x04),
    DISABLE_MOTORS(0x05),

    ARRET_URGENCE(0x06),


            // Asservissement
    ASSERVISSEMENT_POSITION_X(0X07),
    ASSERVISSEMENT_POSITION_Y(0X08),
    ASSERVISSEMENT_POSITION_THETA(0X09),
    ASSERVISSEMENT_VITESSE_V(0x0A),
    ASSERVISSEMENT_VITESSE_W(0x0B),
    ASSERVISSEMENT_POSITION_OK(0x0C),
    ASSERVISSEMENT_VITESSE_OK(0x0D),

            // Odométrie
    RESET_ODOMETRIE(0x20),
    PRINT_ODOMETRIE(0x21),
    ODOMETRIE_X(0x22),
    ODOMETRIE_Y(0x23),
    ODOMETRIE_THETA(0x24),
    ODOMETRIE_OK(0x25),

            // PIDs
    PID1_K_P(0x30),
    PID1_K_I(0x31),
    PID1_K_D(0x32),
    PID1_OK(0x33),
    PID2_K_P(0x34),
    PID2_K_I(0x35),
    PID2_K_D(0x36),
    PID2_OK(0x37), 
    
    ECHO(0x7F);
	
    private final int value;
    private Commande(int value) {
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
