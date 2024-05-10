package Tanks;

/**
 * Represents an abstract LevelObject that is inherited by Tree, Tank and Projectile.
 */
public abstract class LevelObject{
    /**
     * The object's x-coordinate
     */
    protected int x;

    /**
     * The object's y-coordinate
     */
    protected int y;

    /**
     * The object's is active (alive).
     */
    protected boolean active = true;

    /**
     * The object's Level it belongs to.
     */
    protected Level level;

    /**
     * The object's owner: the player.
     * It can be null and have no owner as well.
     */
    protected Player player;


    /**
     * Creates a new level object with no sprite.
     *
     * @param level The level
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public LevelObject(Level level,int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate.
     * @return The x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate.
     * @return The y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns the activity.
     * @return active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Changes the active to true.
     */
    public void setActive() {
        active = true;
    }

    /**
     * Changes the active to true.
     */
    public void setInActive() {
        active = false;
    }

    /**
     * Set the object's owner.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Set the object's owner.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Draws the level object on the screen.
     *
     * @param app The window to draw onto
     */
    public abstract void draw(App app);

}
