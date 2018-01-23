package wolak.jakub.uniop;

import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class Main extends Application {
    // fields regarding navigation
    private Map map;
    private Point point;
    private MapFrame frame;
    private DirVector vector;
    private TrajectoryBuilder tb;
    private Navigator navigator;

    // fields regarding GUI
    private final int pixelSize = 4;
    private final int framePixelSize = 12;

    // GUI controls
    // main
    private GridPane rootNode;
    private FlowPane visNode;
    private GridPane optionNode;
    private Separator separator;
    // visualisation pane
    private Canvas visCanvas;
    private GraphicsContext visGC;
    private ScrollPane visSP;
    // option pane
    // map generation
    private Label mapDimensionsLbl;
    private TextField mapXtf;
    private TextField mapYtf;
    private Label mapMethodLbl;
    private ComboBox<String> mapMethodCB;
    private Label mapIntensityLbl;
    private Slider mapIntensitySlider;
    private Button generateMapBtn;
    private Button mapDefaultValuesBtn;
    private Separator mapTrajectorySeparator;
    // trajectory generation
    private Label startPointLbl;
    private TextField startPointXtf;
    private TextField startPointYtf;
    private Label stepsNumberLbl;
    private TextField stepsNumberTF;
    private Label subFrameSizeLbl;
    private TextField subFrameSizeTF;
    private Label dirChangeProbLbl;
    private TextField dirChangeProbTF;
    private Label velChangeProbLbl;
    private TextField velChangeProbTF;
    private Label velChangeProbInfoLbl;
    private Button generateTrajectoryBtn;
    private Button trajectoryDefaultValuesBtn;
    private Separator trajectoryNavigatorSeparator;
    // navigator
    private Label maxPotentialVelocityLbl;
    private TextField maxPotentialVelocityTF;
    private Label noiseLbl;
    private TextField noiseTF;
    private Label navigatorAlgorithmLbl;
    private ComboBox<String> navigatorAlgorithmCB;
    private CheckBox debugModeChk;
    private Button navigateBtn;
    private Button navigatorDefaultValuesBtn;
    private Separator navigatorResultSeparator;
    // frame view
    private Label stepsSliderLbl;
    private Slider stepsSlider;
    private Canvas frameCanvas;
    private GraphicsContext frameViewGC;
    private ScrollPane frameViewSP;
    // results
    private GridPane resultNode;
    private RadioButton trajectoryViewRB;
    private RadioButton navigatorViewRB;
    private ToggleGroup resultViewToggleGroup;
    private Label iterationsLbl;
    //private Label squaresDifferenceLbl;


    // default values
    // map size & intensity levels
    private static final int DEFAULT_MAP_XDIM = 200;
    private static final int DEFAULT_MAP_YDIM = 200;
    private static final int DEFAULT_MAP_MAX_INTENSITY_LEVEL = 255;
    // start point coordinates
    private static final int DEFAULT_START_POINT_X = 100;
    private static final int DEFAULT_START_POINT_Y = 100;
    // trajectory properties
    private static final int DEFAULT_STEPS_NUMBER = 150;
    private static final double DEFAULT_DIR_CHANGE_PROBABILITY = 0.08;
    private static final double[] DEFAULT_VELOCITY_CHANGE_PROBABILITY = {0.7, 0.2, 0.05, 0.05};
    private static final int DEFAULT_MAX_VELOCITY = 4;
    private static final DiffAlgorithm DEFAULT_ALGORITHM = DiffAlgorithm.SQ_DIFF;
    // frame size
    private static final int DEFAULT_SUBFRAME_SIZE = 5;
    private static final int DEFAULT_FRAME_SPAN = (DEFAULT_SUBFRAME_SIZE-1)/2; // from the center to the edge
    private static final int DEFAULT_FRAME_SIZE = DEFAULT_SUBFRAME_SIZE + 2*(DEFAULT_VELOCITY_CHANGE_PROBABILITY.length+DEFAULT_FRAME_SPAN+1);
    // max tries (for exception handling)
    private static final int MAX_TRIES = 10;
    // colors
    private static final Color TRAJECTORY_COLOR = Color.RED;
    private static final Color TRAJECTORY_POINT_COLOR = Color.YELLOW;
    private static final Color NAVIGATOR_COLOR = Color.LIME;
    private static final Color NAVIGATOR_POINT_COLOR = Color.DARKGREEN;

    private static int tries = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        // initialize with default values
        try {
            map = new Map(DEFAULT_MAP_XDIM, DEFAULT_MAP_YDIM, DEFAULT_MAP_MAX_INTENSITY_LEVEL);
            point = new Point(DEFAULT_START_POINT_X, DEFAULT_START_POINT_Y);
            frame = new MapFrame(map, DEFAULT_FRAME_SIZE, point);
            vector = new DirVector();
            tb = new TrajectoryBuilder(map, DEFAULT_STEPS_NUMBER, point, vector, frame, DEFAULT_DIR_CHANGE_PROBABILITY, DEFAULT_VELOCITY_CHANGE_PROBABILITY);
            navigator = new Navigator(tb.getOutputTrajectory().getMapFrameArrayList(), point, vector, DEFAULT_MAX_VELOCITY,
                    DEFAULT_SUBFRAME_SIZE, DEFAULT_ALGORITHM, false, tb.getOutputTrajectory());
        } catch (IndexOutOfBoundsException oob) {
            // if a trajectory is out of bounds, try another
            ++tries;
            System.out.println("try: " + tries + "; initializing again");
            if (tries > MAX_TRIES) {
                oob.printStackTrace();
                throw new IndexOutOfBoundsException("Nie udało się wygenerować poprawnej trajektorii.");
            }
            init();
        }
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
        stage.setHeight(0.9*primaryScreenBounds.getHeight());

        // disable maximizing
        stage.resizableProperty().setValue(Boolean.FALSE);

        // set a root node
        rootNode = new GridPane();
        rootNode.setAlignment(Pos.TOP_LEFT);
        // set gaps around the child elements and padding
        rootNode.setHgap(10);
        rootNode.setVgap(10);
        rootNode.setPadding(new Insets(20, 20, 20, 20));

        // set a grid layout - the visualization will be on the left; the options on the right
        visNode = new FlowPane();
        visNode.setAlignment(Pos.TOP_LEFT); // todo check the behaviour
        visNode.setHgap(10);
        visNode.setVgap(10);

        optionNode = new GridPane();
        optionNode.setAlignment(Pos.TOP_RIGHT);
        optionNode.setHgap(10);
        optionNode.setVgap(10);

        // place the panes
        GridPane.setConstraints(visNode, 0, 0);
        GridPane.setConstraints(optionNode, 2, 0);
        // set the separator
        separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        // add the panes and the separator
        GridPane.setConstraints(separator, 1, 0);
        rootNode.getChildren().addAll(visNode, separator, optionNode);

        // set the scene
        Scene scene = new Scene(rootNode,map.getXDim()*pixelSize+10, map.getYDim()*pixelSize+10);
        stage.setScene(scene);

        // set the canvas
        visCanvas = new Canvas(map.getXDim()*pixelSize, map.getYDim()*pixelSize);
        visGC = visCanvas.getGraphicsContext2D();

        // set the scroll pane
        visSP = new ScrollPane(visCanvas);
        visSP.setPannable(true);
        // set the viewport, i.e. how much of the map is visible at once
        visSP.setPrefViewportHeight(0.8*stage.getHeight());
        visSP.setPrefViewportWidth(0.8*stage.getHeight()); // we want a square viewport

        // paint the map
        paintMap(map, visGC);

        // paint the trajectory a different colour
        // the x coordinate is stored in [0] and the y coordinate in [1] column of the coordArrayList
        paintTrajectory(tb.getOutputTrajectory(), visGC, TRAJECTORY_COLOR);

        // paint the computed trajectory a different colour
        paintTrajectory(navigator.getResultTrajectory(), visGC, NAVIGATOR_COLOR);

        visNode.getChildren().addAll(visSP, visCanvas);

        // set up the option pane
        int rowCounter = 0; // the controls will be displayed in the order of declaring
        // map dimensions
        mapDimensionsLbl = new Label("Map dimensions:");
        mapXtf = new TextField();
        mapYtf = new TextField();
        mapXtf.setPromptText("Width");
        mapYtf.setPromptText("Height");
        GridPane.setConstraints(mapDimensionsLbl, 0, rowCounter);
        GridPane.setConstraints(mapXtf, 1, rowCounter);
        GridPane.setConstraints(mapYtf, 2, rowCounter);
        optionNode.getChildren().addAll(mapDimensionsLbl, mapXtf, mapYtf);
        rowCounter++;

        // map generation method
        mapMethodLbl = new Label("Generation method:");
        ObservableList<String> generationMethods = FXCollections.observableArrayList("Random", "OpenSimplex");
        mapMethodCB = new ComboBox<>(generationMethods);
        mapMethodCB.setValue("Random");
        GridPane.setConstraints(mapMethodLbl, 0, rowCounter);
        GridPane.setConstraints(mapMethodCB, 1, rowCounter);
        optionNode.getChildren().addAll(mapMethodLbl, mapMethodCB);
        rowCounter++;

        // intensity levels
        mapIntensityLbl = new Label("Intensity levels:");
        mapIntensitySlider = new Slider(2, 256, 256);
        mapIntensitySlider.setShowTickLabels(true);
        mapIntensitySlider.setShowTickMarks(true);
        GridPane.setConstraints(mapIntensityLbl, 0, rowCounter);
        GridPane.setConstraints(mapIntensitySlider, 1, rowCounter, 2, 1);
        optionNode.getChildren().addAll(mapIntensityLbl, mapIntensitySlider);
        rowCounter++;

        // generate map button
        generateMapBtn = new Button("Generate a new map");
        generateMapBtn.setOnAction(event -> {
            Thread t = new Thread(() -> { // todo check how threads actually work; for now - do not generate large maps
                // todo look into Task<Void>
                try {
                    if (mapMethodCB.getValue().equals("Random"))
                        map = new Map(Integer.parseInt(mapXtf.getText()), Integer.parseInt(mapYtf.getText()), (int) mapIntensitySlider.getValue());
                    else if (mapMethodCB.getValue().equals("OpenSimplex"))
                        map = new Map(Integer.parseInt(mapXtf.getText()), Integer.parseInt(mapYtf.getText()), (int) mapIntensitySlider.getValue(), 1);
                    visGC.clearRect(0, 0, visCanvas.getWidth(), visCanvas.getHeight());
                    visCanvas.setWidth(map.getXDim() * pixelSize);
                    visCanvas.setHeight(map.getYDim() * pixelSize);
                    paintMap(map, visGC);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong input");
                    alert.showAndWait();
                }
            });
            t.run();
        });
        GridPane.setConstraints(generateMapBtn, 0, rowCounter);

        // default values button
        mapDefaultValuesBtn = new Button("Default values");
        mapDefaultValuesBtn.setOnAction(event -> {
            mapXtf.setText(String.valueOf(DEFAULT_MAP_XDIM));
            mapYtf.setText(String.valueOf(DEFAULT_MAP_YDIM));
            mapIntensitySlider.setValue(DEFAULT_MAP_MAX_INTENSITY_LEVEL+1); // +1 due to zero-indexing
        });
        GridPane.setConstraints(mapDefaultValuesBtn, 1, rowCounter);

        optionNode.getChildren().addAll(generateMapBtn, mapDefaultValuesBtn);
        rowCounter++;

        mapTrajectorySeparator = new Separator();
        optionNode.getChildren().add(mapTrajectorySeparator);
        GridPane.setConstraints(mapTrajectorySeparator, 0, rowCounter, 3, 1);
        rowCounter++;

        // trajectory properties
        startPointLbl = new Label("Start point:");
        startPointXtf = new TextField();
        startPointYtf = new TextField();
        startPointXtf.setPromptText("x");
        startPointYtf.setPromptText("y");
        stepsNumberLbl = new Label("Number of steps:");
        stepsNumberTF = new TextField();
        subFrameSizeLbl = new Label("Subframe size:");
        subFrameSizeTF = new TextField();
        dirChangeProbLbl = new Label("Probability of a change\nof the direction:");
        dirChangeProbTF = new TextField();
        velChangeProbLbl = new Label("Probability of a change\nof the velocity:");
        velChangeProbTF = new TextField();
        velChangeProbTF.setPromptText("e.g. 0.9, 0.08, 0.02");
        velChangeProbInfoLbl = new Label("e.g. \"0.9, 0.08, 0.02\":\nthe point will move\n"
        + "1 pixel with p = 90%,\n2 pixels with p = 8%\nand 3 pixels with p = 2%");
        GridPane.setConstraints(startPointLbl, 0, rowCounter);
        GridPane.setConstraints(startPointXtf, 1, rowCounter);
        GridPane.setConstraints(startPointYtf, 2, rowCounter);
        rowCounter++;
        GridPane.setConstraints(stepsNumberLbl, 0, rowCounter);
        GridPane.setConstraints(stepsNumberTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(subFrameSizeLbl, 0, rowCounter);
        GridPane.setConstraints(subFrameSizeTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(dirChangeProbLbl, 0, rowCounter);
        GridPane.setConstraints(dirChangeProbTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(velChangeProbLbl, 0, rowCounter);
        GridPane.setConstraints(velChangeProbTF, 1, rowCounter);
        GridPane.setConstraints(velChangeProbInfoLbl, 2, rowCounter, 1, 2);
        rowCounter++;
        optionNode.getChildren().addAll(startPointLbl, startPointXtf, startPointYtf,
                stepsNumberLbl, stepsNumberTF, subFrameSizeLbl, subFrameSizeTF, dirChangeProbLbl, dirChangeProbTF,
                velChangeProbLbl, velChangeProbTF, velChangeProbInfoLbl);

        // generate trajectory button
        generateTrajectoryBtn = new Button("Generate a new trajectory");
        generateTrajectoryBtn.setOnAction(event -> {
            try {
                point = new Point(Integer.parseInt(startPointXtf.getText()), Integer.parseInt(startPointYtf.getText()));

                // frame size = subframe size + maximal velocity * 2 (because we can go either way) + frame span * 2
                int frameSize = Integer.parseInt(subFrameSizeTF.getText()) + 2*(parseProbability(velChangeProbTF.getText()).length + (Integer.parseInt(subFrameSizeTF.getText())-1)/2 + 1);
                frame = new MapFrame(map, frameSize, point.getX(), point.getY());

                parseProbability(velChangeProbTF.getText());
                tb = new TrajectoryBuilder(map, Integer.parseInt(stepsNumberTF.getText()), point,
                        vector, frame, Double.parseDouble(dirChangeProbTF.getText()), parseProbability(velChangeProbTF.getText()));
                paintMap(map, visGC);
                paintTrajectory(tb.getOutputTrajectory(), visGC, TRAJECTORY_COLOR);
                visSP.setHvalue((double)point.getX()/(double)map.getXDim());
                visSP.setVvalue((double)point.getY()/(double)map.getYDim());
                frameViewGC.clearRect(0, 0, frameCanvas.getWidth(), frameCanvas.getHeight());
                frameCanvas.setWidth(framePixelSize*frameSize);
                frameCanvas.setHeight(framePixelSize*frameSize);

                // todo better
                final int sliderRowCounter = GridPane.getRowIndex(stepsSlider);
                optionNode.getChildren().remove(stepsSlider);
                stepsSlider = new Slider(1, tb.getOutputTrajectory().getMapFrameArrayList().size(), 0);
                stepsSlider.setShowTickLabels(true);
                stepsSlider.setShowTickMarks(true);
                stepsSlider.valueProperty().addListener(new stepsSliderChangeListener());
                GridPane.setConstraints(stepsSlider, 1, sliderRowCounter, 1, 1);
                optionNode.getChildren().add(stepsSlider);
            } catch (NumberFormatException e) {
                // todo find out a way to know which textfield input throws the exception, then set focus on it
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong input");
                alert.showAndWait();
            } catch (IndexOutOfBoundsException oob) { // this shouldn't happen as the algorithm should avoid walls, but still...
                generateTrajectoryBtn.fire();
            } catch (IllegalArgumentException ia) {
                startPointXtf.requestFocus();
                Alert alert = new Alert(Alert.AlertType.ERROR, "The start point coordinates exceed map dimensions or the frame size is not an odd number.");
                alert.showAndWait();
            }
        });
        GridPane.setConstraints(generateTrajectoryBtn, 0, rowCounter);

        // trajectory default values button
        trajectoryDefaultValuesBtn = new Button("Default values");
        trajectoryDefaultValuesBtn.setOnAction(event -> {
            startPointXtf.setText(String.valueOf(DEFAULT_START_POINT_X));
            startPointYtf.setText(String.valueOf(DEFAULT_START_POINT_Y));
            stepsNumberTF.setText(String.valueOf(DEFAULT_STEPS_NUMBER));
            dirChangeProbTF.setText(String.valueOf(DEFAULT_DIR_CHANGE_PROBABILITY));
            subFrameSizeTF.setText(String.valueOf(DEFAULT_SUBFRAME_SIZE));
            velChangeProbTF.setText(parseProbability(DEFAULT_VELOCITY_CHANGE_PROBABILITY));
        });
        GridPane.setConstraints(trajectoryDefaultValuesBtn, 1, rowCounter);

        optionNode.getChildren().addAll(generateTrajectoryBtn, trajectoryDefaultValuesBtn);
        rowCounter++;

        trajectoryNavigatorSeparator = new Separator();
        GridPane.setConstraints(trajectoryNavigatorSeparator, 0, rowCounter, 3, 1);
        optionNode.getChildren().add(trajectoryNavigatorSeparator);
        rowCounter++;

        // navigator properties
        maxPotentialVelocityLbl = new Label("Maximal potential velocity:");
        maxPotentialVelocityTF = new TextField();
        noiseLbl = new Label("Noise:");
        noiseTF = new TextField();
        GridPane.setConstraints(maxPotentialVelocityLbl, 0, rowCounter);
        GridPane.setConstraints(maxPotentialVelocityTF, 1, rowCounter);
        rowCounter++;
        GridPane.setConstraints(noiseLbl, 0, rowCounter);
        GridPane.setConstraints(noiseTF, 1, rowCounter);
        rowCounter++;

        navigatorAlgorithmLbl = new Label("Generation method:");
        ObservableList<String> navigatorAlgorithms = FXCollections.observableArrayList(
                "Least squares difference", "Least linear difference", "Least value difference");
        navigatorAlgorithmCB = new ComboBox<>(navigatorAlgorithms);
        navigatorAlgorithmCB.setValue("Least squares difference");
        GridPane.setConstraints(navigatorAlgorithmLbl, 0, rowCounter);
        GridPane.setConstraints(navigatorAlgorithmCB, 1, rowCounter);
        rowCounter++;

        // debug mode
        debugModeChk = new CheckBox("Debug mode");
        debugModeChk.setSelected(false);
        GridPane.setConstraints(debugModeChk, 2, rowCounter);

        // navigator button
        navigateBtn = new Button("Navigate");
        navigateBtn.setOnAction(event -> {
            try {
                // choose the algorithm with which to compare frames
                DiffAlgorithm mode = DEFAULT_ALGORITHM; // initialization
                if (navigatorAlgorithmCB.getValue().equals("Least squares difference")) mode = DiffAlgorithm.SQ_DIFF;
                else if (navigatorAlgorithmCB.getValue().equals("Least linear difference")) mode = DiffAlgorithm.LIN_DIFF;
                else if (navigatorAlgorithmCB.getValue().equals("Least value difference")) mode = DiffAlgorithm.MAX_VAL_DIFF;

                List<MapFrame> framesWithNoise = Trajectory.addNoise(tb.getOutputTrajectory(), Integer.parseInt(noiseTF.getText())); // include noise

                navigator = new Navigator(framesWithNoise, point, tb.getOutputTrajectory().getVectorArrayList().get(0),
                        Integer.parseInt(maxPotentialVelocityTF.getText()), Integer.parseInt(subFrameSizeTF.getText()),
                        mode, debugModeChk.isSelected(), tb.getOutputTrajectory());
                paintMap(map, visGC);
                paintTrajectory(tb.getOutputTrajectory(), visGC, TRAJECTORY_COLOR);
                paintTrajectory(navigator.getResultTrajectory(), visGC, NAVIGATOR_COLOR);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong input");
                alert.showAndWait();
                maxPotentialVelocityTF.requestFocus();
            }
        });
        GridPane.setConstraints(navigateBtn, 0, rowCounter);

        // navigator default values button
        navigatorDefaultValuesBtn = new Button("Default values");
        navigatorDefaultValuesBtn.setOnAction(event -> {
            maxPotentialVelocityTF.setText(String.valueOf(DEFAULT_VELOCITY_CHANGE_PROBABILITY.length));
            noiseTF.setText("0");
        });
        GridPane.setConstraints(navigatorDefaultValuesBtn, 1, rowCounter);

        optionNode.getChildren().addAll(maxPotentialVelocityLbl, maxPotentialVelocityTF, noiseLbl, noiseTF,
                navigatorAlgorithmLbl, navigatorAlgorithmCB, navigateBtn, navigatorDefaultValuesBtn, debugModeChk);
        rowCounter++;

        navigatorResultSeparator = new Separator();
        GridPane.setConstraints(navigatorResultSeparator, 0, rowCounter, 3, 1);
        optionNode.getChildren().add(navigatorResultSeparator);
        rowCounter++;

        // slider to cycle through trajectory steps
        stepsSliderLbl = new Label("Trajectory step:");
        GridPane.setConstraints(stepsSliderLbl, 0, rowCounter);
        stepsSlider = new Slider(1, navigator.getResultTrajectory().getMapFrameArrayList().size(), 0); // +1 due to zero-indexing
        GridPane.setConstraints(stepsSlider, 1, rowCounter, 1, 1);
        stepsSlider.setShowTickMarks(true);
        stepsSlider.setShowTickLabels(true);
        optionNode.getChildren().addAll(stepsSliderLbl, stepsSlider);

        // EventHandler for the slider
        stepsSlider.valueProperty().addListener(new stepsSliderChangeListener());

        // frame view
        frameCanvas = new Canvas(frame.getSize()*framePixelSize, frame.getSize()*framePixelSize);
        frameViewGC = frameCanvas.getGraphicsContext2D();
        frameViewSP = new ScrollPane(frameCanvas);
        frameViewSP.setPrefViewportWidth(frame.getSize()*framePixelSize);
        frameViewSP.setPrefViewportHeight(frame.getSize()*framePixelSize);
        frameViewSP.setPannable(true);
        GridPane.setConstraints(frameViewSP, 2, rowCounter, 1, 2);
        optionNode.getChildren().add(frameViewSP);
        rowCounter++;

        // results
        // they will be displayed in a new GridPane
        resultNode = new GridPane();
        resultNode.setAlignment(Pos.TOP_LEFT);
        resultNode.setHgap(10);
        resultNode.setVgap(5);
        GridPane.setConstraints(resultNode, 0, rowCounter, 3, 1);
        optionNode.getChildren().add(resultNode);

        // toggle trajectory/navigator frame view
        trajectoryViewRB = new RadioButton("View generated trajectory");
        navigatorViewRB = new RadioButton("View calculated trajectory");
        resultViewToggleGroup = new ToggleGroup();
        trajectoryViewRB.setToggleGroup(resultViewToggleGroup);
        navigatorViewRB.setToggleGroup(resultViewToggleGroup);
        trajectoryViewRB.setSelected(true);
        GridPane.setConstraints(trajectoryViewRB,0, 0);
        GridPane.setConstraints(navigatorViewRB,0, 1);
        resultNode.getChildren().addAll(trajectoryViewRB, navigatorViewRB);
        // upon change, redraw either the generated, or the calculated trajectory
        resultViewToggleGroup.selectedToggleProperty().addListener(observable -> {
            updateFrameView((int)stepsSlider.getValue()-1); // -1 due to zero-indexing
        });

        // iterations counter
        iterationsLbl = new Label("Iterations: ");
        GridPane.setConstraints(iterationsLbl, 1, 0);
        resultNode.getChildren().add(iterationsLbl);

        // frame values
        /*squaresDifferenceLbl = new Label("Least squares method\nsquares difference: ");
        GridPane.setConstraints(squaresDifferenceLbl, 1, 1);
        resultNode.getChildren().addAll(squaresDifferenceLbl);*/

        /*
        // debug
        Label xLbl = new Label("slider value = " + String.valueOf(((int) mapIntensitySlider.getValue())));
        GridPane.setConstraints(xLbl, 1, rowCounter);
        optionNode.getChildren().addAll(xLbl);
        mapIntensitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            xLbl.setText("slider value = " + String.valueOf(newValue.intValue()));
        });
        rowCounter++;
        // end debug
        */

        stage.show();
    }

    private void paintMap(Map map, GraphicsContext gc) {
        for (int x = 0; x < map.getYDim(); x++) {
            for (int y = 0; y < map.getXDim(); y++) {
                Color cellColor = Color.rgb(map.cellValue(x, y), map.cellValue(x, y), map.cellValue(x, y));
                gc.setStroke(cellColor);
                gc.setFill(cellColor);
                gc.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
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

    private void paintFrame(MapFrame frame, GraphicsContext gc) {
        for (int x = 0; x < frame.getSize(); x++) {
            for (int y = 0; y < frame.getSize(); y++) {
                Color cellColor = Color.rgb(frame.cellValue(x, y), frame.cellValue(x, y), frame.cellValue(x, y));
                gc.setStroke(cellColor);
                gc.setFill(cellColor);
                gc.fillRect(x * framePixelSize, y * framePixelSize, framePixelSize, framePixelSize);
            }
        }
    }

    private void updateFrameView(int step) {
        // draw either the generated, or the calculated trajectory
        Trajectory trajectoryToPaint;
        Color trajectoryColor;
        Color pointColor;
        if (resultViewToggleGroup.getSelectedToggle() == trajectoryViewRB) {
            trajectoryToPaint = tb.getOutputTrajectory();
            trajectoryColor = TRAJECTORY_COLOR;
            pointColor = TRAJECTORY_POINT_COLOR;
        } else {
            trajectoryToPaint = navigator.getResultTrajectory();
            trajectoryColor = NAVIGATOR_COLOR;
            pointColor = NAVIGATOR_POINT_COLOR;
        }

        paintFrame(trajectoryToPaint.getMapFrameArrayList().get(step), frameViewGC);
        paintTrajectory(trajectoryToPaint, visGC, trajectoryColor);
        visGC.setFill(pointColor);
        visGC.setStroke(pointColor);
        visGC.fillRect(trajectoryToPaint.getCoordArrayList().get(step)[0]*pixelSize,
                trajectoryToPaint.getCoordArrayList().get(step)[1]*pixelSize,
                pixelSize, pixelSize);
    }

    private double[] parseProbability(String text) {
        Pattern pattern = Pattern.compile("([0-9]\\.[0-9]+)+");
        Matcher matcher = pattern.matcher(text);
        boolean found = false;
        ArrayList<Double> resultList = new ArrayList<>();
        while (matcher.find()) {
            resultList.add(Double.parseDouble(matcher.group()));
            found = true;
        }
        if (!found) throw new NumberFormatException();
        return resultList.stream().mapToDouble(d -> d).toArray(); // convert ArrayList<Double> to double[]
        // see https://stackoverflow.com/a/27531513
    }

    private String parseProbability(double[] probabilityArray) {
        StringBuilder builder = new StringBuilder();
        for (double d : probabilityArray) {
            builder.append(d);
            builder.append(", ");
        }
        builder.delete(builder.length()-2, builder.length()-1);
        return builder.toString();
    }

    /*private void addNoise(Trajectory trajectory, int level) {
        if (level != 0) {
            for (MapFrame frame : trajectory.getMapFrameArrayList()) {
                //System.out.println(frame);
                for (int x = 0; x < frame.getSize(); x++) {
                    for (int y = 0; y < frame.getSize(); y++) {
                        try {
                            Random rand = new Random();
                            int newVal = frame.cellValue(x, y) + rand.ints(1, (-1)*level, level).findFirst().getAsInt(); // the noise may be positive or negative
                            //System.out.println(frame.cellValue(x, y) + "\t" + newVal + "\t" + (frame.cellValue(x, y) - newVal));
                            frame.setCellValue(x, y, newVal);
                        } catch (NumberFormatException e) {
                            continue; // if the value exceeds 0-255, just skip it
                        }
                    }
                }
                //System.out.println(frame + "\n");
            }
        }
    }*/

    private class stepsSliderChangeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            updateFrameView(newValue.intValue()-1); // -1 due to zero-indexing
            stepsSliderLbl.setText("Trajectory step: " + String.valueOf(newValue.intValue()));
            iterationsLbl.setText("Iterations: " + String.valueOf(navigator.getIterations(newValue.intValue()-1)));
            /*squaresDifferenceLbl.setText("Least squares method\nsquares difference: "
                    + String.valueOf((int)MapFrame.squaresDifference(tb.getOutputTrajectory().getMapFrameArrayList().get(newValue.intValue()-1),
                    navigator.getResultTrajectory().getMapFrameArrayList().get(newValue.intValue()-1))));*/
        }
    }
}