with open("saunders_tambe_phrases.txt", "r") as infile:
	with open("saunders_tambe_phrases_dash.txt", "w") as outfile:
		for line in infile:
			line = line.strip()
			arr = line.split()
			if len(arr) > 1:
				new_word = arr[0].strip() + "-" + arr[1].strip()
			else:
				new_word = arr[0].strip()
			outfile.write(new_word + "\n")

