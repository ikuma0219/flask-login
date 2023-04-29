from flask import Flask,request,render_template, redirect, url_for, session
from datetime import timedelta
from flask_sqlalchemy import SQLAlchemy


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
   

@app.route("/")
def home():
    return redirect(url_for("signin"))

@app.route('/signup',methods=['POST', 'GET'])
def signup():
    if request.method == "POST":  
        user = User()
        user.email = request.form["email"]
        user.password = request.form["password"]
        
        db.session.add(user)
        db.session.commit()
        return redirect(url_for("signin"))
    
    else:
        return render_template("signup.html")

@app.route('/signin',methods=['POST', 'GET'])
def signin():
    if request.method == "POST":
        if request.form["btn1"] == "新規登録":
            return redirect(url_for("signup"))
        else:
            session.permanent =True      
            user = request.form["email"]
        
            user = session.query(User.email, User.password).all()
            if User.email == request.form["email"] and User.password == request.form["password"]:
                session["user"]=user
                return redirect(url_for("user"))
            else:
                return redirect(url_for("signup"))
    else:
        if "user" in session:
            return redirect(url_for("user"))
        else:
            return render_template("signin.html")
    
@app.route("/user",methods=['POST', 'GET'])
def user():
    if request.method == "POST":
        return redirect(url_for("logout"))
    else:
        if "user" in session:
            return render_template("user.html", user= user.email)
        else:
            return redirect(url_for("signin"))
        

@app.route("/logout")
def logout():
    session.pop("user", None)
    return redirect(url_for("signin"))

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)