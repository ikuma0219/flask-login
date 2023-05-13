from concurrent import futures
import logging

import grpc
import qrcode_pb2
import qrcode_pb2_grpc
import pyotp
import qrcode
import base64

def create_otp(digits) -> str:
    secret_key = pyotp.random_base32()
    totp =pyotp.TOTP(s=secret_key, digits=digits)
    return totp.now()

def create_qr():
    img = qrcode.make(create_otp(digits=4))
    img.save("test.png")
    file_data = open ("test.png", "rb").read()
    b64_data = base64.b64encode(file_data).decode('utf-8')
    return b64_data


class Greeter(qrcode_pb2_grpc.GreeterServicer):

    def QrCode(self, request, context):
        return qrcode_pb2.QrReply(message=create_qr())


def serve():
    port = '50051'
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    qrcode_pb2_grpc.add_GreeterServicer_to_server(Greeter(), server)
    server.add_insecure_port('[::]:' + port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()
