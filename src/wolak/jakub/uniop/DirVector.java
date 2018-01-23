package wolak.jakub.uniop;

public class DirVector {
    Direction dir;
    int value;

    DirVector() {
        this.dir = randomDir();
        this.value = 1;
    }

    DirVector(Direction dir) {
        this(dir, 1);
    }

    DirVector(Direction dir, int value) {
        this.dir = dir;
        this.value = value;
    }

    DirVector(DirVector dirVector) {
        this(dirVector.getDir(), dirVector.getValue());
    }

    public Direction oppositeDir(Direction dir) {
        switch (dir) {
            case NORTH:
                return Direction.SOUTH;
            case EAST:
                return Direction.WEST;
            case SOUTH:
                return Direction.NORTH;
            case WEST:
                return Direction.EAST;
            default:
                return null;
        }
    }

    public Direction randomDir() {
        double p = Math.random();
        if (p >= 0 && p < 0.25) return Direction.NORTH;
        if (p >= 0.25 && p < 0.5) return Direction.EAST;
        if (p >= 0.5 && p < 0.75) return Direction.SOUTH;
        else return Direction.WEST;
    }

    public void turnClockwise() {
        switch (this.dir) {
            case NORTH:
                this.dir = Direction.EAST;
                break;
            case EAST:
                this.dir = Direction.SOUTH;
                break;
            case SOUTH:
                this.dir = Direction.WEST;
                break;
            case WEST:
                this.dir = Direction.NORTH;
                break;
        }
    }

    public void turnClockwise(int times) {
        for (int i = 0; i < times; i++) turnClockwise();
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    // this method returns the number of clockwise turns necessary to align the "from" vector with the "to" vector
    public static int howManyTurns(DirVector from, DirVector to) {
        int diff = to.getDir().ordinal() - from.getDir().ordinal(); // the values in the Direction enum are sorted clockwise starting from North
        if (diff < 0) {
            diff += 4; // if the difference is negative, the necessary turn would be anticlockwise; add 4 turns for an equivalent clockwise turn
            return diff;
        } else return diff;
    }

    @Override
    public String toString() {
        return "Direction: " + dir + ", value: " + value;
    }
}