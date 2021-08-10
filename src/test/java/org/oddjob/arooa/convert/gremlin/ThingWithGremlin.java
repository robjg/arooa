package org.oddjob.arooa.convert.gremlin;

import java.util.Objects;

public class ThingWithGremlin implements Runnable {

    private Gremlin myGremlin;

    @Override
    public void run() {

        Gremlin myGremlin = Objects.requireNonNull(this.myGremlin);

        System.out.println("My Gremlin is " + myGremlin.getName());

    }

    public Gremlin getMyGremlin() {
        return myGremlin;
    }

    public void setMyGremlin(Gremlin myGremlin) {
        this.myGremlin = myGremlin;
    }
}
