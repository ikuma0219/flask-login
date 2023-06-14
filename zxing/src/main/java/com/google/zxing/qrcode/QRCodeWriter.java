/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.qrcode;

import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * This object renders a QR Code as a BitMatrix 2D array of greyscale values.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class QRCodeWriter implements Writer {

	private static final int QUIET_ZONE_SIZE = 4;

	@Override
	public BitMatrix encode(String contents, BarcodeFormat format, int width, int height)
			throws WriterException {

		return encode(contents, format, width, height, null);
	}

	@Override
	public BitMatrix encode(String contents,
			BarcodeFormat format,
			int width,
			int height,
			Map<EncodeHintType, ?> hints) {
		return encode(contents, format, width, height, null);
	}

	@Override
	public BitMatrix encode(String contents,
			BarcodeFormat format,
			int width,
			int height,
			Map<EncodeHintType, ?> hints,
			int[] keiretu) throws WriterException {

		if (contents.isEmpty()) {
			throw new IllegalArgumentException("Found empty contents");
		}

		if (format != BarcodeFormat.QR_CODE) {
			throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
		}

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +
					height);
		}

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = QUIET_ZONE_SIZE;
		if (hints != null) {
			if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
				errorCorrectionLevel = ErrorCorrectionLevel
						.valueOf(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
			}
			if (hints.containsKey(EncodeHintType.MARGIN)) {
				quietZone = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
			}
		}
		// ここ変更すればよさそう
		QRCode code = Encoder.encode(contents, errorCorrectionLevel, hints, keiretu);
		return renderResult(code, width, height, quietZone);
	}

	@Override
	public BitMatrix[] encode(String contents, BarcodeFormat format, int width, int height,
			Map<EncodeHintType, ?> hints,
			int[] keiretu, int maskpattern) throws WriterException {
		if (contents.isEmpty()) {
			throw new IllegalArgumentException("Found empty contents");
		}

		if (format != BarcodeFormat.QR_CODE) {
			throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
		}

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +
					height);
		}

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = QUIET_ZONE_SIZE;
		if (hints != null) {
			if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
				errorCorrectionLevel = ErrorCorrectionLevel
						.valueOf(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
			}
			if (hints.containsKey(EncodeHintType.MARGIN)) {
				quietZone = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
			}
		}
		// ここ変更すればよさそう
		QRCode[] code = Encoder.blackOutEncode(contents, errorCorrectionLevel, hints, keiretu, maskpattern);
		return renderResultkai(code, width, height, quietZone);
	}

	// Note that the input matrix uses 0 == white, 1 == black, while the output
	// matrix uses
	// 0 == black, 255 == white (i.e. an 8 bit greyscale bitmap).
	private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (quietZone * 2);
		int qrHeight = inputHeight + (quietZone * 2);
		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);

		int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		// Padding includes both the quiet zone and the extra white pixels to
		// accommodate the requested
		// dimensions. For example, if input is 25x25 the QR will be 33x33 including the
		// quiet zone.
		// If the requested size is 200x160, the multiple will be 4, for a QR of
		// 132x132. These will
		// handle all the padding from 100x100 (the actual QR) up to 200x160.
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(outputWidth, outputHeight);

		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}

		return output;
	}

	private static BitMatrix[] renderResultkai(QRCode[] code, int width, int height, int quietZone) {
		ByteMatrix inputa = code[0].getMatrix();
		ByteMatrix inputb = code[1].getMatrix();
		if (inputa == null) {
			throw new IllegalStateException();
		}
		int inputWidth = inputa.getWidth();
		int inputHeight = inputa.getHeight();
		int qrWidth = inputWidth + (quietZone * 2);
		int qrHeight = inputHeight + (quietZone * 2);
		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);

		int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		// Padding includes both the quiet zone and the extra white pixels to
		// accommodate the requested
		// dimensions. For example, if input is 25x25 the QR will be 33x33 including the
		// quiet zone.
		// If the requested size is 200x160, the multiple will be 4, for a QR of
		// 132x132. These will
		// handle all the padding from 100x100 (the actual QR) up to 200x160.
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(outputWidth, outputHeight);

		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (inputa.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}

		if (inputb == null) {
			throw new IllegalStateException();
		}
		int inputWidthb = inputb.getWidth();
		int inputHeightb = inputb.getHeight();
		int qrWidthb = inputWidth + (quietZone * 2);
		int qrHeightb = inputHeight + (quietZone * 2);
		int outputWidthb = Math.max(width, qrWidthb);
		int outputHeightb = Math.max(height, qrHeightb);

		int multipleb = Math.min(outputWidthb / qrWidthb, outputHeightb / qrHeightb);
		// Padding includes both the quiet zone and the extra white pixels to
		// accommodate the requested
		// dimensions. For example, if input is 25x25 the QR will be 33x33 including the
		// quiet zone.
		// If the requested size is 200x160, the multiple will be 4, for a QR of
		// 132x132. These will
		// handle all the padding from 100x100 (the actual QR) up to 200x160.
		int leftPaddingb = (outputWidthb - (inputWidthb * multipleb)) / 2;
		int topPaddingb = (outputHeightb - (inputHeightb * multipleb)) / 2;

		BitMatrix outputb = new BitMatrix(outputWidthb, outputHeightb);

		for (int inputYb = 0, outputYb = topPaddingb; inputYb < inputHeightb; inputYb++, outputYb += multipleb) {
			// Write the contents of this row of the barcode
			for (int inputXb = 0, outputXb = leftPaddingb; inputXb < inputWidthb; inputXb++, outputXb += multipleb) {
				if (inputb.get(inputXb, inputYb) == 1) {
					outputb.setRegion(outputXb, outputYb, multipleb, multipleb);
				}
			}
		}
		BitMatrix[] result = { output, outputb };
		return result;
	}

}
