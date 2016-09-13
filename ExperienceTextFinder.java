import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ExperienceTextFinder {	
	//list of files 
	static List<File> files;
	
	//main 
	public static void main(String[] args) {
		//gets file list 
		setUp();
		
		//individual go through every file 
		for (File file : files) {
			
			
			
			Set<String> experiences = new HashSet<String>();
			
			if(file.isDirectory()){
				continue; 
			}
			
			if(!file.getName().contains(".txt")){
				continue; 
			}
			
			if(!file.getName().contains("Data")){
				continue;
			}

			//initializes file reader 
			FileReader fr;
			try {
				//set up file reader/buffered reader 
				fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);

				//iterate through lines 
				String nextLine = br.readLine();
				while (nextLine != null) {
					//get the experiences 
					if (nextLine.equals("Experience")) {
						//loop through until skills 
						while (nextLine != null && !nextLine.toLowerCase().contains("skills")) {
							//parse the line if it exists 
							if (nextLine.trim().length() > 0 && !nextLine.equals("Experience")) {
								if(nextLine.contains("no experience listed")){
									break;
								}
								
								String experienceDescription = parse(nextLine);

								//break if no data was retrieved (end)
								if (experienceDescription == null) {
									break;
								}
								
								System.out.println(experienceDescription);
								
								experiences.add(experienceDescription);
							}
							//go through to next line 
							nextLine = br.readLine();
						}
						break; 
					}
					//go through to next line 
					nextLine = br.readLine();
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try{
				String fileName = file.getName();
				fileName = fileName.substring(0, fileName.length() - 4); 
				fileName = fileName + "_position.txt";

				File newDir = new File("PositionParsed");
	            
	            // create new dir if it doesn't exist already
	            if (!newDir.exists()) {
	                try{
	                    newDir.mkdir();
	                }
	                catch(Exception e){
	                    System.out.println("Error occured");
	                }
	            }
				
	            File outfile = new File("PositionParsed/" + fileName);
				FileWriter fw = new FileWriter(outfile); 
				BufferedWriter bw = new BufferedWriter(fw); 
				for(String experience : experiences){
					bw.write(experience + "\n");
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//helper function to parse experience file 
	private static String parse(String nextLine) {
		//return nothing if no experiences 
		if (nextLine.contains("no experience listed")) {
			return null;
		}

		//split line by $ 
		String[] parsedString = nextLine.split("\\$");
		
		//get experience and put in output array 
		String experience = parsedString[0]; 
		experience = experience.trim();
		return experience;
	}

	//helper function to get files 
	private static void setUp() {
		//makes list of files 
		files = new ArrayList<File>();
		//gets file directory with experiences 
		File currentDir = new File(".");
		//puts in the list 
		File[] arrayOfFiles = currentDir.listFiles();
		for (int i = 0; i < arrayOfFiles.length; i++) {
			files.add(arrayOfFiles[i]);
		}
	}
}