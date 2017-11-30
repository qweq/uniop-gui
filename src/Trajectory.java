public class Trajectory {
    private int[][] trajectory;
    private int stepsCount;
    private double probOneStep;
    private double probTwoSteps;
    private double probThreeSteps;
    private Point pointer;

    Trajectory(Map map, int steps, Point startPoint, int frameReach, double prob1, double prob2, double prob3) {
        this.trajectory = new int[steps][2]; // "steps" rows of [x, y]

        // set default values
        this.probOneStep = prob1;
        this.probTwoSteps = prob2;
        this.probThreeSteps = prob3;
        // normalize if the user has provided probabilities that do not add up to 1
        if (this.probOneStep + this.probTwoSteps + this.probThreeSteps != 1) {
            prob1 /= this.probOneStep + this.probTwoSteps + this.probThreeSteps;
            prob2 /= this.probOneStep + this.probTwoSteps + this.probThreeSteps;
            prob3 /= this.probOneStep + this.probTwoSteps + this.probThreeSteps;
        }
        // todo what if the user provides a negative probability?

        // set first value in trajectory array
        trajectory[0][0] = startPoint.getX();
        trajectory[0][1] = startPoint.getY();

        // create a pointer that we will move around
        pointer = new Point(startPoint.getX(), startPoint.getY());

        // choose an initial direction
        DirVector initialDir = new DirVector();
        do {
            initialDir.setDir(initialDir.chooseDir()); // choose an initial direction at random
            System.out.println(pointer.canMove(map, initialDir.getDir(), frameReach));
        } while (!(pointer.canMove(map, initialDir.getDir(), frameReach))); // if the pointer were out of bounds, choose another

        // generate the trajectory step-by-step
        double p = Math.random(); // this will be used for probability
        DirVector dir = new DirVector(); // this will be used so as not to step back in the same direction

        for (int i = 1; i < steps; i++) {
            //if (p >= this.probOneStep) {
                do {
                    dir.setDir(dir.chooseDir());
                } while (dir.getDir() == initialDir.oppositeDir(initialDir.getDir()) || !(pointer.canMove(map, dir.getDir(), frameReach)));
                // e.g. if the pointer went west, don't let it go east again and don't let it wander off the map
                // it would probably be good to implement some better trajectory-making algorithm, but for now it does the job
                pointer.move(dir.getDir());
                initialDir.setDir(dir.getDir());
                trajectory[i][0] = pointer.getX();
                trajectory[i][1] = pointer.getY();
            //}
        }

        for (int i = 0; i < trajectory.length; i++) {
            System.out.println(trajectory[i][0] + "\t" + trajectory[i][1]);
        }
    }

    Trajectory(Map map, int steps, Point startPoint, int frameReach) {
        this(map, steps, startPoint, frameReach, 0.8, 0.15, 0.5);
    }

    Trajectory(Map map, int steps, Point startPoint) {
        this(map, steps, startPoint, 0);
    }


    private class DirVector {
        Direction dir;

        DirVector() {
            dir = null;
        }

        DirVector(Direction dir) {
            this.dir = dir;
        }

        public Direction oppositeDir(Direction dir) {
            if (dir == Direction.NORTH) return Direction.SOUTH;
            if (dir == Direction.SOUTH) return Direction.NORTH;
            if (dir == Direction.EAST) return Direction.WEST;
            if (dir == Direction.WEST) return Direction.EAST;
            else return null;
        }

        public Direction chooseDir() {
            double p = Math.random();
            if (p >= 0 && p < 0.25) return Direction.NORTH;
            if (p >= 0.25 && p < 0.5) return Direction.EAST;
            if (p >= 0.5 && p < 0.75) return Direction.SOUTH;
            else return Direction.WEST;
        }

        public Direction getDir() {
            return dir;
        }

        public void setDir(Direction dir) {
            this.dir = dir;
        }

        public String toString() {
            return "Direction is " + dir;
        }
    }
}
