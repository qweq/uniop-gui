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

    // for new random numbers or when the intensity level count changes
    public void redraw() {
        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                mapArray[i][j] = (int) (Math.random() * (intensityLevelCount + 1));
            }
        }
    }

    // square with a defined centre and the amount of cells in each direction from the centre
    public int[][] getFrame(int centreX, int centreY, int reach) {
        try {
            int x = 0;  // the following for-loop is iterated relative to the desired frame position on the map
            int y = 0;  // but the frame itself has to be populated starting from [0][0]
            int[][] frameArray = new int[2*reach + 1][2*reach + 1];
            for (int i = centreX - reach; i <= centreX + reach; i++) {
                for (int j = centreY - reach; j <= centreY + reach; j++) {
                    frameArray[x][y] = mapArray[i][j];
                    System.out.print(frameArray[x][y] + "\t");
                }
                y++;
                System.out.println("");
            }
            x++;
            return frameArray;
        } catch (IndexOutOfBoundsException e) { // instead of checking whether the frame exceeds the map, just handle the exception
            return null;
        }
    }

    // no setters for dimensions since they have to be declared at the time of the array's creation
    public int getXDim() {
        return xDim;
    }

    public int getYDim() {
        return yDim;
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
