package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import java.util.*;

/**
 * Represents game application as an extension of the PApplet object.
 * It mainly setups all the resources required for the game, draws the level continuously, updates the UI, checking player and game state, and processing input from players.
 */
public class App extends PApplet {

    public static int WIDTH = 864; //Width of application
    public static int HEIGHT = 640;

    public static final int SMOOTHING_AVG = 32;
    public static final int INITIAL_PARACHUTES = 3;
    public static final int FPS = 60;

    private static String configPath;
    public static JSONObject playerColoursConfig;
    private static PImage fuelCanImg = null;
    public static PImage parachuteImg = null;
    private static PImage bigboiImg = null;
    private static PImage windRightImg = null;
    private static PImage windLeftImg = null;
    public static Map<Character,Player> players = new HashMap<>();
    private static int arrowTimer =0;
    private static int finalScoreBoardDelayTimer =0;
    private static int levelDelayTimer =0;
    private static int playerIndex = 0;
    private static boolean isGameOver = false;
    private static boolean isLevelOver = false;
    private static final Random rand = new Random();

    public int currentLevel = 0;
    public Level currentPlayingLevel=null;
    public ArrayList<Level> levels = new ArrayList<>();
    public Iterator<Character> playerIterator = null;
    public Iterator<Character> drawPlayerIterator = null;
    public ListIterator<Character> playerListiterator = null;
    public List<Character> playerList =new ArrayList<>();
    public Character currentPlayer = null;
    public Tank currentTank = null;

    /**
     * Loads all the configuration required by the application
     * Constructor of the application, called only ONCE
     **/
    public App() {
        configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     * size method defines the dimension of the display window in units of pixels.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     * Called only ONCE when the program starts; hence need to load all the resources here, then access them
     */
	@Override
    public void setup() {
        frameRate(FPS);
        JSONArray levelsConfig = loadJSONObject(configPath).getJSONArray("levels");
        playerColoursConfig = loadJSONObject(configPath).getJSONObject("player_colours");

        for (int i = 0; i < levelsConfig.size(); i++){
            JSONObject level = levelsConfig.getJSONObject(i);
            Level l = new Level();

            if(level.hasKey("layout")){l.setLayout(level.getString("layout"));}
            if(level.hasKey("foreground-colour")){l.setForegroundColour(level.getString("foreground-colour"));}
            if(level.hasKey("background")){
                PImage backgroundIMG = loadImage(Objects.requireNonNull(getClass().getResource(level.getString("background"))).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                if (backgroundIMG != null){l.setBackground(backgroundIMG);}
            }
            if (level.hasKey("trees")){
                PImage treeIMG = loadImage(Objects.requireNonNull(getClass().getResource(level.getString("trees"))).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                treeIMG.resize(32,32);
                l.setTreeSprite(treeIMG);
            } else {
                // Setting default tree to be tree1.png if not provided in the config JSON for the level
                PImage treeIMG = loadImage(Objects.requireNonNull(getClass().getResource("tree1.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                treeIMG.resize(32,32);
                l.setTreeSprite(treeIMG);
            }

            PImage parachuteIMG = loadImage(Objects.requireNonNull(getClass().getResource("parachute.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            parachuteIMG.resize(20,20);
            levels.add(l);
        }

        fuelCanImg = loadImage(Objects.requireNonNull(getClass().getResource("fuel.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        parachuteImg = loadImage(Objects.requireNonNull(getClass().getResource("parachute.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        bigboiImg = loadImage(Objects.requireNonNull(getClass().getResource("bigboi.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        windRightImg = loadImage(Objects.requireNonNull(getClass().getResource("wind.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        windLeftImg = loadImage(Objects.requireNonNull(getClass().getResource("wind-1.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        fuelCanImg.resize(20,20);
        parachuteImg.resize(20,20);
        bigboiImg.resize(20,20);

        currentPlayingLevel = levels.get(0);
        playerIterator = currentPlayingLevel.getPlayerTurn().iterator();

        while(playerIterator.hasNext()){
            playerList.add(playerIterator.next());
        }

        playerListiterator = playerList.listIterator();
        currentPlayer = playerListiterator.next();
    }

    /**
     * Receive key pressed signal from the keyboard.<p>
     * Spacebar: 32 - Shots projectile or changes level at the end the latter <p>
     * Left: 37 - Moves tank to the left <p>
     * Up: 38 - Moves turret to the left <p>
     * Right: 39 - Moves tank to the right <p>
     * Down: 40 - Moves turret to the right <p>
     * W: 87 - Increases power of tank <p>
     * S: 83 - Decreases power of tank <p>
     * P: 80 - Increments player parachute by 1 <p>
     * X: 88 - Triggers larger projectile for a player's next shot <p>
     * R: 82 - Restores a player's health by 20 or restarts the game at the end <p>
     * F: 70 - Increments player fuel by 200
     */
	@Override
    public void keyPressed(KeyEvent event){
        //Spacebar
        if (this.keyCode==32){
            if (currentTank.isActive()){
                currentPlayingLevel.setProjectiles(new Projectile(currentPlayingLevel,currentTank,currentPlayingLevel.getWind()));
                currentTank.player.setBigProjectileInactive();
            }
            changePlayerTurn();
            if(isLevelOver && currentLevel<2){
                changeLevel();
            }
        }

        if(!isGameOver){
              // Left
              if (this.keyCode == 37) {
                currentTank.setX(currentTank.getX() - 4);
              }
              // Right
              if (this.keyCode == 39) {
                currentTank.setX(currentTank.getX() + 4);
              }

              // Up
              if (this.keyCode == 38) {
                currentTank.setAngle(currentTank.getAngle() + 3.0 / (15));
              }

              // Down
              if (this.keyCode == 40) {
                currentTank.setAngle(currentTank.getAngle() - 3.0 / (15));
              }

              // W
              if (this.keyCode == 87) {
                currentTank.setPower(currentTank.getPower() + 36.0 / (15));
              }

              // S
              if (this.keyCode == 83) {
                currentTank.setPower(currentTank.getPower() - 36.0 / (15));
              }

              // P
              if (this.keyCode == 80) {
                if (currentTank.player.getScore() > 15) {
                  currentTank.player.setScore(-15);
                  currentTank.player.setParachute(currentTank.player.getParachute() + 1);
                }
              }

              // X
              if (this.keyCode == 88) {
                if (currentTank.player.getScore() > 20 && !currentTank.player.isBigProjectile()) {
                  currentTank.player.setScore(-20);
                  currentTank.player.setBigProjectileActive();
                }
              }

            // F
            if(this.keyCode==70){
                if(currentTank.player.getScore()>10){
                    currentTank.player.setScore(-10);
                    currentTank.setFuel(200);
                }
            }
        }

        // R
        if(this.keyCode==82){
            if(isGameOver){
                restartGame();
            } else {
                if(currentTank.player.getScore()>20 && currentTank.getHealth()!=100){
                    currentTank.player.setScore(-20);
                    currentTank.setHealth(20);
                }
            }
        }

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        currentPlayingLevel.draw(this);
        componentGUI();
        healthBarGUI();
        windGUI();
        playerArrow();

        if(isGameOver){
            finalScoreboard();
        } else {
            inGameScoreboard();
        }

        int remainingTank=0;
        for (Tank t : currentPlayingLevel.getPlayerTanks().values()){
            if(t.isActive()){
                remainingTank++;
            } else if (currentTank.equals(t) && remainingTank>0){
                changePlayerTurn();
            }
        }

        if(remainingTank<=1 && currentLevel<levels.size()-1){
            isLevelOver = true;
            levelDelayTimer +=1;
        } else if (remainingTank<=1 && currentLevel==levels.size()-1){
            isLevelOver = true;
            isGameOver = true;
        }

        if(levelDelayTimer >60){
            changeLevel();
        }

    }

    private void componentGUI () {
        image(fuelCanImg,130,15);
        image(parachuteImg,130,40);

        currentTank = currentPlayingLevel.getPlayerTanks().get(currentPlayer);
        if(currentTank.player.isBigProjectile()){
            image(bigboiImg,200,15);
        }

        fill(0);
        textSize(15);

        text("Player " + currentPlayer + "'s turn", 15, 32);
        text(currentTank.getFuel(),150,32);
        text(players.get(currentPlayer).getParachute(),150,57);
        text("Health:",352,32);
        text(currentTank.getHealth(),565,32);
        text("Power:",352,55);
        text((int)currentTank.getPower(),408,55);

    }

    private void windGUI(){
        fill(0);
        if(currentPlayingLevel.getWind()<=0){
            image(windLeftImg,WIDTH-120,0);
        } else {
            image(windRightImg,WIDTH-120,0);
        }
        text(Math.abs(currentPlayingLevel.getWind()),WIDTH-50,36);

    }

    private void healthBarGUI(){
        stroke(0,0,0);
        strokeWeight(4);
        fill(256,256,256);
        rect(410,16,150,20);

        int [] playerRGG = currentTank.player.rgbColors;
        stroke(0,0,0);
        strokeWeight(0);
        fill(playerRGG[0], playerRGG[1], playerRGG[2]);
        float healthDisplay =  (float) (currentTank.getHealth()/100.0 * 150);
        rect(410,16,healthDisplay,20);

        stroke(128,128,128);
        strokeWeight(4);
        float powerDisplay =  (float) (currentTank.getPower()/100.0 * 150);
        rect(410,16,powerDisplay,20);

        stroke(255,0,0);
        strokeWeight(1);
        line(410+powerDisplay,10,410+powerDisplay,42);

    }

    private void playerArrow(){
        stroke(0,0,0);
        strokeWeight(2);
        if(arrowTimer <FPS*2 && !isLevelOver && !isGameOver){
            line(currentTank.getX(),currentTank.getY()-50,currentTank.getX(),currentTank.getY()-100);
            line(currentTank.getX(),currentTank.getY()-50, (int)((currentTank.getX())-20*sin((float) Math.PI/6)),(int)((currentTank.getY()-50)-20*cos((float) Math.PI/6)));
            line(currentTank.getX(),currentTank.getY()-50, (int)((currentTank.getX())+20*sin((float) Math.PI/6)),(int)((currentTank.getY()-50)-20*cos((float) Math.PI/6)));
            arrowTimer +=1;
        }

    }

    private void inGameScoreboard(){
        stroke(0,0,0);
        strokeWeight(3);
        fill(0);
        line(WIDTH*0.82F,HEIGHT*0.10F,WIDTH*0.99F,HEIGHT*0.10F);
        text("Scores",WIDTH*0.82F + 4,HEIGHT*0.10F+16);
        line(WIDTH*0.82F,HEIGHT*0.13F,WIDTH*0.99F,HEIGHT*0.13F);
        float textX = WIDTH*0.82F+4;
        float textY = HEIGHT*0.13F+16;

        drawPlayerIterator = currentPlayingLevel.getPlayerTurn().iterator();

        while(drawPlayerIterator.hasNext()){
            fill(0);
            Player p = players.get(drawPlayerIterator.next());
            text(p.getScore(),textX+100,textY);
            text(p.getScore(),textX+100,textY);
            fill(p.rgbColors[0],p.rgbColors[1],p.rgbColors[2]);
            text("Player " + p.playerChar,textX,textY);
            textY +=20;
        }

        line(WIDTH*0.82F,textY-16,WIDTH*0.99F,textY-16);
        line(WIDTH*0.82F,HEIGHT*0.10F,WIDTH*0.82F,textY-16);
        line(WIDTH*0.99F,HEIGHT*0.10F,WIDTH*0.99F,textY-16);

    }

    private void finalScoreboard() {
        List<Map.Entry<Character, Player>> list = new ArrayList<>(players.entrySet());
        Comparator<Map.Entry<Character, Player>> comparator = (entry1,entry2) -> entry2.getValue().getScore() - entry1.getValue().getScore();

        list.sort(comparator);
        LinkedHashMap<Character, Player> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Character, Player> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        Character firstPlayer = sortedHashMap.keySet().iterator().next();
        Player pWon = sortedHashMap.get(firstPlayer);

        textSize(20);
        strokeWeight(2);
        fill(pWon.rgbColors[0],pWon.rgbColors[1],pWon.rgbColors[2]);
        text("Player " + firstPlayer + " wins!",350,125);

        int tableHeight = 25*(players.size()+1);
        int tableWidth = 300;
        stroke(0);
        strokeWeight(2);
        fill(pWon.rgbColors[0],pWon.rgbColors[1],pWon.rgbColors[2],50);
        rect(275,150,tableWidth,tableHeight);

        fill(0);
        text("Final Scores",285,172);
        line(275,178,275+tableWidth,178);

        int drawStartX = 285;
        int drawStartY = 200;

        Iterator<Player> iterator = sortedHashMap.values().iterator();
        ArrayList<Player> drawPlayers = new ArrayList<>(sortedHashMap.values());

        Player drawThisPlayerNext = iterator.next();
        drawPlayers.add(drawThisPlayerNext);

        for(int i = 0; i<= playerIndex; i++) {
            Player p = drawPlayers.get(i);
            fill(p.rgbColors[0],p.rgbColors[1],p.rgbColors[2]);
            text("Player " + p.playerChar,drawStartX,drawStartY);
            fill(0);
            text(p.getScore(),drawStartX+235,drawStartY);
            drawStartY +=20;
        }

        finalScoreBoardDelayTimer++;
        if(finalScoreBoardDelayTimer >FPS*0.8){
            finalScoreBoardDelayTimer =0;
            if(playerIndex <players.size()-1){
                playerIndex = playerIndex +1;
            }
        }

    }

    private void changePlayerTurn() {
        arrowTimer = 0;
        int index = 0;
        while(true){

            if(!playerListiterator.hasNext()){
                playerListiterator = playerList.listIterator();
            }

            if(currentPlayingLevel.getPlayerTanks().get(playerListiterator.next()).isActive()){
                currentPlayer = playerListiterator.previous();
                break;
            }

            if(index ++> players.size()*3){
                break;
            }
        }

        if(playerListiterator.hasNext()){
            currentPlayer = playerListiterator.next();
            currentPlayingLevel.setWind(currentPlayingLevel.getWind());
            currentTank = currentPlayingLevel.getPlayerTanks().get(currentPlayer);
        }

    }

    private void changeLevel() {
        currentLevel +=1;
        currentPlayingLevel = levels.get(currentLevel);
        playerListiterator = playerList.listIterator();
        currentPlayer = playerListiterator.next();
        arrowTimer =0;
        finalScoreBoardDelayTimer =0;
        levelDelayTimer =0;
        isLevelOver = false;

    }

    private void restartGame() {
        for (Level l : levels){
            l.restartLevel();
        }

        for(Player p : players.values()){
            p.resetPlayer();
        }

        currentLevel=0;
        arrowTimer =0;
        finalScoreBoardDelayTimer =0;
        levelDelayTimer =0;
        playerIndex = 0;
        currentPlayingLevel = levels.get(currentLevel);
        playerListiterator = playerList.listIterator();
        currentPlayer = playerListiterator.next();
        isGameOver = false;
        isLevelOver = false;

    }


    /**
     * Retrieves the RGB values from a comma delimited string into a list of integer.
     * @param input
     *      Comma delimited string to extract the RGB values.
     * @return
     *      List of the RGB values.
     */
    public static int[] setRBGValues(String input){
        int[] rgbValues = new int[3];
        if(input.equals("random")){
            rgbValues[0] = rand.nextInt(256);
            rgbValues[1] = rand.nextInt(256);
            rgbValues[2] = rand.nextInt(256);
        } else {
            String[] rgbStringValues = input.split(",");
            rgbValues[0] = Integer.parseInt(rgbStringValues[0]);
            rgbValues[1] = Integer.parseInt(rgbStringValues[1]);
            rgbValues[2] = Integer.parseInt(rgbStringValues[2]);
        }
        return rgbValues;

    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
