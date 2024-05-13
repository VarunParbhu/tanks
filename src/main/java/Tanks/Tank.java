package Tanks;

import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Represents the Tanks of a level generated from level file based on their initial position.
 */
public class Tank extends LevelObject implements Explosion {
    private int health;
    private int fuel;
    private double power;
    private double angle = 3*Math.PI/4.0;
    private int explosionRadius = 30;
    private int radius = 0;
    private boolean exploded = false;
    private boolean isFalling = false;
    private int fallDamage = 0;
    private Projectile opponentP;

    /**
     * Creates a tank by associating the Level it belongs to, the starting x and y position, and the player this tanks belong to.
     * The health is set at 100, fuel at 250 and power 50 when a new tank is created.
     *
     * @param level
     *      Level Object that the tank belongs to.
     * @param x
     *      Initial x-position
     * @param y
     *      Initial y-position
     * @param player
     *      Player object that the tank belongs to.
     */
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

    private boolean getExploded(){return exploded;}

    private void useParachute(){
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

    /**
     * @return
     *      Remaining fuel of the tank.
     */
    public int getFuel(){
        return fuel;
    }

    /**
     * Sets the new fuel value of the
     * @param fuel
     *      Value to increase the current fuel by.
     */
    public void setFuel(int fuel){
        if(this.fuel+fuel>250){
            this.fuel=250;
        } else {
            this.fuel+=fuel;
        }
    }

    private void useFuel(){
        if(fuel-1<=0){
            fuel=0;
        } else {
            fuel -= 1;
        }
    }

    private boolean isFalling(){return isFalling;}

    private void falling(){
        int [] height = level.getHeight();
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

    private void fall(){
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

    /**
     * Sets the new value x-coordinate of the Tank and adjust to the corresponding terrain height.
     * Prevents tank from moving off a cliff and go outside the playing terrain.
     * @param x
     *      x-coordinate the tank moves to.
     */
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

    /**
     * @return
     *      Angle of the turret
     */
    public double getAngle(){
        return angle;
    }

    /**
     * Sets the angle of the turret.
     * @param angle
     *      Value to set the angle to.
     */
    public void setAngle(double angle){
        if(angle<Math.PI/2){
            this.angle=Math.PI - Math.PI/2;
        } else if (angle>3*Math.PI/2){
            this.angle=Math.PI + Math.PI/2;
        } else {
            this.angle = angle;
        }
    }

    /**
     * @return
     *      Power of the turret.
     */
    public double getPower(){
        return power;
    }

    /**
     * Sets the power of the turret of the tank.
     * @param power
     *      Value to set the power to.
     */
    public void setPower(double power){
        if(power<0){
            this.power=0;
        } else if (power>this.health){
            this.power=this.health;
        } else {
            this.power = power;
        }
    }

    /**
     * @return
     *      Health remaining for the tank.
     */
    public int getHealth(){
        return health;
    }

    /**
     * Changes the health of the tank.
     * @param health
     *      Amount to change the health by.
     */
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

    /**
     * Draws the tank object on the screen at the defined position.
     * Tank is made up of a rectangle, semicircle and a line.
     * Tank activity is checked first before drawing.
     * Tank parachute is drawn when in use.
     *
     * @param app
     *      The window to draw onto
     */
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
