def phrase_checker(phrase):
    with open("percentile_phrases_tfidf.txt", "r") as score_file:
        for line in score_file:
            if phrase in line:
                print(line.strip())
               
with open("saunders_tambe_phrases.txt", "r") as phrase_file:
        for phrase_line in phrase_file:
            phrase = phrase_line.strip()
            phrase = phrase.replace(" ", "-")
            phrase_checker(phrase)