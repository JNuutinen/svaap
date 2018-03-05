package model.weapons;

import controller.Controller;
import javafx.scene.paint.Color;
import model.Component;
import model.Player;
import model.Unit;
import model.projectiles.LazyMissile;

import static view.GameMain.ENEMY_PROJECTILE_TAG;
import static view.GameMain.PLAYER_PROJECTILE_TAG;

/**
 * Raketinheitin. Paitsi ampuu ohjuksia.
 */
public class RocketShotgun extends Component implements Weapon {

    /**
     * Rakettihaulukon ammuksien nopeus.
     */
    private static final int SPEED = 15;

    /**
     * Rakettihaulukon ammuksien vahinko.
     */
    private static final int DAMAGE = 7;

    /**
     * Rakettihaulukon tulinopeus.
     */
    private static final double FIRE_RATE = 0.5;

    /**
     * Ammusten käääntymisnopeus.
     */
    private double initialMissileRotatingSpeed = 11;
    private double latterMissileRotatingSpeed = 11;

    /**
     * Ammuttavien ammusten suunnat. Alkioiden lukumäärä on ammuttavien ammusten lukumäärä.
     */
    //private static final int[] PROJECTILE_DIRECTIONS = {-40, -20, 0, 20, 40};
    //private static final int[] PROJECTILE_DIRECTIONS = {-30, -15, 0, 15, 30};
    //private static final int[] PROJECTILE_DIRECTIONS = {-20, -10, 0, 10, 20};
    private static final int[] PROJECTILE_DIRECTIONS = {-10, -5, 0, 5, 10};

    /**
     * Rakettihaulukon väri.
     */
    private static final Color COLOR = Color.PINK;

    /**
     * Kontrolleriin viittaus projectilen spawnaamisen mahdollistamiseen.
     */
    private Controller controller;

    /**
     * Unit, jolla ase on käytössä.
     */
    private Unit shooter;

    private int tag;

    /**
     * Konstruktori. Kutsuu yliluokan (Component) konstruktoria jonka jälkeen asettaa kontrollerin ja ampujan.
     * @param controller Pelin kontrolleri.
     * @param shooter Unit, jolla ase on käytössä.
     * @param shape Raketinheittimen muoto merkkijonona.
     * @param size Raketinheittimen koko.
     * @param orientation Raketinheittimen suunta (kulma).
     * @param xOffset Raketinheittimen sijainnin heitto unitista x-suunnassa.
     * @param yOffset Raketinheittimen sijainnin heitto unitista y-suunnassa.
     */
    public RocketShotgun(Controller controller, Unit shooter, String shape, int size, int orientation, double xOffset,
                          double yOffset, double initialMissileRotatingSpeed, double latterMissileRotatingSpeed) {
        super(shape, size, orientation, COLOR, xOffset, yOffset);
        this.controller = controller;
        this.shooter = shooter;
        this.initialMissileRotatingSpeed = initialMissileRotatingSpeed;
        this.latterMissileRotatingSpeed = latterMissileRotatingSpeed;
        if (shooter instanceof Player){
            this.tag = PLAYER_PROJECTILE_TAG;
        }
        else{
            this.tag = ENEMY_PROJECTILE_TAG;
        }
    }

    @Override
    public double getFireRate() {
        return FIRE_RATE;
    }

    @Override
    public void shoot() {
        for (int i = 0; i < PROJECTILE_DIRECTIONS.length; i++) {
            controller.addUpdateable(new LazyMissile(controller, shooter, SPEED, DAMAGE, PROJECTILE_DIRECTIONS[i],
                    initialMissileRotatingSpeed, latterMissileRotatingSpeed, tag));
        }
    }
}
