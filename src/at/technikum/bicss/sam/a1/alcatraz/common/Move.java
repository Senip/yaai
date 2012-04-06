/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.common;

import at.falb.games.alcatraz.api.Prisoner;
import at.falb.games.alcatraz.api.Player;
import java.io.Serializable;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class Move implements Serializable {
    Player player;
    Prisoner prisoner;
    int rowOrCol;
    int row;

    public Move(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        this.player = player;
        this.prisoner = prisoner;
        this.rowOrCol = rowOrCol;
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Prisoner getPrisoner() {
        return prisoner;
    }

    public void setPrisoner(Prisoner prisoner) {
        this.prisoner = prisoner;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRowOrCol() {
        return rowOrCol;
    }

    public void setRowOrCol(int rowOrCol) {
        this.rowOrCol = rowOrCol;
    }
    int col;    
}
