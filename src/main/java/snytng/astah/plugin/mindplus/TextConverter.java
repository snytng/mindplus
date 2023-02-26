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
				.replaceAll("。", "")
				.trim();
	}

	public static String deleteCR(String input){
		return input
				.replaceAll("[\\r\\n]", "")
				.trim();
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

	public static String deleteBL(String input){
		return input
				//.replaceAll("\\r", "")
				.replaceAll("(\\s*\\r*\\n){2,}",  System.lineSeparator())
				.replaceAll("(\\s*\\r*\\n)\\s*+$",  System.lineSeparator())
				.trim();
	}


	// split

	public static String splitMaru(String input){
		return replaceWord2WordCR(input, "。");
	}

	public static String splitTen(String input){
		return replaceWord2WordCR(input, "、");
	}

	public static String splitCR(String input){
		return input
				//.replaceAll("\\r", "")
				.replaceAll("\\r*\\n", System.lineSeparator())
				.trim();
	}

	public static String splitBL(String input){
		return input
				//.replaceAll("\\r", "")
				.replaceAll("(\\s*\\r*\\n){2,}", System.lineSeparator())
				.replaceAll("(\\s*\\r*\\n)\\s*+$", System.lineSeparator())
				.trim();
	}

	public static String splitMaruAndCR(String input){
		return splitMaru(splitCR(input));
	}

	public static String splitRegex(String input, String regex){
		return input
				.replaceAll(regex, regex + System.lineSeparator())
				.trim();
	}

	// replace

	public static String replaceMaru2CR(String input){
		return deleteMaru(splitMaru(input));
	}

	public static String replaceWord2WordCR(String input, String word){
		return input
				.replaceAll(word, word + System.lineSeparator())
				.trim();
	}


	public static String replaceMaru2MaruCR(String input){
		return replaceWord2WordCR(input, "。");
	}

	public static String replaceTen2TenCR(String input){
		return replaceWord2WordCR(input, "、");
	}



}
