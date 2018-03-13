package model;

import controller.Controller;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;


public class Explosion extends Sprite implements Updateable{

    private List<Line> lines = new ArrayList<>();
    private final double lineLength = 150;
    private Controller controller;
    private Stop[] colors;

    private final int linesAmount = 5;
    private Point2D[] directionVector;
    private double scaleMultiplier = 1;



    public Explosion(Controller controller, Color color, Point2D position, double scaleMultiplier){
        this.controller = controller;
        this.setPosition(position.getX(), position.getY());
        this.scaleMultiplier = scaleMultiplier;
        colors = new Stop[]{new Stop(0, color), new Stop(1, Color.TRANSPARENT)};


        directionVector = new Point2D[linesAmount];
        for(int i = 0; i < linesAmount; i++){
            directionVector[i] = degreesToVector(Math.random() * (360 / linesAmount) + i * (360 / linesAmount));
            //lines.add(new Line(0, 0, -0.9085408752374184 * 100, -0.41779597655174416 * 100));
            lines.add(new Line(0, 0, directionVector[i].getX() * lineLength * scaleMultiplier,
                    directionVector[i].getY() * lineLength * scaleMultiplier));
        }


        for(int i = 0; i < linesAmount; i++){
            LinearGradient lg = new LinearGradient(0, 0, directionVector[i].getX() * lineLength * 1.05 * scaleMultiplier,
                    directionVector[i].getY() * lineLength * 1.05 * scaleMultiplier, false, CycleMethod.REFLECT, colors);
            lines.get(i).setStroke(lg);
            lines.get(i).setStrokeWidth(3 * scaleMultiplier + 7);
        }

        getChildren().addAll(lines);
        controller.addUpdateable(this);

        destroyThis();

    }

    //TODO onks taa nyt hyva?
    public void destroyThis(){
        Explosion toBeRemved = this;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        toBeRemved.destroyCompletely();
                    }
                });
            }
        });
        thread.start();
    }

    private void destroyCompletely(){
        controller.removeUpdateable(this);
    }

    @Override
    public void update(double deltaTime) {
        double deltaTimeMultiplier = 0.4;

        for(int i = 0; i < linesAmount; i++){
            if(colors[0].getColor().getOpacity() > deltaTime * deltaTimeMultiplier) {
                colors[0] = new Stop(0, new Color(colors[0].getColor().getRed(), colors[0].getColor().getGreen(), colors[0].getColor().getBlue(),
                        colors[0].getColor().getOpacity() - deltaTime * deltaTimeMultiplier));
                LinearGradient lg = new LinearGradient(0, 0, directionVector[i].getX() * lineLength * 1.05 * scaleMultiplier,
                        directionVector[i].getY() * lineLength * 1.05 * scaleMultiplier, false, CycleMethod.REFLECT, colors);
                lines.get(i).setStroke(lg);
            }
        }
    }

    @Override
    public void collides(Updateable collidingUpdateable) { }
}
