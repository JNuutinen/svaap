package model;

import controller.Controller;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;

import static view.GameMain.input;

public class Player extends Unit {
    private Controller controller;
    private double xVelocity;
    private double yVelocity;
    private int score = 0;
    private double fireRate;//sekunneissa
    private double fireRateCounter;
    private double deltaTime = 0;
    private double secondaryFirerate;
    private double secondaryFirerateCounter;


    private final double accelerationForce = 5000; // voima joka kiihdyttaa alusta
    private final double maxVelocity = 300.0; // maksiminopeus
    private final double decelerateForce = 1000; // kitkavoima joka  hidastaa alusta jos nappia ei paineta

    public Player(GraphicsContext gc, Controller controller) {
        super(gc, controller);
        this.setTag("player");
        this.controller = controller;
        this.setHp(500);

        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(-60.0, -30.0,
                60.0, 00.0,
                -60.0, 30.0);

        setShape(triangle);

        setHitbox(30);
    }


    @Override
    public void update(double deltaTime){
        this.deltaTime = deltaTime;
        resetVelocity(); // TODO: tää kutsu?
        if (input.contains("A")) addVelocity(-1, 0);
        else if (input.contains("D")) addVelocity(1, 0);
        else if(xVelocity != 0) decelerateX();

        if (input.contains("W")) addVelocity(0, -1);
        else if (input.contains("S")) addVelocity(0, 1);
        else if(yVelocity != 0) decelerateY();

        // Primary fire
        if (input.contains("O")) {
            if (getPrimaryWeapon() != null) {
                if (fireRateCounter > getPrimaryWeapon().getFireRate()) {
                    fireRateCounter = 0;
                    shootPrimary();
                }
            }
        }

        // Secondary fire
        if (input.contains("P")) {
            if (getSecondaryWeapon() != null) {
                if (secondaryFirerateCounter > getSecondaryWeapon().getFireRate()) {
                    secondaryFirerateCounter = 0;
                    shootSecondary();
                }
            }
        }

        fireRateCounter += deltaTime;
        secondaryFirerateCounter += deltaTime;
        if (input.contains("V")) System.exit(0);

        // Päivitä sijainti
        setPosition(getXPosition() + xVelocity * deltaTime, getYPosition() + yVelocity * deltaTime);
        //System.out.println("player " + (getAngleFromTarget(new Point2D(0, 0))));
        drawSprite();
    }

    public void addScore(int points){

        score += points;
        if(score < 0){
            score = 0;
        }
    }

    public int getScore() {
        return score;
    }

    // laske ja lisaa vauhtia alukseen riippuen sen nykyisestä nopeudesta ja sen suunnasta: x/yVelocity
    private void addVelocity(double directionX, double directionY) {
        if(directionX == 0);
        else if(directionX * xVelocity >= 0) { // jos kiihdyttaa nopeuden suuntaan, eli lisaa vauhtia:
            if (xVelocity < maxVelocity && xVelocity > maxVelocity * -1) { //jos alle maksiminopeuden (sama vastakkaiseen suuntaan)
                xVelocity += directionX * deltaTime * accelerationForce;
            } else { // jos ylittaa maksiminopeuden
                xVelocity = maxVelocity * directionX;
            }
        }
        else{ // jos kiihdyttaa nopeuden vastaiseen suuntaan, eli hidastaa
            xVelocity += directionX * deltaTime * accelerationForce;
        }
        //samat Y:lle
        if(directionY==0);
        else if(directionY * yVelocity >= 0) { // jos kiihdyttaa nopeuden suuntaan, eli lisaa vauhtia:
            if (yVelocity < maxVelocity && yVelocity > maxVelocity * -1) { //jos alle maksiminopeuden (sama vastakkaiseen suuntaan)
                yVelocity += directionY * deltaTime * accelerationForce;
            } else { // jos ylittaa maksiminopeuden
                yVelocity = maxVelocity * directionY;
            }
        }
        else{ // jos kiihdyttaa nopeuden vastaiseen suuntaan, eli hidastaa
            yVelocity += directionY * deltaTime * accelerationForce;
        }
    }

    // hidasta x suunnassa
    private void decelerateX() {
        if (xVelocity > 0) {
            if (xVelocity < decelerateForce * deltaTime) { // pysayta jos nopeus < seuraavan framen nopeus
                xVelocity = 0;
            } else {
                xVelocity -= decelerateForce * deltaTime;
            }
        } else {//xVelociy < 0
            if (xVelocity * -1 < decelerateForce * deltaTime) {
                xVelocity = 0;
            } else {
                xVelocity += decelerateForce * deltaTime;
            }
        }
    }

    // hidasta y suunnassa
    private void decelerateY(){
        if(yVelocity > 0){
            if(yVelocity < decelerateForce * deltaTime){ // pysayta jos nopeus < seuraavan framen nopeus
                yVelocity = 0;
            }
            else {
                yVelocity -= decelerateForce * deltaTime;
            }
        }
        else {//xVelociy < 0
            if (yVelocity * -1 < decelerateForce * deltaTime) {
                yVelocity = 0;
            } else {
                yVelocity += decelerateForce * deltaTime;
            }
        }
    }

    // TODO: ei käytössä?
    private void resetVelocity() {
        //xVelocity = 0;
        //yVelocity = 0;
    }
}
