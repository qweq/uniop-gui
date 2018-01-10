/* TODO as the vehicle turns, so should the frames
   TODO navigator should then try and turn each frame left and right to see if it fits better

   TODO perhaps make a low-contrast (practically already implemented) and high-contrast low-level-count map

   TODO improve the visualisation

   TODO allow the user to choose frame size (remember that the size should be odd-numbered)

   TODO ideas:
   TODO     - a slider that highlights the current step + a pane to show the frame
 */

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;

public class Main extends Application {
    // fields regarding navigation
    private Map map;
    private Point point;
    private MapFrame frame;
    private DirVector vector;
    private TrajectoryBuilder tb;
    private Navigator navigator;

    // fields regarding GUI
    private GraphicsContext gc;
    private int pixelSize = 4;

    // default values
    // map size & intensity levels
    private static final int DEFAULT_MAP_XDIM = 150;
    private static final int DEFAULT_MAP_YDIM = 150;
    private static final int DEFAULT_MAP_MAX_INTENSITY_LEVEL = 255;
    // start point coordinates
    private static final int DEFAULT_START_POINT_X = 50;
    private static final int DEFAULT_START_POINT_Y = 50;
    // frame size
    private static final int DEFAULT_FRAME_SIZE = 7;
    // trajectory properties
    private static final int DEFAULT_STEPS_NUMBER = 158;
    private static final double DEFAULT_DIR_CHANGE_PROBABILITY = 0.08;
    private static final double[] DEFAULT_VELOCITY_CHANGE_PROBABILITY = {0.7, 0.2, 0.05, 0.05};
    // max tries (for exception handling)
    private static final int MAX_TRIES = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        // initialize with default values
        map = new Map(DEFAULT_MAP_XDIM, DEFAULT_MAP_YDIM, DEFAULT_MAP_MAX_INTENSITY_LEVEL);
        point = new Point(DEFAULT_START_POINT_X, DEFAULT_START_POINT_Y);
        frame = new MapFrame(map, DEFAULT_FRAME_SIZE, point);
        vector = new DirVector();
        tb = new TrajectoryBuilder(map, DEFAULT_STEPS_NUMBER, point, vector, frame, DEFAULT_DIR_CHANGE_PROBABILITY, DEFAULT_VELOCITY_CHANGE_PROBABILITY);
        navigator = new Navigator(map, tb.getOutputTrajectory().getMapFrameArrayList(), point);
        /*
        // console version legacy code
        System.out.println(map + "\n");
        System.out.println(frame);
        System.out.println(vector);
        System.out.println(tb);
        System.out.println(navigator);
        System.out.println(map.toStringWithTrajectory(tb.getOutputTrajectory()));
        System.out.println(map.toStringWithTrajectory(navigator.getResultTrajectory()));
        */
    }

    // create the GUI
    @Override
    public void start(Stage stage) {
        stage.setTitle("UNiOP - Nawigacja wizyjna");

        // set bounds
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(0.8*primaryScreenBounds.getWidth());
        stage.setHeight(0.8*primaryScreenBounds.getHeight());

        // set a root node
        GridPane rootNode = new GridPane();
        rootNode.setAlignment(Pos.CENTER_LEFT);
        // set gaps around the child elements and padding
        rootNode.setHgap(10);
        rootNode.setVgap(10);
        rootNode.setPadding(new Insets(20, 20, 20, 20));

        // set a grid layout - the visualization will be on the left; the options on the right
        FlowPane visNode = new FlowPane();
        visNode.setAlignment(Pos.TOP_LEFT); // todo check the behaviour
        visNode.setHgap(10);
        visNode.setVgap(10);

        GridPane optionNode = new GridPane();
        optionNode.setAlignment(Pos.TOP_RIGHT);
        optionNode.setHgap(10);
        optionNode.setVgap(10);

        // place the panes
        GridPane.setConstraints(visNode, 0, 0);
        GridPane.setConstraints(optionNode, 2, 0);
        // set the separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        // add the panes and the separator
        GridPane.setConstraints(separator, 1, 0);
        rootNode.getChildren().addAll(visNode, separator, optionNode);

        // set the scene
        Scene scene = new Scene(rootNode,map.getXDim()*pixelSize+10, map.getYDim()*pixelSize+10);
        stage.setScene(scene);

        // set the canvas
        Canvas canvas = new Canvas(map.getXDim()*pixelSize, map.getYDim()*pixelSize);
        gc = canvas.getGraphicsContext2D();

        // set the scroll pane
        ScrollPane sp = new ScrollPane(canvas);
        sp.setPannable(true);
        // set the viewport, i.e. how much of the map is visible at once
        sp.setPrefViewportHeight(0.8*stage.getHeight());
        sp.setPrefViewportWidth(0.8*stage.getHeight()); // we want a square viewport

        // paint the map
        paintMap(map, gc);

        // paint the trajectory a different colour
        // the x coordinate is stored in [0] and the y coordinate in [1] column of the coordArrayList
        paintTrajectory(tb.getOutputTrajectory(), gc, Color.RED);

        // paint the computed trajectory a different colour
        paintTrajectory(navigator.getResultTrajectory(), gc, Color.GREEN);

        visNode.getChildren().addAll(sp, canvas);

        // set up the option pane
        int rowCounter = 0; // the controls will be displayed in the order of declaring
        // map dimensions
        Label mapDimensionsLbl = new Label("Map dimensions:");
        TextField mapXtf = new TextField();
        TextField mapYtf = new TextField();
        mapXtf.setPromptText("Width");
        mapYtf.setPromptText("Height");
        GridPane.setConstraints(mapDimensionsLbl, 0, rowCounter);
        GridPane.setConstraints(mapXtf, 1, rowCounter);
        GridPane.setConstraints(mapYtf, 2, rowCounter);
        optionNode.getChildren().addAll(mapDimensionsLbl, mapXtf, mapYtf);
        rowCounter++;

        // intensity levels
        Label mapIntensityLbl = new Label("Intensity levels:");
        Slider mapIntensitySlider = new Slider(2, 256, 256);
        mapIntensitySlider.setShowTickLabels(true);
        mapIntensitySlider.setShowTickMarks(true);
        GridPane.setConstraints(mapIntensityLbl, 0, rowCounter);
        GridPane.setConstraints(mapIntensitySlider, 1, rowCounter, 2, 1);
        optionNode.getChildren().addAll(mapIntensityLbl, mapIntensitySlider);
        rowCounter++;

        // generate map button
        Button generateMapBtn = new Button("Generate a new map");
        generateMapBtn.setOnAction(event -> {
            Thread t = new Thread(() -> { // todo check how threads actually work; for now - do not generate large maps
                try {
                    map = new Map(Integer.parseInt(mapXtf.getText()), Integer.parseInt(mapYtf.getText()), (int) mapIntensitySlider.getValue() - 1);
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    canvas.setWidth(map.getXDim() * pixelSize);
                    canvas.setHeight(map.getYDim() * pixelSize);
                    paintMap(map, gc);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong input");
                    alert.showAndWait();
                }
            });
            t.run();
        });
        GridPane.setConstraints(generateMapBtn, 0, rowCounter);

        // default values button
        Button mapDefaultValuesBtn = new Button("Default values");
        mapDefaultValuesBtn.setOnAction(event -> {
            mapXtf.setText(String.valueOf(DEFAULT_MAP_XDIM));
            mapYtf.setText(String.valueOf(DEFAULT_MAP_YDIM));
            mapIntensitySlider.setValue(DEFAULT_MAP_MAX_INTENSITY_LEVEL+1); // +1 due to zero-indexing
        });
        GridPane.setConstraints(mapDefaultValuesBtn, 1, rowCounter);

        optionNode.getChildren().addAll(generateMapBtn, mapDefaultValuesBtn);
        rowCounter++;

        Separator mapTrajectorySeparator = new Separator();
        optionNode.getChildren().add(mapTrajectorySeparator);
        GridPane.setConstraints(mapTrajectorySeparator, 0, rowCounter, 3, 1);
        rowCounter++;

        // trajectory properties
        Label startPointLbl = new Label("Start point:");
        TextField startPointXtf = new TextField();
        TextField startPointYtf = new TextField();
        startPointXtf.setPromptText("x");
        startPointYtf.setPromptText("y");

        Label stepsNumberLbl = new Label("Number of steps:");
        TextField stepsNumberTF = new TextField();
        Label dirChangeProbLbl = new Label("Probability of a change\nof the direction:");
        TextField dirChangeProbTF = new TextField();
        Label velChangeProbLbl = new Label("Probability of a change\nof the velocity:");
        // todo ####################################
        TextField velChangeProbTF = new TextField();
        velChangeProbTF.setPromptText("TBD");
        velChangeProbTF.setEditable(false);
        // todo ####################################
        GridPane.setConstraints(startPointLbl, 0, rowCounter);
        GridPane.setConstraints(startPointXtf, 1, rowCounter);
        GridPane.setConstraints(startPointYtf, 2, rowCounter);
        rowCounter++;
        GridPane.setConstraints(stepsNumberLbl, 0, rowCounter);
        GridPane.setConstraints(stepsNumberTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(dirChangeProbLbl, 0, rowCounter);
        GridPane.setConstraints(dirChangeProbTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(velChangeProbLbl, 0, rowCounter);
        GridPane.setConstraints(velChangeProbTF, 1, rowCounter);
        rowCounter++;
        optionNode.getChildren().addAll(startPointLbl, startPointXtf, startPointYtf,
                stepsNumberLbl, stepsNumberTF, dirChangeProbLbl, dirChangeProbTF,
                velChangeProbLbl, velChangeProbTF);

        // generate trajectory button
        Button generateTrajectoryBtn = new Button("Generate a new trajectory");
        generateTrajectoryBtn.setOnAction(event -> {
            try {
                // todo frame, vector
                point = new Point(Integer.parseInt(startPointXtf.getText()), Integer.parseInt(startPointYtf.getText()));
                tb = new TrajectoryBuilder(map, Integer.parseInt(stepsNumberTF.getText()), point,
                        vector, frame, Double.parseDouble(dirChangeProbTF.getText()), DEFAULT_VELOCITY_CHANGE_PROBABILITY);
                paintMap(map, gc);
                paintTrajectory(tb.getOutputTrajectory(), gc, Color.RED);
                sp.setHvalue((double)point.getX()/(double)map.getXDim());
                sp.setVvalue((double)point.getY()/(double)map.getYDim());
            } catch (NumberFormatException e) {
                // todo find out a way to know which textfield input throws the exception, then set focus on it
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong input");
                alert.showAndWait();
            } catch (IndexOutOfBoundsException oob) { // this shouldn't happen as the algorithm should avoid walls, but still...
                generateTrajectoryBtn.fire();
            } catch (IllegalArgumentException ia) {
                startPointXtf.requestFocus();
                Alert alert = new Alert(Alert.AlertType.ERROR, "The start point coordinates exceed map dimensions");
                alert.showAndWait();
            }
        }); // todo
        GridPane.setConstraints(generateTrajectoryBtn, 0, rowCounter);

        // trajectory default values button
        Button trajectoryDefaultValuesBtn = new Button("Default values");
        trajectoryDefaultValuesBtn.setOnAction(event -> {
            startPointXtf.setText(String.valueOf(DEFAULT_START_POINT_X));
            startPointYtf.setText(String.valueOf(DEFAULT_START_POINT_Y));
            stepsNumberTF.setText(String.valueOf(DEFAULT_STEPS_NUMBER));
            dirChangeProbTF.setText(String.valueOf(DEFAULT_DIR_CHANGE_PROBABILITY));
        });
        GridPane.setConstraints(trajectoryDefaultValuesBtn, 1, rowCounter);

        optionNode.getChildren().addAll(generateTrajectoryBtn, trajectoryDefaultValuesBtn);
        rowCounter++;

        Separator trajectoryNavigatorSeparator = new Separator();
        GridPane.setConstraints(trajectoryNavigatorSeparator, 0, rowCounter, 3, 1);
        optionNode.getChildren().add(trajectoryNavigatorSeparator);
        rowCounter++;

        // navigator properties
        Label maxPotentialVelocityLbl = new Label("Maximal potential velocity:");
        TextField maxPotentialVelocityTF = new TextField();
        // navigator button
        Button navigateBtn = new Button("Navigate");
        navigateBtn.setOnAction(event -> {
            navigator = new Navigator(map, tb.getOutputTrajectory().getMapFrameArrayList(), point, Integer.parseInt(maxPotentialVelocityTF.getText()));
            paintTrajectory(navigator.getResultTrajectory(), gc, Color.GREEN);
        });
        GridPane.setConstraints(maxPotentialVelocityLbl, 0, rowCounter);
        GridPane.setConstraints(maxPotentialVelocityTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(navigateBtn, 0, rowCounter, 3, 1);
        optionNode.getChildren().addAll(maxPotentialVelocityLbl, maxPotentialVelocityTF, navigateBtn);
        rowCounter++;

        Separator navigatorResultSeparator = new Separator();
        GridPane.setConstraints(navigatorResultSeparator, 0, rowCounter, 3, 1);
        optionNode.getChildren().add(navigatorResultSeparator);
        rowCounter++;

        /*
        // debug
        Label xLbl = new Label("scrollPane hValue = " + String.valueOf(sp.getHvalue()));
        Label yLbl = new Label("scrollPane vValue = " + String.valueOf(sp.getVvalue()));
        GridPane.setConstraints(xLbl, 1, rowCounter);
        GridPane.setConstraints(yLbl, 2, rowCounter);
        optionNode.getChildren().addAll(xLbl, yLbl);
        sp.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            xLbl.setText("scrollPane hValue = " + String.valueOf(newValue));
        });
        sp.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            yLbl.setText("scrollPane vValue = " + String.valueOf(newValue));
        });
        rowCounter++;
        // end debug
        */

        stage.show();
    }

    private void paintMap(Map map, GraphicsContext gc) {
        for (int rows = 0; rows < map.getYDim(); rows++) {
            for (int cols = 0; cols < map.getXDim(); cols++) {
                Color cellColor = Color.rgb(map.cellValue(cols, rows), map.cellValue(cols, rows), map.cellValue(cols, rows));
                gc.setStroke(cellColor);
                gc.setFill(cellColor);
                gc.fillRect(cols * pixelSize, rows * pixelSize, pixelSize, pixelSize);
            }
        }
    }

    private void paintTrajectory(Trajectory t, GraphicsContext gc, Color color) {
        for (int steps = 0; steps < t.getCoordArrayList().size(); steps++) {
            gc.setStroke(color);
            gc.setFill(color);
            gc.fillRect(t.getCoordArrayList().get(steps)[0]*pixelSize, t.getCoordArrayList().get(steps)[1]*pixelSize, pixelSize, pixelSize);
        }
    }
}