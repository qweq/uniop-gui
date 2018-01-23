package wolak.jakub.uniop;

import java.util.Random;

public class Map {
    private int xDim;
    private int yDim;
    private int maxIntensityLevel;
    private int[][] mapArray;

    Map(int xDim, int yDim, int maxIntensityLevel, int mode) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.mapArray = new int[xDim][yDim];
        this.maxIntensityLevel = maxIntensityLevel;
        Random rand = new Random();
        double scalingCoefficient = 255.0/maxIntensityLevel; // todo perhaps a MAX_INTENSITY_LEVEL value instead of "256"?

        switch (mode) {
            case 0: // todo modes -> "random" and "opensimplex"
                for (int x = 0; x < xDim; x++) {
                    for (int y = 0; y < yDim; y++) {
                        mapArray[x][y] = (int)(scalingCoefficient * rand.nextInt(maxIntensityLevel));
                    }
                }
                break;
            case 1:
                OpenSimplexNoise noise = new OpenSimplexNoise(rand.nextLong());
                for (int x = 0; x < xDim; x++) {
                    for (int y = 0; y < yDim; y++) {
                        // since noise.eval returns a value between -1 and 1, we add +1 to that and divide by 2 to get (0;1)
                        mapArray[x][y] = (int)(scalingCoefficient * (int)(maxIntensityLevel * (noise.eval(x, y)+1)/2));
                    }
                }
                break;
        }

    }

    Map(int xDim, int yDim, int maxIntensityLevel) {
        this(xDim, yDim, maxIntensityLevel, 0);
    }

    Map(MapFrame frame) {
        this.xDim = frame.getSize();
        this.yDim = frame.getSize();
        this.mapArray = frame.getFrameArray();
        this.maxIntensityLevel = 255;
        // notice: this is a hack and should be done better
        // this is here because Navigator should not take the whole map into consideration, only the actual frames taken
        // by the point; in fact I think Map/MapFrame structure should be redone; perhaps MapFrame should extend Map
        // deadline's too soon though
    }

    public enum Mode {
        RANDOM, OPENSIMPLEX;
    }

    // no setters for dimensions since they have to be declared at the time of the array's creation
    public int getXDim() {
        return xDim;
    }

    public int getYDim() {
        return yDim;
    }

    public int cellValue(int x, int y) {
        return mapArray[x][y];
    }

    public int getMaxIntensityLevel() {
        return maxIntensityLevel;
    }

    public void setMaxIntensityLevel(int maxIntensityLevel) {
        this.maxIntensityLevel = maxIntensityLevel;
    }

    // console version legacy code
    public String toStringWithTrajectory(Trajectory trajectory) {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < xDim; y++) {
            for (int x = 0; x < yDim; x++) {
                for (int step = 0; step < trajectory.getCoordArrayList().size(); step++) {
                    if (trajectory.getCoordArrayList().get(step)[0] == x && trajectory.getCoordArrayList().get(step)[1] == y) {
                        builder.append(Character.toString((char) (65 + step)) + "\t"); // A, B, C etc.
                        break; // if found, no need to check further in that cell
                               // actually it might happen that the trajectory intersects itself and then that character will not be shown, but it's only an issue with the representation
                    } else {
                        if (step == trajectory.getCoordArrayList().size()-1)
                            builder.append(".\t"); // just display dots; the intensity levels are not that important here
                    }
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < xDim; y++) {
            for (int x = 0; x < yDim; x++) {
                builder.append(mapArray[x][y] + "\t"); // [x] comes first so we want [0][0] \t [1][0] \t [2][0] \t etc...
            }
            builder.append("\n"); // [y] comes second so we want ...[n-1][0] \t [n][0] \n [0][1] etc...
        }
        return builder.toString();
    }
}