package model.projectiles;

import controller.Controller;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import model.*;

import static view.GameMain.*;

/**
 * Ohjusammuksen luokka. Luokassa vain ohjukselle ominaisten
 * piirteiden hallinta, kaikille ammuksille yhteiset piirteet
 * yliluokassa (BaseProjectile).
 */
public class Missile extends BaseProjectile implements Updateable {

    /**
     * Ohjuksen perusväri.
     */
    private static final Color COLOR = Color.YELLOW;

    /**
     * Etäisyys pikseleinä, jonka ammus voi mennä x- tai y-akselilla ruudun ulkopuolelle,
     * ennen kuin se poistetaan pelistä.
     */
    private static final double OUT_OF_AREA_TRESHOLD = 500;

    /**
     * Ohjuksen tekemä vahinko
     */
    private int damage;

    /**
     * Ammuksen kääntymisnopeus, vakioarvo 9.
     */
    private double rotatingSpeed = 9;

    /**
     * Viittaus kontrolleriin kohteen löytämiseksi updateables-listasta.
     */
    private Controller controller;

    /**
     * Ammuksen kohde.
     */
    private Updateable target;
    private Trail trail;
    private boolean initialDirectionToTarget = false;
    private double closestDistance = 999999;


    /**
     * Konstruktori projectilen vakiovärillä ja kääntymisnopeudella.
     * @param controller Pelin kontrolleri
     * @param shooter Unit, jonka aseesta projectile ammutaan
     * @param speed Projectilen nopeus
     * @param damage Projectilen vahinko
     */
    public Missile(Controller controller, Unit shooter, double speed, int damage, double rotatingSpeed, int tag) {
        // Kutsutaan BaseProjectilen konstruktoria
        super(controller, shooter, speed, tag);
        this.rotatingSpeed = rotatingSpeed;
        this.controller = controller;
        this.damage = damage;

        Polygon shape = buildProjectile(speed, COLOR);
        getChildren().add(shape);
        // TODO: hitboxin koko kovakoodattu
        setHitbox(10);

        trail = new Trail(controller, this);
        this.getChildren().addAll(trail);
    }

    public Missile(Controller controller, Unit shooter, double speed, int damage, double rotatingSpeed, int tag, boolean initialDirectionToTarget){
        this(controller, shooter, speed, damage, rotatingSpeed, tag);
        this.initialDirectionToTarget = initialDirectionToTarget;
    }

    @Override
    public void destroyThis(){
        trail.destroyThis();
        controller.removeUpdateable(this);
    }
    /*
    @Override
    public void collides(Updateable collidingUpdateable){
        destroyThis();
        for (Unit unit : controller.getCollisionList()) {
            if (unit == collidingUpdateable) {
                unit.takeDamage(damage);
            }
        }
    }
    */

    @Override
    //Optimoidumpi versio collidesta (?)
    public void collides(Updateable collidingUpdateable){
        destroyThis();
        ((Unit)collidingUpdateable).takeDamage(damage);
    }

    @Override
    public void update(double deltaTime) {
        // chekkaa menikö ulos ruudulta
        // Nää arvot vähän isompia kuin uniteilla, että ammukset voi kaartaa ruudun ulkopuolelta
        if (getXPosition() < -OUT_OF_AREA_TRESHOLD
                || getXPosition() > WINDOW_WIDTH + OUT_OF_AREA_TRESHOLD
                || getYPosition() < -OUT_OF_AREA_TRESHOLD
                || getYPosition() > WINDOW_HEIGHT + OUT_OF_AREA_TRESHOLD) {
            destroyThis();
        } else {
            moveStep(deltaTime * getVelocity());
        }

        double angleToTarget;
        if (target != null) {
            if(initialDirectionToTarget){ // suorittaa sisällön kerran
                //TODO KOVAKOODATTU ):
                if(getShooter() instanceof Player){
                    this.rotate(getAngleFromTarget(target.getPosition()) - getDirection() - this.getDirection());
                }
                else{
                    this.rotate(getAngleFromTarget(target.getPosition()) - getDirection() - this.getDirection() + 180);
                }
                initialDirectionToTarget = false;
            }
            if (controller.getUpdateables().contains(target)) {
                double distanceToTarget = getDistanceFromTarget(target.getPosition());
                if(distanceToTarget < closestDistance){
                    closestDistance = distanceToTarget;
                }
                if(distanceToTarget > closestDistance + 1){

                }
                else{
                    angleToTarget = getAngleFromTarget(target.getPosition()) - getDirection();
                    // taa vaa pitaa asteet -180 & 180 valissa
                    while (angleToTarget >= 180.0) {
                        angleToTarget -= 360.0;
                    }
                    while (angleToTarget < -180) {
                        angleToTarget += 360.0;
                    }
                    rotate(angleToTarget * rotatingSpeed * deltaTime);
                }

            } else {
                findAndSetTarget();
            }
        } else {
            findAndSetTarget();
        }
    }

    /**
     * Rakentaa projectilen Polygonin
     * @param speed Projectilen nopeus, vaikuttaa hännän pituuteen
     * @param color Projectilen väri
     * @return Rakennettu Polygon
     */
    private Polygon buildProjectile(double speed, Color color) {
        // Ammuksen muoto
        Polygon shape = new Polygon();
        shape.getPoints().addAll(-9.0, 0.0,
                0.0, -3.0,
                speed*0.6+1.0, 0.0, // ammuksen hanta skaalautuu nopeuden mukaan, mutta on ainakin 1.0
                0.0, 3.0);
        Bloom bloom = new Bloom(0.0);
        GaussianBlur blur = new GaussianBlur(3.0);
        blur.setInput(bloom);
        //shape.setEffect(blur); TODO
        shape.setFill(Color.TRANSPARENT);


        //setVelocity(0);

        shape.setStroke(Color.WHITE);
        shape.setStrokeWidth(5.0);
        shape.getTransforms().add(new Rotate(180, 0, 0));
        return shape;
    }

    /**
     * Asettaa lähimmän vihollisen missilen kohteeksi.
     */
    private void findAndSetTarget() {
        double shortestDistance = Double.MAX_VALUE;
        Updateable closestEnemy = null;
        if (getShooter().getTag() == PLAYER_SHIP_TAG){
            for (Updateable updateable : controller.getUpdateables()) {
                if (updateable.getTag() == ENEMY_SHIP_TAG || updateable.getTag() == BOSS_SHIP_TAG) {
                    double distance = getShooter().getDistanceFromTarget(updateable.getPosition());
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closestEnemy = updateable;
                    }
                }
            }
        }
        else if(getShooter().getTag() == ENEMY_SHIP_TAG || getShooter().getTag() == BOSS_SHIP_TAG){
            for (Updateable updateable : controller.getUpdateables()) {
                if (updateable.getTag() == PLAYER_SHIP_TAG) {
                    double distance = getShooter().getDistanceFromTarget(updateable.getPosition());
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closestEnemy = updateable;
                    }
                }
            }
        }
        target = closestEnemy;
        closestDistance = 9999999;
    }

    /**
     * Asettaa missilen kääntymisnopeuden.
     * @param rotatingSpeed Missilen kääntymisnopeus.
     */
    public void setRotatingSpeed(double rotatingSpeed){
        this.rotatingSpeed = rotatingSpeed;
    }

    public double getRotatingSpeed(){
        return rotatingSpeed;
    }

}
