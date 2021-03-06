package model.units;

import controller.Controller;
import controller.GameController;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.HitboxCircle;

public class TurretBase extends Unit {

    /**
     * Pelin kontrolleri
     */
    private Controller controller;

    /**
     * Aluksen kohde, jota se seuraa "katseellaan".
     */
    private HitboxCircle target = null;

    /**
     * Aluksen kääntymisnopeus
     */
    private double rotatingSpeed = 4;

    private Unit parentUnit;

    /**
     * Konstruktori.
     *
     * @param color                  Unitin shapen väri.
     * @param componentSideGaps      Visuaalinen väli komponenteilla toisiinsa kun ne kasataan alukseen.
     * @param projectilesFrontOffset Alukseen kasattavien aseiden ammusten poikkeama aluksen keskeltä sen etusuuntaan.
     *                               Sivusuunnassa ammuksen syntyvät komponenttien kohdasta.
     */
    public TurretBase(Color color, double componentSideGaps, double projectilesFrontOffset, Unit parentUnit) {
        super(color, componentSideGaps, projectilesFrontOffset);
        this.parentUnit = parentUnit;
        controller = GameController.getInstance();
        controller.addUpdateable(this);
        findAndSetTarget();
    }

    @Override
    public void update(double deltaTime){
        double angleToTarget;
        if(target != null && !parentUnit.isDestroyed()){
            this.setPosition(parentUnit.getPosition().getX(), parentUnit.getPosition().getY());

            getAngleFromTarget(target.getPosition());
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
        else{
            destroyThis();
        }
        shootPrimary();
    }

    @Override
    public Point2D getPosition(){
        return parentUnit.getPosition();
    }

    /**
     * Etsii lähimmän pelaajan playerHitboxObjects listasta ja asettaa sen kohteeksi
     */
    public void findAndSetTarget(){
        double shortestDistance = Double.MAX_VALUE;
        HitboxCircle closestPlayer = null;
        for (HitboxCircle hitboxCircle : controller.getPlayerHitboxObjects()) {
            double distance = getDistanceFromTarget(hitboxCircle.getPosition());
            if (distance < shortestDistance) {
                shortestDistance = distance;
                closestPlayer = hitboxCircle;
            }
        }
        target = closestPlayer;
    }
}
