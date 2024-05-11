package Tanks;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {

    @Test
    public void simpleTest() {

    }

    @Test
    public void levelInitialization() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
//        app.setup();
        app.delay(1000); // to give time to initialise stuff before drawing begins

        assertEquals(3, app.levels.size());
    }

//    @Test
//    public void levelInitialization() {
//        App app = new App();
//        app.loop();
//        PApplet.runSketch(new String[] { "App" }, app);
//        app.setup();
//        app.delay(1000); // to give time to initialise stuff before drawing begins
//
//        app.keyCode = 32;
//        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
//
////        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
////        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
////        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));// all that matters is keyCode if this is all you check in your code
//        // check velocity of tank is as expected
//
//        // assertEquals(expected, app.getCurrentPlayer().getVelocity());
//        System.out.println(app.currentPlayingLevel.getProjectiles().size());
//        assertEquals(1, app.currentPlayingLevel.getProjectiles().size());
//    }



}
