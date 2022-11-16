package org.oddjob.arooa.design.view;

import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class ScreenPresenceTest extends Assert {

    @Test
    public void testLocationToCenter() {

        ScreenPresence test = ScreenPresence.atLocation(400, 600)
                .ofSize(100, 200);

        Point point = test.locationToCenter(
                new Dimension(50, 100));

        assertEquals(425.0, point.getX(), 0.01);
        assertEquals(650.0, point.getY(), 0.01);
    }

    @Test
    public void testCenterNotOffTopLeftOfScreen() {

        ScreenPresence test = ScreenPresence.atLocation(-50, -30)
                .ofSize(100, 200);

        Point point = test.locationToCenter(
                new Dimension(200, 300));

        assertEquals(-50.0, point.getX(), 0.01);
        assertEquals(-30.0, point.getY(), 0.01);
    }

    @Test
    public void testCenterWithNegativeScreen() {

        ScreenPresence test = ScreenPresence.atLocation(-1000, -800)
                .ofSize(800, 600);

        Point point = test.locationToCenter(
                new Dimension(200, 300));

        assertEquals(-700.0, point.getX(), 0.01);
        assertEquals(-650.0, point.getY(), 0.01);
    }

    @Test
    public void testSmaller() {

        ScreenPresence test = ScreenPresence.atLocation(70, 0)
                .ofSize(100, 200);

        ScreenPresence result = test.resize(0.8);

        assertEquals(80.0, result.getLocation().getX(), 0.001);
        assertEquals(20.0, result.getLocation().getY(), 0.001);
        assertEquals(80.0, result.getSize().getWidth(), 0.001);
        assertEquals(160.0, result.getSize().getHeight(), 0.001);
    }
}
