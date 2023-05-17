import subprocess

command = ["python","server/api/grpc/qrcode_server.py"]           
proc = subprocess.Popen(command)
proc.communicate()