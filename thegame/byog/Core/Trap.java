package byog.Core;

import java.io.Serializable;

public class Trap implements Serializable {
    private String type;
    private int[] position;
    private boolean discovered;

    public Trap(int i, int j, String str) {
        position = new int[] {i, j};
        type = str;
        discovered = false;
    }

    public int[] getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void gotDiscovered() {
        this.discovered = true;
    }
}
