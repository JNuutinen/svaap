package model.level;

import controller.Controller;
import controller.GameController;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import model.Tag;
import model.units.Boss2;
import model.units.Enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static view.GameMain.WINDOW_HEIGHT;
import static view.GameMain.WINDOW_WIDTH;

/**
 * Kakkostaso.
 *
 * @author Ilari Anttila
 * @author Jerry Hällfors
 * @author Juha Nuutinen
 * @author Henrik Virrankoski
 */
public class Level2 extends Thread implements Level{
    Controller controller;
    Random rnd = new Random();

    public Level2(){
        this.controller = GameController.getInstance();
    }

    @Override
    public void startLevel() {
        start();
    }

    public void run(){
        try {
            spawnSquad(Tag.WEAPON_LASER_GUN);

            Thread.sleep(5000);

            for(int i = 0; i < 8; i++){
                spawnFighter();
                Thread.sleep(1500);
            }

            Thread.sleep(2000);
            spawnSquad(Tag.WEAPON_BLASTER);
            Thread.sleep(10000);

            Boss2 finalBoss = new Boss2(new Point2D(WINDOW_WIDTH + 200, WINDOW_HEIGHT * 0.5));

            // pyöri silmukas 1 sec välein niin kauan kuin bossi on olemassa
            while (!finalBoss.isDestroyed()) {
                Thread.sleep(1_000);
            }

            // Hyperdrive
            controller.changeBackgroundScrollSpeed(2000, 5);
            Thread.sleep(6_000);

            Platform.runLater(() -> controller.addScore(500));

            // Seuraavan tason aloitus
            controller.startLevel(3);

        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void spawnSquad(Tag tag){
        new Enemy(new ArrayList<>(Arrays.asList(tag)),
                0, new Point2D(WINDOW_WIDTH + 200, 100));
        new Enemy(new ArrayList<>(Arrays.asList(tag)),
                0, new Point2D(WINDOW_WIDTH +150, 200));
        new Enemy(new ArrayList<>(Arrays.asList(tag)),
                0, new Point2D(WINDOW_WIDTH + 100, 300));
        new Enemy(new ArrayList<>(Arrays.asList(tag)),
                0, new Point2D(WINDOW_WIDTH + 50, 400));
        new Enemy(new ArrayList<>(Arrays.asList(tag)),
                0, new Point2D(WINDOW_WIDTH, 500));
        new Enemy(new ArrayList<>(Arrays.asList(tag)),
                0, new Point2D(WINDOW_WIDTH - 50, 600));

    }

    public void spawnFighter(){
        new Enemy(new ArrayList<>(Arrays.asList(Tag.WEAPON_MACHINE_GUN)),
                0, new Point2D(WINDOW_WIDTH + 100, rnd.nextInt(500) + 100));
    }

}
