import java.util.List;

public class Navigator {
    private Trajectory outputTrajectory;
    private int iterations = 0;

    Navigator(Map map, List<MapFrame> frames, Point startPoint, int maxVelocity) {
        outputTrajectory = new Trajectory(frames.size());
        double sqDiff, minSqDiff;
        Point currentPoint = new Point(startPoint);


        for (int step = 0; step < frames.size(); step++) {  // iterate every frame of the trajectory; this is ok as velocity iterates from 0
            // initialize values for each step
            minSqDiff = Integer.MAX_VALUE;

            currentFrame:
            for (int vel = 0; vel <= maxVelocity; vel++) {   // try a range of velocities, including 0
                for (int dir = 0; dir < Direction.values().length; dir++) { // for each velocity, try every direction
                    try {
                        // create buffer objects to move around and compare
                        Point bufferPoint = new Point(currentPoint);
                        DirVector bufferVector = new DirVector(Direction.values()[dir], vel);
                        bufferPoint.move(bufferVector);
                        MapFrame bufferFrame = new MapFrame(map, frames.get(step).getSize(), bufferPoint);

                        sqDiff = MapFrame.squaresDifference(frames.get(step), bufferFrame); // find the squares difference
                        if (Math.abs(sqDiff) < Math.abs(minSqDiff)) { // if it's smaller than the current minimum
                            minSqDiff = sqDiff; // make it the new minimum
                            int[] coords = {bufferPoint.getX(), bufferPoint.getY()};
                            outputTrajectory.set(step, coords, bufferVector, bufferFrame); // set the values for the current step in the output trajectory
                            // if this is the first iteration of a step, the method will add the values instead
                        }
                    } catch (IndexOutOfBoundsException e) { // this will be thrown if the currently compared buffer frame gets out of bounds
                        System.out.println(e + ". Przechodzę do kolejnej iteracji.");
                        continue; // the algorithm will check another direction in the next iteration and it'll eventually find an in-bound best fit
                    }

                    iterations++;
                    if (vel == 0) break; // no need to check more than one direction for zero velocity
                    if (sqDiff == 0.0) break currentFrame; // won't get better than that
                }
            }

            currentPoint.move(outputTrajectory.getVectorArrayList().get(step)); // move to the next point
        }
    }

    Navigator(Map map, List<MapFrame> frames, Point startPoint) {
        this(map, frames, startPoint, 4); // this is the default value for now
    }

    public Trajectory getResultTrajectory() {
        return outputTrajectory;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Calculated trajectory:\n[x, y]\n");
        for (int i = 0; i < outputTrajectory.getCoordArrayList().size(); i++) {
            builder.append("[" + outputTrajectory.getCoordArrayList().get(i)[0] + ", " + outputTrajectory.getCoordArrayList().get(i)[1] + "]\n");
        }
        builder.append("Iterations: " + iterations);

        return builder.toString();
    }
}