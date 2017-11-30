public class Vehicle {
    public int xPos;
    public int yPos;

    // todo chyba troche point == vehicle

    Vehicle(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void move(int deltaX, int deltaY) {
        xPos += deltaX;
        yPos += deltaY;
    }

    public void correlate(Map map, int frameSize) {
        int[][] frame = map.getFrame(xPos, yPos, frameSize);
        int[][] nextFrame;
        // todo jeszcze jedna pętla for dla np. 4 kierunków - może enum?
        for (int i = 0; i < frameSize; i++) {
            for (int j = 0; j < frameSize; j++) {

            }
        }
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }
}
