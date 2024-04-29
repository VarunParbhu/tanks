package Tanks;


import java.util.Random;

public class Tank {
    private static Random rand = new Random();
    private Level level;
    private Integer x;
    private Integer y;
    private double angle = Math.PI/2 + rand.nextDouble()*Math.PI;
    public Integer health;
    public Integer fuel;
    public Integer power;
    public Integer parachute;
    public Integer score;
    private boolean turn = false;


    public Tank (Level level, Integer x, Integer y){
        this.level=level;
        this.x=x;
        this.y=y;
        this.health=100;
        this.fuel=250;
        this.parachute=3;
        this.power = 50;
        this.score=0;
    }

    public Integer getParachute(){return parachute;}

    public void useParachute(){
        if(this.parachute-1<=0){
            this.parachute=0;
        } else {
            this.parachute -= 1;
        }
    }


    public Integer getFuel(){return fuel;}

    public void useFuel(){
        if(this.fuel-1<=0){
            this.fuel=0;
        } else {
            this.fuel -= 1;
        }
    }

    public Integer getX(){return x;}
    public Integer getY(){return y;}
    public void setX(Integer x){
        if(x<0){
            this.x=0;
        } else if (x> App.WIDTH){
            this.x = App.WIDTH;
        } else {
            this.x=x;
        }
        this.y = level.getHeight()[this.x];

    }
    public void setY(Integer y){
        if(y<0){
            this.y=0;
        } else {
            this.y=y;
        }
    }

    public double getAngle(){return angle;}

    public void setAngle(double angle){
        if(angle<Math.PI/2){
            this.angle=Math.PI - Math.PI/2;
        } else if (angle>3*Math.PI/2){
            this.angle=Math.PI + Math.PI/2;
        } else {
            this.angle = angle;
        }
    }

    public void endTurn(){
        turn = false;
    }

    public void startTurn(){
        turn = true;
    }


}
