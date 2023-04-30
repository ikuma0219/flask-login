from flask import Flask,flash,request,render_template, redirect, url_for, session
from datetime import timedelta
from flask_sqlalchemy import SQLAlchemy
import pymysql
from sqlalchemy import exc
import hashlib

db = SQLAlchemy()
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://{user}:{password}@{host}/{db_name}?charset=utf8'.format(**{
    'user':"root",
    'password':"Ikuma0219!",
    'host':"localhost",
    'db_name':"myDB"
    })
app.config['SQLALCHEMY_TRACK_MODIFICAIONS'] = False
app.secret_key = "hello"
app.permanent_session_lifetime = timedelta(minutes=5)
db.init_app(app)

class User(db.Model):
    __tablename__ = "users"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(255), unique=True, nullable=False)
    password = db.Column(db.String(255), unique=False, nullable=False)

with app.app_context():
    db.create_all()
   
def hashing_password(password:str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()

def verify_password(hashed_password:str, password:str):
    return hashed_password == hashlib.sha256(password.encode('utf-8')).hexdigest()

@app.route("/")
def home():
    return redirect(url_for("signin"))

@app.route('/signup',methods=['POST', 'GET'])
def signup():
    if request.method == "POST": 
        session.permanent =True
        user = User()
        user.email = request.form["email"]
        user.password = hashing_password(request.form["password"])
        try:
            db.session.add(user)
            db.session.commit()
            return redirect(url_for("signin"))
        except pymysql.err.IntegrityError as e:
            db.session.rollback()
        except exc.IntegrityError as e:
            db.session.rollback()
        except Exception as e:
            db.session.rollback()
            return redirect(url_for("signup"))      
    return render_template("signup.html")

@app.route('/signin',methods=['POST', 'GET'])
def signin():  
        if request.method == "POST":
            session.permanent =True      
            email = request.form["email"]
            password = request.form["password"]
            if email == "" and password == "":
                return redirect(url_for("signin"))
            users = db.session.query(User).filter(User.email == email).limit(1).all()
            for user in users:
                if user != None:
                    hashed_password = user.password
                if verify_password(hashed_password=hashed_password, password = request.form["password"]) == False:
                    return redirect(url_for("signin"))
                session["user"] = email
                return redirect(url_for("user"))
        return render_template("signin.html")
    
@app.route("/user",methods=['POST', 'GET'])
def user():
    if request.method == "POST":
        return redirect(url_for("logout"))
    else:
        if "user" in session:
            user = session["user"]
            return render_template("user.html", user = user)
        else:
            return redirect(url_for("signin"))
        

@app.route("/logout")
def logout():
    session.pop("user", None)
    return redirect(url_for("signin"))

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)