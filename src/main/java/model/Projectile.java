package model;

import javafx.scene.image.Image;
import view.GameMain;
import model.Sprite;


public class Projectile extends Sprite implements Updateable{
    private int damage;
    private int speed = 200;
    private int size;
    private int direction;
    private boolean hit = false;
    private int xPosition = 100; //TODO nää voi ei-kovakoodata esim unit.getX, niin ammus lähtee ampujasta
    private int yPosition = 100;

    public Projectile(int damage, int direction){
        Image projectileImage = new Image("/images/projectile_ball_small_cyan.png");
        this.setImage(projectileImage);
        this.damage = damage;
        setVelocity(speed);
        setPosition(xPosition, yPosition);
        setDirection(0);
        size = 2;
        setIsMoving(true);
        GameLoop.updateables.add(this);
    }

    public void update(double deltaTime){
        move(deltaTime);
    }

    public void move(double deltaTime) {
        if (!hit) {
            moveStep(deltaTime);
            hitCheck();
        }
    }

    public void hitCheck() {
        for (Unit unit : GameMain.units) {
            if (this.collides(unit)) {
                unit.takeDamage(damage);
                hit = true;
                GameLoop.updateables.remove(this);
                System.out.println("Osu!");
            }
        }
    }
}
