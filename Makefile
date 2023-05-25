.PHONY: proto-gen
proto-gen:
	rm -f server/services/gen/src/main/java/proto/*.java \
	&& rm -f server/services/api/proto*.py \
	&& protoc -I=server/proto --java_out=server/services/gen/src/main/java qrcode.proto \
	&& protoc -I=server/proto --python_out=server/services/api/proto qrcode.proto