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

    @Override
    public String toString() {
        return "Direction: " + dir + ", value: " + value;
    }
}
