package byog.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile PLAYER = new TETile('@', Color.white, new Color(20,20,20), "its a-me, mario");
    public static final TETile WALL = new TETile('▒', new Color(140, 140, 140), Color.darkGray,
            "the walls seem to be the only true thing about this place.");
    public static final TETile FLOOR = new TETile(' ', new Color(128, 192, 128), new Color(20,20,20),
            "could be the floor. or something else.");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing left out there except dust and the dark.");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('#', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('⸙', Color.green, Color.black, "tree");
    public static final TETile SHINY = new TETile('✨', Color.yellow, new Color(20,20,20), "oooooooh shiny.");
    public static final TETile SPIKY = new TETile('✵', Color.orange, new Color(20,20,20), "spikes stick out of the floor where you just stood.");
    public static final TETile COFFEE =
            new TETile('☕', new Color(139, 69, 19), new Color(20,20,20), "why's there coffee in a dungeon?");
    public static final TETile PORTAL1 = new TETile('҉', Color.cyan, new Color(20,20,20), "this portal looks fun.");
    public static final TETile PORTAL2 = new TETile('҉', Color.orange, new Color(20,20,20), "this portal looks fun.");
    public static final TETile MONOLITH = new TETile('۩', Color.lightGray, Color.black, "monolith");
    public static final TETile PENTACLE = new TETile('۞', Color.red, Color.black, "pentacle");
    public static final TETile PENTACLE2 = new TETile('֎', Color.red, Color.black, "pentacle2");
    public static final TETile WARP = new TETile('۝', Color.red, new Color(20,20,20), "the warp looks at you hungrily.");

}


