package org.usfirst.frc.team3630.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CSVCode {
	
	String csvFile = "/Users/clarca/Desktop/ScoutFileCode.csv"; //replace clarca with your username
    File f = new File(csvFile);
    ScoutFrame scoutFrame;
    PrintWriter pw;
    char shift = ',';
	char newLine = '\n';
	
	public void frameInit() {
		if (scoutFrame == null) {
			scoutFrame = new ScoutFrame();
			scoutFrame.setSize(1440, 900);
		}
	}
	
	/**
	 * Call this only once to create the file on your computer
	 */
	
	public void createFile() {
		if (!f.exists()) {
			f.createNewFile();
		}
	}
	
	public void clearText() {
		scoutFrame.textField1.setText("");
        scoutFrame.textField2.setText("");
        scoutFrame.textField3.setText("");
        scoutFrame.textField4.setText("");
        scoutFrame.textField5.setText("");
        scoutFrame.textField6.setText("");
        scoutFrame.textField6.setText("");
    }
	
	public void saveFile() throws Exception {
		pw = new PrintWriter(new FileWriter(f, true));
		
        scoutFrame.subButton.addActionListener((x) -> {
			pw.write(newLine);
			pw.write(scoutFrame.textField1.getText());
			pw.write(shift);
			pw.write(scoutFrame.textField2.getText());
			pw.write(shift);
			pw.write(scoutFrame.textField3.getText());
			pw.write(shift);
			pw.write(scoutFrame.textField4.getText());
			pw.write(shift);
			pw.write(scoutFrame.textField5.getText());
			pw.write(shift);
			pw.write(scoutFrame.textField6.getText());
			pw.write(newLine);
			
			pw.flush();
			clearText();
        	});
	}
	
	public static void main(String[] args) throws Exception {
		CSVCode c = new CSVCode();
		c.frameInit();
		c.clearText();
		c.saveFile();
	}
}