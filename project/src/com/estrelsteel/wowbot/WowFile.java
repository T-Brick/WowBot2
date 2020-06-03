package com.estrelsteel.wowbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WowFile {

	private String path;
	private ArrayList<String> lines;

	public WowFile(String path) {
		this.path = path;
		this.lines = new ArrayList<String>();
	}

	public String getPath() {
		return path;
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public WowFile appendFile() throws IOException {
		String line;
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		line = br.readLine();
		while (line != null) {
			lines.add(line);
			line = br.readLine();
		}
		fr.close();
		br.close();
		return this;
	}

	public WowFile loadFile() throws IOException {
		lines = new ArrayList<String>();
		return appendFile();
	}

	public void saveFile() throws IOException {
		FileWriter fw = new FileWriter(path);
		BufferedWriter bw = new BufferedWriter(fw);
		for (String line : lines) {
			bw.write(line + "\n");
		}
		fw.close();
		bw.flush();
		bw.close();
	}

	public WowFile setPath(String path) {
		this.path = path;
		return this;
	}

	public WowFile setLines(ArrayList<String> lines) {
		this.lines = lines;
		return this;
	}

}
