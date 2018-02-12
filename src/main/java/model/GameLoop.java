package model;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.*;

public class GameLoop {

    private Controller controller;
    private volatile Queue<Updateable> updateableQueue;
    private volatile Queue<Updateable> removeUpdateableQueue;
    private ArrayList<Updateable> updateables;

    private AnimationTimer mainLoop;
    private AnimationTimer collisionLoop;

    public GameLoop(Controller controller) {
        this.controller = controller;
        updateableQueue = new LinkedList<>();
        removeUpdateableQueue = new LinkedList<>();
        updateables = new ArrayList<>();
    }

    synchronized public void queueUpdateable(Updateable updateable) {
        updateableQueue.add(updateable);
    }

    synchronized public void removeUpdateable(Updateable updateable) {
        removeUpdateableQueue.add(updateable);
    }

    public ArrayList<Updateable> getUpdateables(){
        return updateables;
    }

    public void stopLoops(){
        mainLoop.stop();
        collisionLoop.stop();
    }


    // ----- looppien alotusmetodi
    public void startLoop(){

        // -------- main looppi
        mainLoop = new AnimationTimer(){

            private long lastUpdate = 0 ;

            double debugger_avgFps = 0;
            double debugger_toSecondCounter = 0;
            int debugger_frameCounter = 0;

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;

                if ((double)(now - lastUpdate)/1_000_000_000.0 >= 1.0/120.0) {




                    // Tarkista updateable -jono
                    if (!updateableQueue.isEmpty()) {
                        updateables.addAll(updateableQueue);
                        updateableQueue.clear();
                    }

                    // tarkista poistojono
                    if (!removeUpdateableQueue.isEmpty()) {
                        for (Updateable toBeRemoved : removeUpdateableQueue) {
                            if (updateables.contains(toBeRemoved)) {
                                updateables.remove(toBeRemoved);
                            }
                        }
                        removeUpdateableQueue.clear();
                    }

                    // Gameloopin taika
                    for (Updateable updateable : updateables) {
                        if (updateable != null){
                            //paivita rajapintaoliot
                            updateable.update(deltaTime);
                        }
                    }

                    debugger_toSecondCounter += deltaTime;
                    debugger_frameCounter++;
                    if(debugger_toSecondCounter > 1){
                        controller.setFps(debugger_frameCounter);
                        debugger_toSecondCounter = 0;
                        debugger_frameCounter = 0;
                    }

                    lastUpdate = now ;
                }
            }
        };

        // ------ osumatarkastelun looppi
        collisionLoop = new AnimationTimer(){

            private long lastUpdate = 0 ;

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;

                if ((double)(now - lastUpdate)/1_000_000_000.0 >= 1.0/120.0) {
                    // Gameloopin taika
                    for (Updateable updateable : updateables) {
                        if (updateable != null) {

                            //osumistarkastelu
                            //TODO taa on KOVAKOODATTU >:(
                            if (updateable.getTag().equals("projectile_enemy")) {
                                for (Updateable updateable2 : updateables) {
                                    if (updateable != updateable2 && updateable2 != null) {
                                        if (updateable2.getTag().equals("player")) {
                                            if (((Path) Shape.intersect(updateable.getSpriteShape(), updateable2.getSpriteShape())).getElements().size() > 0) {
                                                updateable.collides(updateable2.getUpdateable());
                                            }
                                        }
                                    }

                                }
                            }
                            if (updateable.getTag().equals("projectile_player")) {
                                for (Updateable updateable2 : updateables) {
                                    if (updateable != updateable2 && updateable2 != null) {
                                        if (updateable2.getTag().equals("enemy")) {
                                            if (((Path) Shape.intersect(updateable.getSpriteShape(), updateable2.getSpriteShape())).getElements().size() > 0) {
                                                updateable.collides(updateable2.getUpdateable());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };

        mainLoop.start();
        collisionLoop.start();
    }
}
