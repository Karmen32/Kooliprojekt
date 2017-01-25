import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will deal with reading the content fof the levels in the text files and changing them into a 2-dimensional arrays
 * @author Karmen, with some help from my brother
 */
class FileManager
{
    /**
     * Method for converting the content of the text file into a 2-dimensional array that can later be compared to the array on the game field.
     * @param fileName name of the chosen level in the text file
     * @return finalPicture, completed picture in the form of a 2-dimensional array
     * @throws IOException
     */
	int[][] getPicture(String fileName) throws IOException {

		BufferedReader reader;
		try {
			reader = getBufferedReader(fileName);
		} catch (IOException | URISyntaxException e) {
			System.out.println(e.getMessage());
			return null;
		}

		String fileLine;
		Integer lineNumber = 0;
		List<int[]> picture = new ArrayList<>();

		while ((fileLine = reader.readLine()) != null) {
			int[] row = new int[fileLine.length()];
			for (int i = 0; i < fileLine.length(); i++) {
				/* @see http://stackoverflow.com/questions/4968323/java-parse-int-value-from-a-char
				this is necessary in order to get correct integer values from the file */
				row[i] = fileLine.charAt(i) - '0';
			}
			picture.add(lineNumber, row);
			lineNumber++;
		}

		int[][] finalPicture = new int[picture.size()][picture.get(0).length];
		for (int i = 0; i < picture.size(); i++) {
			finalPicture[i] = picture.get(i);
		}

		return finalPicture;
	}

    /**
     *Method for reading the contents of the text files
     * @param fileName the name of the text file for the corresponding level
     * @return buffered file from the text file
     * @throws IOException
     * @throws URISyntaxException
     */
	private BufferedReader getBufferedReader(String fileName) throws IOException, URISyntaxException {
		String fileNameWithExtension = "/Levels/" + fileName + ".txt";
		File file = new File(this.getClass().getResource(fileNameWithExtension).toURI());
		return new BufferedReader(new FileReader(file));
	}
}
