package Tanks;

import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Tank extends LevelObject {
    public Integer health;
    public Integer fuel;
    protected double power;
    protected double angle = 3*Math.PI/4.0;

    private Projectile opponentP;
    private Integer explosionRadius = 30;
    private Integer radius = 0;
    private boolean exploded = false;
    private boolean isFalling = false;
    public Integer fallDamage = 0;

    public Tank (Level level, Integer x, Integer y,Player player){
        super(level,x,y);
        this.setPlayer(player);
        this.health=50;
        this.fuel=250;
        this.power = 50;
    }


    public Integer getRadius(){return radius;}
    public Integer getExplodingRadius(){return explosionRadius;}
    public boolean getExploded(){return exploded;}


    public void useParachute(){
        Player p = App.players.get(player.playerChar);
        p.setParachute(Math.max(p.getParachute() - 1, 0));
    }

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
        if(this.fuel-1<=0){
            this.fuel=0;
        } else {
            this.fuel -= 1;
        }
    }

    public boolean isFalling(){return isFalling;}

    public void fall(){
        if(this.y < level.getHeight()[this.x]){
            this.isFalling=true;
            //get the last inactive projectile
            if(this.opponentP==null) {
                ArrayList<Projectile> projectileList = this.level.getProjectiles();
                int index = projectileList.size() - 1;


                while ((this.opponentP==null) && (index > 0)) {
                    Projectile p = projectileList.get(index);

                    if (Math.abs(p.getX() - this.getX()) <= p.tank.getExplodingRadius() && !p.isActive()) {
//                        System.out.println(Math.abs(p.getX() - this.getX()));
//                        System.out.println(Math.abs(p.tank.getExplodingRadius()));
                        break;
                    }
                    index = index - 1;
                }
                opponentP =  projectileList.get(index);
            }

            falling();
        } else {
            if(this.isFalling){
                this.isFalling=false;
                this.setHealth(-1*fallDamage);
                this.opponentP.tank.player.setScore(fallDamage);
                this.opponentP=null;
                this.fallDamage=0;
                useParachute();
            }

        }
    }

    public void setX(Integer x){
        if(!this.isFalling) {
            if (x < 0) {
                this.x = 0;
            } else if (x > App.WIDTH) {
                this.x = App.WIDTH;
            } else if (level.getHeight()[x] >= App.HEIGHT) {
                return;
            } else if (this.fuel > 0) {
                this.x = x;
                useFuel();
            }
            this.y = level.getHeight()[this.x];

        }
    }

    public void falling(){
        Integer [] height = level.getHeight();
        Player p = App.players.get(player.playerChar);
        // set which projectile caused the fall


        if(y<0){
            y=0;
        } else if (y>=App.HEIGHT){
            y=App.HEIGHT;
        } else if (height[x]>y && p.getParachute()>0){
            y += 60/App.FPS;
        } else if (height[x]>y && p.getParachute()<=0){
            y += 120/App.FPS;
            fallDamage += 120/App.FPS;
        } else if (height[x]<y){
            y = height[x];
            explosionRadius = 30;
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

    public double getPower(){return power;}

    public void setPower(double power){
        if(power<0){
            this.power=0;
        } else if (power>this.health){
            this.power=this.health;
        } else {
            this.power = power;
        }
    }

    public Integer getHealth(){return health;}

    public void setHealth(Integer health){

        if(this.health+health<=0){
            this.health=0;
        } else if (this.health+health>100){
            this.health=100;
        } else {
            this.health +=health;
        }

        if(health<50){
            this.setPower(this.health);
        }
    }

    @Override
    public void draw(App app){
        Player p = App.players.get(player.playerChar);

        if(this.getPower()>this.getHealth()){
            this.setPower(this.getPower());
        }

        if(this.getExploded()) {
            this.setInActive();

        }

        if (this.isActive()) {
            int [] playerRGG = player.rgbColors;
            this.fall();

            // Drawing the turret
            app.fill(0, 0, 0);
            app.stroke(0, 0, 0);
            app.strokeWeight(3);
            app.line(
                    this.getX(),
                    this.getY() - 6,
                    this.getX() + (int) (10 * sin(this.getAngle())),
                    this.getY() - 6 + (int) (10 * cos(this.getAngle()))
            );

            // Drawing the tank
            app.strokeWeight(1);
            app.fill(playerRGG[0], playerRGG[1], playerRGG[2]);
            app.stroke(playerRGG[0], playerRGG[1], playerRGG[2]);
            app.rect(this.getX() - 6, this.getY() - 6, 12, 6, 4);
            app.ellipse(this.getX(), this.getY() - 6, 6, 6);

            // Drawing parachute
            if (this.isFalling() && p.getParachute() > 0) {
                app.image(App.parachuteImg, this.getX() - 11, this.getY() - 26);
            }

            // Exploding the tank if below map or health zero
            if (this.getHealth() <= 0 || this.getY() >= App.HEIGHT) {
                if (!this.getExploded()) {
                    app.fill(255, 0, 0);
                    app.stroke(255, 0, 0);
                    app.ellipse(this.getX(), this.getY(), Math.min(this.getRadius(), this.getExplodingRadius()), Math.min(this.getRadius(), this.getExplodingRadius()));

                    app.fill(255, 165, 0);
                    app.stroke(255, 165, 0);
                    app.ellipse(this.getX(), this.getY(), Math.min(this.getRadius(), 0.5F * this.getExplodingRadius()), 0.5F * this.getExplodingRadius());

                    app.fill(255, 255, 0);
                    app.stroke(255, 255, 0);
                    app.ellipse(this.getX(), this.getY(), Math.min(this.getRadius(), 0.2F * this.getExplodingRadius()), 0.2F * this.getExplodingRadius());

                    this.explode();
                }
            }
        }

    }


}
