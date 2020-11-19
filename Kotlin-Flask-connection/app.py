from flask import Flask,jsonify,request,send_from_directory,abort,send_file
import sqlite3 as lite
import os,io
import base64
import jwt
from flask_bcrypt import Bcrypt
from PIL import Image,ImageDraw
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.fernet import Fernet
import time

password_provided = "password"
password = password_provided.encode()
salt = b';\xa9\x03\x81L.\x19D\xd7\xf8\xd7\xc1\xce\xc6`\x10'

kdf = PBKDF2HMAC(
	algorithm = hashes.SHA256(),
	length = 32,
	salt = salt,
	iterations = 100000,
	backend = default_backend()
	)

key = base64.urlsafe_b64encode(kdf.derive(password))

# used ngrock for using this localhost as my server for app

def token_verification(token):
	if not token:
			abort(403)

	con = lite.connect('files.db')
	with con:
		cur = con.cursor()
		cur = con.execute("select * FROM users")
		while True:
			row = cur.fetchone()
			if row == None:
				abort(403)
			if bcrypt.check_password_hash(row[3],token):
   				break

app = Flask(__name__)
bcrypt = Bcrypt(app)
app.config['SECRET_KEY'] = '5791628bb0b13ce0c676dfde280ba245'

con = lite.connect('files.db')
with con:
	cur = con.cursor()
	cur.execute("CREATE TABLE IF NOT EXISTS filenames(id INTEGER PRIMARY KEY,username, user_filename , saved_filename)")
	cur.execute("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY, username , password, token )")

@app.route('/register', methods = ['GET','POST'])
def register():
	if request.method == 'POST':
		username = request.form['username']
		password = request.form['password']

		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * FROM users")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				if row[1] == username:
					return jsonify(result1 = "Username already exists! Please enter another username" , result2 = "no")

			hashed_pass = bcrypt.generate_password_hash(password).decode('utf-8')
			token = jwt.encode({'user' : username} , app.config['SECRET_KEY'])
			hashed_token = bcrypt.generate_password_hash(token).decode('utf-8')

			cur.execute("INSERT INTO users (username,password,token) VALUES (?,?,?)",(username,hashed_pass,hashed_token))
			return jsonify(result1 = "Registered Successfully! Please login now." , result2 = "yes")

	return jsonify(result = "Route for User registartion")



@app.route('/login', methods = ['GET','POST'])
def login():
	if request.method == 'POST':
		username = request.form['username']
		password = request.form['password']

		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * from users")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				if row[1] == username:
					if bcrypt.check_password_hash(row[2],password):
						token = jwt.encode({'user' : username} , app.config['SECRET_KEY'])
						token = token.decode('UTF-8')
						return jsonify(result1 = "Logged in Successfully!" , result2 = "yes" , token = token)
					else:
						return jsonify(result1 = 'Incorrect password! Please enter again', result2 = 'no')

			return jsonify(result1 = 'Invalid username! Please enter again', result2 = 'no')

	return jsonify(result = "Route for User login")



@app.route('/upload', methods=['GET', 'POST'])
def upload():
	if request.method == 'POST':
		token = request.form['token']

		token_verification(token)		

		saved_filename = str(int(time.time()//1)) + ".jpg.encrypted"
		user_filename = request.form['user_file_name']
		username = request.form['username']

		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * FROM filenames")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				if row[2] == user_filename and row[1] == username:
					return jsonify(result = "Filename alteady exists") 

			file = request.files['file']

			data = file.read()
			fernet = Fernet(key)
			encrypted = fernet.encrypt(data)

			path = os.path.join("Uploads", saved_filename)
			with open(path,'wb') as f:
				f.write(encrypted)

			cur.execute("INSERT INTO filenames(username ,user_filename, saved_filename) VALUES (?,?,?)",(username,user_filename,saved_filename))
			# file.save(os.path.join("Uploads",actual_filename))
			print("file_saved")
			return jsonify(result = "File Uploaded") 
			
	return jsonify(result = "Route for Uploading Files")



@app.route('/download',methods = ['GET','POST'])
def download():
	if request.method == 'POST':
		token = request.form['token']

		token_verification(token)		

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
				if row[2] == user_filename:
					actual_filename = row[3]

					# path = os.path.join('Uploads',actual_filename)
					# file = Image.open(path)

					path = os.path.join("Uploads" , actual_filename)
					with open(path,'rb') as f:
						data = f.read()

					fernet = Fernet(key)
					decrypted = fernet.decrypt(data)

					# use send_file instead to send files
					return send_file(
						io.BytesIO(decrypted),
						mimetype='image/jpg',
						as_attachment=True,
						attachment_filename="harsh.jpg"
						)
					# return send_from_directory(directory = 'Uploads' ,filename = actual_filename)
					# return jsonify(result = "Filename alteady exists")

	return jsonify(result = "Route for Downloading Files")



@app.route('/filenames', methods = ['GET','POST'])
def filenames():
	if request.method == 'POST':
		token = request.form['token']

		token_verification(token)

		username = request.form['username']
		filename_list = []
		con = lite.connect('files.db')
		with con:
			cur = con.cursor()
			cur.execute("select * FROM filenames")
			while True:
				row = cur.fetchone()
				if row == None:
					break
				if row[1] == username:
					filename_list = filename_list + [row[2]]
			filename_list = sorted(filename_list)
			return jsonify (result = filename_list)

	return jsonify(result = "Route for Displaying filenames")

if __name__ == '__main__':
    app.run(debug=True)