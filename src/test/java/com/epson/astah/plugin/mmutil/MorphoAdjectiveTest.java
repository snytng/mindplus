package com.epson.astah.plugin.mmutil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import snytng.astah.plugin.mindplus.Morpho;

public class MorphoAdjectiveTest {

	@Test
	public void test単文形容詞なし() {
		String[] output = Morpho.getAdjectives("自動車は道路を走る");
		assertThat(output.length, is(0));
	}

	@Test
	public void test単文形容詞１つ() {
		String[] output = Morpho.getAdjectives("白い自動車は道路を走る");
		assertThat(output.length, is(1));
		assertArrayEquals(output, new String[]{"白い"});
	}

	@Test
	public void test単文形容詞２つ() {
		String[] output = Morpho.getAdjectives("白い自動車は黒い道路を走る");
		assertThat(output.length, is(2));
		assertArrayEquals(output, new String[]{"白い", "黒い"});
	}

	@Test
	public void test単文形容詞２つ連続() {
		String[] output = Morpho.getAdjectives("明るく白い自動車は道路を走る");
		assertThat(output.length, is(2));
		assertArrayEquals(output, new String[]{"明るい", "白い"});
	}

	@Test
	public void test複文形容詞４つ() {
		String[] output = Morpho.getAdjectives("白い自動車は黒い道路を走る、青い帆船は広い海洋を進む");
		assertThat(output.length, is(4));
		assertArrayEquals(output, new String[]{"白い","黒い","青い","広い"});
	}
	
}
