package at.falb.fh.vtsys;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

/**
 * A test class initializing a local Alcatraz game -- illustrating how
 * to use the Alcatraz API.
 */
public class Test implements MoveListener {

    private Alcatraz other[] = new Alcatraz[4];
    private int numPlayer = 2;

    public Test() {
    }
    
    public void setOther(int i, Alcatraz t) {
        this.other[i] = t;
    }
    
    public int getNumPlayer() {
        return numPlayer;
    }

    public void setNumPlayer(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
        for (int i = 0; i < getNumPlayer() - 1; i++) {
            other[i].doMove(other[i].getPlayer(player.getId()), other[i].getPrisoner(prisoner.getId()), rowOrCol, row, col);
        }
    }

    public void undoMove() {
         System.out.println("Undoing move");
    }
    
    public void gameWon(Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }

    /**
     * @param args Command line args
     */
    public static void main(String[] args) {
        Test t1 = new Test();
        Test t2 = new Test();
//        Test t3 = new Test();
        
        Alcatraz a1 = new Alcatraz();
        Alcatraz a2 = new Alcatraz();
//        Alcatraz a3 = new Alcatraz();
        
        t1.setNumPlayer(2);
        t2.setNumPlayer(2);
//        t1.setNumPlayer(3);
//        t2.setNumPlayer(3);
//        t3.setNumPlayer(3);

        a1.init(2, 0);
        a2.init(2, 1);
//        a1.init(3, 0);
//        a2.init(3, 1);
//        a3.init(3, 2);
        
        a1.getPlayer(0).setName("Player 1");
        a1.getPlayer(1).setName("Player 2");

        a2.getPlayer(0).setName("Player 1");
        a2.getPlayer(1).setName("Player 2");

//        a3.getPlayer(0).setName("Player 3");
//        a3.getPlayer(0).setName("Player 3");
//        a3.getPlayer(0).setName("Player 3");
        
        t1.setOther(0, a2);
//        t1.setOther(1, a3);
        t2.setOther(0, a1);
//        t2.setOther(1, a3);
//        t3.setOther(0, a1);
//        t3.setOther(1, a2);
        
        a1.showWindow();
        a1.addMoveListener(t1);
        a2.showWindow();
        a2.addMoveListener(t2);
//        a3.showWindow();
//        a3.addMoveListener(t3);
        
        a1.start();
        a2.start();
//        a3.start();
    }

}
