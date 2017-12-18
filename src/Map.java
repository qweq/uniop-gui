public class Map {
    private int xDim;
    private int yDim;
    private int intensityLevelCount;
    private int[][] mapArray;

    // todo make use of points

    Map(int xDim, int yDim, int intensityLevelCount) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.mapArray = new int[xDim][yDim];

        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                mapArray[i][j] = (int) (Math.random() * (intensityLevelCount + 1));
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

    public int getIntensityLevelCount() {
        return intensityLevelCount;
    }

    public void setIntensityLevelCount(int intensityLevelCount) {
        this.intensityLevelCount = intensityLevelCount;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                result += mapArray[i][j] + "\t";
            }
            result += "\n";
        }
        return result;
    }
}
