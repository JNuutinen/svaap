package model;

import javafx.application.Platform;
import javafx.scene.shape.Shape;
import view.GameMain;

import static view.GameMain.input;

public class Player extends Unit implements Updateable {
    private double xVelocity;
    private double yVelocity;
    private int hp;

    private int fireRate = 30;
    private int fireRateCounter = 30;

    public Player(){
        GameMain.units.add(this);
        this.setTag("player");
    }

    @Override
    public void update(double deltaTime){
        if (fireRateCounter <= fireRate) fireRateCounter++;
        resetVelocity();
        if (input.contains("A")) addVelocity(getVelocity()*-1, 0);
        if (input.contains("D")) addVelocity(getVelocity(), 0);
        if (input.contains("W")) addVelocity(0, getVelocity()*-1);
        if (input.contains("S")) addVelocity(0, getVelocity());
        if (input.contains("O")) {
            if (fireRateCounter >= fireRate) {
                fireRateCounter = 0;
                Platform.runLater(() -> GameMain.addProjectile(new Projectile(10, this, "projectile_player")));
            }
        }
        if (input.contains("V")) System.exit(0);
        setPosition(getXPosition() + xVelocity * deltaTime, getYPosition() + yVelocity * deltaTime);


    }

    public void collides(Updateable collidingUpdateable){
        // tagin saa: collidingUpdateable.getTag()
    }


    public Updateable getUpdateable(){
        return this;
    }
    private void addVelocity(double x, double y) {
        xVelocity += x;
        yVelocity += y;
    }

    private void resetVelocity() {
        xVelocity = 0;
        yVelocity = 0;
    }

}
