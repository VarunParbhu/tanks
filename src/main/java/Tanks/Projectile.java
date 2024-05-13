package Tanks;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Represents the Projectiles of a level generated from Tanks.
 */
public class Projectile extends LevelObject implements Explosion{

    public Tank tank;
    private final Level level;
    private final int wind;
    private boolean exploded = false;
    private boolean collided = false;
    private float xPos;
    private float yPos;
    private float dx;
    private float dy;
    private int [] height;
    private int explodingRadius = 30;
    private int radius =0;

    /**
     * Creates a projectile by associating the Level it belongs to, the Tank that fired the projectile and the Wind when shot.
     * The projectile size is first check by verifying whether the player bought a big projectile during their turn.
     * The initial positioning and velocity of the projectile is determined by considering the power of the tank and its turret positioning.
     *
     * @param l
     *      Level Object that the projectile belongs to.
     * @param t
     *      Tank Object that created the projectile.
     * @param wind
     *      Wind value at the moment the projectile was fired.
     */
    public Projectile(Level l,Tank t,int wind){
        super(l,t.getX(),t.getY()-6);
        level = l;
        tank = t;

        if(t.player.isBigProjectile()){
            this.explodingRadius *=2;
        }

        double power = 1 + (t.getPower()/100.0) * 8;
        double angle = t.getAngle();
        this.wind = wind;
        xPos = (float)t.getX() + (float)(10*sin(t.getAngle()));
        yPos = (float)t.getY() + (float)(10*cos(t.getAngle())) - 6;

        dx = (float)((power *sin(angle)));
        dy = (float)((power *cos(angle)));

    }

    @Override
    public int getRadius(){
        return radius;
    }

    @Override
    public int getExplodingRadius(){
        return explodingRadius;
    }

    private boolean getExploded(){
        return exploded;
    }

    private boolean getCollided(){
        return collided;
    }

    private void move(){
        if(x<-5 || x>= App.WIDTH+5 || y>=App.HEIGHT+5){
            setInActive();
        } else {
            if (!collided) {
                xPos += dx;
                yPos += dy;
                dx += (float) (wind * 0.03) / App.FPS;
                dy += (float) 3.6 / App.FPS;
                x = (int) xPos;
                y = (int) yPos;
            }
            // check if collision happens
            height = level.getHeight();
            if (((int) Math.floor(xPos)) >= 0 && ((int) Math.floor(xPos)) < App.WIDTH)
                if (height[(int) Math.floor(xPos)] - yPos < 0) {
                    collided = true;
                }
        }
    }

    @Override
    public void explode(){
        if (!exploded) {
            radius += 5 *(explodingRadius/30);
        }
        if (radius >explodingRadius){

            for (Tank t : level.getPlayerTanks().values()){
                double distanceFromTank = Math.sqrt(Math.pow(t.getX() - xPos, 2) + Math.pow(t.getY() - yPos, 2));

                if (distanceFromTank <= explodingRadius) {
                    int damageCause = (int) (((explodingRadius - distanceFromTank) / explodingRadius) * 60);
                    if(!t.equals(tank)) {
                        t.setHealth(-1*damageCause);
                        tank.player.setScore(damageCause);
                    } else {
                        t.setHealth(-1*damageCause);
                    }
                }
            }
            exploded = true;
        }
    }

    private void levelTerrain() {
        height = level.getHeight();
        int xcoordinate = (int)Math.floor(xPos);
        int ycoordinate = (int)Math.floor(yPos);

        for(int i=xcoordinate-explodingRadius;i<xcoordinate+explodingRadius;i++){
            if(i>0 && i<height.length){
                double heightCircle = Math.sqrt(Math.pow(explodingRadius, 2.0) - Math.pow(xcoordinate - i, 2.0));

                int ycoordinateA = ycoordinate -  (int) heightCircle;
                int ycoordinateB = ycoordinate +  (int) heightCircle;
                int currentHeight = height[i];

                if (currentHeight<=ycoordinateA){
                    height[i] = ycoordinateB - (ycoordinateA-currentHeight);
                } else if (currentHeight<=ycoordinateB){
                    height[i] = ycoordinateB;
                }
            }
        }
        level.setHeight(height);

    }

    /**
     * Draws the projectile object on the screen at the defined position and continuously move them.
     * Projectile is made up of a coloured circle (same as character) and a black dot.
     * Projectile activity is checked first before drawing.
     * Terrain is leveled as soon as explosion animation is done.
     * @param app
     *      The window to draw onto
     */
    @Override
    public void draw(App app){
        int [] playerRGG = tank.player.rgbColors;
        if(isActive()){
            if(!getExploded()){
                if(!getCollided()){

                    app.fill(playerRGG[0],playerRGG[1],playerRGG[2]);
                    app.stroke(playerRGG[0],playerRGG[1],playerRGG[2]);
                    app.ellipse(getX(),getY(),5,5);

                    app.fill(0,0,0);
                    app.stroke(0,0,0);
                    app.ellipse(getX(),getY(),1,1);

                    move();

                } else {
                    explodeLevelObject(app,this);

                }
            } else {
                levelTerrain();
                setInActive();
            }
        }
    }

}
