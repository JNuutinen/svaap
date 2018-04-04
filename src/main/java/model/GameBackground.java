package model;

import controller.Controller;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.GameMain;

/**
 * Pelin liikkuva tausta. Sprite ei itsessään liiku, vaan se liikuttelee kuvia itsensä sisällä.
 */
public class GameBackground extends Sprite implements Updateable  {

    /**
     * Taustan vierimisnopeus.
     */
    private double scrollSpeed = 30;

    /**
     * Kuva1
     */
    private ImageView centerImage;

    /**
     * Seuraava kuva
     */
    private ImageView nextHorizontalImage;

    /**
     * Konstruktori, luo kuvat ja lisää ne tämän Spriten Paneen.
     * @param controller Pelin kontrolleri.
     */
    public GameBackground(Controller controller) {

        controller.addUpdateableAndSetToScene(this);

        String imagePath = "images/darkSpace.jpg";

        centerImage = new ImageView(new Image(imagePath, //Kuvaa on nyt vain levitetty havainnollistamisen vuoksi
                GameMain.WINDOW_WIDTH, GameMain.WINDOW_HEIGHT, false, false));

        nextHorizontalImage = new ImageView(new Image(imagePath, //Kuvaa on nyt vain levitetty havainnollistamisen vuoksi
                GameMain.WINDOW_WIDTH, GameMain.WINDOW_HEIGHT, false, false));

        centerImage.setY(centerImage.getY()); //siirretään backgroundia alemmas, jotta fps näkyy

        nextHorizontalImage.setY(nextHorizontalImage.getY()); //siirretään backgroundia alemmas, jotta fps näkyy
        nextHorizontalImage.setX(centerImage.getX() + centerImage.getImage().getWidth());

        this.getChildren().add(centerImage);
        this.getChildren().add(nextHorizontalImage);
    }

    @Override
    public void update(double deltaTime) {
        if(deltaTime < 100){ // fiksaa oudon bugin tason alussa
            centerImage.setX(centerImage.getX() - (scrollSpeed * deltaTime));
            nextHorizontalImage.setX(nextHorizontalImage.getX() - (scrollSpeed * deltaTime));
        }

        if (centerImage.getImage().getWidth() + centerImage.getX() <= 0) {
            ImageView iv = centerImage;
            centerImage = nextHorizontalImage;
            nextHorizontalImage = iv;
            nextHorizontalImage.setX(centerImage.getX() + centerImage.getImage().getWidth());
        }
    }

    @Override
    public void collides(Updateable collidingUpdateable) {

    }

    @Override
    public void destroyThis() {

    }
}