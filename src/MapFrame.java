public class MapFrame {
    private int size;
    private int[][] frameArray;

    MapFrame(Map map, int size, int xCenter, int yCenter) {
        if (size % 2 == 0) throw new IllegalArgumentException("Rozmiar ramki musi być liczbą nieparzystą"); // the frame size has to be odd so as to define the center easily
        this.size = size;
        this.frameArray = new int[size][size];
        final int span = (size - 1)/2; // from the center to the edge
        final int xLeft = xCenter - span; // left edge coord
        final int yUp = yCenter - span;   // upper edge coord

        // todo handle frame out of bounds
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                try {
                    this.frameArray[x][y] = map.cellValue(xLeft + y, yUp + x);
                } catch (IndexOutOfBoundsException e) {
                    throw new IndexOutOfBoundsException("Dla podanych współrzędnych środka oraz szerokości, ramka wychodzi poza mapę");
                }
            }
        }
    }

    MapFrame(Map map, int size, Point centerPoint) {
        this(map, size, centerPoint.getX(), centerPoint.getY());
    }

    public int getSize() {
        return size;
    }

    public int getSpan() {
        return (size - 1)/2;
    }

    public int[][] getFrameArray() {
        return frameArray;
    }

    public static double squaresDifference(MapFrame f1, MapFrame f2) {
        if (f1.getFrameArray().length != f2.getFrameArray().length) { // MapFrames are square by definition
            throw new IllegalArgumentException("Ramki muszą być tego samego rozmiaru");
        }

        double sum = 0;

        for (int row = 0; row < f1.getFrameArray().length; row++) {
            for (int col = 0; col < f1.getFrameArray()[row].length; col++) {
                sum += f1.getFrameArray()[row][col]*f1.getFrameArray()[row][col] - f2.getFrameArray()[row][col]*f2.getFrameArray()[row][col];
            }
        }

        return sum;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                builder.append(frameArray[x][y] + "\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
