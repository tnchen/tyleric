package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;

public class Player implements Serializable {
    private int[] position;
    private int[] prevPosition;
    private int health;

    public Player() {
        health = 100;
    }

    public void initializePosition(int i, int j) {
        position = new int[] {i, j};
        prevPosition = new int[] {i, j};
    }

    public void warp(int i, int j) {
        prevPosition = position;
        position = new int[] {i, j};
    }

    public int getHealth() {
        return health;
    }

    public void damage(int x) {
        if (health - x < 0) {
            health = 0;
        } else {
            health -= x;
        }
    }

    public int[] getPosition() {
        return position;
    }

    public int[] getPrevPosition() {
        return prevPosition;
    }

    public void movePlayer(char c, int[][] worldArray) {
        if (c == 'W' || c == 'w') {
            if (worldArray[position[0]][position[1] + 1] != 2) {
                prevPosition[0] = position[0];
                prevPosition[1] = position[1];
                position[1] += 1;
            }
        } else if (c == 'A' || c == 'a') {
            if (worldArray[position[0] - 1][position[1]] != 2) {
                prevPosition[0] = position[0];
                prevPosition[1] = position[1];
                position[0] -= 1;
            }
        } else if (c == 'S' || c == 's') {
            if (worldArray[position[0]][position[1] - 1] != 2) {
                prevPosition[0] = position[0];
                prevPosition[1] = position[1];
                position[1] -= 1;
            }
        } else if (c == 'D' || c == 'd') {
            if (worldArray[position[0] + 1][position[1]] != 2) {
                prevPosition[0] = position[0];
                prevPosition[1] = position[1];
                position[0] += 1;
            }
        }
    }

    public TETile[][] visibleGrid(TETile[][] worldGrid) {
        return null;
    }
}
