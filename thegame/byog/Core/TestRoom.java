package byog.Core;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestRoom {

    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestRoom.class);
    }

    public void printWorld(int[][] floorCoords, int width, int height) {
        String[][] strMap = new String[height][width + 1];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                strMap[i][j] = " ";
            }
            strMap[i][width] = "\n";
        }
        for (int[] coord : floorCoords) {
            strMap[coord[1]][coord[0]] = "#";
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j <= width; j++) {
                System.out.print(strMap[i][j]);
            }
        }
    }

    private int[][] concatCoords(int[][] coords1, int[][] coords2) {
        int[][] newCoords = new int[coords1.length + coords2.length][2];
        System.arraycopy(coords1, 0, newCoords, 0, coords1.length);
        System.arraycopy(coords2, 0, newCoords, coords1.length, coords2.length);
        return newCoords;
    }

    @Test
    public void testWall() {
        Room room = new Room(1, 1,  1, 1, new int[]{1, 1});
        int[][] x = new int[][]{{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {0, 1}};
        assertArrayEquals(x, room.wallCoordinates());
    }
}
