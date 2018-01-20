package wolak.jakub.uniop;

public class TrajectoryBuilder {
    private Trajectory outputTrajectory;

    TrajectoryBuilder(Map map, int steps, Point startPoint, DirVector startVector, MapFrame frame, double dirChangeProbability, double... velocityChangeProbability) {
        this.outputTrajectory = new Trajectory(steps); // trajectory of a fixed maximum size

        // input check:
        // probability of a change of the velocity
        double probSum = 0;
        for (double p : velocityChangeProbability) {
            if (p < 0) throw new IllegalArgumentException("Prawdopodobieństwo nie może być ujemne");
            probSum += p;
        }
        if (probSum != 1) normalize(velocityChangeProbability);
            //throw new IllegalArgumentException("Prawdopodobieństwa muszą sumować się do jedności");

        // probability of a change of the direction
        if (dirChangeProbability > 1 || dirChangeProbability < 0)
            throw new IllegalArgumentException("Prawdopodobieństwo musi mieścić się w przedziale 0-1"); // todo this might also be normalized

        // is the point not out of bounds
        if (!(startPoint.isInMapBounds(map))) throw new IllegalArgumentException("Błędne współrzędne punktu startowego");

        // is the initial vector realizable
        if (!(startPoint.canMove(map, startVector, frame))) throw new IllegalArgumentException("Błędny wektor początkowy");

        // if everything is okay so far, create a point and a vector to move around, so as not to change the initial ones passed in arguments
        Point currentPoint = new Point(startPoint);
        DirVector currentVector = new DirVector(startVector);

        for (int step = 0; step < steps; step++) {
            // update the output arrays
            int[] coords = new int[2];
            coords[0] = currentPoint.getX();
            coords[1] = currentPoint.getY();
            outputTrajectory.add(coords);

            outputTrajectory.add(new DirVector(currentVector));

            // try to prevent the point going off the map
            // we will try to mitigate that by turning the vector clockwise if the point were to go off the map
            final int maxTries = 3; // that's why if the 3rd turn has not succeeded, we should break the loop (4th would be a 360* turn)
            for (int count = 0; count < maxTries; count++) {
                if (currentPoint.canMove(map, currentVector, frame)) {
                    MapFrame frameToAdd = new MapFrame(map, frame.getSize(), currentPoint);
                    // when the point turns, so should the frames; the map is oriented to the North by default
                    // important: when the point (and the camera) turns clockwise, "the world" in relation turns anticlockwise
                    frameToAdd.turnAnticlockwise(DirVector.howManyTurns(new DirVector(Direction.NORTH), currentVector));
                    outputTrajectory.add(frameToAdd);
                    currentPoint.move(currentVector);
                    break; // if everything is okay, break the loop
                } else {
                    System.out.println("step: " + step + ", old vector: " + currentVector);
                    currentVector.turnClockwise();
                    System.out.println("turning clockwise (" + count + "); new vector: " + currentVector);
                }
                if (count == maxTries) throw new IndexOutOfBoundsException("Nie udało się zapobiec wyjściu punktu poza mapę");
            }

            double p = Math.random();
            // will the direction change?
            if (dirChangeProbability > p) {
                Direction newDir;
                do {
                    newDir = currentVector.randomDir();
                } while (newDir == currentVector.getDir()          /* keep picking if the direction is the same as it was before */
                        || newDir == currentVector.oppositeDir(currentVector.getDir())); // or if the direction is opposite (i.e. don't go back and forth)
                currentVector.setDir(newDir);
            }

            p = Math.random();
            // will the velocity change?
            for (int pIndex = 0; pIndex < velocityChangeProbability.length; pIndex++) {
                if (velocityChangeProbability[pIndex] > p) currentVector.setValue(pIndex + 1); // +1 due to zero-indexing - we don't want v=0
                // e.g. the args are 0.85, 0.1, 0.05 -> 85% chance that the velocity will be 1, 10% chance for 2, 5% chance for 3
            }
        }
    }

    // this method normalizes the provided probabilities so that they are all between 0 and 1
    private double[] normalize(double[] p) {
        double sum = 0;
        double[] output = new double[p.length];
        for (double i : p) {
            sum += i;
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = p[i]/sum;
        }
        return output;
    }

    public Trajectory getOutputTrajectory() {
        return outputTrajectory;
    }

    @Override
    public String toString() {
        return outputTrajectory.toString();
    }
}