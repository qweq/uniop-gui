package wolak.jakub.uniop;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    private Trajectory outputTrajectory;
    private ArrayList<Integer> iterations = new ArrayList<>();

    Navigator(List<MapFrame> frames, Point startPoint, DirVector startVector, int maxVelocity, int subframeSize, DiffAlgorithm mode, boolean debugMode, Trajectory... inputTrajectory) {
        outputTrajectory = new Trajectory(frames.size());
        double diff, minDiff;
        Point currentPoint = new Point(startPoint);
        final int centerCoord = frames.get(0).getSpan(); // central point of a subframe
        int[] startCoords = {startPoint.getX(), startPoint.getY()};
        outputTrajectory.set(0, startCoords, startVector, frames.get(0).getSubFrame(new Point(centerCoord, centerCoord), subframeSize));
        iterations.add(0);

        for (int step = 1; step < frames.size(); step++) {  // iterate every frame of the trajectory; this is ok as velocity iterates from 0
            // initialize values for each step
            minDiff = Integer.MAX_VALUE;
            iterations.add(0);
            MapFrame targetFrame = frames.get(step).getSubFrame(new Point(centerCoord, centerCoord), subframeSize);

            currentFrame:
            for (int vel = 0; vel <= maxVelocity; vel++) {   // try a range of velocities, including 0
                for (int dir = 0; dir < Direction.values().length; dir++) { // for each velocity, try every direction
                    try {
                        // create buffer objects to move around and compare
                        Point bufferPoint = new Point(centerCoord, centerCoord); // create a buffer point at the centre of the subframe
                        DirVector bufferVector = new DirVector(Direction.values()[dir], vel);
                        bufferPoint.move(bufferVector); // move the buffer point around
                        MapFrame bufferFrame = frames.get(step-1).getSubFrame(bufferPoint, subframeSize);

                        // when the point turns, so do the frames taken by it
                        bufferFrame.turnAnticlockwise(DirVector.howManyTurns(new DirVector(Direction.NORTH), bufferVector));
                        bufferVector.turnClockwise(DirVector.howManyTurns(bufferVector, inputTrajectory[0].getVectorArrayList().get(step-1)));

                        // choose the method with which the frames should be compared
                        switch (mode) {
                            case SQ_DIFF:
                                diff = MapFrame.squaresDifference(targetFrame, bufferFrame);
                                break;
                            case LIN_DIFF:
                                diff = MapFrame.linearDifference(targetFrame, bufferFrame);
                                break;
                            case MAX_VAL_DIFF:
                                diff = MapFrame.maxValueDifference(targetFrame, bufferFrame);
                                break;
                            default:
                                diff = 0; // to appease the compiler that "diff" might not have been initialized
                                throw new IllegalArgumentException("Wrong mode");
                        }

                        if (Math.abs(diff) < Math.abs(minDiff)) { // if it's smaller than the current minimum
                            minDiff = diff; // make it the new minimum
                            Point bufferBufferPoint = new Point(currentPoint);
                            bufferBufferPoint.move(bufferVector);
                            int[] coords = {bufferBufferPoint.getX(),bufferBufferPoint.getY()};
                            outputTrajectory.set(step, coords, bufferVector, bufferFrame); // set the values for the current step in the output trajectory
                            // if this is the first iteration of a step, the method will add the values instead
                        }
                    } catch (IndexOutOfBoundsException e) { // this will be thrown if the currently compared buffer frame gets out of bounds
                        //e.printStackTrace();
                        System.out.println(e + ". Przechodzę do kolejnej iteracji. (Navigator)");
                        continue; // the algorithm will check another direction in the next iteration and it'll eventually find an in-bounds best fit
                    }

                    iterations.set(step, iterations.get(step)+1);
                    if (vel == 0) break; // no need to check more than one direction for zero velocity
                    if (diff == 0.0) break currentFrame; // won't get better than that
                }
            }
            currentPoint.move(outputTrajectory.getVectorArrayList().get(step)); // move to the next point


            // ### DEBUG ###
            if (debugMode) {
                System.out.println(outputTrajectory.getCoordArrayList().get(step)[0] + "\t" + inputTrajectory[0].getCoordArrayList().get(step)[0]);
                System.out.println(outputTrajectory.getCoordArrayList().get(step)[1] + "\t" + inputTrajectory[0].getCoordArrayList().get(step)[1]);

                System.out.println(outputTrajectory.getCoordArrayList().get(step)[0] != inputTrajectory[0].getCoordArrayList().get(step)[0]);
                System.out.println(outputTrajectory.getCoordArrayList().get(step)[1] != inputTrajectory[0].getCoordArrayList().get(step)[1]);
                if (outputTrajectory.getCoordArrayList().get(step)[0] != inputTrajectory[0].getCoordArrayList().get(step)[0]
                        || outputTrajectory.getCoordArrayList().get(step)[1] != inputTrajectory[0].getCoordArrayList().get(step)[1]) {
                    // this is called when the calculated trajectory starts to deviate from the generated one
                    // then the situation is as follows: currentPoint, currentFrame are equal to equivalent (step-1) inputTrajectory objects
                    // but in the next step, the point will get off the trail

                    if (step == 0) continue; //

                    // print some info
                    System.out.println("\nStep: " + step);
                    System.out.print("TB point (" + (step - 1) + "): " + inputTrajectory[0].getCoordArrayList().get(step - 1)[0] + ", " + inputTrajectory[0].getCoordArrayList().get(step - 1)[1]);
                    System.out.println("\tNavigator point (" + (step - 1) + "): " + outputTrajectory.getCoordArrayList().get(step - 1)[0] + ", " + outputTrajectory.getCoordArrayList().get(step - 1)[1]);
                    System.out.println("Current calculated vector: " + outputTrajectory.getVectorArrayList().get(step));
                    System.out.print("TB point (" + step + "): " + inputTrajectory[0].getCoordArrayList().get(step)[0] + ", " + inputTrajectory[0].getCoordArrayList().get(step)[1]);
                    System.out.println("\tNavigator point (" + step + "): " + outputTrajectory.getCoordArrayList().get(step)[0] + ", " + outputTrajectory.getCoordArrayList().get(step)[1]);
                    System.out.println("TB point (" + (step + 1 + "): " + inputTrajectory[0].getCoordArrayList().get(step + 1)[0]) + ", " + inputTrajectory[0].getCoordArrayList().get(step + 1)[1]);

                    System.out.println("\nOverview (TB frame step-1):\n" + inputTrajectory[0].getMapFrameArrayList().get(step - 1));
                    //System.out.println("\nOverview (map subframe):\n" + new MapFrame(debugMap, frames.get(0).getSize(), currentPoint));
                    System.out.println("\nOverview (TB frame step):\n" + inputTrajectory[0].getMapFrameArrayList().get(step));
                    System.out.println("\nTB frame (" + (step - 1) + "):\n" + inputTrajectory[0].getMapFrameArrayList().get(step - 1).getSubFrame(new Point(centerCoord, centerCoord), subframeSize));
                    System.out.println("\nNavigator frame (" + (step - 1) + "):\n" + outputTrajectory.getMapFrameArrayList().get(step - 1));

                    System.out.println("\nTB frame (" + step + "):\n" + inputTrajectory[0].getMapFrameArrayList().get(step).getSubFrame(new Point(centerCoord, centerCoord), subframeSize));
                    System.out.println("\nNavigator frame (" + step + "):\n" + outputTrajectory.getMapFrameArrayList().get(step));

                    System.out.println("\nOther possibilities:");

                    currentFrameDebug:
                    for (int vel = 0; vel <= maxVelocity; vel++) {   // try a range of velocities, including 0
                        for (int dir = 0; dir < Direction.values().length; dir++) { // for each velocity, try every direction
                            try {
                                // create buffer objects to move around and compare
                                Point bufferPoint = new Point(centerCoord, centerCoord);
                                MapFrame targetFrameDebug = frames.get(step).getSubFrame(bufferPoint, subframeSize);
                                DirVector bufferVector = new DirVector(Direction.values()[dir], vel);
                                bufferPoint.move(bufferVector);
                                MapFrame bufferFrame = frames.get(step - 1).getSubFrame(bufferPoint, subframeSize);
                                // when the point turns, so do the frames taken by it
                                bufferFrame.turnAnticlockwise(DirVector.howManyTurns(new DirVector(Direction.NORTH), bufferVector));

                                //diff = MapFrame.squaresDifference(frames.get(step), bufferFrame); // find the squares difference
                                //diff = MapFrame.linearDifference(frames.get(step), bufferFrame);
                                diff = MapFrame.maxValueDifference(targetFrameDebug, bufferFrame);

                                System.out.println("\nPoint: " + bufferPoint + "; vector: " + bufferVector + "; delta: " + (bufferPoint.getX() - centerCoord) + ", " + (bufferPoint.getY() - centerCoord) + "; diff: " + diff);
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
                }
            }
        }
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