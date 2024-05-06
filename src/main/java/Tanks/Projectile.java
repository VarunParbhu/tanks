package Tanks;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Projectile extends LevelObject{
    private Level level;
    private Tank tank;
    private boolean exploded = false;
    private boolean collided = false;
    private final Integer wind;
    private float xPos;
    private float yPos;
    private float dx;
    private float dy;
    private Integer [] height;
    private Integer explodingRadius = 30;
    private Integer radius =0;
    private boolean active = true;
    private double distance;
    private int damageCause;

    public Projectile(Level l,Tank t,Integer wind){
        super(l,t.getX(),t.getY()-6);

        if(t.player.isBigProjectile()){
            this.explodingRadius *=2;
        }

        this.level = l;
        this.tank = t;
        double power = 1 + (t.getPower() / 100.0) * 8;
        double angle = t.getAngle();
        this.wind = wind;
        this.xPos = (float)t.getX() + (float)(10*sin(t.getAngle()));
        this.yPos = (float)t.getY() + (float)(10*cos(t.getAngle())) - 6;

        dx = (float)((power *sin(angle)));
        dy = (float)((power *cos(angle)));

    }


    public Integer getRadius(){return radius;}
    public boolean getExploded(){return exploded;}
    public boolean getCollided(){return collided;}
    public boolean getActive(){return active;}
    public void setInactive(){active = false;}
    public Character getTankChar(){return tank.getPlayer().getPlayerName();}

    public void move(){
        if (!collided) {
            this.xPos += dx;
            this.yPos += dy;
            dx += (float) (wind * 0.03) / App.FPS;
            dy += (float) 3.6 / App.FPS;
            x = (int) xPos;
            y = (int) yPos;
        }
        // check if collision happens
        height= level.getHeight();
        if( ((int)Math.floor(this.xPos))>=0 && ((int)Math.floor(this.xPos))<App.WIDTH)
            if(height[(int) Math.floor(this.xPos)]-this.yPos <0){
            collided = true;
        }
    }

    //get height of level and set level here

    public void explode(){
        if (!exploded) {
            radius += 5 *(explodingRadius/30);
        }
        if (radius >explodingRadius){

            for (Tank t : level.getPlayerTanks().values()){
                if(!t.equals(tank)) {
                    distance = Math.sqrt(Math.pow(t.getX() - this.xPos, 2) + Math.pow(t.getY() - this.yPos, 2));
                    if (distance <= explodingRadius) {
                        damageCause = (int) (((explodingRadius - distance) / explodingRadius) * 60);
                        tank.player.setScore(damageCause);
                        t.setHealth(-1*damageCause);
                    }
                }
            }
            exploded = true;
        }
    }

    //change height of level

    public void levelTerrain() {
        height= level.getHeight();
        int xcoordinate = (int)Math.floor(this.xPos);
        int ycoordinate = (int)Math.floor(this.yPos);

        int count = 0;
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
//        Integer[] playerRGG = level.getRBGValues(App.playerColoursConfig.getString(tank.getPlayer().getPlayerName().toString()));
        int [] playerRGG = tank.player.rgbColors;
        if(this.getActive()){
            if(!this.getExploded()){
                if(!this.getCollided()){

                    app.fill(playerRGG[0],playerRGG[1],playerRGG[2]);
                    app.stroke(playerRGG[0],playerRGG[1],playerRGG[2]);
                    app.ellipse(this.getX(),this.getY(),5,5);

                    app.fill(0,0,0);
                    app.stroke(0,0,0);
                    app.ellipse(this.getX(),this.getY(),1,1);

                    this.move();
                } else {
                    app.fill(255,0,0);
                    app.stroke(255,0,0);
                    app.ellipse(this.getX(),this.getY(), Math.min(this.getRadius(),explodingRadius) ,Math.min(this.getRadius(),explodingRadius));

                    app.fill(255,165,0);
                    app.stroke(255,165,0);
                    app.ellipse(this.getX(),this.getY(), Math.min(this.getRadius(),explodingRadius*0.5F) ,Math.min(this.getRadius(),explodingRadius*0.5F));

                    app.fill(255,255,0);
                    app.stroke(255,255,0);
                    app.ellipse(this.getX(),this.getY(), Math.min(this.getRadius(),explodingRadius*0.2F) ,Math.min(this.getRadius(),explodingRadius*0.2F));

                    this.explode();
                }
            } else {
                this.levelTerrain();
                this.setInactive();
            }
        }

    }


}
