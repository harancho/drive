from flask import Flask,jsonify,request,send_from_directory
import sqlite3 as lite
import os
from PIL import Image,ImageDraw

# used ngrock for using this localhost as my server for app

app = Flask(__name__)

con = lite.connect('files.db')
with con:
	cur = con.cursor()
	cur.execute("CREATE TABLE IF NOT EXISTS filenames(id INTEGER PRIMARY KEY, user_filename , actual_filename)")

@app.route('/upload', methods=['GET', 'POST'])
def upload():
	if request.method == 'POST':
		actual_filename = request.form['actual_file_name']
		user_filename = request.form['user_file_name']

		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * FROM filenames")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				if row[1] == user_filename:
					return jsonify(result = "Filename alteady exists") 

			file = request.files['file']
			cur.execute("INSERT INTO filenames(user_filename, actual_filename) VALUES (?,?)",(user_filename,actual_filename))
			file.save(os.path.join("Uploads",actual_filename))
			print("file_saved")
			return jsonify(result = "File Uploaded") 
			
	return jsonify(result = "Route for Uploading Files")

@app.route('/download',methods = ['GET','POST'])
def download():
	if request.method == 'POST':
		user_filename = request.form['filename']
		print(user_filename)

		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * FROM filenames")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				if row[1] == user_filename:
					actual_filename = row[2]

					# path = os.path.join('Uploads',actual_filename)
					# file = Image.open(path)

					return send_from_directory(directory = 'Uploads' ,filename = actual_filename)
					# return jsonify(result = "Filename alteady exists")

	return jsonify(result = "Route for Downloading Files")

@app.route('/filenames', methods = ['GET','POST'])
def filenames():
	if request.method == 'POST':
		filename_list = []
		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * FROM filenames")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				else:
					filename_list = filename_list + [row[1]]
			filename_list = sorted(filename_list)
			return jsonify (result = filename_list)

	return jsonify(result = "Route for Displaying filenames")

if __name__ == '__main__':
    app.run(debug=True)