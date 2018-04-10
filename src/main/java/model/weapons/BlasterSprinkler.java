package model.weapons;

import controller.Controller;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.Component;
import model.Player;
import model.Unit;
import model.Updateable;
import model.projectiles.SmallProjectile;


import static view.GameMain.ENEMY_PROJECTILE_TAG;
import static view.GameMain.PLAYER_PROJECTILE_TAG;

/**
 * Blasterruiskuttelija.
 */
public class BlasterSprinkler extends Component implements Weapon, Updateable {

    /**
     * Aseen ammuksien vahinko.
     */
    private static final int DAMAGE = 5;

    /**
     * Aseen tulinopeus.
     */
    private double firerate = 0.07;

    /**
     * Aseen vakioväri.
     */
    private static final Color COLOR = Color.RED;

    /**
     * Ammuksen väri.
     */
    private Color projectileColor;

    /**
     * Ammuksen nopeus.
     */
    private double projectileSpeed;

    /**
     * Tieto siitä, ammutaanko parhaillaan.
     */
    private boolean isShooting = false;

    /**
     * Fireraten laskuri.
     */
    private double firerateCounter = 0;

    /**
     * Ampumisen pituus.
     */
    private double shootingTime;

    /**
     * Ampumisajan laskuri.
     */
    private double shootingTimeCounter = 0;


    /**
     * Pelin kontrolleri.
     */
    private Controller controller;

    /**
     * Konstruktori.
     * @param controller Pelin kontrolleri.
     * @param orientation Aseen orientation.
     * @param projectileSpeed Ammuksen nopeus.
     * @param shootingTime Ampumisen kesto.
     */
    public BlasterSprinkler(Controller controller, int orientation, double projectileSpeed, double shootingTime) {
        super("rectangle", 4, orientation, COLOR);
        this.shootingTime = shootingTime;
        this.controller = controller;
        controller.addUpdateable(this);
        this.projectileSpeed = projectileSpeed;

    }

    /**
     * Konstruktori.
     * @param controller Pelin kontrolleri.
     * @param orientation Aseen orientation.
     * @param projectileSpeed Ammuksen nopeus.
     * @param componentOffset TODO
     * @param projectileOffset TODO
     * @param shootingTime Ampumisen kesto.
     */
    public BlasterSprinkler(Controller controller, int orientation, double projectileSpeed, double shootingTime, Point2D componentOffset, Point2D projectileOffset) {
        this(controller, orientation, projectileSpeed, shootingTime);
        setProjectileOffset(projectileOffset);
        setComponentOffset(componentOffset);
    }

    @Override
    public double getFireRate() {
        return firerate;
    }

    @Override
    public void shoot() {
        isShooting = true;
    }

    @Override
    public void update(double deltaTime) {
        if(getParentUnit() != null) {
            if (isShooting) {
                firerateCounter += deltaTime;
                shootingTimeCounter += deltaTime;
                if (firerateCounter >= firerate) {
                    controller.addUpdateableAndSetToScene(new SmallProjectile(controller, getParentUnit(), projectileSpeed, DAMAGE,
                            getProjectileOffset(), getParentUnitColor(), Math.random() * 140 - 70, getTag()));
                    firerateCounter = 0;
                    if (shootingTimeCounter > shootingTime) {
                        isShooting = false;
                        shootingTimeCounter = 0;
                    }
                }
            }
        }
    }

    @Override
    public void collides(Updateable collidingUpdateable) {

    }

    @Override
    public void destroyThis() {

    }
}
