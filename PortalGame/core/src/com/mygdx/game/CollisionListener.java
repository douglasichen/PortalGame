package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class CollisionListener implements ContactListener {

    public CollisionListener() {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        Entity e1 = Entity.entityFromBody(f1.getBody());
        Entity e2 = Entity.entityFromBody(f2.getBody());

        // sort entity alphabetically
        Entity tempE = e1;
        Fixture tempF = f1;
        if (e1.getName().compareTo(e2.getName()) > 0) {
            e1 = e2;
            e2 = tempE;
            f1 = f2;
            f2 = tempF;
        }

        String contactString = (f1.isSensor() ? "is sensor " : "not sensor ") + e1.getName() + ", " +
                (f2.isSensor() ? "is sensor " : "not sensor ") + e2.getName();


        switch (contactString) {
            case "not sensor Player, not sensor weakEnemy":
//                e1.alive = false;
                break;
            case "is sensor map object, not sensor weakEnemy":
            case "not sensor Player, is sensor map object":
                // portals
                Fixture solidFixture = f1.isSensor() ? f2 : f1;
                Fixture wallFixture = f1.isSensor() ? f1 : f2;

                Entity e = Entity.entityFromBody(solidFixture.getBody());
                if (e.inPortal) return;

                Portals portals = ((Player) Entity.entityFromName("Player")).portals;

                Integer portalNumber = null;
                if (portals.portals[0].getSurface() == portals.portals[1].getSurface()) {
                    float topBoundPortal1 = portals.portals[0].getPosition().y + Portal.portalLength / 2f - e.size.y / 2f;
                    float botBoundPortal1 = portals.portals[0].getPosition().y - Portal.portalLength / 2f + e.size.y / 2f;

                    float topBoundPortal2 = portals.portals[1].getPosition().y + Portal.portalLength / 2f - e.size.y / 2f;
                    float botBoundPortal2 = portals.portals[1].getPosition().y - Portal.portalLength / 2f + e.size.y / 2f;

                    if (e.getPosition().y < topBoundPortal1 && e.getPosition().y > botBoundPortal1) portalNumber = 0;
                    else if (e.getPosition().y < topBoundPortal2 && e.getPosition().y > botBoundPortal2) portalNumber = 1;
                }
                else {
                    portalNumber = wallFixture == portals.portals[0].getSurface() ? 0 : 1;
                }

                if (portalNumber != null) {
                    if (!portals.properPositionToPortal(portals.portals[portalNumber], e)) return;

                    Portal portalEntering = portals.portals[portalNumber];

                    boolean goingIntoPortal = portals.isGoingIntoPortal(e, portalEntering);
                    if (goingIntoPortal) {
                        portals.linkPortal(solidFixture, portalNumber);
                    }
                }
                break;
        }

//        if (f1.isSensor() || f2.isSensor()) {
//            if (f1.isSensor() && f2.isSensor()) return;
//
//
//        }
    }
};