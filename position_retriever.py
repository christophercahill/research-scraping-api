import os 

position_list = []
def file_checker(f):
	for line in f:
		arr = line.split("$")
		position = arr[1]
		position = position.trim() 
		if position not in position_list:
			position_list.append(position)

files = [f for f in os.listdir('.') if os.path.isfile(f)]
file_list = []
for f in files:
	file_checker(f)

with open("positions.txt", "w") as outfile:
	for position in position_list:
		outfile.write(position)