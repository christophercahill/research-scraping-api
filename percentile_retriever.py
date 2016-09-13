line_counter = 0
with open("phrase_refined_tfidf.txt", "r") as infile:
	with open("percentile_phrases_tfidf.txt", "w") as outfile:
		for line in infile:
			line_counter = line_counter + 1
			perc = float(line_counter)/278822
			top_per = int(perc*100) 
			arr = line.split() 
			outfile.write(arr[0] + " " + str(int(top_per)) + "\n")

