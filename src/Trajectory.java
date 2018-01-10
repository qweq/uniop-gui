import java.util.ArrayList;
import java.util.List;

public class Trajectory {
    private List<int[]> coordArrayList;
    private List<DirVector> vectorArrayList;
    private List<MapFrame> mapFrameArrayList;

    Trajectory() {
        coordArrayList = new ArrayList<>();
        vectorArrayList = new ArrayList<>();
        mapFrameArrayList = new ArrayList<>();
    }

    Trajectory(int steps) {
        coordArrayList = new ArrayList<>(steps);
        vectorArrayList = new ArrayList<>(steps);
        mapFrameArrayList = new ArrayList<>(steps);
    }

    public void add(int[] coords) {
        coordArrayList.add(coords);
    }

    public void add(DirVector vector) {
        vectorArrayList.add(vector);
    }

    public void add(MapFrame frame) {
        mapFrameArrayList.add(frame);
    }

    public void add(int[] coords, DirVector vector, MapFrame frame) {
        this.add(coords);
        this.add(vector);
        this.add(frame);
    }

    public void set(int index, int[] coords, DirVector vector, MapFrame frame) {
        try {
            coordArrayList.set(index, coords);
            vectorArrayList.set(index, vector);
            mapFrameArrayList.set(index, frame);
        } catch (IndexOutOfBoundsException e) { // this exception will be thrown if the element had not yet been added
            this.add(coords, vector, frame);
        }
    }

    public List<int[]> getCoordArrayList() {
        return coordArrayList;
    }

    public List<DirVector> getVectorArrayList() {
        return vectorArrayList;
    }

    public List<MapFrame> getMapFrameArrayList() {
        return mapFrameArrayList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trajectory:\n[x, y]\n");
        for (int i = 0; i < coordArrayList.size(); i++) {
            builder.append("[" + coordArrayList.get(i)[0] + ", " + coordArrayList.get(i)[1] + "]\n");
        }

        return builder.toString();
    }
}
