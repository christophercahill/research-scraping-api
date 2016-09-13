phrase_list = []
with open("phrases.txt", "r") as infile:
	with open("phrase_set", "w") as outfile:
		for line in infile:
			line = line.strip()
			arr = line.split()
            
			if len(arr) > 0:
				word = arr[0]
			else:
				word = line
            
			if word in phrase_list:
				continue
			else:
				phrase_list.append(word)
				outfile.write(line + "\n")

