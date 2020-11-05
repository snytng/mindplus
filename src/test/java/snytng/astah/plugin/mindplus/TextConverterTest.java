package snytng.astah.plugin.mindplus;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextConverterTest {

	@Test
	public void test_deleteCR_改行なし() {
		String input  = "車は道を走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行１最後() {
		String input  = "車は道を走る\r\n";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行１最初() {
		String input  = "\r\n車は道を走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行１途中() {
		String input  = "車は\r\n道を走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２最後() {
		String input  = "車は道を走る\r\n\r\n";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２最初() {
		String input  = "\r\n\r\n車は道を走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２途中() {
		String input  = "車は\r\n\r\n道を走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２途中バラバラ() {
		String input  = "車は\r\n道を\r\n走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２最初最後() {
		String input  = "\r\n車は道を走る\r\n";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２最初途中() {
		String input  = "\r\n車は\r\n道を走る";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteCR_改行２途中最後() {
		String input  = "車は\r\n道を走る\r\n";
		String output = "車は道を走る";
		String result = TextConverter.deleteCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitSentence() {
		String input  = "車は道を走る。タイヤは車の一部だ。";
		String output = "車は道を走る。" + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitMaru(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitSentenceTen() {
		String input  = "車は、道を走る。タイヤは、車の一部だ。";
		String output = "車は、"  + System.lineSeparator() + "道を走る。タイヤは、" + System.lineSeparator() + "車の一部だ。";
		String result = TextConverter.splitTen(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitSentenceTenEnd() {
		String input  = "車は、道を走る。タイヤは、車の一部だ、";
		String output = "車は、"  + System.lineSeparator() + "道を走る。タイヤは、" + System.lineSeparator() + "車の一部だ、";
		String result = TextConverter.splitTen(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitCR() {
		String input  = "車は道を走る\r\nタイヤは車の一部だ\r\n";
		String output = "車は道を走る" + System.lineSeparator() + "タイヤは車の一部だ";
		String result = TextConverter.splitCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitBL() {
		String input  = "車\r\nは\n道を走る\r\n\r\nタイヤは車の一部だ \r\n  \r\n	\r\n";
		String output = "車\nは\n道を走る" + System.lineSeparator() + "タイヤは車の一部だ";
		String result = TextConverter.splitBL(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitBLlastOneReturn() {
		String input  = "車\r\nは\n道を走る\r\n\r\nタイヤは車の一部だ \r\n ";
		String output = "車\nは\n道を走る" + System.lineSeparator() + "タイヤは車の一部だ";
		String result = TextConverter.splitBL(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitSentenceAndCR() {
		String input  = "車は道を走る。\r\nタイヤは車の一部だ。\r\n\r\n";
		String output = "車は道を走る。" + System.lineSeparator() + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitMaruAndCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_splitRegexPeriod() {
		String input  = "車は道を走る.タイヤは車の一部だ。";
		String output = "車は道を走る." + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitRegex(input, "\\.");
		assertEquals(result, output);
	}

	@Test
	public void test_splitRegexComma() {
		String input  = "車は道を走る,タイヤは車の一部だ。";
		String output = "車は道を走る," + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitRegex(input, "\\,");
		assertEquals(result, output);
	}

	@Test
	public void test_splitRegex読点() {
		String input  = "車は道を走る、タイヤは車の一部だ。";
		String output = "車は道を走る、" + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitRegex(input, "、");
		assertEquals(result, output);
	}

	@Test
	public void test_splitRegexカッコ() {
		String input  = "車は道を走る)タイヤは車の一部だ。";
		String output = "車は道を走る)" + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitRegex(input, "\\)");
		assertEquals(result, output);
	}

	@Test
	public void test_splitRegexカッコ全角() {
		String input  = "車は道を走る）タイヤは車の一部だ。";
		String output = "車は道を走る）" + System.lineSeparator() + "タイヤは車の一部だ。";
		String result = TextConverter.splitRegex(input, "）");
		assertEquals(result, output);
	}

	@Test
	public void test_deleteSpace空白なし() {
		String input  = "車は道を走る。";
		String output = "車は道を走る。";
		String result = TextConverter.deleteSpace(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteSpace行頭空白あり() {
		String input  = " 　車は 道を走る。";
		String output = "車は 道を走る。";
		String result = TextConverter.deleteSpace(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteSpace行末空白あり() {
		String input  = "車は 道を走る。 　";
		String output = "車は 道を走る。";
		String result = TextConverter.deleteSpace(input);
		assertEquals(result, output);
	}

	@Test
	public void test_deleteRegex() {
		String input  = "車は、道を走る。";
		String output = "車は道を走る。";
		String result = TextConverter.deleteRegex(input, "、");
		assertEquals(result, output);
	}

	@Test
	public void test_replaceMaru2CR() {
		String input  = "車。道を走る。";
		String output = "車" + System.lineSeparator() + "道を走る";
		String result = TextConverter.replaceMaru2CR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_replaceWord2WordCR_Maru() {
		String input  = "車。道を走る。";
		String output = "車。" + System.lineSeparator() + "道を走る。";
		String result = TextConverter.replaceMaru2MaruCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_replaceWord2WordCR_Ten() {
		String input  = "車、道を走る、";
		String output = "車、" + System.lineSeparator() + "道を走る、";
		String result = TextConverter.replaceTen2TenCR(input);
		assertEquals(result, output);
	}

	@Test
	public void test_replaceWord2WordCR_Word() {
		String input  = "車.道を走る.";
		String output = "車."  + System.lineSeparator() + "道を走る.";
		String result = TextConverter.replaceWord2WordCR(input, "\\.");
		assertEquals(result, output);
	}


}
