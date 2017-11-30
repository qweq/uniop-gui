public class Point {
    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void move(Direction dir) {
        if (dir == Direction.NORTH) {
            x += 0;
            y += 1;
        } else if (dir == Direction.EAST) {
            x += 1;
            y += 0;
        } else if (dir == Direction.SOUTH) {
            x += 0;
            y -= 1;
        } else if (dir == Direction.WEST) {
            x -= 1;
            y += 0;
        }
    }

    public boolean canMove(Map map, Direction dir, int frameReach) {
        Point bufferPoint = new Point(this.x, this.y);  // create a "buffer" point to be able to
        bufferPoint.move(dir);                          // move it and check whether it's out of bounds afterwards

        // check if the buffer Point's coordinates after the movement do not exceed map's dimensions
        // if there's a frame, include the frame's span as well
        if (bufferPoint.getX()+frameReach > map.getXDim() || bufferPoint.getX()-frameReach < 0
                || bufferPoint.getY()+frameReach > map.getYDim() || bufferPoint.getY()-frameReach < 0) {
            // todo check if > or >=; check frameReach signs
            return false;
        }
        return true;
    }

    public boolean canMove(Map map, Direction dir) {
        return this.canMove(map, dir, 0);
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

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
