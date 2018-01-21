package wolak.jakub.uniop;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    private Trajectory outputTrajectory;
    private ArrayList<Integer> iterations = new ArrayList<>();

    Navigator(Map map, List<MapFrame> frames, Point startPoint, int maxVelocity, DiffAlgorithm mode/*, Trajectory... inputTrajectory*/) {
        outputTrajectory = new Trajectory(frames.size());
        double diff, minDiff;
        Point currentPoint = new Point(startPoint);

        for (int step = 0; step < frames.size(); step++) {  // iterate every frame of the trajectory; this is ok as velocity iterates from 0
            // initialize values for each step
            minDiff = Integer.MAX_VALUE;
            iterations.add(0);

            currentFrame:
            for (int vel = 0; vel <= maxVelocity; vel++) {   // try a range of velocities, including 0
                for (int dir = 0; dir < Direction.values().length; dir++) { // for each velocity, try every direction
                    try {
                        // create buffer objects to move around and compare
                        Point bufferPoint = new Point(currentPoint);
                        DirVector bufferVector = new DirVector(Direction.values()[dir], vel);
                        bufferPoint.move(bufferVector);
                        MapFrame bufferFrame = new MapFrame(map, frames.get(step).getSize(), bufferPoint);
                        // when the point turns, so do the frames taken by it
                        bufferFrame.turnAnticlockwise(DirVector.howManyTurns(new DirVector(Direction.NORTH), bufferVector));

                        // choose the method with which the frames should be compared
                        switch (mode) {
                            case SQ_DIFF:
                                diff = MapFrame.squaresDifference(frames.get(step), bufferFrame);
                                break;
                            case LIN_DIFF:
                                diff = MapFrame.linearDifference(frames.get(step), bufferFrame);
                                break;
                            case MAX_VAL_DIFF:
                                diff = MapFrame.maxValueDifference(frames.get(step), bufferFrame);
                                break;
                            default:
                                diff = 0; // to appease the compiler that "diff" might not have been initialized
                                throw new IllegalArgumentException("Wrong mode");
                        }

                        if (Math.abs(diff) < Math.abs(minDiff)) { // if it's smaller than the current minimum
                            minDiff = diff; // make it the new minimum
                            int[] coords = {bufferPoint.getX(), bufferPoint.getY()};
                            outputTrajectory.set(step, coords, bufferVector, bufferFrame); // set the values for the current step in the output trajectory
                            // if this is the first iteration of a step, the method will add the values instead
                        }
                    } catch (IndexOutOfBoundsException e) { // this will be thrown if the currently compared buffer frame gets out of bounds
                        System.out.println(e + ". Przechodzę do kolejnej iteracji.");
                        continue; // the algorithm will check another direction in the next iteration and it'll eventually find an in-bounds best fit
                    }

                    iterations.set(step, iterations.get(step)+1);
                    if (vel == 0) break; // no need to check more than one direction for zero velocity
                    if (diff == 0.0) break currentFrame; // won't get better than that
                }
            }

            /*
            // ### DEBUG ###
            if (outputTrajectory.getCoordArrayList().get(step)[0] != inputTrajectory[0].getCoordArrayList().get(step)[0]
                    || outputTrajectory.getCoordArrayList().get(step)[1] != inputTrajectory[0].getCoordArrayList().get(step)[1]) {
                // this is called when the calculated trajectory starts to deviate from the generated one
                // then the situation is as follows: currentPoint, currentFrame are equal to equivalent (step-1) inputTrajectory objects
                // but in the next step, the point will get off the trail

                if (step == 0) continue; //

                // print some info
                System.out.println("\nStep: " + step);
                System.out.print("TB point (step-1): " + inputTrajectory[0].getCoordArrayList().get(step-1)[0] +", "+ inputTrajectory[0].getCoordArrayList().get(step-1)[1]);
                System.out.println("\tNavigator point (step-1): " + currentPoint);
                System.out.println("Current calculated vector: " + outputTrajectory.getVectorArrayList().get(step));
                System.out.print("TB point (step): " + inputTrajectory[0].getCoordArrayList().get(step)[0] +", "+ inputTrajectory[0].getCoordArrayList().get(step)[1]);
                System.out.print("\tNavigator point (step): ");
                Point currentPointDebug = new Point(currentPoint);
                currentPointDebug.move(outputTrajectory.getVectorArrayList().get(step));
                System.out.println(currentPointDebug);

                System.out.println("\nOverview:\n" + new MapFrame(map, frames.get(step).getSize()*4+1, currentPoint));
                System.out.println("\nTB frame (step-1):\n" + inputTrajectory[0].getMapFrameArrayList().get(step-1));
                System.out.println("\nNavigator frame (step-1):\n" + outputTrajectory.getMapFrameArrayList().get(step-1));

                System.out.println("\nTB frame (step):\n" + inputTrajectory[0].getMapFrameArrayList().get(step));
                System.out.println("\nNavigator frame (step):\n" + new MapFrame(map, frames.get(step).getSize(), currentPointDebug));

                System.out.println("\nOther possibilities:");

                currentFrameDebug:
                for (int vel = 0; vel <= maxVelocity; vel++) {   // try a range of velocities, including 0
                    for (int dir = 0; dir < Direction.values().length; dir++) { // for each velocity, try every direction
                        try {
                            // create buffer objects to move around and compare
                            Point bufferPoint = new Point(currentPoint);
                            DirVector bufferVector = new DirVector(Direction.values()[dir], vel);
                            bufferPoint.move(bufferVector);
                            MapFrame bufferFrame = new MapFrame(map, frames.get(step).getSize(), bufferPoint);
                            // when the point turns, so do the frames taken by it
                            bufferFrame.turnAnticlockwise(DirVector.howManyTurns(new DirVector(Direction.NORTH), bufferVector));

                            //diff = MapFrame.squaresDifference(frames.get(step), bufferFrame); // find the squares difference
                            //diff = MapFrame.linearDifference(frames.get(step), bufferFrame);
                            diff = MapFrame.maxValueDifference(frames.get(step), bufferFrame);

                            System.out.println("\nPoint: " + bufferPoint + "; vector: " + bufferVector + "; diff: " + diff);
                            System.out.println(bufferFrame);
                        } catch (IndexOutOfBoundsException e) { // this will be thrown if the currently compared buffer frame gets out of bounds
                            System.out.println(e + ". Przechodzę do kolejnej iteracji.");
                            continue; // the algorithm will check another direction in the next iteration and it'll eventually find an in-bounds best fit
                        }
                        if (vel == 0) break;
                    }
                }
                System.out.println();

                System.exit(1);
            }*/

            currentPoint.move(outputTrajectory.getVectorArrayList().get(step)); // move to the next point
        }
    }

    Navigator(Map map, List<MapFrame> frames, Point startPoint) {
        this(map, frames, startPoint, 4, DiffAlgorithm.SQ_DIFF); // this is the default value for now
    }

    public Trajectory getResultTrajectory() {
        return outputTrajectory;
    }

    public ArrayList<Integer> getIterations() {
        return iterations;
    }

    public int getIterations(int step) {
        return iterations.get(step);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Calculated trajectory:\n[x, y]\n");
        for (int i = 0; i < outputTrajectory.getCoordArrayList().size(); i++) {
            builder.append("[" + outputTrajectory.getCoordArrayList().get(i)[0] + ", " + outputTrajectory.getCoordArrayList().get(i)[1] + "]\n");
        }

        return builder.toString();
    }
}