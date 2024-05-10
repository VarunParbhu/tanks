package Tanks;

import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Tank extends LevelObject implements Explosion {
    public int health;
    public int fuel;
    private double power;
    private double angle = 3*Math.PI/4.0;

    private Projectile opponentP;
    private int explosionRadius = 30;
    private int radius = 0;
    private boolean exploded = false;
    private boolean isFalling = false;
    public int fallDamage = 0;

    public Tank (Level level, Integer x, Integer y,Player player){
        super(level,x,y);
        setPlayer(player);
        health=100;
        fuel=250;
        power = 50;
    }

    @Override
    public int getRadius(){return radius;}

    @Override
    public int getExplodingRadius(){return explosionRadius;}


    public boolean getExploded(){return exploded;}


    public void useParachute(){
        Player p = App.players.get(player.playerChar);
        p.setParachute(Math.max(p.getParachute() - 1, 0));
    }

    @Override
    public void explode(){
        if(health<=0){
            explosionRadius=15;
        }

        if (!exploded) {
            radius += 5;
        }

        if (radius > explosionRadius){
            exploded = true;
        }
    }

    public int getFuel(){
        return fuel;
    }

    public void setFuel(int fuel){
        if(this.fuel+fuel>250){
            this.fuel=250;
        } else {
            this.fuel+=fuel;
        }
    }


    public void useFuel(){
        if(fuel-1<=0){
            fuel=0;
        } else {
            fuel -= 1;
        }
    }

    public boolean isFalling(){return isFalling;}

    public void fall(){
        if(y < level.getHeight()[x]){
            isFalling=true;
            //get the last inactive projectile
            if(opponentP==null) {
                ArrayList<Projectile> projectileList = level.getProjectiles();
                int index = projectileList.size() - 1;

                while ((this.opponentP==null) && (index > 0)) {
                    Projectile p = projectileList.get(index);
                    if (Math.abs(p.getX() - getX()) <= p.getExplodingRadius() && !p.isActive()){
                        break;
                    }
                    index = index - 1;
                }
                opponentP =  projectileList.get(index);
                System.out.println(opponentP.tank.player.playerChar);
            }

            falling();
        } else {
            if(isFalling){
                isFalling=false;
                setHealth(-1*fallDamage);
                if (!opponentP.tank.equals(this)) {
                    opponentP.tank.player.setScore(fallDamage);
                }
                opponentP=null;
                fallDamage=0;
                useParachute();
            }

        }
    }

    public void setX(int x){
        if(!isFalling) {
            if (x < 0) {
                this.x = 0;
            } else if (x > App.WIDTH) {
                this.x = App.WIDTH;
            } else if (level.getHeight()[x] >= App.HEIGHT) {
                return;
            } else if (fuel > 0) {
                this.x = x;
                useFuel();

            }
            y = level.getHeight()[this.x];
        }
    }

    public void falling(){
        int [] height = level.getHeight();
        // Player p = App.players.get(player.playerChar);
        // set which projectile caused the fall
        if(y<0){
            y=0;
        } else if (y>=App.HEIGHT){
            y=App.HEIGHT;
        } else if (height[x]>y && player.getParachute()>0){
            y += 60/App.FPS;
        } else if (height[x]>y && player.getParachute()<=0){
            y += 120/App.FPS;
            fallDamage += 120/App.FPS;
        } else if (height[x]<y){
            y = height[x];
            explosionRadius = 30;
        }
    }

    public double getAngle(){
        return angle;
    }

    public void setAngle(double angle){
        if(angle<Math.PI/2){
            this.angle=Math.PI - Math.PI/2;
        } else if (angle>3*Math.PI/2){
            this.angle=Math.PI + Math.PI/2;
        } else {
            this.angle = angle;
        }
    }

    public double getPower(){
        return power;
    }

    public void setPower(double power){
        if(power<0){
            this.power=0;
        } else if (power>this.health){
            this.power=this.health;
        } else {
            this.power = power;
        }
    }

    public int getHealth(){
        return health;
    }

    public void setHealth(int health){
        if(this.health+health<=0){
            this.health=0;
        } else if (this.health+health>100){
            this.health=100;
        } else {
            this.health +=health;
        }

        if(this.health<50 && power>50){
            setPower(this.health);
        }
    }

    @Override
    public void draw(App app){
        if(getPower()>getHealth()){
            setPower(getPower());
        }

        if(getExploded()) {
            setInActive();
        }

        if (isActive()) {
            int [] playerRGG = player.rgbColors;
            fall();

            // Drawing the turret
            app.fill(0, 0, 0);
            app.stroke(0, 0, 0);
            app.strokeWeight(3);
            app.line(getX(), getY() - 6, getX() + (float)(10 * sin(getAngle())), getY() - 6 + (float)(10 * cos(getAngle())));

            // Drawing the tank
            app.strokeWeight(1);
            app.fill(playerRGG[0], playerRGG[1], playerRGG[2]);
            app.stroke(playerRGG[0], playerRGG[1], playerRGG[2]);
            app.rect(getX() - 6, getY() - 6, 12, 6, 4);
            app.ellipse(getX(), getY() - 6, 6, 6);

            // Drawing parachute
            if (isFalling() && player.getParachute() > 0) {
                app.image(App.parachuteImg, getX() - 11, getY() - 26);
            }
            // Exploding the tank if below map or health zero
            if (getHealth() <= 0 || getY() >= App.HEIGHT) {
                if (!getExploded()) {
                    explodeLevelObject(app,this);
                }
            }
        }
    }

}
