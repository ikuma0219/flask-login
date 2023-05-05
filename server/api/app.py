from flask import Flask,flash,request,render_template, redirect, url_for, session
import smtplib,ssl
from email.mime.text import MIMEText
from datetime import timedelta
from flask_sqlalchemy import SQLAlchemy
import hashlib
import pyotp
import time
import datetime 
import jwt
from typing import Any,Dict

app = Flask(__name__)
app.permanent_session_lifetime = timedelta(minutes=5)

#database
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://{user}:{password}@{host}/{db_name}?charset=utf8'.format(**{
    'user':"root",
    'password':"Ikuma0219!",
    'host':"localhost",
    'db_name':"myDB"
    })
app.config['SQLALCHEMY_TRACK_MODIFICAIONS'] = False
app.config['SECRET_KEY'] = b'B\xe7\xfe\x07\x95\xf8\xd9e%\x9a\xc2\x15Ny{Y\xe0\xa8\t0V\x11\xcb\x11'

db = SQLAlchemy(app)
db.init_app(app)
class User(db.Model):
    __tablename__ = "users"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(255), unique=True, nullable=False)
    password = db.Column(db.String(255), unique=False, nullable=False)
with app.app_context():
    db.create_all()

#auth
def hashing_password(password:str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()
def verify_password(hashed_password:str, password:str):
    return hashed_password == hashlib.sha256(password.encode('utf-8')).hexdigest()

#otp
def create_otp():
    totp = pyotp.TOTP('base32secret3232')
    return totp.now()
def check_otp(get_otp, token_otp):
    return get_otp == token_otp
#token
def encode(secretkey: str, payload: Dict[str,Any]):
    return jwt.encode(payload=payload, key=secretkey, algorithm="HS256")
def decode(secretkey: str, token: str):
    return jwt.encode(jwt=token, key=secretkey, algorithm="HS256")

#mail
my_account = 'qr.otp.auth.es3@gmail.com'
my_password = 'hihgevczbsbhqjmw'
class Mail:
    def send_email(msg):
        server = smtplib.SMTP_SSL('smtp.gmail.com', 465, context=ssl.create_default_context())
        server.set_debuglevel(0)
        server.login(my_account, my_password)
        server.send_message(msg)

    def make_mime(mail_to, subject, body):
        msg = MIMEText(body, 'plain')
        msg['Subject'] = subject
        msg['To'] = mail_to
        msg['From'] =my_account
        return msg

    def send_my_message(email):
        otp = create_otp()
        msg = Mail.make_mime(
            mail_to = email,
            subject='ワンタイムパスワード | ES3 Lab.',
            body='ワンタイムパスワードが発行されました．\n以下のワンタイムパスワードをブラウザに入力してください.\n\nワンタイムパスワード：{otp}\n\n神戸大学大学院工学研究科・工学部 情報通信研究室（ES3）'.format(otp=otp))
        Mail.send_email(msg)

#router
@app.route("/")
def home():
    if "user" in session:
        return redirect(url_for("user"))
    return redirect(url_for("signin"))

@app.route('/signup',methods=['POST', 'GET'])
def signup():
    if "user" in session:
        return redirect(url_for("user"))
    if request.method == "POST": 
        email = request.form["email"]
        password = request.form["password"]
        if email == "" or password == "":
            flash("入力してください．")
            return redirect(url_for("signup"))
        user = User()
        user.email = request.form["email"]
        user.password = hashing_password(request.form["password"])
        try:
            db.session.add(user)
            db.session.commit()
            return redirect(url_for("signin"))
        except Exception as e:
            db.session.rollback()
        flash("すでに登録されています．")
        return redirect(url_for("signup"))      
    return render_template("signup.html")

@app.route('/signin',methods=['POST', 'GET'])
def signin():
    if "user" in session:
        return redirect(url_for("user"))
    if request.method == "POST":
        session.permanent =True      
        email = request.form["email"]
        password = request.form["password"]
        if email == "" or password == "":
            flash("入力してください．")
            return redirect(url_for("signin"))
        users = db.session.query(User).filter(User.email == email).limit(1).all()
        for user in users:
            if user != None:
                hashed_password = user.password
                if verify_password(hashed_password=hashed_password, password = request.form["password"]) == False:
                    flash("パスワードが違います．")
                    return redirect(url_for("signin"))
                Mail.send_my_message(email=email)
                session["otp"] =email
                return redirect(url_for("otp"))
        flash("ユーザーが存在しません．") 
    return render_template("signin.html")
    
@app.route("/otp",methods=['POST', 'GET'])
def otp():
    if request.method == "POST":
        get_otp = request.form["otp"]
        token_otp = "otp"
        if check_otp(get_otp, token_otp) == False:
            flash('ワンタイムパスワードが違います．')
            return redirect(url_for("otp"))
        session['user'] = token_otp
        return redirect(url_for('user'))
    if "otp" in session:
        user = session["otp"]
        return render_template("otp.html", user=user)
    return redirect(url_for("signin"))
    
@app.route("/user",methods=['POST', 'GET'])
def user():
    if request.method == "POST":
        return redirect(url_for("logout"))
    if "user" in session:
        user = session["user"]
        return render_template("user.html", user = user)
    return redirect(url_for("signin"))

@app.route("/logout")
def logout():
    session.pop("user", None)
    return redirect(url_for("signin"))

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)