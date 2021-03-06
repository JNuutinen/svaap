package model.weapons;

import controller.Controller;
import controller.GameController;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.projectiles.Missile;

/**
 * Raketinheitin. Paitsi ampuu ohjuksia.
 * @author Ilari Anttila
 * @author Jerry Hällfors
 * @author Juha Nuutinen
 * @author Henrik Virrankoski
 */
public class RocketLauncher extends Weapon {

    /**
     * Raketinheittimen ammuksien nopeus.
     */
    private static final int SPEED = 31;

    /**
     * Ammuksien kääntymisnopeus.
     */
    private double rotatingSpeed = 9.0;


    /**
     * Raketinheittimen väri.
     */
    private static final Color COLOR = Color.BLUE;


    /**
     * Kontrolleriin viittaus projectilen spawnaamisen mahdollistamiseen.
     */
    private Controller controller;

    /**
     * Apumuuttuja joka määrittelee voiko ohjus hävittää kohteen jos menee liian kauas kohteesta
     */
    private boolean missileCanLoseTarget = true;

    /**
     * Konstruktori.
     * @param orientation Aseen orientation.
     * @param rotatingSpeed Ammuksen kääntymisnopeus.
     * @param firerate tulinopeus
     * @param missileCanLoseTarget Kertoo voiko ohjus kadottaa kohteen jos etäisyys kasvaa liikaa kohteesta.
     */
    public RocketLauncher(int orientation, double rotatingSpeed, double firerate, boolean missileCanLoseTarget) {
        super("circle", 4, orientation, COLOR, 30, firerate);
        this.controller = GameController.getInstance();
        this.rotatingSpeed = rotatingSpeed;
        this.missileCanLoseTarget = missileCanLoseTarget;
    }

    /**
     * Konstruktori ammusten ja komponentin visuaalisella poikkeamalla.
     * @param orientation Aseen orientation.
     * @param rotatingSpeed Ammuksen kääntymisnopeus.
     * @param firerate tulinopeus
     * @param missileCanLoseTarget Kertoo voiko ohjus kadottaa kohteen jos etäisyys kasvaa liikaa kohteesta.
     * @param componentOffset Aseen visuaalinen poikkeama aluksesta.
     * @param prjoectileOffset Ammuksen aloituspaikan poikkeama aluksesta (x = eteenpäin, y = vasempaan päin; aluksesta)
     */
    public RocketLauncher(int orientation, double rotatingSpeed, double firerate, boolean missileCanLoseTarget,
                          Point2D componentOffset, Point2D prjoectileOffset) {
        this(orientation, rotatingSpeed, firerate, missileCanLoseTarget);
        setComponentOffset(componentOffset);
        setProjectileOffset(prjoectileOffset);
    }

    @Override
    public void shoot() {
        if (getParentUnit() != null) {
            if (getFireRateCounter() >= getFirerate()) {
                setFireRateCounter(0);

                Missile missile = new Missile(getParentUnit(), SPEED, (int) (getDamage() * getDamageMultiplier()), rotatingSpeed, getWeaponProjectileTag(), missileCanLoseTarget);
                controller.addUpdateableAndSetToScene(missile);
                controller.addHitboxObject(missile);
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        if (getFireRateCounter() <= getFirerate()) {
            setFireRateCounter(getFireRateCounter() + deltaTime);
        }
    }
}
