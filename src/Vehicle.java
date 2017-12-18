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

    // todo zrobić ramkę jako osobną klasę???


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
