package Tanks;

import processing.core.PImage;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Represents the level of a game generated from a text file and contains the LevelObjects.
 */
public class Level {

    private final Random rand = new Random();
    private Integer wind = rand.nextInt(71)-35;
    private PImage background;
    private PImage treesSprite;
    private String layoutInput;
    private int [] height = new int[896];
    private int [] foregroundRGBValues;
    private Character[][] screenLayout = new Character[640][896];
    private final Character[][] layout = new Character[20][28];
    private final List<LevelObject> levelObjects = new ArrayList<>();
    private final ArrayList<Tree> trees = new ArrayList<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final Map<Character,Tank> playerTanks = new HashMap<>();

    /**
     * Takes the file path of the level text file to be created.
     * @param layoutInput
     *      File path of the level text file.
     */
    public void setLayout(String layoutInput) {
        this.layoutInput = layoutInput;
        createLevel();
    }

    /**
     * Every time a projectile is created, a projectile object is added to the level list of projectile objects to be drawn.
     * @param projectile
     *      Projectile object to be drawn in the level
     */
    public void setProjectiles(Projectile projectile) {
        projectiles.add(projectile);
    }

    /**
     * @return
     *      The list of projectile in the level.
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Changes the wind by a value between -5 and 5 after every player turn by a player.
     * @param wind
     *      Old win wind value on the level
     */
    public void setWind(Integer wind) {
        this.wind = wind + rand.nextInt(11) - 5;
    }

    /**
     * @return
     *      Current wind value of the level.
     */
    public int getWind(){ return wind;}

    /**
     * Sets up the background image of the level to be used when creating the level.
     * @param background
     *      Image for the background.
     */
    public void setBackground(PImage background) {
        this.background = background;
    }

    /**
     * Sets up the foreground colour of the level to be used when creating the level.
     * @param foregroundColour
     *      Foreground colour of the level.
     */
    public void setForegroundColour(String foregroundColour) {
        foregroundRGBValues = App.setRBGValues(foregroundColour);
    }

    /**
     * Sets up the tree image of the level to be used when creating the level and drawing the trees.
     * @param trees
     *      Tree image of the level.
     */
    public void setTreeSprite(PImage trees) {
        treesSprite = trees;
    }

    /**
     * @return
     *      Tree image used to draw the trees.
     */
    public PImage getTreesSprite() {
        return treesSprite;
    }

    /**
     * @return
     *      The height of the terrain of the level at each x-coordinates.
     */
    public int[] getHeight(){
        return height;
    }

    /**
     * Changes the terrain heights
     * @param inputHeight
     *      List of integers representing the height of the terrain at each x-coordinates.
     */
    public void setHeight(int [] inputHeight) {
        height=inputHeight;
    }

    /**
     * @return
     *      A natural ordering of the Character and Tank HashMap, used to create the playerTurn iterator.
     */
    public TreeSet<Character> getPlayerTurn (){
        return new TreeSet<>(playerTanks.keySet());
    }

    /**
     * @return
     *      Tank and their associated Character (player).
     */
    public Map<Character,Tank> getPlayerTanks(){
        return playerTanks;
    }


    private void createLevel() {
        // Create a multidimensional array; then sub the values with characters in the file
        // loop through the file and check character at each col
        // record the row and character position

        String filePath = layoutInput;
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            int row = 0;
            while (scanner.hasNextLine() && (row < layout.length)) {

                String line = scanner.nextLine();

                for(int col = 0; col<Math.min(line.length(), layout[0].length); col++){
                    layout[row][col] = line.charAt(col);
                }
                row++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        }


        // drawing the layout from the text file
        for(int col = 0; col < screenLayout[0].length; col++){
            boolean foundLayout = false;

            for(int row = 0; row < screenLayout.length; row++){
                if(Objects.equals(layout[row/32][col/32], 'X') && !foundLayout){
                    screenLayout[row][col] = layout[row/32][col/32];
                    foundLayout = true;
                }
            }
        }


        // getting the height of the terrain at each column
        for(int col=0; col<screenLayout[0].length; col++){
            for (int row=0; row < screenLayout.length; row++){
                if(screenLayout[row][col] !=null){
                    height[col] = row;
                }
            }
        }

        height = calculateMovingAverage(calculateMovingAverage(height));

        for(int col=0; col<screenLayout[0].length; col++){
            for(int row=0; row<screenLayout.length; row++){
                if(Objects.equals(screenLayout[row][col], 'X')) {
                    screenLayout[row][col] = null;
                    screenLayout[height[col]][col] = 'X';
                }
            }
        }


        // Getting the initial tree positions and randomizing them

        for(int col = 0; col < layout[0].length; col++) {
            int treeRow;
            int treeCol;

            for (int row = 0; row < layout.length; row++) {
                if (Objects.equals(layout[row][col], 'T')) {
                    if (col == 0) {
                        treeCol = rand.nextInt(16);
                    } else if (col == 27) {
                        treeCol = 27 * 32 - rand.nextInt(16);
                    } else {
                        treeCol = (col + 1) * 32 - 16 + rand.nextInt(31) - 15;
                    }

                    treeRow = height[treeCol];
                    trees.add(new Tree(this, treeCol , treeRow));
                }
            }
        }

        for(int col=0;col<layout[0].length; col++) {
            for (int row = 0; row < layout.length; row++) {
                if (layout[row][col] != null && layout[row][col] != 'X' && layout[row][col] != 'T' && layout[row][col] >='A' && layout[row][col] <='Z') {
                    if(App.players.containsKey(layout[row][col])) {
                        Player p = App.players.get(layout[row][col]);
                        playerTanks.put(layout[row][col],new Tank(this, col*32 ,height[col*32], p));
                    } else {
                        Player p = new Player(layout[row][col]);
                        App.players.put(layout[row][col],p);
                        playerTanks.put(layout[row][col],new Tank(this, col*32 ,height[col*32], p));
                    }
                }
            }
        }


    }

    /**
     * Restarts a level by resetting the wind,projectiles,tanks,trees and terrain.
     */
    public void restartLevel(){
        wind = rand.nextInt(71)-35;
        projectiles.clear();
        playerTanks.clear();
        trees.clear();
        height = new int[896];
        screenLayout = new Character[640][896];
        createLevel();
    }

    private static int[] calculateMovingAverage(int[] data) {
        int[] movingAverages = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            if (i < data.length - App.SMOOTHING_AVG + 1) {
                int sum = 0;
                for (int j = i; j < i + App.SMOOTHING_AVG; j++) {
                    sum += data[j];
                }
                movingAverages[i] = sum/32;
            } else {
                movingAverages[i] = data[i];
            }
        }
        return movingAverages;
    }

    /**
     * Draw all elements in the level by current frame.
     * @param app
     *      The window to draw onto.
     */
    public void draw(App app){
        app.background(background);
        for (int col = 0; col < screenLayout[0].length; col++) {
                app.strokeWeight(1);
                app.fill(foregroundRGBValues[0], foregroundRGBValues[1], foregroundRGBValues[2]);
                app.stroke(foregroundRGBValues[0], foregroundRGBValues[1], foregroundRGBValues[2]);
                app.rect(col,height[col],1,640-height[col]);
        }

        levelObjects.clear();
        levelObjects.addAll(trees);
        levelObjects.addAll(new ArrayList<>(playerTanks.values()) );
        levelObjects.addAll(projectiles);

        for (LevelObject o : levelObjects) {
            if (o.isActive()){
                o.draw(app);
            }
        }
    }

}