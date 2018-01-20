package wolak.jakub.uniop;

public class Point {
    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point(Point p) {
        this(p.getX(), p.getY());
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void move(DirVector vector) {
        switch (vector.getDir()) {
            case NORTH:
                move(0, (-1)*vector.getValue());
                break;
            case EAST:
                move(vector.getValue(), 0);
                break;
            case SOUTH:
                move(0, vector.getValue());
                break;
            case WEST:
                move((-1)*vector.getValue(), 0);
                break;
        }
    }

    public boolean canMove(Map map, DirVector vector, MapFrame frame) {
        Point bufferPoint = new Point(this.x, this.y);  // create a "buffer" point to be able to
        bufferPoint.move(vector);                       // move it and check whether it's out of bounds afterwards
        int frameSpan = frame.getSpan();
        // check if the buffer Point's coordinates after the movement do not exceed map's dimensions
        if (frame == null) frameSpan = 0;
        if (bufferPoint.getX() + frameSpan > map.getXDim()
                || bufferPoint.getX() - frameSpan < 0
                || bufferPoint.getY() + frameSpan > map.getYDim()
                || bufferPoint.getY() - frameSpan < 0) {
            return false;
        }
        return true;
    }

    public boolean isInMapBounds(Map map) {
        if (this.x > map.getXDim()
                || this.y > map.getYDim()
                || this.x < 0
                || this.y < 0) return false;
        else return true;
    }

    public boolean canMove(Map map, DirVector vector) { // likely to be unused
        return this.canMove(map, vector, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "x = " + x + ", y = " + y;
    }
}