package Tanks;

public interface Explosion {

    int getRadius();
    int getExplodingRadius();
    void explode();

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
