package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

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

    public static final int INITIAL_PARACHUTES = 3;

    public static final int FPS = 30;

    public String configPath;

    public Integer currentLevel = 0;
    public Level currentPlayingLevel=null;
    public Iterator<Character> currentPlayerIterator = null;
    public Iterator<Character> drawPlayerIterator = null;
    public ListIterator<Character> newiterator = null;
    public List<Character> playerrrr =new ArrayList<>();;

    public Character currentPlayer = null;
    public Tank currentTank = null;
    String currentPlayerText = " ";
    public static Map<Character,Player> players = new HashMap<>();

    public static PImage fuelCanImg = null;
    public static PImage parachuteImg = null;
    public static PImage bigboiImg = null;
    public static PImage windRightImg = null;
    public static PImage windLeftImg = null;

    public Integer timer=0;

    private static Random rand = new Random();



    /**
     * Level Attributes
     **/
    public ArrayList<Level> levels = new ArrayList<>();
    public static JSONObject playerColoursConfig;


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

            PImage parachuteIMG = this.loadImage(Objects.requireNonNull(getClass().getResource("parachute.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            parachuteIMG.resize(20,20);
            l.setParachuteSprite(parachuteIMG);

            levels.add(l);
        }

        fuelCanImg = this.loadImage(Objects.requireNonNull(getClass().getResource("fuel.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        parachuteImg = this.loadImage(Objects.requireNonNull(getClass().getResource("parachute.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        bigboiImg = this.loadImage(Objects.requireNonNull(getClass().getResource("bigboi.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        windRightImg = this.loadImage(Objects.requireNonNull(getClass().getResource("wind.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        windLeftImg = this.loadImage(Objects.requireNonNull(getClass().getResource("wind-1.png")).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));


        fuelCanImg.resize(20,20);
        parachuteImg.resize(20,20);
        bigboiImg.resize(20,20);


        currentPlayingLevel = levels.get(currentLevel);
        currentPlayerIterator = currentPlayingLevel.getPlayerTurn().iterator();
//        List<Character> playerrrr = new ArrayList<>();

        while(currentPlayerIterator.hasNext()){
            playerrrr.add(currentPlayerIterator.next());
        }

        newiterator = playerrrr.listIterator();

        currentPlayer = newiterator.next();



//        currentPlayer = currentPlayerIterator.next();
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
            if (currentTank.isActive()){
                currentPlayingLevel.setProjectiles(new Projectile(currentPlayingLevel,currentTank,currentPlayingLevel.getWind()));
                currentTank.player.setBigProjectile(false);
            }

            timer = 0;

            boolean test = true;
            while(test){

                if(!newiterator.hasNext()){
                    newiterator = playerrrr.listIterator();
                }

                if(currentPlayingLevel.getPlayerTanks().get(newiterator.next()).isActive()){
                    currentPlayer = newiterator.previous();
                    test = false;
                }
            }



            currentPlayer = newiterator.next();

            currentPlayingLevel.setWind(currentPlayingLevel.getWind());
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

        // P
        if(this.keyCode==80){
            if(currentTank.player.getScore()>15){
                currentTank.player.setScore(-15);
                currentTank.player.setParachute(currentTank.player.getParachute()+1);
            }
        }

        // X
        if(this.keyCode==88){
            if(currentTank.player.getScore()>20 && !currentTank.player.isBigProjectile()){
                currentTank.player.setScore(-20);
                currentTank.player.setBigProjectile(true);
            }
        }

        // R
        if(this.keyCode==82){
            if(currentTank.player.getScore()>20){
                currentTank.player.setScore(-20);
                currentTank.setHealth(20);
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
        image(fuelCanImg,130,15);
        image(parachuteImg,130,40);

        currentTank = currentPlayingLevel.getPlayerTanks().get(currentPlayer);
        if(currentTank.player.isBigProjectile()){
            image(bigboiImg,200,15);
        }

        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO

        fill(0);
        textSize(15);

        if(currentPlayer!=null){currentPlayerText = "Player " + currentPlayer + "'s turn";}
        text(currentPlayerText, 15, 32);
        text(currentTank.fuel,150,32);
        text(players.get(currentPlayer).getParachute(),150,57);
        text("Health:",352,32);
        text(currentTank.getHealth(),565,32);
        text("Power:",352,55);
        text((int)currentTank.getPower(),408,55);

        if(currentPlayingLevel.getWind()<=0){
            image(windLeftImg,WIDTH-120,0);
        } else {
            image(windRightImg,WIDTH-120,0);
        }

        text(Math.abs(currentPlayingLevel.getWind()),WIDTH-50,36);

        this.stroke(0,0,0);
        this.strokeWeight(4);
        this.fill(256,256,256);
        this.rect(410,16,150,20);

//        Integer[] playerRGG = currentPlayingLevel.getRBGValues(playerColoursConfig.getString(currentPlayer.toString()));
        int [] playerRGG = currentTank.player.rgbColors;
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

        this.stroke(0,0,0);
        this.strokeWeight(3);
        fill(0);
        this.line(WIDTH*0.82F,HEIGHT*0.10F,WIDTH*0.99F,HEIGHT*0.10F);
        text("Scores",WIDTH*0.82F + 4,HEIGHT*0.10F+16);
        this.line(WIDTH*0.82F,HEIGHT*0.13F,WIDTH*0.99F,HEIGHT*0.13F);
        float textX = WIDTH*0.82F+4;
        float textY = HEIGHT*0.13F+16;

        drawPlayerIterator = currentPlayingLevel.getPlayerTurn().iterator();
        while(drawPlayerIterator.hasNext()){
            fill(0);
            Player p = players.get(drawPlayerIterator.next());
            text(p.getScore(),textX+100,textY);
            fill(p.rgbColors[0],p.rgbColors[1],p.rgbColors[2]);
            text("Player " + p.playerName,textX,textY);
            textY +=20;
        }

        this.line(WIDTH*0.82F,textY-16,WIDTH*0.99F,textY-16);

        this.line(WIDTH*0.82F,HEIGHT*0.10F,WIDTH*0.82F,textY-16);
        this.line(WIDTH*0.99F,HEIGHT*0.10F,WIDTH*0.99F,textY-16);

        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }

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
