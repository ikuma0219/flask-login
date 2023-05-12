from flask import Flask,flash,request,render_template, redirect, url_for, session
import smtplib,ssl
from email.mime.text import MIMEText
from datetime import timedelta
from flask_sqlalchemy import SQLAlchemy
import hashlib
import pyotp
from email.mime.image import MIMEImage
from email.mime.multipart import MIMEMultipart
import qrcode
import base64

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
    qrcode = db.Column(db.String(800), unique=False, nullable=False)
with app.app_context():
    db.create_all()

#hash
def hashing_password(password:str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()
def verify_password(hashed_password:str, password:str):
    return hashed_password == hashlib.sha256(password.encode('utf-8')).hexdigest()

#qrcode
def create_qr():
    img = qrcode.make(create_otp())
    img.save("test.png")
    file_data = open ("test.png", "rb").read()
    b64_data = base64.b64encode(file_data).decode('utf-8')
    return b64_data

#otp
def create_otp():
    totp = pyotp.TOTP('base32secret3232')
    return totp.now()
def check_otp(get_otp, token_otp):
    return get_otp == token_otp

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
        
    def make_qr_mime(mail_to, subject, body):
        msg = MIMEMultipart()
        msg['Subject'] = subject
        msg['To'] = mail_to
        msg['From'] =my_account
        msg.attach(MIMEText(body, 'plain'))
        with open("test.png", 'rb') as img:
            attachment = MIMEImage(img.read())
            attachment.add_header("Content-Disposion", "attachment", filename = 'test.png')
        msg.attach(attachment)
        return msg

    def send_my_message(email, otp):
        msg = Mail.make_mime(
            mail_to = email,
            subject='ワンタイムパスワード | ES3 Lab.',
            body='ワンタイムパスワードが発行されました．\n以下のワンタイムパスワードをブラウザに入力してください.\n\nワンタイムパスワード：{otp}\n\n神戸大学大学院工学研究科・工学部 情報通信研究室（ES3）'.format(otp=otp))
        Mail.send_email(msg) 

    def send_my_qrcode(email):
        msg = Mail.make_qr_mime(
            mail_to = email,
            subject='QRコード | ES3 Lab.',
            body='QRコードを添付しました．\nこのQRコードは安全に保存してください．\n\n神戸大学大学院工学研究科・工学部 情報通信研究室（ES3）')
        Mail.send_email(msg) 



#router
@app.route("/")
def home():
    if "user" in session:
        return redirect(url_for("user"))
    if "otp" in session:
        return redirect(url_for("otp"))
    return redirect(url_for("signin"))

@app.route('/signup',methods=['POST', 'GET'])
def signup():
    if "user" in session:
        return redirect(url_for("user"))
    if "otp" in session:
        return redirect(url_for("otp"))
    if request.method == "POST": 
        email = request.form["email"]
        password = request.form["password"]
        if email == "" or password == "":
            flash("入力してください．")
            return redirect(url_for("signup"))
        user = User()
        user.email = request.form["email"]
        user.password = hashing_password(request.form["password"])
        user.qrcode = create_qr()
        try:
            db.session.add(user)
            db.session.commit()
            Mail.send_my_qrcode(email=email)
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
    if "otp" in session:
        return redirect(url_for("otp"))
    if request.method == "POST":     
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
                otp = create_otp()
                Mail.send_my_message(email=email, otp=otp)
                session["otp"] = otp
                return redirect(url_for("otp"))
        flash("ユーザーが存在しません．") 
    return render_template("signin.html")
    
@app.route("/otp",methods=['POST', 'GET'])
def otp():
    if "user" in session:
        return redirect(url_for("user"))
    if request.method == "POST":
        get_otp = request.form["otp"]
        token_otp = session["otp"]
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
    return redirect(url_for("otp"))

@app.route("/logout")
def logout():
    session.pop("user", None)
    session.pop("otp", None)
    return redirect(url_for("signin"))

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)