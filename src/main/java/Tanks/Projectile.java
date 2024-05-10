package Tanks;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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

    public int getRadius(){
        return radius;
    }

    public int getExplodingRadius(){
        return explodingRadius;
    }

    public boolean getExploded(){
        return exploded;
    }

    public boolean getCollided(){
        return collided;
    }

    public void move(){
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

    //get height of level and set level here

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

    //change height of level

    public void levelTerrain() {
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
