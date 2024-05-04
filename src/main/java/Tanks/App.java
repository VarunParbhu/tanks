package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public Integer currentLevel = 0;
    public Level currentPlayingLevel=null;
    public Iterator<Character> currentPlayerIterator = null;
    public Character currentPlayer = null;
    public Tank currentTank = null;
    String currentPlayerText = " ";

    public PImage fuelCan = null;
    public PImage parachute = null;
    public PImage windRight = null;
    public PImage windLeft = null;

    public Integer timer=0;



    /**
     * Level Attributes
     **/
    public ArrayList<Level> levels = new ArrayList<>();
    public JSONObject playerColoursConfig;

	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    /**
     * Loads all the configuration required by the application
     * Constructor of the application, called only ONCE
     **/
    public App() {
        this.configPath = "config.json";
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
		//Load the Level First
        JSONArray levelsConfig = loadJSONObject(configPath).getJSONArray("levels");
        playerColoursConfig = loadJSONObject(configPath).getJSONObject("player_colours");

        for (int i = 0; i < levelsConfig.size(); i++){
            JSONObject level = levelsConfig.getJSONObject(i);
            Level l = new Level();

            if(level.hasKey("layout")){l.setLayout(level.getString("layout"));}
            if(level.hasKey("foreground-colour")){l.setForegroundColour(level.getString("foreground-colour"));}
            if(level.hasKey("background")){
                //PImage backgroundIMG = this.loadImage(getClass().getResource(level.getString("background")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                PImage backgroundIMG = this.loadImage(Objects.requireNonNull(getClass().getResource(level.getString("background"))).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                if (backgroundIMG != null){l.setBackground(backgroundIMG);}
            }
            if (level.hasKey("trees")){
                //PImage treeIMG = this.loadImage(getClass().getResource(level.getString("trees")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                PImage treeIMG = this.loadImage(Objects.requireNonNull(getClass().getResource(level.getString("trees"))).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                treeIMG.resize(32,32);
                l.setTreeSprite(treeIMG);
            } else {
                // Setting default tree to be tree1.png if not provided in the config JSON for the level
                PImage treeIMG = this.loadImage(Objects.requireNonNull(getClass().getResource("tree1.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
                treeIMG.resize(32,32);
                l.setTreeSprite(treeIMG);
            }

            l.setPlayerColoursConfig(playerColoursConfig);

            //System.out.println(Objects.requireNonNull(getClass().getResource(level.getString("trees"))).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            levels.add(l);

        }
        //See PApplet javadoc:
        //loadJSONObject(configPath);
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        currentPlayingLevel = levels.get(currentLevel);
        currentPlayerIterator = currentPlayingLevel.getPlayerTurn().iterator();
        currentPlayer = currentPlayerIterator.next();

        fuelCan = this.loadImage(Objects.requireNonNull(getClass().getResource("fuel.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        fuelCan.resize(20,20);
        parachute = this.loadImage(Objects.requireNonNull(getClass().getResource("parachute.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        parachute.resize(20,20);
        windRight = this.loadImage(Objects.requireNonNull(getClass().getResource("wind.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        windLeft = this.loadImage(Objects.requireNonNull(getClass().getResource("wind-1.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        // if you press Spacebar, the turn ends and changes to a new player
        // Spacebar = 32
        // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40

        //Spacebar
        if (this.keyCode==32){
            currentPlayingLevel.setProjectiles(new Projectile(currentPlayingLevel,currentTank,currentPlayingLevel.getWind()));
            timer = 0;

            if(!currentPlayerIterator.hasNext()){
                currentPlayerIterator = currentPlayingLevel.getPlayerTurn().iterator();
            }

            currentPlayingLevel.setWind(currentPlayingLevel.getWind());
            currentPlayer = currentPlayerIterator.next();
            currentTank = currentPlayingLevel.getPlayerTanks().get(currentPlayer);


        }

        // Left
        if(this.keyCode==37){
            currentTank.setX(currentTank.getX()-2);
        }
        // Right
        if(this.keyCode==39){
            currentTank.setX(currentTank.getX()+2);
        }

        // Up
        if(this.keyCode==38){
            currentTank.setAngle(currentTank.getAngle()- 3.0/ (App.FPS));
        }

        // Down
        if(this.keyCode==40){
            currentTank.setAngle(currentTank.getAngle()+ 3.0/ (App.FPS));
        }

        // W
        if(this.keyCode==87){
            currentTank.setPower(currentTank.getPower()+ 36.0/(App.FPS));
        }

        // S
        if(this.keyCode==83){
            currentTank.setPower(currentTank.getPower()- 36.0/(App.FPS));
        }

    }


    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        //Draw Level
        currentPlayingLevel.draw(this);
        image(fuelCan,130,15);
        image(parachute,130,40);


        currentTank = currentPlayingLevel.getPlayerTanks().get(currentPlayer);

        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO

        fill(0);
        textSize(15);

        if(currentPlayer!=null){currentPlayerText = "Player " + currentPlayer + "'s turn";}
        text(currentPlayerText, 15, 32);
        text(currentTank.fuel,150,32);
        text(currentTank.getParachute(),150,57);
        text("Health:",352,32);
        text(currentTank.getHealth(),565,32);
        text("Power:",352,55);
        text((int)currentTank.getPower(),408,55);

        if(currentPlayingLevel.getWind()<=0){
            image(windLeft,WIDTH-120,0);
        } else {
            image(windRight,WIDTH-120,0);
        }

        text(Math.abs(currentPlayingLevel.getWind()),WIDTH-50,36);

        this.stroke(0,0,0);
        this.strokeWeight(4);
        this.fill(256,256,256);
        this.rect(410,16,150,20);

        Integer[] playerRGG = currentPlayingLevel.getRBGValues(playerColoursConfig.getString(currentPlayer.toString()));
        this.stroke(0,0,0);
        this.strokeWeight(0);
        this.fill(playerRGG[0], playerRGG[1], playerRGG[2]);
        float healthDisplay =  (float) (currentTank.getHealth()/100.0 * 150);
        this.rect(410,16,healthDisplay,20);


        this.stroke(128,128,128);
        this.strokeWeight(4);
        float powerDisplay =  (float) (currentTank.getPower()/100.0 * 150);
        this.rect(410,16,powerDisplay,20);

        this.stroke(255,0,0);
        this.strokeWeight(1);

        this.line(410+powerDisplay,10,410+powerDisplay,42);


        // Drawing Arrow
        this.stroke(0,0,0);
        this.strokeWeight(2);
        if(timer<FPS*2){
            this.line(currentTank.getX(),currentTank.getY()-50,currentTank.getX(),currentTank.getY()-100);
            this.line(currentTank.getX(),currentTank.getY()-50, (int)((currentTank.getX())-20*sin((float) Math.PI/6)),(int)((currentTank.getY()-50)-20*cos((float) Math.PI/6)));
            this.line(currentTank.getX(),currentTank.getY()-50, (int)((currentTank.getX())+20*sin((float) Math.PI/6)),(int)((currentTank.getY()-50)-20*cos((float) Math.PI/6)));
            timer+=1;
        }


        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO




        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }


    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
