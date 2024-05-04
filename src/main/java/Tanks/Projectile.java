package Tanks;

import java.util.Map;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Projectile {
    private Level level;
    private Tank tank;
    private boolean exploded = false;
    private boolean collided = false;
    private final Integer wind;
    private float x;
    private float y;
    private float dx;
    private float dy;
    private Integer [] height;
    private Integer radius =0;
    private boolean active = true;

    public Projectile(Level l,Tank t,Integer wind){
        this.level = l;
        this.tank = t;
        double power = 1 + (t.getPower() / 100.0) * 8;
        double angle = t.getAngle();
        this.wind = wind;
        this.x = (float)t.getX() + (float)(10*sin(t.getAngle()));
        this.y = (float)t.getY() + (float)(10*cos(t.getAngle())) - 6;

        dx = (float)((power *sin(angle)));
        dy = (float)((power *cos(angle)));

    }

    public float getX(){return x;}
    public float getY(){return y;}
    public Integer getRadius(){return radius;}
    public boolean getExploded(){return exploded;}
    public boolean getCollided(){return collided;}
    public boolean getActive(){return active;}
    public void setInactive(){active = false;
    System.out.println("Inactive Projectile");}
    public String getTankChar(){return tank.getC();}

    public void move(){

        if (!collided) {
            this.x += dx;
            this.y += dy;
            dx += (float) (wind * 0.03) / App.FPS;
            dy += (float) 3.6 / App.FPS;
        }
        // check if collision happens
        height= level.getHeight();
        if( ((int)Math.floor(this.x))>=0 && ((int)Math.floor(this.x))<App.WIDTH)
            if(height[(int) Math.floor(this.x)]-this.y<0){
            collided = true;
        }

    }

    //get height of level and set level here

    public void explode(){
        if (!exploded) {
            radius += 5;
        }
        if (radius >30){
            exploded = true;
        }
    }

    //change height of level

    public void levelTerrain() {
        height= level.getHeight();
        int xcoordinate = (int)Math.floor(this.x);
        int ycoordinate = (int)Math.floor(this.y);

        int count = 0;
        for(int i=xcoordinate-30;i<xcoordinate+30;i++){
            if(i>0 && i<height.length){
                double heightCircle = Math.sqrt(Math.pow(30.0, 2.0) - Math.pow(xcoordinate - i, 2.0));

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

//    public void adjustScore(Map<Character, Tank> playerTanks) {
//
//    }

}
