from __future__ import print_function

import logging
import grpc
import qrcode_pb2
import qrcode_pb2_grpc


def qr_client():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = qrcode_pb2_grpc.QrgeneraterStub(channel)
        response = stub.QrCode(qrcode_pb2.QrRequest())
    return response.message

if __name__ == '__main__':
    logging.basicConfig()
    qr_client()