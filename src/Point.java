import java.util.InvalidPropertiesFormatException;

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
        switch (dir) {
            case NORTH:
                x += 0;
                y += 1;
                break;
            case EAST:
                x += 1;
                y += 0;
                break;
            case SOUTH:
                x += 0;
                y -= 1;
                break;
            case WEST:
                x -= 1;
                y += 0;
                break;
        }
    }

    public void move(Direction dir, int delta) {
        for (int i = 0; i < delta; i++) move(dir);
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

    // todo this should return Trajectory probably...
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
