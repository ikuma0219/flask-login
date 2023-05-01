import hashlib
def hashing_password(password:str):
    return hashlib.sha256(password.encode('utf-8')).hexdigest()

def verify_password(hashed_password:str, password:str):
    return hashed_password == hashlib.sha256(password.encode('utf-8')).hexdigest()