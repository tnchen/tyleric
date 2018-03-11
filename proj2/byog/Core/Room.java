package byog.Core;
import java.io.Serializable;
import java.util.Arrays;

public class Room implements Serializable {
    int[] origin;
    private int width;
    private int height;
    private int[] direction;
    private boolean isHallway;
    private boolean[] hasRoom;

    public static final int WORLDWIDTH = 80;
    public static final int WORLDHEIGHT = 30;

    public Room(int x, int y, int inWidth, int inHeight, int[] inDirection) {
        origin = new int[]{x, y};
        width = inWidth;
        height = inHeight;
        direction = inDirection;
        isHallway = (width == 1 || height == 1);

        //hasRoom = {bottom, right, top, left}
        //Consider halls already to have rooms at 3/4 walls
        if (width == 1) {
            if (inDirection[1] == -1) {
                hasRoom = new boolean[] {false, true, true, true};
            } else {
                hasRoom = new boolean[] {true, true, false, true};
            }
        } else if (height == 1) {
            if (inDirection[0] == -1) {
                hasRoom = new boolean[]{true, true, true, false};
            } else {
                hasRoom = new boolean[]{true, false, true, true};
            }
        //Consider rooms already to have room (hall) at 1/4 walls
        } else if (inDirection[0] == 1) {
            //If room built on top of prev hall
            if (inDirection[1] == 1) {
                hasRoom = new boolean[]{true, false, false, false};
            //If room built on right of prev hall
            } else {
                hasRoom = new boolean[]{false, false, false, true};
            }
        } else {
            //If room built on bottom of prev hall
            if (inDirection[1] == -1) {
                hasRoom = new boolean[]{false, false, true, false};
            //If room built on left of prev hall
            } else {
                hasRoom = new boolean[]{false, true, false, false};
            }
        }
    }

    public boolean[] hasRoom() {
        return hasRoom;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean isHallway() {
        return isHallway;
    }

    public boolean canBeDrawn() {
        int[][] corners = this.cornerCoordinates();
        for (int[] coord : corners) {
            if (coord[0] < 1 || coord[0] > WORLDWIDTH - 1
                    || coord[1] < 1 || coord[1] > WORLDHEIGHT - 1) {
                return false;
            }
        }
        return true;
    }

    public int[][] floorCoordinates() {
        // Generates set of floor coordinates
        int[][] coords = new int[width * height][2];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                coords[j + i * width] = new int[]{origin[0] + j * direction[0],
                        origin[1] + i * direction[1]};
            }
        }
        return coords;
    }

    // Generates array of coordinates for walls, minus corner positions (for next room generation)
    public int[][] wallCoordinates() {
        int perimeter = 2 * width + 2 * height;
        int[][] wallCoordinates = new int[perimeter][2];
        for (int i = 0; i < perimeter; i++) {
            int[] a;
            if (i < width) {
                a = new int[]{origin[0] + i * direction[0], origin[1] - 1 * direction[1]};
            } else if (i < 2 * width) {
                a = new int[]{origin[0] + (i - width) * direction[0],
                        origin[1] + height * direction[1]};
            } else if (i < 2 * width + height) {
                a = new int[]{origin[0] - 1 * direction[0],
                        origin[1] + (i - 2 * width) * direction[1]};
            } else {
                a = new int[]{origin[0] + width * direction[0],
                        origin[1] + (i - (2 * width + height)) * direction[1]};
            }
            wallCoordinates[i] = a;
        }
        return wallCoordinates;
    }

    public int[][] topWallCoordinates() {
        int[][] topWallCoordinates = new int[width][2];
        int yMax = 0;
        for (int[] a : wallCoordinates()) {
            if (a[1] > yMax) {
                yMax = a[1];
            }
        }
        int i = 0;
        for (int[] a : wallCoordinates()) {
            if (a[1] == yMax) {
                topWallCoordinates[i] = new int[]{a[0], a[1]};
                i++;
            }
        }
        return topWallCoordinates;
    }

    public int[][] bottomWallCoordinates() {
        int[][] bottomWallCoordinates = new int[width][2];
        int yMin = WORLDHEIGHT;
        for (int[] a : wallCoordinates()) {
            if (a[1] < yMin) {
                yMin = a[1];
            }
        }
        int i = 0;
        for (int[] a : wallCoordinates()) {
            if (a[1] == yMin) {
                bottomWallCoordinates[i] = new int[]{a[0], a[1]};
                i++;
            }
        }
        return bottomWallCoordinates;
    }

    public int[][] leftWallCoordinates() {
        int[][] leftWallCoordinates = new int[height][2];
        int xMin = WORLDWIDTH;
        for (int[] a : wallCoordinates()) {
            if (a[0] < xMin) {
                xMin = a[0];
            }
        }
        int i = 0;
        for (int[] a : wallCoordinates()) {
            if (a[0] == xMin) {
                leftWallCoordinates[i] = new int[]{a[0], a[1]};
                i++;
            }
        }
        return leftWallCoordinates;
    }

    public int[][] rightWallCoordinates() {
        int[][] rightWallCoordinates = new int[height][2];
        int xMax = 0;
        for (int[] a : wallCoordinates()) {
            if (a[0] > xMax) {
                xMax = a[0];
            }
        }
        int i = 0;
        for (int[] a : wallCoordinates()) {
            if (a[0] == xMax) {
                rightWallCoordinates[i] = new int[]{a[0], a[1]};
                i++;
            }
        }
        return rightWallCoordinates;
    }

    //Generates coordinates of wall corners
    public int[][] cornerCoordinates() {
        int[][] corners = new int[4][2];
        corners[0] = new int[]{origin[0] - 1 * direction[0], origin[1] - 1 * direction[1]};
        corners[1] = new int[]{origin[0] + width * direction[0], origin[1] - 1 * direction[1]};
        corners[2] = new int[]{origin[0] - 1 * direction[0], origin[1] + height * direction[1]};
        corners[3] = new int[]{origin[0] + width * direction[0], origin[1] + height * direction[1]};
        return corners;
    }

    private boolean isIn(int[][] items, int[] item) {
        for (int i = 0; i < items.length; i++) {
            if (Arrays.equals(items[i], item)) {
                return true;
            }
        }
        return false;
    }

    public int edgeForPoint(int[] point) {
        for (int[] a : bottomWallCoordinates()) {
            if (Arrays.equals(a, point)) {
                return 0;
            }
        }
        for (int[] a : rightWallCoordinates()) {
            if (Arrays.equals(a, point)) {
                return 1;
            }
        }
        for (int[] a : topWallCoordinates()) {
            if (Arrays.equals(a, point)) {
                return 2;
            }
        }
        return 3;
    }
}
