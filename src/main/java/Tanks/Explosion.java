package Tanks;

/**
 * Represents an interface for LevelObjects that explodes (projectile and tanks).
 */
public interface Explosion {

    /**
     * The radius of explosion while it is happening.
     */
    int getRadius();

    /**
     * The final radius of the explosion.
     */
    int getExplodingRadius();

    /**
     * Increment of the radius while the explosion is happening and determines when the object has exploded.
     */
    void explode();

    /**
     * Default method that draws the explosion from start to finish as the radius changes.
     */
    default void explodeLevelObject (App app, LevelObject object){
        app.fill(255,0,0);
        app.stroke(255,0,0);
        app.ellipse(object.getX(),object.getY(), Math.min(getRadius(),getExplodingRadius()) ,Math.min(getRadius(),getExplodingRadius()));

        app.fill(255,165,0);
        app.stroke(255,165,0);
        app.ellipse(object.getX(),object.getY(), Math.min(getRadius(),getExplodingRadius()*0.5F) ,Math.min(getRadius(),getExplodingRadius()*0.5F));

        app.fill(255,255,0);
        app.stroke(255,255,0);
        app.ellipse(object.getX(),object.getY(), Math.min(getRadius(),getExplodingRadius()*0.2F) ,Math.min(getRadius(),getExplodingRadius()*0.2F));

        explode();
    }
}
