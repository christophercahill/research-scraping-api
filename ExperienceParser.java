import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ExperienceParser {
	static ArrayList<String> experiences = new ArrayList<String>();
	static ArrayList<File> files;
	static BufferedReader br;
	static String firstLine;
	static String secondLine;
	static String thirdLine;

	public static void main(String[] args) throws IOException {
		setUp();

		for (File f : files) {
			if(!f.getName().contains(".txt") || f.getName().contains("_parsed.txt") || f.isDirectory()){
				continue;
			}
						
			// initializes file reader
			FileReader fr;
			try {
				// set up file reader/buffered reader
				fr = new FileReader(f);
				br = new BufferedReader(fr);

				// first line is experience
				br.readLine();

				// retrieve all information
				findNextExperience();

				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String fileName = f.getName();
			fileName = fileName.substring(0, fileName.length() - 4); 
			fileName = fileName + "_parsed.txt";
            //make new parsed file in new dir
            
            File newDir = new File("ExperienceParsed");
            
            // create new dir if it doesn't exist already
            if (!newDir.exists()) {
                try{
                    newDir.mkdir();
                }
                catch(Exception e){
                    System.out.println("Error occured");
                }
            }
            
            File outfile = new File("ExperienceParsed/" + fileName);
			FileWriter fw = new FileWriter(outfile);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String s : experiences) {
				if (!s.equals("")) {
					bw.write(s + "\n");
				}
			}
			bw.close();
            //reset experiences
			experiences = new ArrayList<String>();
		}
	}

    //gets line of experience description text
    private static void findNextExperience() throws IOException {
		thirdLine = br.readLine();
		secondLine = "";
		firstLine = "";
        
        //while loop for when there is experience text
		while (thirdLine != null && !thirdLine.trim().equals("Skills") && !thirdLine.trim().contains("skills listed") && !thirdLine.trim().contains("skills found") && !thirdLine.trim().contains("education found")) {
            //dumby
			if (thirdLine == null) {
				break;
			}
            
            //knows when to reset text
			if (thirdLine.contains("present") || thirdLine.contains("year") || thirdLine.contains("month") || thirdLine.contains("January")
					|| thirdLine.contains("February") || thirdLine.contains("March") || thirdLine.contains("April")
					|| thirdLine.contains("May") || thirdLine.contains("June") || thirdLine.contains("July")
					|| thirdLine.contains("August") || thirdLine.contains("September") || thirdLine.contains("October")
					|| thirdLine.contains("November") || thirdLine.contains("December")) {

				thirdLine = br.readLine();
				secondLine = "";
				firstLine = "";
			}

            //add the experience text line
			experiences.add(firstLine);
            
            
            //keep parsing down
			firstLine = secondLine;
			secondLine = thirdLine;
			thirdLine = br.readLine();
		}
        
        //add last firstline
		experiences.add(firstLine);
	}

	// helper function to get files
	private static void setUp() {
		// makes list of files
		files = new ArrayList<File>();
		// gets file directory with experiences
		File currentDir = new File(".");
		// puts in the list
		File[] arrayOfFiles = currentDir.listFiles();
		for (int i = 0; i < arrayOfFiles.length; i++) {
			files.add(arrayOfFiles[i]);
		}
	}
}
