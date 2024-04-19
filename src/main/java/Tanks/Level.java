package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Level {
    private String layout;
    private PImage background;
    private String foregroundColour;
    private PImage trees;

    public void setLayout(String layout) {
        this.layout = layout;
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
        // Create a multidimentional array; then sub the values with characters in the file
        // loop through the file and check character at each col
        // record the row and character position


        String filePath = layout;
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        }
    }

    public void draw(PApplet app){
        app.background(this.background);
    }
}
