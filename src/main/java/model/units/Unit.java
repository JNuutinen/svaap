package model.units;

import controller.Controller;
import controller.GameController;
import javafx.application.Platform;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import model.*;
import model.fx.Explosion;
import model.weapons.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Lisää spriteen avaruusalukselle ominaisia piirteitä.
 * @author Ilari Anttila
 * @author Jerry Hällfors
 * @author Juha Nuutinen
 * @author Henrik Virrankoski
 */
public class Unit extends SpriteImpl implements Updateable, HitboxCircle {

    /**
     * apumuuttuja jotta komponentit tietävät onko alus oikeasti tuhottu, koska aluksesta jää viittaus komponenttiin.
     */
    private boolean isDestroyed = false;

    /**
     * Aluksen koko. Käytetään mm. räjähdyksen suuruuden määrittelemisessä.
     */
    private double unitSize = 1;

    /**
     * Kontrolleri.
     */
    private Controller controller;

    /**
     * Unitin alkuperäiset hitpointsit.
     */
    private int originalHp;

    /**
     * Unitin shapen väri.
     */
    private Color color;

    /**
     * Tieto siitä, onko Unit ottanut vahinkoa. Käytetään osumaefektin tekemiseen.
     */
    private boolean tookDamage = false;

    /**
     * Unitin shape.
     */
    private Shape shape;

    /**
     * Yksikön hitpointsit.
     */
    int hp = 30;

    /**
     * Yksikön taso.
     */
    private int level;

    /**
     * Komponenttilista.
     *
    ArrayList<Weapon> components = new ArrayList<>(); */

    /**
     * Kaikki aseet mm. järjestämistä varten.
     */
    private List<Weapon> weapons = new ArrayList<>();

    /**
     * Pääaseet.
     */
    private List<Weapon> primaryWeapons = new ArrayList<>();

    /**
     * Sivuase.
     */
    private Weapon secondaryWeapon;

    /**
     * Konstruktori.
     * @param color Unitin shapen väri.
     * @param componentSideGaps Visuaalinen väli komponenteilla toisiinsa kun ne kasataan alukseen.
     * @param projectilesFrontOffset Alukseen kasattavien aseiden ammusten poikkeama aluksen keskeltä sen etusuuntaan. Sivusuunnassa ammuksen syntyvät komponenttien kohdasta.
     */
    public Unit(Color color, double componentSideGaps, double projectilesFrontOffset){
        controller = GameController.getInstance();
        this.color = color;
    }

    /**
     * Asettaa yksikön hitpointsit.
     * @param hp Hitpointsien määrä.
     */
    public void setHp(int hp){
        this.hp = hp;
        originalHp = hp;
    }

    /**
     * Lisää Unitin hitpointseja.
     * @param hp Määrä, jolla hitpointseja lisätään.
     */
    public void addHP(int hp){
        this.hp += hp;
    }

    /**
     * Palauttaa yksikön hitpointsit.
     * @return Hitpointsien määrä.
     */
    public int getHp(){
        return hp;
    }

    /**
     * Palauttaa yksikön alkuperäiset hitpointit
     * @return Alkuperäiset hitpointit.
     */
    public int getOriginalHp(){
        return originalHp;
    }

    @Override
    public void update(double deltaTime) {
        // Tyhjä oletustoteutus, overridetaan perivissä luokissa.
    }

    /**
     * Luodaan aseet int-listasta.
     * Aseet on luotava sen jälkeen kun aluksen muoto on laitettu (tai ollaan laittamassa) spriten lapseksi.
     * @param primaries primary-aseet tägeinä eli int muodossa.
     */
    public void makePrimaryWeapons(List<Tag> primaries) {
        List<model.weapons.Weapon> initialPrimaryWeapons = new ArrayList<>();

        if(primaries != null && controller != null) {
            for (Tag primaryWeaponTag : primaries) {
                switch (primaryWeaponTag){
                    default:
                        break;
                    case WEAPON_BLASTER:
                        initialPrimaryWeapons.add(new Blaster(2, 20, 1.5));
                        break;
                    case WEAPON_BLASTER_SHOTGUN:
                        initialPrimaryWeapons.add(new BlasterShotgun(2, 20, 1.5));
                        break;
                    case WEAPON_BLASTER_SPRINKLER:
                        initialPrimaryWeapons.add(new BlasterSprinkler(2, 20, 2, 3));
                        break;
                    case WEAPON_ROCKET_LAUNCHER:
                        initialPrimaryWeapons.add(new RocketLauncher(2, 4.8, 3, true));
                        break;
                    case WEAPON_ROCKET_SHOTGUN:
                        initialPrimaryWeapons.add(new RocketShotgun(2, 0, 5, 4.8, true));
                        break;
                    case WEAPON_LASER_GUN:
                        initialPrimaryWeapons.add(new LaserGun(2, 1, 1.5));
                        break;
                    case WEAPON_MACHINE_GUN:
                        initialPrimaryWeapons.add(new MachineGun(2, 55, 0.1));
                        break;
                }
            }
        }

        for(Weapon weapon : initialPrimaryWeapons){
            addPrimaryWeapon(weapon);
        }
    }

    /**
     * Palauttaa Unitin pääaseen.
     * @return Unitin pääase.
     */
    List<Weapon> getPrimaryWeapons() {
        return primaryWeapons;
    }

    /**
     * Lisää pääaseen unittiin. Ei oteta huomioon aseen kompontin poikkeamia. Jos halutaan ottaa huomioon niin ks. addPrimaryWeaponWithCustomOffsets().
     * @param primaryWeapon Weapon-rajapinnan toteuttava olio.
     */
    public void addPrimaryWeapon(Weapon primaryWeapon) {
        (primaryWeapon).setParentUnit(this); // asettaa aseen ampujaksi tämän.
        primaryWeapons.add(primaryWeapon); // lisää pääaseisiin parametrin
        Platform.runLater(()-> this.getChildren().add(primaryWeapon.getShape()));

        sortComponents();
    }

    /**
     * Lisätään pääase unittiin. Asetta ei laiteta koottavien komponenttien joukkoon, sen sijaan ase sijoitetaan sen komponentin poikkeaman perusteella.
     * @param primaryWeapon Weapon-rajapinnan toteuttava olio.
     */
    public void addPrimaryWeaponWithCustomOffsets(Weapon primaryWeapon) {
        (primaryWeapon).setParentUnit(this);

        // asetetaan komponentin poikkeama
        Shape componentShape = primaryWeapon.getShape();
        componentShape.setLayoutX(primaryWeapon.getOffset().getX());
        componentShape.setLayoutY(primaryWeapon.getOffset().getY());

        this.primaryWeapons.add(primaryWeapon);
        Platform.runLater(()-> this.getChildren().add((primaryWeapon).getShape()));
        // tästä metodista ei mene koottavien komponenttien joukkoon
    }

    /**
     * Ampuu yksikön pääaseella.
     */
    public void shootPrimary() {
        if (primaryWeapons != null) {
            for(Weapon primaryWeapon : primaryWeapons) {
                primaryWeapon.shoot();
            }
        } else {
            System.out.println(getTag() + ": No primary weapon set.");
        }
    }

    /**
     * Palauttaa Unitin sivuaseen.
     * @return Unitin sivuase.
     */
    Weapon getSecondaryWeapon() {
        return secondaryWeapon;
    }

    /**
     * Asettaa sekundaarisen aseen unittiin. Ei oteta huomioon aseen kompontin poikkeamia. Jos halutaan ottaa huomioon niin ks. setSecondaryWeaponWithCustomOffsets().
     * @param secondaryWeapon Weapon-rajapinnan toteuttava ase.
     */
    public void setSecondaryWeapon(Weapon secondaryWeapon) {
        (secondaryWeapon).setParentUnit(this); // asettaa aseen ampujaksi tämän.
        Platform.runLater(()->this.getChildren().add((secondaryWeapon).getShape()));
        this.secondaryWeapon = secondaryWeapon; // asettaa sekundaariaseeksi parametrin

        sortComponents();
    }

    /**
     * Asetetaan sekundaarinen ase unittiin. Asetta ei laiteta koottavien komponenttien joukkoon, sen sijaan ase sijoitetaan sen komponentin poikkeaman perusteella.
     * @param secondaryWeapon Weapon-rajapinnan toteuttava olio.
     */
    public void setSecondaryWeaponWithCustomOffsets(Weapon secondaryWeapon) {
        secondaryWeapon.setParentUnit(this);

        // asetetaan komponentin poikkeama
        Shape componentShape = secondaryWeapon.getShape();
        componentShape.setLayoutX(secondaryWeapon.getOffset().getX());
        componentShape.setLayoutY(secondaryWeapon.getOffset().getY());

        this.secondaryWeapon = secondaryWeapon;
        Platform.runLater(()-> this.getChildren().add((secondaryWeapon).getShape()));
        // tästä metodista ei mene koottavien komponenttien joukkoon
    }

    /**
     * Ampuu yksikön sivuaseella.
     */
    void shootSecondary() {
        if (secondaryWeapon != null) {
            secondaryWeapon.shoot();
        } else {
            System.out.println(getTag() + ": No secondary weapon set.");
        }
    }

    /**
     * Lajittelee komponenttilistan komoponenttien koon mukaan suurimmasta pienimpään. Apumetodi equipComponents():lle.
     */
    private void sortComponents() {
        List<List<Weapon>> weaponLists = new ArrayList<>();
        weaponLists.add(new ArrayList<>());
        List<Integer> alreadyAddedIndex = new ArrayList<>();


        for(int i = 0; i < primaryWeapons.size(); i++) {
            int sameWeaponsAmount = 1; //
            for (int j = 0; j < primaryWeapons.size(); j++) {
                if (primaryWeapons.get(i) != primaryWeapons.get(j) && primaryWeapons.get(i).getClass() == primaryWeapons.get(j).getClass() &&
                        !alreadyAddedIndex.contains(i)) {
                    alreadyAddedIndex.add(j);
                    sameWeaponsAmount++;
                }
            }
            // niin kauan kun asejonoja (aluksen sivusuuntaan) ei ole yhtä monta kuin saman tyyppisiä aseita lisätään, luodaan 2 uutta jonoa.
            // jotta jonolukumäärä pysyy parittomana.
            while (weaponLists.size() < sameWeaponsAmount) {
                weaponLists.add(0, new ArrayList<>());
                weaponLists.add(new ArrayList<>());
            }

            int firstIndex = (weaponLists.size() - sameWeaponsAmount) / 2;
            int midIndex = (weaponLists.size()) / 2;
            int addedIndex = 0;
            System.out.println("    -   -   ");
            for (int k = firstIndex; k < sameWeaponsAmount; k++) {
                if(sameWeaponsAmount % 2 == 0){
                    if(k == midIndex){
                        System.out.println("2");
                        addedIndex = 1;
                        weaponLists.get(k+addedIndex).add(primaryWeapons.get(k));
                    }
                    else{
                        System.out.println("3");
                        weaponLists.get(k+addedIndex).add(primaryWeapons.get(k));
                    }
                }
                else{
                    System.out.println("1");
                    weaponLists.get(k).add(primaryWeapons.get(i));
                }
            }
        }
        System.out.println("...");
        int iMidIndex = (weaponLists.size()) / 2;
        for(int i = 0; i < weaponLists.size(); i++){
            int jMidIndex = (weaponLists.get(i).size()) / 2;
            for(int j = 0; j < weaponLists.get(i).size(); j++){
                System.out.println(weaponLists.get(i).get(j) + "i&j " + i + ", " + j + ", iMid & jMid + " + iMidIndex + ", " + jMidIndex);
                Shape componentShape = weaponLists.get(i).get(j).getShape();
                //componentShape.setLayoutX(50 * (j - jMidIndex));
                componentShape.setLayoutY(150 * (i - iMidIndex));
            }
        }

        /*
        for (int i = 0; i < components.size(); i++) { //Lajitellaan komponentit suurimmasta pienimpään
            for (int n = 0; n < components.size(); n++) {
                if (components.get(i).getShape().getLayoutBounds().getHeight() * components.get(i).getShape().getLayoutBounds().getWidth()
                        > components.get(n).getShape().getLayoutBounds().getHeight() * components.get(n).getShape().getLayoutBounds().getWidth()) {
                    Weapon x = components.get(n);
                    components.set(n, components.get(i));
                    components.set(i, x);

                }
            }
        }*/
    }

    @Override
    public void collides(Object obj) {
        // Tyhjä oletustoteutus, overridetaan perivissä luokissa.
    }

    /**
     * getteri onko alus null, koska aluksesta saattaa jäädä viittaus mm. komponenttiin.
     * @return Tieto onko alus null.
     */
    public boolean isDestroyed(){
        return isDestroyed;
    }

    @Override
    public void destroyThis() {
        isDestroyed = true;
        new PowerUp(this, (int)(Math.random() * 5), 10); //Tiputtaa jonkun komponentin jos random < powerup tyyppien määrä
        new Explosion(color, getPosition(), unitSize);
        controller.removeUpdateable(this, this);
    }

    /**
     * Asettaa Unitin aluksen visuaalisia piirteitä.
     * Käyttää Plarform.runLater kuvion asettamiseksi.
     * @param shape Aluksen Shape-olio.
     */
    void drawShip(Shape shape) {
        this.shape = shape;
        this.shape.setEffect(new GaussianBlur(2.0));
        this.shape.setFill(Color.BLACK);
        this.shape.setStrokeWidth(5.0);
        Platform.runLater(()->getChildren().add(this.shape));
        this.shape.setStroke(color);
    }

    /**
     * Yksikkö ottaa vahinkoa. Kutsutaan osuman tapahtuessa.
     * @param damage Vahinkomäärä, jonka yksikkö ottaa.
     */
    public void takeDamage(int damage) {
        sortComponents();
        tookDamage = true; // efektiä varten
        if(shape != null){
            shape.setStroke(Color.WHITE);
            shape.setStrokeWidth(9.0);
        }

        hp -= damage;
        if(hp <= 0){
            hp = 9999; // ettei voi ottaa vahinkoa poiston yhteydessä
            if (getTag() == Tag.SHIP_ENEMY) {
                controller.addScore(100);
            }
            if (getTag() == Tag.SHIP_PLAYER) {
                controller.setHealthbar(0, 1);
                controller.returnToMain();
            }
            if (getTag() == Tag.SHIP_BOSS) {
                controller.addScore(1000);
                controller.setHealthbar(0, 0);
            }

            destroyThis();
        }
        if (getTag() == Tag.SHIP_PLAYER) {
            controller.addScore(-50);
        }
    }

    /**
     * Palauttaa tiedon, onko Unit ottanut vahinkoa.
     * @return Tieto onko Unit ottanut vahinkoa.
     */
    public boolean getTookDamage(){
        return tookDamage;
    }

    /**
     * Asettaa Unitin shapen värin vakioksi.
     */
    public void setOriginalColor(){
        shape.setStroke(color);
        shape.setStrokeWidth(5);
    }

    /**
     * Asettaa Unittiin tiedon, onko se ottanut vahinkoa.
     * @param tookDamage True jos Unit on ottanut vahinkoa, muuten false.
     */
    public void setTookDamage(boolean tookDamage){
        this.tookDamage = tookDamage;
    }

    /**
     * Palauttaa Unitin värin.
     * @return Unitin väri.
     */
    public Color getUnitColor(){
        return color;
    }

    /**
     * Asettaa Unitin koon.
     * @param unitSize Unitin koko.
     */
    public void setUnitSize(double unitSize) {
        this.unitSize = unitSize;
    }

}