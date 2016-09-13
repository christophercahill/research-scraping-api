with open("saunders_phrases.txt", "r") as infile:
    with open("top_saunders_phrases.txt", "w") as outfile:
        for line in infile:
            arr = line.split() 
            perc = int(arr[1])
            if perc > 80:
                outfile.write(line.strip() + "\n")