public class Map {
    private int xDim;
    private int yDim;
    private int maxIntensityLevel;
    private int[][] mapArray;

    Map(int xDim, int yDim, int maxIntensityLevel) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.mapArray = new int[xDim][yDim];
        this.maxIntensityLevel = maxIntensityLevel;

        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                mapArray[i][j] = (int) (Math.random() * (maxIntensityLevel + 1));
            }
        }
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
                builder.append(mapArray[x][y] + "\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
