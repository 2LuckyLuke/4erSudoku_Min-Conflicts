package Model;

public class Position {
    int x;
    int y;
    int value;

    boolean changeable;

    public Position(int x, int y, int value, boolean changeable) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.changeable = changeable;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if(this.isChangeable()){
            this.value = value;
        }else {
            System.err.println("Trying to access unwritable data");
        }

    }

    public boolean isChangeable() {
        return changeable;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }
}
