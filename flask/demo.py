from flask import Flask,request,render_template, redirect, url_for, session
from datetime import timedelta

app = Flask(__name__)
app.secret_key = "hello"
app.permanent_session_lifetime = timedelta(minutes=5)

user_email = "ikuhig4585@gmail.com"
user_pass = "pass"


@app.route("/")
def home():
    return redirect(url_for("login"))

@app.route('/login',methods=['POST', 'GET'])
def login():
    if request.method == "POST":
        session.permanent =True      
        user = request.form["email"]
        
        if user_email == request.form["email"]and user_pass == request.form["password"]:
            session["user"]=user
            return redirect(url_for("user"))
        else:
            return redirect(url_for("login"))
    else:
        if "user" in session:
            return redirect(url_for("user"))
        else:
            return render_template("login.html")
    
@app.route("/user",methods=['POST', 'GET'])
def user():
    if request.method == "POST":
        return redirect(url_for("logout"))
    else:
        if "user" in session:
            return render_template("content.html", user=user_email)
        else:
            return redirect(url_for("login"))
        

@app.route("/logout")
def logout():
    session.pop("user", None)
    return redirect(url_for("login"))

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)