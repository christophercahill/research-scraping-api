import tweepy
import sys

#made with help from https://gist.github.com/yanofsky/5436496

#user and password for twitter oauth
CONSUMER_KEY = 'KEY'
CONSUMER_SECRET = 'SECRET'
OAUTH_TOKEN = 'TOKEN'
OAUTH_TOKEN_SECRET = 'TOKENSECRET'

#auth login
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(OAUTH_TOKEN, OAUTH_TOKEN_SECRET)

# variable accounts with time handler     
api = tweepy.API(auth0, wait_on_rate_limit=True, wait_on_rate_limit_notify=True)

#open twitter names for retrieval
with open("usernames.txt", "r") as infile:
    for line in infile:
        line = line.rstrip("\n")
        name = line.strip()      
        
        #print name
        print name  
   
        try:
            #actual retrieval command 
            user = api.get_user(name)
            
            #make a new file and write information as it comes
            file_name = name + ".txt"
            file = open(file_name, "w")
            file.write(name + "\n")
            
            file.write("\n")
            file.write("Followers:" + "\n")
            
            #write follower names
            for user in tweepy.Cursor(api.followers, screen_name=name).items():
                file.write(user.screen_name + "\n")
            
            file.write("\n")
            file.write("Following:" + "\n")    
            
            #write following names
            for user in tweepy.Cursor(api.friends, screen_name=name).items():
                file.write(user.screen_name + "\n")
                
            file.write("\n")
            tweets = []
            
            #repeat until all tweets retrieved
            some_tweets = api.user_timeline(screen_name=name, count=200)
            tweets.extend(some_tweets)
        
            last_tweet_id = tweets[-1].id - 1

            while len(some_tweets) > 0:
                some_tweets = api.user_timeline(screen_name=name, count=200, max_id=last_tweet_id)
                tweets.extend(some_tweets)
                last_tweet_id = tweets[-1].id - 1
        
            #get all tweet id, text, date
            file.write("\n")
            file.write("Tweets:")
            file.write("\n")
            for tweet in tweets:
                tweet_id = str(tweet.id)
                file.write(tweet_id.encode('utf8'))
                file.write("\n")
                
                date = str(tweet.created_at)
                file.write(date.encode('utf8'))
                file.write("\n")
                
                text = str(tweet.text.encode('utf8'))
                file.write(text)
                file.write("\n")
                file.write("\n")
            
            file.write("\n")                
            file.close()
        except:
            e = sys.exc_info()[0]
            print e
            continue