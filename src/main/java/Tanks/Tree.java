package Tanks;

/**
 * Represents a Tree Objects generated in each level.
 * Modelling the fall mechanism of the tree with the terrain also.
 */
public class Tree extends LevelObject{

    /**
     * Creates a new tree belonging a certain level inheriting from the LevelObject.
     * @param level The level the tree belongs to.
     * @param x The x-coordinate of the tree position.
     * @param y The x-coordinate of the tree position.
     */
    public Tree (Level level, Integer x, Integer y){
        super(level,x,y);
    }

    /**
     * Draws the tree object on the screen at the defined position.
     * Tree is made up of a sprite which is of size 32 x 32.
     * The center of the tree is offsetted from the top right corner of the image to the middle-bottom position where the trunk begins in the sprite.
     * If the height of the terrain is different from the y-coordinate of the tree, the position is changed to the height of the terrain at position x.
     *
     * @param app The window to draw onto
     */
    public void draw(App app){
        app.image(level.getTreesSprite(),x-16,y-32);
        Integer [] height = level.getHeight();
        if(height[x]>y) {
            this.y = height[x];
        }
    }

}
