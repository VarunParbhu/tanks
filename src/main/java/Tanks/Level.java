package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Level {
    private String layoutInput;
    private PImage background;
    private String foregroundColour;
    private PImage trees;
    private Character[][] layout = new Character[20][28];
    private Character[][] screenLayout = new Character[640][896];
    private Integer [] height = new Integer[896];


    public void setLayout(String layoutInput) {
        this.layoutInput = layoutInput;
        createLevel();
    }

    public void setBackground(PImage background) {
        this.background = background;
    }

    public void setForegroundColour(String foregroundColour) {
        this.foregroundColour = foregroundColour;
    }

    public void setTrees(PImage trees) {
        this.trees = trees;
    }

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

        System.out.printf("%d,%d",screenLayout.length,screenLayout[0].length);

        for(int col = 0; col < screenLayout[0].length; col++){
            boolean found = false;
            for(int row = 0; row < screenLayout.length; row++){
                if(Objects.equals(layout[row/32][col/32], 'X') && !found){
                    screenLayout[row][col] = layout[row/32][col/32];
                    found = true;
                }
            }
        }

        int count = 0;
        for(int row = 0; row < screenLayout.length; row++){
            for(int col = 0; col < screenLayout[0].length; col++){
                if(Objects.equals(screenLayout[row][col], 'X')){
                    count++;
                }
            }
        }

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

    public void draw(PApplet app){
        app.background(this.background);
        for (int row = 0; row < screenLayout.length; row++) {
            for (int col = 0; col < screenLayout[0].length; col++) {
                if(Objects.equals(screenLayout[row][col], 'X')){
                    app.fill(255, 255, 255);
                    app.stroke(255, 255, 255);
                    app.rect(col,row,1,640-row);
                }
            }
        }
    }
}
