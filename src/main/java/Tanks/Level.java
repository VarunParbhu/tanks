package Tanks;

import processing.core.PImage;
import processing.data.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Math.sin;
import static java.lang.Math.cos;


public class Level {
    private String layoutInput;
    private PImage background;
    private Map<Character,Tank> playerTanks = new HashMap<>();
    private Integer[] foregroudRGBValues;
    private Integer R;
    private Integer G;
    private Integer B;
    private PImage treesSprite;
    private Character[][] layout = new Character[20][28];
    private Character[][] screenLayout = new Character[640][896];
    private Integer [] height = new Integer[896];
    private ArrayList<Tree> trees = new ArrayList<>();
    private static Random rand = new Random();
    private JSONObject playerColoursConfig;

    private Integer wind = rand.nextInt(71)-35;

    public void setLayout(String layoutInput) {
        this.layoutInput = layoutInput;
        createLevel();
    }

    public Integer getWind(){ return wind;}

    public void setWind(Integer wind) { this.wind = wind + rand.nextInt(11) - 5; }

    public void setBackground(PImage background) {
        this.background = background;
    }

    public void setForegroundColour(String foregroundColour) {this.foregroudRGBValues = getRBGValues(foregroundColour);}

    public void setTreeSprite(PImage trees) {
        this.treesSprite = trees;
    }

    public void setPlayerColoursConfig(JSONObject config){this.playerColoursConfig = config;}

    public PImage getTreesSprite() {return treesSprite;}

    public Integer[] getHeight(){ return height; }

    public void setHeight(Integer [] inputHeight) { height=inputHeight; }

    public ArrayList<Tree> getTrees(){return trees;}

    public void setTrees(ArrayList<Tree> inputTrees) {this.trees = inputTrees;}

    public TreeSet<Character> getPlayerTurn (){return new TreeSet<>(this.playerTanks.keySet());}

    public Map<Character,Tank> getPlayerTanks(){return playerTanks;}

    public void setPlayerTanks(Map<Character,Tank> playerTanks) {this.playerTanks = playerTanks;}

    public void createLevel() {
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

//        System.out.printf("%d,%d",screenLayout.length,screenLayout[0].length);


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


//        int count = 0;
//        for(int row = 0; row < screenLayout.length; row++){
//            for(int col = 0; col < screenLayout[0].length; col++){
//                if(Objects.equals(screenLayout[row][col], 'X')){
//                    count++;
//                }
//            }
//        }
//
//        System.out.println(count);

        // getting the height of the terrain at each column
        for(int col=0; col<screenLayout[0].length; col++){
            for (int row=0; row < screenLayout.length; row++){
                if(screenLayout[row][col] !=null){
                    height[col] = row;
                }
            }
        }

        height = calculateMovingAverage(calculateMovingAverage(height,App.CELLAVG),App.CELLAVG);

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

                    treeRow = height[treeCol] - 30;

                    trees.add(new Tree(this, treeCol - 16, treeRow));
                }
            }
        }



        for(int col=0;col<layout[0].length; col++) {
            for (int row = 0; row < layout.length; row++) {
                if (layout[row][col] != null && layout[row][col] != 'X' && layout[row][col] != 'T' && layout[row][col] >='A' && layout[row][col] <='Z') {
                    playerTanks.put(layout[row][col],new Tank(this, col*32 ,height[col*32]));
                }
            }
        }


    }



    public static Integer[] calculateMovingAverage(Integer[] data, int windowSize) {
        Integer[] movingAverages = new Integer[data.length];

        for (int i = 0; i < data.length; i++) {
            if (i < data.length - windowSize + 1) {
                int sum = 0;
                for (int j = i; j < i + windowSize; j++) {
                    sum += data[j];
                }
                movingAverages[i] = sum/32;
            } else {
                movingAverages[i] = data[i];
            }
        }
        return movingAverages;
    }

    public static Integer[] getRBGValues(String input){
        Integer[] rgbValues = new Integer[3];
        if(input.equals("random")){
            rgbValues[0] = rand.nextInt(256);
            rgbValues[1] = rand.nextInt(256);
            rgbValues[2] = rand.nextInt(256);
        } else {
            String[] rgbStringValues = input.split(",");
            rgbValues[0] = Integer.valueOf(rgbStringValues[0]);
            rgbValues[1] = Integer.valueOf(rgbStringValues[1]);
            rgbValues[2] = Integer.valueOf(rgbStringValues[2]);
        }
        return rgbValues;
    }




    public void draw(App app){
        app.background(this.background);
        for (int row = 0; row < screenLayout.length; row++) {
            for (int col = 0; col < screenLayout[0].length; col++) {
                if(Objects.equals(screenLayout[row][col], 'X')){
                    app.fill(foregroudRGBValues[0], foregroudRGBValues[1], foregroudRGBValues[2]);
                    app.stroke(foregroudRGBValues[0], foregroudRGBValues[1], foregroudRGBValues[2]);
                    app.rect(col,row,1,640-row);
                }
            }
        }



        // Loop through players to draw their tank
        for (Character c : playerTanks.keySet()) {
            Integer[] playerRGG = getRBGValues(playerColoursConfig.getString(c.toString()));

            app.fill(0,0,0);
            app.stroke(0,0,0);
//            app.rect(playerTanks.get(c).getX()-1,playerTanks.get(c).getY()-16, 2,10 ,1);
            app.strokeWeight(3);
            app.line(
                    playerTanks.get(c).getX(),
                    playerTanks.get(c).getY()-6,
                    playerTanks.get(c).getX() + (int)(10*sin(playerTanks.get(c).getAngle())),
                    playerTanks.get(c).getY()-6+ (int)(10*cos(playerTanks.get(c).getAngle()))
            );

            app.strokeWeight(1);
            app.fill(playerRGG[0],playerRGG[1],playerRGG[2]);
            app.stroke(playerRGG[0],playerRGG[1],playerRGG[2]);
            app.rect(playerTanks.get(c).getX() - 6 ,playerTanks.get(c).getY()-6, 12,6 ,4);
            app.ellipse(playerTanks.get(c).getX(), playerTanks.get(c).getY()-6, 6, 6);
        }


        // loop for the tree array and draw them
        for (Tree tree : trees) {
            app.image(treesSprite,tree.x,tree.y);
        }



    }
}
