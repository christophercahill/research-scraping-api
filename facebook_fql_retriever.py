from selenium import webdriver
import facebook 
from selenium.webdriver.common.keys import Keys
import time 
import os.path

TOKEN = "TOKEN"
graph = facebook.GraphAPI(TOKEN)


#function to scrape and change token 
def change_information():
    global TOKEN 
    global graph
    
    usr = "USER"
    pwd = "PASSWORD"
    
    #make driver
    driver = webdriver.Firefox()
    driver.get("https://developers.facebook.com/tools/explorer/")
    assert "Facebook" in driver.title
    
    #log in
    try:
        elem = driver.find_element_by_id("email")
        elem.send_keys(usr)
        elem = driver.find_element_by_id("pass")
        elem.send_keys(pwd)
        elem.send_keys(Keys.RETURN)
        
        elem = driver.find_element_by_link_text("Get Token")
        elem.click() 
    except:
        driver.close()
        time.sleep(30)
        change_information()
        return 
    
    #get access token
    try: 
        new_elem = driver.find_element_by_link_text("ACCOUNT NAME")
        new_elem.click()
        
        source_code = driver.page_source
        location = source_code.find("Get User Access Token")
        location = location + 36
        source_code = source_code[location:]
        objects = source_code.split("\"")
        
        new_token = objects[0]
        TOKEN = new_token
        graph = facebook.GraphAPI(TOKEN)
    except:
        driver.close()
        time.sleep(30)
        change_information()
        return 
    driver.close()

#get firefox driver (selenium built in)
driver = webdriver.Firefox()
change_information()

with open("facebook_accounts.txt", "r") as infile:
    for line in infile:
        #refine line to get page_name
        page_name = line; 
        page_name = page_name.rstrip('\n')
        page_name = page_name.strip()
        
        try:
            #if file exists, continue
            if os.path.isfile(page_name + ".txt"):
                continue
            
            #otherwise, retrieve the profile
            profile = graph.get_object(page_name)
            
            #get the profile id
            page_id = profile['id']
            
            #make URL to get relevant information like posts and comments/likes for posts
            url = "https://api.facebook.com/method/fql.query?access_token=" + TOKEN +"&query=SELECT%20message,%20attachment,%20comments,%20likes,%20share_info,%20created_time,%20post_id%20FROM%20stream%20WHERE%20source_id%20=" + page_id
            
            #retrieve url
            driver.get(url)
            
            #write source code to file with account name
            with open(page_name + ".txt", "w") as outfile:
                outfile.write(driver.page_source.encode('utf-8'))
            time.sleep(1)
        except Exception as e:
            try: 
                error_text = str(e)
                #prints the error to output 
                print(error_text)
            except:
                print("couldnt print out error")
            continue 
driver.close()
        
        