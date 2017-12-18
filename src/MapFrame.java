public class MapFrame { // todo it probably should extend Map
    private int[][] frameArray;

    MapFrame(Map map, int centreX, int centreY, int maxDistFromCentre) {
        try {
            int x = 0;  // the following for-loop is iterated relative to the desired frame position on the map
            int y = 0;  // but the frame itself has to be populated starting from [0][0]
            this.frameArray = new int[2*maxDistFromCentre + 1][2*maxDistFromCentre + 1];
            for (int i = centreX - maxDistFromCentre; i <= centreX + maxDistFromCentre; i++) {
                for (int j = centreY - maxDistFromCentre; j <= centreY + maxDistFromCentre; j++) {
                    this.frameArray[x][y] = map.cellValue(i, j);
                    //System.out.print(frameArray[x][y] + "\t");
                }
                y++;
                //System.out.println("");
            }
            x++;
        } catch (IndexOutOfBoundsException e) { // instead of checking whether the frame exceeds the map, just handle the exception
            // todo
        }
    }

    public int[][] getFrame() {
        return frameArray;
    }
}
