package Tanks;

public class Player {
    public final Character playerName;
    private int parachute = App.INITIAL_PARACHUTES;
    private int score=0;
    private boolean bigProjectile = false;
    public int [] rgbColors;

    public Player(Character playerName) {
        this.playerName = playerName;
        this.rgbColors = App.setRBGValues(App.playerColoursConfig.getString(playerName.toString()));
    }

    public Character getPlayerName() {return playerName;}

    public int getParachute() {return parachute;}

    public void setParachute(int parachute) {this.parachute = parachute;}

    public int getScore() {return score;}

    public void setScore(int score) {this.score += score;}

    public void setBigProjectile (boolean bigProjectile) {this.bigProjectile = bigProjectile;}

    public boolean isBigProjectile() {return bigProjectile;}

}
