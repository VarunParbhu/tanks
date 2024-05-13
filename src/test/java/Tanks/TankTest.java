package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TankTest {

    /**
     * Checking if left arrow key moves the tank to the left.
     */
    @Test
    public void moveTankLeft() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        int xBefore = app.currentTank.getX();

        app.keyCode = 37;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        app.delay(100);

        int xAfter = app.currentTank.getX();

        assertEquals(xBefore-4,xAfter);
    }

    /**
     * Checking if right arrow key moves the tank to the right
     */
    @Test
    public void moveTankRight() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        int xBefore = app.currentTank.getX();

        app.keyCode = 39;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        app.delay(100);

        int xAfter = app.currentTank.getX();

        assertEquals(xBefore+4,xAfter);
    }

    /**
     * Checking if the tank is bounded to the right of the screen.
     */
    @Test
    public void moveTankRightEdge() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        app.currentTank.setX(App.WIDTH+1);

        int xPostAfter = app.currentTank.getX();

        assertEquals(App.WIDTH,xPostAfter);
    }

    /**
     * Checking if the tank is bounded to the left of the screen.
     */
    @Test
    public void moveTankLeftEdge() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        int initialX = app.currentTank.getX();
        app.currentTank.setX(-1);

        int xPostAfter = app.currentTank.getX();

        assertEquals(0,xPostAfter);

        app.currentTank.setX(initialX);
    }

    /**
     * Checking if up arrow key changes the angle of the turret to the left
     */
    @Test
    public void moveTurretLeft() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        double angleBefore = app.currentTank.getAngle();

        app.keyCode = 38;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        app.delay(100);

        double angleAfter = app.currentTank.getAngle();

        assertTrue(angleBefore<angleAfter);
    }

    /**
     * Checking if down key changes the angle of the turret to the right
     */
    @Test
    public void moveTurretRight() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        double angleBefore = app.currentTank.getAngle();

        app.keyCode = 40;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        app.delay(100);

        double angleAfter = app.currentTank.getAngle();

        assertTrue(angleBefore>angleAfter);
    }

    /**
     * Checking if 'W' key increases the power of the turret.
     */
    @Test
    public void increasePower() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        double powerBefore = app.currentTank.getPower();

        app.keyCode = 87;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 87));
        app.delay(100);

        double powerAfter = app.currentTank.getPower();

        assertTrue(powerBefore<powerAfter);
    }

    /**
     * Checking if 'S' key decreases the power of the turret.
     */
    @Test
    public void decreasePower() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        double powerBefore = app.currentTank.getPower();

        app.keyCode = 83;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 83));
        app.delay(100);

        double powerAfter = app.currentTank.getPower();

        assertTrue(powerBefore>powerAfter);
    }


    /**
     * Checking if 'X' key gives the player a larger projectile
     * The score is set manually for this test
     */
    @Test
    public void largeProjectile() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        boolean isBigProjectileBefore = app.currentTank.getPlayer().isBigProjectile();
        app.currentTank.getPlayer().setScore(100);

        app.keyCode = 88;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 88));
        app.delay(100);

        boolean isBigProjectileAfter = app.currentTank.getPlayer().isBigProjectile();

        assertEquals(isBigProjectileBefore,!isBigProjectileAfter);

        app.currentTank.player.setBigProjectileInactive();
    }

    /**
     * Checking if 'X' key gives the player more fuel
     * The score is set manually for this test
     * The fuel is set manually before testing
     */
    @Test
    public void moreFuel() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        app.currentTank.setFuel(-200);
        app.currentTank.player.setScore(50);
        int fuelBefore = app.currentTank.getFuel();

        app.keyCode = 70;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 70));
        app.delay(100);

        int fuelAfter = app.currentTank.getFuel();

        assertEquals(fuelBefore,fuelAfter-200);
    }

    /**
     * Checking if 'P' key gives the player an additional parachute.
     * The score is set manually for this test
     */
    @Test
    public void additionalParachute() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        int parachuteBefore = app.currentTank.getPlayer().getParachute();
        app.currentTank.getPlayer().setScore(100);

        app.keyCode = 80;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 80));
        app.delay(100);

        int parachuteAfter = app.currentTank.getPlayer().getParachute();

        assertEquals(parachuteBefore,parachuteAfter-1);
    }

    /**
     * Checking if 'R' key gives the player additional health.
     * The score is set manually for this test
     */
    @Test
    public void repairKit() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);


        app.currentTank.setHealth(-50);
        int healthBefore = app.currentTank.getHealth();
        app.currentTank.getPlayer().setScore(100);

        app.keyCode = 82;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));
        app.delay(100);

        int healthAfter = app.currentTank.getHealth();

        assertEquals(healthBefore+20,healthAfter);
    }
}
