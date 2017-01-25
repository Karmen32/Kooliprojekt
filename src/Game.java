import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 *This class creates the game field/grid.
 * @author Karmen
 */
class Game extends GridPane
{
	private final RowConstraints rowConstraint = new RowConstraints(Window.squareSize);
	private final RowConstraints rowHeaderConstraint = new RowConstraints(Window.headerSize);//sets a different size for the row header
	private final ColumnConstraints colConstraint = new ColumnConstraints(Window.squareSize);
	private final ColumnConstraints colHeaderConstraint = new ColumnConstraints(Window.headerSize); //sets a different size for the column header
	private final Background blackBackground = new Background(new BackgroundFill(Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY));//this background object is necessary for latter use in order to change background colour
	private final int[][] picture;
	private final Window window;

	private List<StackPane> rowHeaders = new ArrayList<>();
	private List<StackPane> colHeaders = new ArrayList<>();
	private List<List<Integer>> rowHints;
	private List<List<Integer>> colHints;

	/**
	 * Constructor
	 * This takes in the picture and sets it as a class variable for easier use.
	 * @param picture int[][], a 2-dimensional array.
	 * @param window this is the window where the whole game takes place.
	 */
	Game(int[][] picture, Window window) {
		this.window = window;
		this.picture = picture;
	}

    /**
     * Method for creating the game field with hints.
     */
	void init() {
		this.initHints();
		this.initSquares();
	}

    /**
     * Method for creating lists of hints according to the level chosen. Hints are added to a list.
     */
	private void initHints() {
		List<List<Integer>> rowHints = new ArrayList<>();
		for (int i = 0; i < picture.length; i++) {
			int rowCounter = 0;
			List<Integer> oneRowHints = new ArrayList<>();
			for (int j = 0; j < picture[0].length; j++) {
				//This deals with row hints
				if (picture[i][j] == 1) {
					rowCounter++; //Shows how many coloured squares there are in a row/after each other.
				} else if (rowCounter > 0) {
					oneRowHints.add(rowCounter);
					rowCounter = 0;
				}

				//If last element and rowCounter is bigger than 0, means there is a coloured square in the end; necessary in order to keep the hints in correct rows and not have them flow over to other rows.
				if (j + 1 == picture[0].length && rowCounter > 0) {
					oneRowHints.add(rowCounter);
				}
			}
			rowHints.add(i, oneRowHints);

		}
		this.rowHints = rowHints;

		List<List<Integer>> colHints = new ArrayList<>();
		for (int i = 0; i < picture[0].length; i++) {
			int colCounter = 0;
			List<Integer> oneColHints = new ArrayList<>();
			for (int j = 0; j < picture.length; j++) {
				//This deals with column hints
				if (picture[j][i] == 1) {
					colCounter++;
				} else if (0 < colCounter) {
					oneColHints.add(colCounter);
					colCounter = 0;
				}

				//If last element and colCounter is bigger than 0, means there is a picture in the end
				if (j + 1 == picture.length && colCounter > 0) {
					oneColHints.add(colCounter);
				}

			}
			colHints.add(i, oneColHints);
		}
		this.colHints = colHints;
	}

	/**
	 * This method creates the grid which forms the game field.
	 */

	private void initSquares() {
		int rowCount = picture.length;
		int colCount = picture[0].length;
		int rowElementCount = rowCount + 1;//The gamefield itself is 1 tile bigger than the original level in the text file
		int colElementCount = colCount + 1;

		for (int row = 0; row < rowElementCount; row++) {
			for (int col = 0; col < colElementCount; col++) {
				StackPane stack = new StackPane();
				stack.setStyle("-fx-border-color: black");

				if (row == 0 && col != 0) {//This is where the hints are for different columns
					//we do row - 1 because hint containers are stacks also
					Text text = new Text(getHints(colHints, col - 1));
					stack.getChildren().add(text);
					rowHeaders.add(stack);//Hints are added as visible numbers
				} else if (col == 0 && row != 0) {
					//It is col - 1 because hint containers are stacks also
					Text text = new Text(getHints(rowHints, row - 1));
					stack.getChildren().add(text);
					colHeaders.add(stack);
				} else {
					stack.setBackground(Background.EMPTY);//For the rest of the gamefield, the background will be set as empty
				}

				add(stack, col, row);
			}
		}

        /*
		For loops for the column and row headers in order to set their widths
		@see http://stackoverflow.com/questions/23272924/dynamically-add-elements-to-a-fixed-size-gridpane-in-javafx
		 */
		for (int i = 0; i < rowElementCount; i++) {
			if (i == 0) {
				getRowConstraints().add(rowHeaderConstraint);
			} else {
				getRowConstraints().add(rowConstraint);
			}
		}

		for (int i = 0; i < colElementCount; i++) {
			if (i == 0) {
				getColumnConstraints().add(colHeaderConstraint);
			} else {
				getColumnConstraints().add(colConstraint);
			}
		}
	}

    /**
     * Method for taking a list of int values and converting them to a string.
     * @param hints a list which contains the hits of a row or column
     * @param index column or row index of this hint list
     * @return stringHint, which is a string of numbers with spaces between
     */
	private String getHints(List<List<Integer>> hints, int index) {
		String stringHint = "";
		for (int hint : hints.get(index)) { //For each loops which goes over every hint in the list
			stringHint = stringHint + " " + hint; //Adds the hints as String with spaces in between
		}
		return stringHint;
	}

	/**
	 * This method checks if the picture is complete or not; solved according to the solution in the corresponding text file
     *I got some help from my brother
	 */
	private boolean isComplete() {
		for (Node stack : getChildren()) {
			ObservableMap<Object, Object> properties = stack.getProperties();//Tracks changes
			int col = Integer.parseInt(properties.get("gridpane-column").toString()) - 1;//Changes the gridpane node address to an integer; -1 is added because the original level is 1 tile smaller than the gamefield
			int row = Integer.parseInt(properties.get("gridpane-row").toString()) - 1;
			if (col == -1 || row == -1) {
				continue;
			}

			//State of the square in reality
			boolean isColored = ((StackPane) stack).getBackground() == blackBackground;

			//State of the square in the actual picture in the text file
			boolean isActuallyColored = picture[row][col] == 1;

			if (isColored != isActuallyColored) {
				return false;
			}
		}
		return true;
	}

    /**
     * Method for controlling what a mouse click does
     */
	void addMouseEventHandler() {
		setOnMouseClicked(e -> {
			if (e.getButton().compareTo(MouseButton.PRIMARY) != 0) { //If it is not a primary mouseclick, the program will do nothing
				return;
			}

			for (Node stack : getChildren()) {
				if (!(stack instanceof StackPane) || rowHeaders.contains(stack) || colHeaders.contains(stack)) { //If the clicked place is not in the gridpane or is at either the column or row header, the program will continue without reactng
					continue;
				}

				if (stack.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) { //Left mouse click happens in the game field
					if (((StackPane) stack).getBackground().isEmpty()) {
						((StackPane) stack).setBackground(blackBackground); //If the square background was white/empty, it will be changed into previously defined background
					} else {
						((StackPane) stack).setBackground(Background.EMPTY);
					}

					if (isComplete()) {
						window.displayEndScreen(); //If the game field matches to the text file content, alert message will pop up
					}
					break;
				}
			}
		});
	}

}
