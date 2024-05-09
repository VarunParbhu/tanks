package Tanks;

public class Player {
    public final Character playerChar;
    public int [] rgbColors;

    private int parachute;
    private int score;
    private boolean bigProjectile;

    public Player(Character playerChar) {
        this.playerChar = playerChar;
        rgbColors = App.setRBGValues(App.playerColoursConfig.getString(playerChar.toString()));
        parachute = App.INITIAL_PARACHUTES;
        score = 0;
        bigProjectile = false;
    }

    public int getParachute() {
        return parachute;
    }

    public void setParachute(int parachute) {
        this.parachute = parachute;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score += score;
    }

    public void setBigProjectileActive() {
        this.bigProjectile = true;
    }

    public void setBigProjectileInactive() {
        this.bigProjectile = false;
    }

    public boolean isBigProjectile() {
        return bigProjectile;
    }

    public void resetPlayer(){
        parachute = App.INITIAL_PARACHUTES;
        score=0;
        bigProjectile = false;
    }

}
