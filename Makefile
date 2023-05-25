.PHONY: proto-gen
proto-gen:
	rm -f server/services/api/proto*.py \
	&& python -m grpc_tools.protoc -Iserver/proto --python_out=server/services/api/proto --pyi_out=server/services/api/proto --grpc_python_out=server/services/api/proto qrcode.proto \