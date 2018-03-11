package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private static Random inRandom = new Random();
    private static LinkedList<Trap> trapList;
    private static LinkedList<Room> roomList;
    private static Player player;
    private static int[][] worldArray;

    // Randomly generates first room from available tiles, skewed towards bottom left
    private static Room generateRoom() {
        int xOrigin = inRandom.nextInt(WIDTH / 2) + WIDTH / 8;
        int yOrigin = inRandom.nextInt(HEIGHT  / 3) + HEIGHT / 5;
        int width = inRandom.nextInt(WIDTH / 10) + 5;
        int height = inRandom.nextInt(HEIGHT / 10) + 5;
        int[] inDirection = new int[]{1, 1};
        Room rm = new Room(xOrigin, yOrigin, width, height, inDirection);
        return rm;
    }

    private static int[] directionFromUpInt(int directionInt) {
        if (directionInt == 0) {
            return new int[]{-1, 1};
        } else {
            return new int[]{1, 1};
        }
    }

    private static int[] directionFromDownInt(int directionInt) {
        if (directionInt == 0) {
            return new int[]{-1, 1};
        } else if (directionInt == 1) {
            return new int[]{1, -1};
        } else {
            return new int[]{1, 1};
        }
    }

    private static int[] directionFromLeftInt(int directionInt) {
        if (directionInt == 0) {
            return new int[]{1, 1};
        } else if (directionInt == 1) {
            return new int[]{-1, 1};
        } else {
            return new int[]{1, -1};
        }
    }

    private static int[] directionFromRightInt(int directionInt) {
        if (directionInt == 0) {
            return new int[]{1, 1};
        } else if (directionInt == 1) {
            return new int[]{1, -1};
        } else {
            return new int[]{1, -1};
        }
    }
    //Room or Hall generation from input list of potential origin spots
    private static Room generateRoom(Room sourceRoom,
                                     int[][] topOrigins,
                                     int[][] bottomOrigins,
                                     int[][] leftOrigins,
                                     int[][] rightOrigins) {
        int direction = inRandom.nextInt(4);
        int[] origin;
        int[] inDirection;
        boolean hall;

        if (sourceRoom.isHallway()) {
            hall = inRandom.nextBoolean();
        } else {
            hall = true;
        }
        boolean verticalHall = false;

        //Bypass the hasRoom restriction on hallway face
        if (sourceRoom.isHallway()) {
            int directionInt;
            if (topOrigins.length == 1) {
                if (!((topOrigins[0][1] == sourceRoom.origin[1] - 1) || (topOrigins[0][1] == sourceRoom.origin[1] + 1))) {
                    origin = topOrigins[0];
                    directionInt = inRandom.nextInt(3);
                    inDirection = directionFromUpInt(directionInt);

                } else {
                    origin = bottomOrigins[0];
                    directionInt = inRandom.nextInt(3);
                    inDirection = directionFromDownInt(directionInt);
                }

            } else {
                if (!((leftOrigins[0][0] == sourceRoom.origin[0] - 1) || (leftOrigins[0][0] == sourceRoom.origin[0] + 1))) {
                    origin = leftOrigins[0];
                    directionInt = inRandom.nextInt(3);
                    inDirection = directionFromLeftInt(directionInt);

                } else {
                    origin = rightOrigins[0];
                    directionInt = inRandom.nextInt(3);
                    inDirection = directionFromRightInt(directionInt);
                }
            }
            verticalHall = (directionInt == 1);
            //If sourceRoom is a room, then generate as normal
        } else {
            if (direction == 2 && !sourceRoom.hasRoom()[2]) {
                origin = topOrigins[inRandom.nextInt(topOrigins.length)];
                inDirection = new int[]{1, 1};
                verticalHall = true;
            } else if (direction == 0 && !sourceRoom.hasRoom()[0]) {
                origin = bottomOrigins[inRandom.nextInt(bottomOrigins.length)];
                inDirection = new int[]{-1, -1};
                verticalHall = true;
            } else if (direction == 3 && !sourceRoom.hasRoom()[3]) {
                origin = leftOrigins[inRandom.nextInt(leftOrigins.length)];
                inDirection = new int[]{-1, 1};
            } else if (direction == 1 && !sourceRoom.hasRoom()[1]) {
                origin = rightOrigins[inRandom.nextInt(rightOrigins.length)];
                inDirection = new int[]{1, -1};
            } else {
                //Create invalid room
                origin = new int[]{-1, -1};
                inDirection = new int[]{1, 1};
            }
        }

        int width = inRandom.nextInt(WIDTH / 10) + 3;
        int height = inRandom.nextInt(HEIGHT / 10) + 3;
        if (hall && verticalHall) {
            width = 1;
        } else if (hall & !verticalHall) {
            height = 1;
        }
        return new Room(origin[0], origin[1], width, height, inDirection);
    }

    //Can speed this up to non-N^2 time
    private static boolean collision(Room a, LinkedList<Room> rmList) {
        for (Room rm : rmList) {
            for (int[] prevTile : rm.floorCoordinates()) {
                for (int[] newTile : a.floorCoordinates()) {
                    if (Arrays.equals(prevTile, newTile)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static LinkedList<Room> generateRoomList() {
        int numBranches = 4;
        int numIterations = 10;
        LinkedList<Room> rmList = new LinkedList<>();
        Room potential = generateRoom();
        while (!potential.canBeDrawn()) {
            potential = generateRoom();
        }
        rmList.add(potential);

        for (int j = 0; j < numIterations; j++) {
            LinkedList<Room> newRooms = new LinkedList<>();
            for (Room room : rmList) {
                for (int i = 0; i < inRandom.nextInt(numBranches); i++) {
                    //Generate room
                    potential = generateRoom(room, room.topWallCoordinates(),
                            room.bottomWallCoordinates(), room.leftWallCoordinates(),
                            room.rightWallCoordinates());
                    int maxIters = 10;
                    int iters = 0;
                    boolean endAlgo = false;
                    while (!potential.canBeDrawn() || collision(potential, rmList)) {
                        potential = generateRoom(room, room.topWallCoordinates(),
                                room.bottomWallCoordinates(), room.leftWallCoordinates(),
                                room.rightWallCoordinates());
                        iters++;
                        if (iters > maxIters) {
                            endAlgo = true;
                            break;
                        }
                    }
                    if (!endAlgo) {
                        room.hasRoom()[room.edgeForPoint(potential.origin)] = true;
                        newRooms.add(potential);
                    }
                }
            }
            rmList.addAll(newRooms);
        }
        return rmList;
    }

    private static void addTraps(String str) {
        int numWarpTraps = 5;
        int numSpikes = 5;

        if (str.equals("warp")) {
            for (int n = 0; n < numWarpTraps; n++) {
                int trapLocation = inRandom.nextInt(numFloorTiles());
                for (int i = 0; i < WIDTH; i++) {
                    for (int j = 0; j < HEIGHT; j++) {
                        if (worldArray[i][j] == 1) {
                            if (trapLocation == 0) {
                                trapList.add(new Trap(i, j, "warp"));
                                trapLocation--;
                                break;
                            } else {
                                trapLocation--;
                            }
                        }
                    }
                }
            }
        } else if (str.equals("portal")) {
            int trapLocation = inRandom.nextInt(numFloorTiles());
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (worldArray[i][j] == 1) {
                        if (trapLocation == 0) {
                            trapList.add(new Trap(i, j, "portal1"));
                            trapLocation--;
                            break;
                        } else {
                            trapLocation--;
                        }
                    }
                }
            }
            trapLocation = inRandom.nextInt(numFloorTiles());
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (worldArray[i][j] == 1) {
                        if (trapLocation == 0) {
                            trapList.add(new Trap(i, j, "portal2"));
                            trapLocation--;
                            break;
                        } else {
                            trapLocation--;
                        }
                    }
                }
            }

        } else if (str.equals("spike")) {
            for (int n = 0; n < numSpikes; n++) {
                int trapLocation = inRandom.nextInt(numFloorTiles());
                for (int i = 0; i < WIDTH; i++) {
                    for (int j = 0; j < HEIGHT; j++) {
                        if (worldArray[i][j] == 1) {
                            if (trapLocation == 0) {
                                trapList.add(new Trap(i, j, "spike"));
                                trapLocation--;
                                break;
                            } else {
                                trapLocation--;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void initializeWorldArray() {
        worldArray = new int[WIDTH][HEIGHT];

        //Wall tiles = 2
        for (Room rm : roomList) {
            for (int[] coord : rm.wallCoordinates()) {
                worldArray[coord[0]][coord[1]] = 2;
            }
        }

        //Corner tiles = Wall tiles = 2
        for (Room rm : roomList) {
            for (int[] coord : rm.cornerCoordinates()) {
                worldArray[coord[0]][coord[1]] = 2;
            }
        }

        //Room floor tiles = 1
        for (Room rm : roomList) {
            for (int[] coord : rm.floorCoordinates()) {
                worldArray[coord[0]][coord[1]] = 1;
            }
        }
    }

    private static void trapWorldArray() {
        //Un/discovered warp trap tiles = -3/3
        for (Trap tp : trapList) {
            if (tp.getType().equals("warp")) {
                if (tp.isDiscovered()) {
                    worldArray[tp.getPosition()[0]][tp.getPosition()[1]] = 3;
                } else {
                    worldArray[tp.getPosition()[0]][tp.getPosition()[1]] = -3;
                }
            } else if (tp.getType().equals("portal1")) {
                worldArray[tp.getPosition()[0]][tp.getPosition()[1]] = 4;
            } else if (tp.getType().equals("portal2")) {
                worldArray[tp.getPosition()[0]][tp.getPosition()[1]] = 5;
            } else if (tp.getType().equals("spike")) {
                if (tp.isDiscovered()) {
                    worldArray[tp.getPosition()[0]][tp.getPosition()[1]] = 6;
                } else {
                    worldArray[tp.getPosition()[0]][tp.getPosition()[1]] = -6;
                }
            }
        }
    }

    //Determines if player is trapped, and updates trap.discovered
    private static String gotTrapped(Player p) {
        for (Trap tp : trapList) {
            if (p.getPosition()[0] == tp.getPosition()[0]
                    && p.getPosition()[1] == tp.getPosition()[1]) {
                tp.gotDiscovered();
                return tp.getType();
            }
        }
        return "nope";
    }

    private static void warpPlayer() {
        int counter = inRandom.nextInt(numFloorTiles());
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (worldArray[i][j] == 1) {
                    if (counter == 0) {
                        player.warp(i, j);
                        worldArray[player.getPosition()[0]][player.getPosition()[1]] = -1;
                        return;
                    } else {
                        counter--;
                    }
                }
            }
        }
    }

    private static void teleportPlayer(String str) {

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (str.equals("portal1")) {
                    if (worldArray[i][j] == 5) {
                        player.warp(i, j);
                        worldArray[player.getPosition()[0]][player.getPosition()[1]] = -1;
                        return;
                    }
                } else if (str.equals("portal2")) {
                    if (worldArray[i][j] == 4) {
                        player.warp(i, j);
                        worldArray[player.getPosition()[0]][player.getPosition()[1]] = -1;
                        return;
                    }
                }
            }
        }
    }

    private static void playerWorldArray() {
        worldArray[player.getPosition()[0]][player.getPosition()[1]] = -1;
    }

    private static int numFloorTiles() {
        int numFloorTiles = 0;
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (worldArray[i][j] == 1) {
                    numFloorTiles++;
                }
            }
        }
        return numFloorTiles;
    }

    private static Player initializePlayer() {
        Player pl = new Player();

        int counter = inRandom.nextInt(numFloorTiles());
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (worldArray[i][j] == 1) {
                    if (counter == 0) {
                        pl.initializePosition(i, j);
                        return pl;
                    } else {
                        counter--;
                    }
                }
            }
        }
        return player;
    }

    private static long findSeed(String str) {
        str = str.toUpperCase();
        int startSeed = str.indexOf("N") + 1;
        int endSeed = startSeed;
        String temp = str.substring(startSeed);
        while (Character.isDigit(temp.charAt(0))) {
            if (temp.length() == 1) {
                endSeed++;
                break;
            }
            temp = temp.substring(1);
            endSeed++;
        }
        return Long.valueOf(str.substring(startSeed, endSeed));
    }

    private static String findActions(String str) {
        str = str.toUpperCase();
        int endSeed = 0;
        if (str.charAt(0) == 'N') {
            int startSeed = 1;
            endSeed = startSeed;
            String temp = str.substring(startSeed);
            while (Character.isDigit(temp.charAt(0))) {
                if (temp.length() == 1) {
                    break;
                }
                temp = temp.substring(1);
                endSeed++;
            }
        } else if (str.charAt(0) == 'L') {
            endSeed = 0;
        }

        String rest = str.substring(endSeed + 1);

        return rest;
    }

    private void drawMainMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear();
        StdDraw.clear(Color.black);

        // Draw Main Menu
        Font title = new Font("Monospaced", Font.BOLD, 50);
        Font subtitle = new Font("Monospaced", Font.BOLD, 20);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.getHSBColor(325, 0f, 0.12f));
        StdDraw.text(WIDTH / 2, HEIGHT - 5, "definitelytonsoftraps");
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT - 5, "definitely  n o traps");
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "we promise");

        StdDraw.setFont(subtitle);
        StdDraw.text(WIDTH / 2, HEIGHT - 16, "new game (n)");
        StdDraw.text(WIDTH / 2, HEIGHT - 18, "load game (l)");
        StdDraw.text(WIDTH / 2, HEIGHT - 20, "quit game (q)");

        StdDraw.show();
    }

    private void drawSeedMenu(String seedString) {
        StdDraw.clear();
        StdDraw.clear(Color.black);

        // Draw Main Menu
        Font title = new Font("Monospaced", Font.BOLD, 30);
        Font subtitle = new Font("Monospaced", Font.BOLD, 20);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "input seed, then s");

        StdDraw.setFont(subtitle);
        StdDraw.text(WIDTH / 2, HEIGHT - 16, "seed:" + seedString);

        StdDraw.show();
    }

    private Character waitGetKeyPress(String str, TETile[][] world) {
        while (true) {
            while (!StdDraw.hasNextKeyTyped()) {
                ter.renderFrame(world, player);
                continue;
            }

            Character keyPress = StdDraw.nextKeyTyped();
            //If on main menu, n, l, q are valid keyPresses
            if (str.equals("menu")) {
                if (keyPress.equals('n')
                        || keyPress.equals('l')
                        || keyPress.equals('q')) {
                    return keyPress;
                }
                //If getting seed, digits and s are valid keyPresses
            } else if (str.equals("seed")) {
                if (Character.isDigit(keyPress)
                        || keyPress.equals('s')) {
                    return keyPress;
                }
                //If in world, w, a, s, d, :, q are valid keyPresses
            } else {
                if (keyPress.equals('w')
                        || keyPress.equals('a') || keyPress.equals('s') || keyPress.equals('d')
                        || keyPress.equals(':') || keyPress.equals('q')) {
                    return keyPress;
                }
            }
        }
    }

    private Character getKeyPress(String str) {
        while (true) {
            while (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            Character keyPress = StdDraw.nextKeyTyped();
            //If on main menu, n, l, q are valid keyPresses
            File gameFile = new File("game.txt");
            if (str.equals("menu")) {
                if (keyPress.equals('n')
                        || (keyPress.equals('l') && gameFile.exists())
                        || keyPress.equals('q')) {
                    return keyPress;
                }
                //If getting seed, digits and s are valid keyPresses
            } else if (str.equals("seed")) {
                if (Character.isDigit(keyPress)
                        || keyPress.equals('s')) {
                    return keyPress;
                }
                //If in world, w, a, s, d, :, q are valid keyPresses
            } else {
                if (keyPress.equals('w')
                        || keyPress.equals('a') || keyPress.equals('s') || keyPress.equals('d')
                        || keyPress.equals(':') || keyPress.equals('q')) {
                    return keyPress;
                }
            }
        }
    }

    private TETile[][] buildTETileWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (worldArray[i][j] == 2) {
                    world[i][j] = Tileset.WALL;
                } else if (worldArray[i][j] == 1 || worldArray[i][j] < -1) {
                    world[i][j] = Tileset.FLOOR;
                } else if (worldArray[i][j] == -1) {
                    world[i][j] = Tileset.PLAYER;
                } else if (worldArray[i][j] == 3) {
                    world[i][j] = Tileset.WARP;
                } else if (worldArray[i][j] == 4) {
                    world[i][j] = Tileset.PORTAL1;
                } else if (worldArray[i][j] == 5) {
                    world[i][j] = Tileset.PORTAL2;
                } else if (worldArray[i][j] == 6) {
                    world[i][j] = Tileset.SPIKY;
                } else {
                    world[i][j] = Tileset.NOTHING;
                }
            }
        }
        return world;
    }

    private void saveGame() {
        try {
            FileOutputStream fileOut = new FileOutputStream("game.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(player);
            out.writeObject(roomList);
            out.writeObject(trapList);
            out.writeObject(inRandom);
            out.close();
            fileOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (AccessControlException e) {
            e.printStackTrace();
        }
    }

    private void loadGame() {
        try {
            FileInputStream fileIn = new FileInputStream("game.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            player = (Player) in.readObject();
            roomList = (LinkedList<Room>) in.readObject();
            trapList = (LinkedList<Trap>) in.readObject();
            inRandom = (Random) in.readObject();
            initializeWorldArray();
            trapWorldArray();
            playerWorldArray();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean gameOver(Player p) {
        if (p.getHealth() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        StdDraw.enableDoubleBuffering();
        drawMainMenu();
        trapList = new LinkedList<>();
        Character keyPress = getKeyPress("menu");
        if (keyPress == 'n') {
            drawSeedMenu("");
            Character seedDigit = getKeyPress("seed");
            String seedString = "";
            while (!seedDigit.equals('s')) {
                seedString = seedString + seedDigit;
                drawSeedMenu(seedString);
                seedDigit = getKeyPress("seed");
            }
            inRandom.setSeed(Long.valueOf(seedString));

            roomList = generateRoomList();
            initializeWorldArray();
            addTraps("warp");
            addTraps("spike");
            addTraps("portal");
            trapWorldArray();
            player = initializePlayer();
            playerWorldArray();

        } else if (keyPress == 'l') {
            loadGame();
        } else {
            System.exit(0);
        }
        TETile[][] world = buildTETileWorld();
        ter.initialize(WIDTH, HEIGHT + 4, 0, 0);
        ter.renderFrame(world, player);

        while (!gameOver(player)) {
            keyPress = waitGetKeyPress("world", world);
            if (keyPress == ':') {
                Character nextKeyPress = waitGetKeyPress("world", world);
                if (nextKeyPress == 'q') {
                    saveGame();
                    System.exit(0);
                    //playWithKeyboard();
                }
            }
            player.movePlayer(keyPress, worldArray);
            if (gotTrapped(player).equals("warp")) {
                warpPlayer();
            } else if (gotTrapped(player).equals("portal1")) {
                teleportPlayer("portal1");
            } else if (gotTrapped(player).equals("portal2")) {
                teleportPlayer("portal2");
            } else if (gotTrapped(player).equals("spike")) {
                player.damage(10);
            }
            initializeWorldArray();
            trapWorldArray();
            playerWorldArray();

            world = buildTETileWorld();
            ter.renderFrame(world, player);
        }
        StdDraw.setPenColor(Color.RED);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 80));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "G A M E  O V E R");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        trapList = new LinkedList<>();
        if (Character.toLowerCase(input.charAt(0)) == 'l') {
            loadGame();
            initializeWorldArray();
            trapWorldArray();
        } else {
            inRandom.setSeed(findSeed(input));

            // Create Room Objects, add all to Room List
            roomList = generateRoomList();
            initializeWorldArray();
            addTraps("warp");
            addTraps("spike");
            addTraps("portal");
            trapWorldArray();
            player = initializePlayer();
        }
        //Initialize world array, then initialize + add player:
            //Player = -1
            //Nothing = 0
            //Floor = 1
            //Wall = 2

        playerWorldArray();
        String actions = findActions(input);
        for (int i = 0; i < actions.length(); i++) {
            if (actions.charAt(i) != ':' && actions.charAt(i) != 'Q') {
                player.movePlayer(actions.charAt(i), worldArray);
                playerWorldArray();
            }
        }
        if (actions.length() > 2
                && actions.charAt(actions.length() - 2) == ':'
                && actions.charAt(actions.length() - 1) == 'Q') {
            saveGame();
        }
        //Initialize TETile world
        TETile[][] finalWorldFrame = buildTETileWorld();
        return finalWorldFrame;
    }
}
