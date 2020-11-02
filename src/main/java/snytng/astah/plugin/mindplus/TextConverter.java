package snytng.astah.plugin.mindplus;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextConverter {

	/**
	 * logger
	 */
	static final Logger logger = Logger.getLogger(TextConverter.class.getName());
	static {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.CONFIG);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
	}

	private TextConverter(){}

	// delete

	public static String deleteMaru(String input){
		return input
				.replaceAll("。", "");
	}

	public static String deleteCR(String input){
		return input
				.replaceAll("\\r", "")
				.replaceAll("\\n", "");
	}

	public static String deleteMaruAndCR(String input){
		return deleteMaru(deleteCR(input));
	}

	public static String deleteRegex(String input, String regex){
		return input
				.replaceAll(regex, "");
	}

	public static String deleteSpace(String input){
		return input
				.replaceFirst("^[\\s　]+", "")
				.replaceFirst("[\\s　]+$", "");
	}


	// split

	public static String splitMaru(String input){
		return input
				.replaceAll("。", "。" + System.lineSeparator());
	}

	public static String splitTen(String input){
		return input
				.replaceAll("、", "、" + System.lineSeparator());
	}

	public static String splitCR(String input){
		return input
				.replaceAll("\\r", "")
				.replaceAll("\\n", System.lineSeparator());

	}

	public static String splitBL(String input){
		return input
				.replaceAll("\\r", "")
				.replaceAll("(\\s*\\n){2,}", System.lineSeparator());

	}

	public static String splitMaruAndCR(String input){
		return splitMaru(splitCR(input));
	}

	public static String splitRegex(String input, String regex){
		return input
				.replaceAll(regex, regex + System.lineSeparator());
	}

	// replace

	public static String replaceMaru2CR(String input){
		return deleteMaru(splitMaru(input));
	}

	public static String replaceWord2WordCR(String input, String word){
		String s = input
				.replaceAll(word, word + System.lineSeparator());
		return deleteRegex(s, System.lineSeparator() + "+$");
	}


	public static String replaceMaru2MaruCR(String input){
		return replaceWord2WordCR(input, "。");
	}

	public static String replaceTen2TenCR(String input){
		return replaceWord2WordCR(input, "、");
	}



}
