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
}    /*

    public boolean canMove(Map map, Direction dir) {
        return this.canMove(map, dir, 0);
    }

    // this should not be in Point class
    /*
    public Direction correlate(Map map, int frameSize) {
        MapFrame frame = new MapFrame(map, this.x, this.y, frameSize);
        MapFrame nextFrame;
        int lsm = 0; // least squares method
        int minLSM = Integer.MAX_VALUE; // initialize
        Direction maxLSMDir = null;

        // analyse four possible directions
        for (int k = 0; k < Direction.values().length; k++) {
            Point buffer = new Point(this.x, this.y);
            // a buffer vehicle to move around so that the original one stays in place
            // this object is created inside the for loop to check for one direction and then be created anew

            buffer.move(Direction.values()[k]);
            nextFrame = new MapFrame(map, buffer.getX(), buffer.getY(), frameSize); // get the next potential frame
            if (nextFrame == null) continue; // if getFrame() returns null, then probably its index is out of bounds
            // todo handle getFrame() being out of bounds better
            for (int i = 0; i < frameSize*2+1; i++) {
                int j;
                for (j = 0; j < frameSize*2+1; j++) {
                    System.out.println("i, j= " + i + "\t" + j);
                    lsm += frame.getFrame()[i][j]*frame.getFrame()[i][j] - nextFrame.getFrame()[i][j]*nextFrame.getFrame()[i][j]; // count the squares difference
                    System.out.println(lsm + "\t" + minLSM);
                }
                j = 0;
            }

            if (lsm < minLSM) { // if the difference is lowest, the correlation is highest
                minLSM = lsm;
                maxLSMDir = Direction.values()[k];
            }
        }

        //System.out.println("Direction is " + maxLSMDir);
        return maxLSMDir;
    }*/
/*

    // todo this should return TrajectoryBuilder probably...
    public Direction[] correlate(Map map, MapFrame[] frames, int initX, int initY, int frameSize) {
        MapFrame currentFrame = frames[0];
        MapFrame potentialFrame;
        int lsm;
        int minLSM;
        Direction minLSMDir = null;
        Direction[] returnArray = new Direction[frames.length];
        //Point buffer = new Point(initX, initY);

        for (int i = 0; i < frames.length; i++) {
            minLSM = Integer.MAX_VALUE;
            for (int j = 0; j < Direction.values().length; j++) {
                lsm = 0;
                Point buffer = new Point(this.x, this.y);
                buffer.move(Direction.values()[j]);
                potentialFrame = new MapFrame(map, buffer.getX(), buffer.getY(), frameSize); // todo frameSize should be a property of MapFrame
                if (potentialFrame == null) continue;
                //System.out.println("Frame: " + i + "\tDirection: " + Direction.values()[j]);

                for (int k = 0; k < frameSize*2+1; k++) {
                    for (int l = 0; l < frameSize*2+1; l++) {
                        lsm += currentFrame.getFrame()[k][l]*currentFrame.getFrame()[k][l]
                                - potentialFrame.getFrame()[k][l]*potentialFrame.getFrame()[k][l];
                        //System.out.println("lsm: " + lsm + "\t minLSM: " + minLSM);
                        //System.out.println("k: " + k + "\tl: " + l);
                    }
                    //k = 0;
                }
                //System.out.println("");

                if (Math.abs(lsm) < Math.abs(minLSM)) { // if the difference is lowest, the correlation is highest
                    minLSM = lsm;
                    minLSMDir = Direction.values()[j];
                }
            }

            returnArray[i] = minLSMDir;
            this.move(minLSMDir);
            currentFrame = frames[i];
        }

        return returnArray;
    }
*/