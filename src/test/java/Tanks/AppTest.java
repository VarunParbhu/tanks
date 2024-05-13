package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    /**
     * Checking if all levels were loaded and created.
     */
    @Test
    public void levelInitialization() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);
        assertEquals(3,app.levels.size());
    }

    /**
     * Getting a random rgbValue, in case more players are added.
     */
    @Test
    public void randomRGBValue() {
        int[] rgb = App.setRBGValues("random");
        assertEquals(3,rgb.length);
    }


    /**
     * Checking if spacebar creates a new projectile, first projectile of the game.
     */
    @Test
    public void shotProjectile() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);

        app.keyCode = 32;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        assertEquals(1, app.currentPlayingLevel.getProjectiles().size());
    }

    /**
     * Checking if projectile state changes after sometime; either by collision or out of bounds.
     */
    @Test
    public void collisionProjectile() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);
        app.currentTank.setAngle(Math.PI/2.0);
        app.keyCode = 32;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        boolean projectileStateBefore = app.currentPlayingLevel.getProjectiles().get(0).isActive();
        app.delay(3000);
        boolean projectileStateAfter = app.currentPlayingLevel.getProjectiles().get(0).isActive();
        assertNotEquals(projectileStateBefore,projectileStateAfter);
    }

    /**
     * Manually reducing health of tank every turn to see if they are set inactive, level changes and game restarts when 'R' is pressed.
     */
    @Test
    public void testingLevelChange() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        int levelBefore = 0;
        int level = app.currentLevel;

        int counter = 0;

        while(counter<50){
            app.keyCode = 32;
            app.currentTank.setHealth(-80);
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            app.delay(100);
            if(level!=app.currentLevel){
                levelBefore++;
            }
            counter++;
        }

        app.keyCode = 82;
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));

        assertNotEquals(0,levelBefore);

    }

    /**
     * Manually testing it tank and tree falls by shooting 250 projectiles continuously.
     */
    @Test
    public void testingTankFall() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        int counter = 0;

        while(counter<250){
            app.keyCode = 32;
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            app.delay(100);
            counter++;
        }
    }

}
