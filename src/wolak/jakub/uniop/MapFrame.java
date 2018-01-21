package wolak.jakub.uniop;

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
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                try {
                    this.frameArray[x][y] = map.cellValue(xLeft + x, yUp + y);
                } catch (IndexOutOfBoundsException e) {
                    throw new IndexOutOfBoundsException("Dla podanych współrzędnych środka oraz szerokości, ramka wychodzi poza mapę");
                }
            }
        }
    }

    MapFrame(Map map, int size, Point centerPoint) {
        this(map, size, centerPoint.getX(), centerPoint.getY());
    }

    MapFrame(MapFrame frame) {
        this.frameArray = frame.getFrameArray();
        this.size = frame.getSize();
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

    public int cellValue(int x, int y) {
        return frameArray[x][y];
    }

    public void setCellValue(int x, int y, int newVal) {
        if (newVal > 255 || newVal < 0) throw new NumberFormatException();
        frameArray[x][y] = newVal;
    }

    public static double squaresDifference(MapFrame f1, MapFrame f2) {
        if (f1.getFrameArray().length != f2.getFrameArray().length) { // MapFrames are square by definition
            throw new IllegalArgumentException("Ramki muszą być tego samego rozmiaru");
        }

        double sum = 0;
        for (int x = 0; x < f1.getFrameArray().length; x++) {
            for (int y = 0; y < f1.getFrameArray()[x].length; y++) {
                sum += f1.getFrameArray()[x][y]*f1.getFrameArray()[x][y] - f2.getFrameArray()[x][y]*f2.getFrameArray()[x][y];
            }
        }
        return sum;
    }

    public static double linearDifference(MapFrame f1, MapFrame f2) {
        if (f1.getFrameArray().length != f2.getFrameArray().length) { // MapFrames are square by definition
            throw new IllegalArgumentException("Ramki muszą być tego samego rozmiaru");
        }

        double sum = 0;
        for (int x = 0; x < f1.getFrameArray().length; x++) {
            for (int y = 0; y < f1.getFrameArray()[x].length; y++) {
                sum += f1.getFrameArray()[x][y] - f2.getFrameArray()[x][y];
            }
        }
        return sum;
    }

    public static int maxValueDifference(MapFrame f1, MapFrame f2) {
        if (f1.getFrameArray().length != f2.getFrameArray().length) { // MapFrames are square by definition
            throw new IllegalArgumentException("Ramki muszą być tego samego rozmiaru");
        }

        int maxDiff = 0;
        for (int x = 0; x < f1.getFrameArray().length; x++) {
            for (int y = 0; y < f1.getFrameArray()[x].length; y++) {
                int diff = f1.getFrameArray()[x][y] - f2.getFrameArray()[x][y];
                if (diff > maxDiff) maxDiff = diff;
            }
        }
        return maxDiff;
    }

    // turn the frame clockwise
    public void turnClockwise(int times) {
        if (times != 0) {
            for (int i = 0; i < times; i++) {
                int[][] bufferFrame = new int[size][size];
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        bufferFrame[(size - 1) - y][x] = frameArray[x][y]; // -1 due to zero-indexing
                    }
                }
                this.frameArray = bufferFrame;
            }
        }
    }

    public void turnAnticlockwise(int times) {
        if (times != 0) {
            for (int i = 0; i < times; i++) {
                int[][] bufferFrame = new int[size][size];
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        bufferFrame[y][(size - 1) - x] = frameArray[x][y]; // -1 due to zero-indexing
                    }
                }
                this.frameArray = bufferFrame;
            }
        }
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
