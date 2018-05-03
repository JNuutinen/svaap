package Multiplayer;

import javafx.scene.paint.Color;
import model.PlayerFactory;
import model.units.Player;
import model.units.Unit;
import view.GameMain;

import java.util.HashMap;

public class Multiplayer {
    static HashMap<Integer, Unit> players = new HashMap<>();
    static boolean connected;


    //TODO: connected stateks
    public static void shootSecondary() {
        if (connected) {
            Sender.streamOut(new ShootSecondaryData(GameMain.player.getPlayerId()));
        }
    }

    public static void shootPrimary() {
        if (connected) {
            Sender.streamOut(new ShootPrimaryData(GameMain.player.getPlayerId()));
        }
    }
    public static void move(double x, double y) {
        if (connected) {
            Sender.streamOut(new MoveData(GameMain.player.getPlayerId(), x, y));
        }
    }

    public static Unit getPlayerById(int id) {
        return players.get(id);
    }

    public static void connect() { //Liitytään muiden peliin
        Player player = GameMain.player;
        System.out.println("player " + player);
        players.put(player.getPlayerId(), player); //lisää oman instanssinsa playerin, listaan
        System.out.println("playerId " + player.getPlayerId());
        System.out.println("players " + players);
        Sender.streamOut(new ConnectionData(player.getPlayerId()));  // lähettää tiedon itsestään eteenpäin
        connected = true;
        System.out.println("connection packet sent...");
    }

    public static void addPlayer(int playerId) {
        Player player = PlayerFactory.getPlayer(Color.WHITE); //Ulkoinen pelaaja liittyy omaan peliin
        players.put(playerId, player);
    }
}
