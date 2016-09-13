with open("shortened_phrases_tfidf.txt", "r") as infile:
    with open("phrase_refined_tfidf.txt", "w") as outfile:
        for line in infile:
            original_line = line
            line = line.replace("-", " ")
            word_list = line.split()
            if len(word_list) < 3:
                continue
            else:
                first_word = word_list[0]                
                second_word = word_list[1]
                score = word_list[2]

                if first_word == 'the':
                    continue
                elif first_word == 'on':
                    continue
                elif first_word == 'a':
                    continue
                elif first_word == 'an':
                    continue
                elif first_word == 'and':
                    continue
                elif first_word == 'as':
                    continue
                elif first_word == 'for':
                    continue
                elif first_word == 'by':
                    continue
                elif first_word == 'with':
                    continue
                elif first_word == 'i':
                    continue
                elif first_word == 'am':
                    continue
                elif first_word == 'was':
                    continue
                elif first_word == 'of':
                    continue
                elif first_word == 'to':
                    continue
                elif first_word == 'more':
                    continue
                elif first_word == 'than':
                    continue
                elif first_word == 'in':
                    continue
                elif first_word == 'dr':
                    continue
                elif first_word == 'is':
                    continue
                elif first_word == 'we':
                    continue
                elif first_word == 'all':
                    continue
                
                if second_word == 'the':
                    continue
                elif second_word == 'on':
                    continue
                elif second_word == 'a':
                    continue
                elif second_word == 'an':
                    continue
                elif second_word == 'and':
                    continue
                elif second_word == 'as':
                    continue
                elif second_word == 'for':
                    continue
                elif second_word == 'by':
                    continue
                elif second_word == 'with':
                    continue
                elif second_word == 'i':
                    continue
                elif second_word == 'am':
                    continue
                elif second_word == 'was':
                    continue
                elif second_word == 'of':
                    continue
                elif second_word == 'to':
                    continue
                elif second_word == 'more':
                    continue
                elif second_word == 'than':
                    continue
                elif second_word == 'in':
                    continue
                elif second_word == 'dr':
                    continue
                elif second_word == 'is':
                    continue
                elif second_word == 'we':
                    continue
                elif second_word == 'all':
                    continue
                
            outfile.write(original_line)
                
                