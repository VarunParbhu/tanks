package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.util.*;

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

    public static Random random = new Random();

    /**
     * Level Attributes
     **/
    public ArrayList<Level> levels = new ArrayList<>();

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
        for (int i = 0; i < 1; i++){
//        for (int i = 0; i < levelsConfig.size(); i++){
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
                if (treeIMG != null){l.setTrees(treeIMG);}
            }
            levels.add(l);
        }

        
        //See PApplet javadoc:
        //loadJSONObject(configPath);
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
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
        levels.get(0).draw(this);

        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO

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
