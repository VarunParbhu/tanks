package Tanks;

/**
 * Represents Player objects that persists across every each level until the game ends.
 */
public class Player {
    public final Character playerChar;
    public int [] rgbColors;
    private int parachute;
    private int score;
    private boolean bigProjectile;

    /**
     * Creates a player with a Character associated to it.
     * @param playerChar
     *      The player character. Used in Hashmaps to associate Tank to players.
     */
    public Player(Character playerChar) {
        this.playerChar = playerChar;
        rgbColors = App.setRBGValues(App.playerColoursConfig.getString(playerChar.toString()));
        parachute = App.INITIAL_PARACHUTES;
        score = 0;
        bigProjectile = false;
    }

    /**
     * @return
     *      Gets the number of parachute left for the player.
     */
    public int getParachute() {
        return parachute;
    }

    /**
     * Sets the number of parachute left for the players.
     * @param
     *      parachute Number of parachute
     */
    public void setParachute(int parachute) {
        this.parachute = parachute;
    }

    /**
     * @return
     *       Gets the score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the player.
     * @param
     *      score score.
     */
    public void setScore(int score) {
        this.score += score;
    }

    /**
     * Sets the next shot to be a big projectile for the player.
     */
    public void setBigProjectileActive() {
        this.bigProjectile = true;
    }

    /**
     * Sets the next shot to be a normal projectile for the player.
     */
    public void setBigProjectileInactive() {
        this.bigProjectile = false;
    }

    /**
     * @return
     *      Checks if the next shot is a big projectile for the player.
     */
    public boolean isBigProjectile() {
        return bigProjectile;
    }

    /**
     * Resets the player parachute, score and projectile to normal.
     */
    public void resetPlayer(){
        parachute = App.INITIAL_PARACHUTES;
        score=0;
        bigProjectile = false;
    }

}
