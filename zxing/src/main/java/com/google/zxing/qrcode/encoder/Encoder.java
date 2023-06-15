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

package com.google.zxing.qrcode.encoder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;
import com.rs.GF;
import com.rs.GFpoly;
import com.rs.RS;

/**
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 */
public final class Encoder {
  // public static int mask=-1;
  private static int mask = -1;

  // The original table is defined in the table 5 of JISX0510:2004 (p.19).
  private static final int[] ALPHANUMERIC_TABLE = {
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 0x00-0x0f
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 0x10-0x1f
      36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, // 0x20-0x2f
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, // 0x30-0x3f
      -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 0x40-0x4f
      25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, // 0x50-0x5f
  };

  static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

  private Encoder() {
  }

  // The mask penalty calculation is complicated. See Table 21 of JISX0510:2004
  // (p.45) for details.
  // Basically it applies four rules and summate all penalties.
  private static int calculateMaskPenalty(ByteMatrix matrix) {
    return MaskUtil.applyMaskPenaltyRule1(matrix)
        + MaskUtil.applyMaskPenaltyRule2(matrix)
        + MaskUtil.applyMaskPenaltyRule3(matrix)
        + MaskUtil.applyMaskPenaltyRule4(matrix);
  }

  public static int chooseMaskByBlank(BitArray bits,
      ErrorCorrectionLevel ecLevel,
      Version version,
      ByteMatrix matrix) throws WriterException {// 白モジュールが最も多くなるマスクパターンの選択

    int minPenalty = 0; // Lower penalty is better.
    int bestMaskPattern = -1;
    // We try all mask patterns to choose the best one.
    for (int maskPattern = 0; maskPattern < QRCode.NUM_MASK_PATTERNS; maskPattern++) {
      MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
      int blankcount = MaskUtil.countBlank(matrix);
      if (blankcount > minPenalty) {
        minPenalty = blankcount;
        bestMaskPattern = maskPattern;
      }
      // System.out.println("maskPattern: "+ maskPattern);
      // System.out.println("numnberblank: "+ blankcount);
      // System.out.println();
    }
    return bestMaskPattern;
  }

  public static int[] matrixToRow(ByteMatrix matrix) {// QRコード(v1-L)で表現されているビット列（マスクパターン付与済み）をシンボル列に
    byte[][] bits = matrix.getArray();// QRコードの2次元配列
    int[] bit = new int[208]; // 符号語部分のbit列
    // for(int i=0;i<bits.length;i++) {
    // for(int j=0;j<bits[0].length;j++) {
    // if(((i>=0&&i<=8)&&(j>=9&&j<=12))||(i>=9&&i<=12)||((i>=13&&i<=20)&&(j>=9))) {
    // bit
    // }
    // }
    // }
    int i = 20;// row of QR
    int j = 20;// column of QR
    int bitcount = 0; //
    // while(bitcount<=208) {
    while (j > 12) {
      while (i > 8) {
        bit[bitcount] = bits[i][j];
        bitcount++;
        bit[bitcount] = bits[i][j - 1];
        bitcount++;
        i--;
      }
      j = j - 2;
      i = 9;
      while (i <= 20) {
        bit[bitcount] = bits[i][j];
        bitcount++;
        bit[bitcount] = bits[i][j - 1];
        bitcount++;
        i++;
      }
      j = j - 2;
      i = 20;
    }
    // System.out.print("検証"+ bitcount);
    // System.out.println();
    while (j >= 9 && j <= 12) {
      while (i >= 0) {
        if (i != 6) {
          bit[bitcount] = bits[i][j];
          bitcount++;
          bit[bitcount] = bits[i][j - 1];
          bitcount++;
          i--;
        }
        if (i == 6)
          i--;
      }
      i = 0;
      j = j - 2;
      while (i <= 20) {
        if (i != 6) {
          bit[bitcount] = bits[i][j];
          bitcount++;
          bit[bitcount] = bits[i][j - 1];
          bitcount++;
          i++;
        }
        if (i == 6)
          i++;
      }
      j = j - 2;
      i = 20;

    }
    // System.out.print("検証"+ bitcount);
    // System.out.println();
    while (j >= 0 && j <= 8) {
      if (j != 6) {
        i = 12;
        if (j != 5) {
          while (i > 8) {
            bit[bitcount] = bits[i][j];
            bitcount++;
            bit[bitcount] = bits[i][j - 1];
            bitcount++;
            i--;
          }
          j = j - 2;
        }
        i = 9;
        if (j == 6)
          j = 5;
        while (i <= 12) {
          bit[bitcount] = bits[i][j];
          bitcount++;
          bit[bitcount] = bits[i][j - 1];
          bitcount++;
          i++;
        }
        j = j - 2;
        i = 12;
      }

    }

    // }

    // 検証
    // System.out.println("bitcount: "+ bitcount);
    int testcount = 0;
    for (int l = 0; l < bit.length; l++) {
      if (testcount == 8) {
        // System.out.println();
        testcount = 0;
      }
      // System.out.print(bit[l]);
      testcount++;
    }

    int[][] presult = new int[26][8];
    int count = 0;
    int symbolorder = 0;
    for (int l = 0; l < bit.length; l++) {
      if (count == 8) {
        symbolorder++;
        count = 0;
      }
      presult[symbolorder][count] = bit[l];
      count++;
    }
    int[] result = GF.bittosymbol(bit);

    return result;
  }

  /**
   * @param content text to encode
   * @param ecLevel error correction level to use
   * @return {@link QRCode} representing the encoded QR code
   * @throws WriterException if encoding can't succeed, because of for example
   *                         invalid content
   *                         or configuration
   */
  public static QRCode encode(String content, ErrorCorrectionLevel ecLevel, int[] keiretu) throws WriterException {
    return encode(content, ecLevel, null, keiretu);
  }

  public static int getMask(String content,
      ErrorCorrectionLevel ecLevel,
      Map<EncodeHintType, ?> hints, int[] keiretu) throws WriterException {
    // Determine what character encoding has been specified by the caller, if any
    String encoding = DEFAULT_BYTE_MODE_ENCODING;
    boolean hasEncodingHint = hints != null && hints.containsKey(EncodeHintType.CHARACTER_SET);
    if (hasEncodingHint) {
      encoding = hints.get(EncodeHintType.CHARACTER_SET).toString();
    }

    // Pick an encoding mode appropriate for the content. Note that this will not
    // attempt to use
    // multiple modes / segments even if that were more efficient. Twould be nice.
    Mode mode = chooseMode(content, encoding);

    // This will store the header information, like mode and
    // length, as well as "header" segments like an ECI segment.
    BitArray headerBits = new BitArray();

    // Append ECI segment if applicable
    if (mode == Mode.BYTE && (hasEncodingHint || !DEFAULT_BYTE_MODE_ENCODING.equals(encoding))) {
      CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding);
      if (eci != null) {
        appendECI(eci, headerBits);
      }
    }

    // (With ECI in place,) Write the mode marker
    appendModeInfo(mode, headerBits);

    // Collect data within the main segment, separately, to count its size if
    // needed. Don't add it to
    // main payload yet.
    BitArray dataBits = new BitArray();
    appendBytes(content, mode, dataBits, encoding);

    Version version;
    if (hints != null && hints.containsKey(EncodeHintType.QR_VERSION)) {
      int versionNumber = Integer.parseInt(hints.get(EncodeHintType.QR_VERSION).toString());
      version = Version.getVersionForNumber(versionNumber);
      int bitsNeeded = calculateBitsNeeded(mode, headerBits, dataBits, version);
      if (!willFit(bitsNeeded, version, ecLevel)) {
        throw new WriterException("Data too big for requested version");
      }
    } else {
      version = recommendVersion(ecLevel, mode, headerBits, dataBits);
    }

    BitArray headerAndDataBits = new BitArray();
    headerAndDataBits.appendBitArray(headerBits);
    // Find "length" of main segment and write it
    int numLetters = mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length();
    appendLengthInfo(numLetters, version, mode, headerAndDataBits);
    // Put data together into the overall payload
    headerAndDataBits.appendBitArray(dataBits);

    Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
    int numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords();

    // Terminate the bits properly.
    terminateBits(numDataBytes, headerAndDataBits);
    // System.out.println(mode.getBits());
    // System.out.println(version.getTotalCodewords());
    // System.out.println(numDataBytes);
    // System.out.println(ecBlocks.getNumBlocks());
    // Interleave data bits with error correction code.
    BitArray finalBits = interleaveWithECBytes(headerAndDataBits,
        version.getTotalCodewords(),
        numDataBytes,
        ecBlocks.getNumBlocks());
    // for(int i=0;i<finalBits.getBitArray().length;i++) {
    // System.out.print(finalBits.getBitArray()[i]+", ");
    // }
    // System.out.println();
    GF gf = new GF(0x011D, 256, 0);
    RS rs = new RS(gf);

    int[] c = gf.bitstosymbol(finalBits.getBitArray());
    // System.out.println(c.length);
    // for(int i=0;i<c.length;i++) {
    // System.out.print(c[i]+", ");
    // }
    // System.out.println();
    int[] a = { 162, 179, 140, 4, 55, 0, 40, 84, 55, 136, 55, 136, 55, 136, 55, 136, 196, 136, 55, 136, 95, 193, 35,
        150, 19, 25 };
    // int[] bits = gf.symboltobits(a);
    GFpoly poly = new GFpoly(gf, c);
    // for(int i=0;i<7;i++) {
    // System.out.print(poly.substitute(gf.getaTable(i))+" ");
    // }
    // System.out.println();
    // 32, 35, 156, 63, 160, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17,
    // 236, 17, 130, 136, 123, 44, 61, 151, 10
    int[] k = { 32, 35, 155, 63, 160, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17 };
    int[] b = rs.encode(k, 8);
    // 5, 210, 12, 221, 142, 141, 202, 158, 157, 0, 122, 253, 122, 113, 122, 87,
    // 223, 114, 122, 2, 137, 61, 214, 43, 143, 176,
    // int[] bits = gf.symboltobits(b);
    // for(int i=0;i<bits.length;i++) {
    // System.out.print(bits[i]+", ");
    // }
    //
    // //-63323132, 931673861, 931673992, 94229896, -1840414189, -1404478543, 40080,
    // 7
    // //481531908, 931673856, 931673992, -1191012728, 1162377190, 1274563720, 57620
    // //int[] fbit = new int[8*version.getTotalCodewords()];
    //
    // GF gf = new GF(0x011D,256,0);
    // int[] abebits = {481531908, 931673856, 931673992, -1191012728, 1162377190,
    // 1274563720, 57620};
    //
    // int[] fbit = new int[finalBits.getBitArray().length];
    // for(int i=0;i<finalBits.getBitArray().length;i++) {
    // finalBits.getBitArray()[i] = finalBits.getBitArray()[i] ^abebits[i];
    // }
    // System.out.println(finalBits.getBitArray().length);
    // int[] bit = gf.bitstobit(finalBits.getBitArray());
    // for(int i=0;i<bit.length;i++) {
    // System.out.print(bit[i]+", ");
    // }
    // GF gf = new GF(0x011D,256,0);
    // RS rs = new RS(gf);
    // int[] k = new int[19];//44
    // k[0] = 1;
    // int[] c = new int[26];
    // //191, 62, 48, 125, 86, 217, 174, 235, 80, 93, 96, 238, 90, 91, 160, 58, 111,
    // 92, 100, 164, 35, 28, 209, 92, 98, 122,
    // //45, 62, 126, 125, 92, 244, 174, 235, 80, 93, 204, 238, 175, 91, 160, 58,
    // 111, 92, 100, 164, 35, 33, 209, 92, 206, 122,
    int[] clog = { 32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236, 15, 236, 17, 9, 17, 236, 4, 142, 111, 249,
        23, 20, 227 };
    for (int i = 0; i < c.length; i++) {
      c[i] = gf.getaTable(clog[i]);
    }

    // int[] f = {32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236, 15, 236,
    // 17, 9, 17, 236, 4, 142, 111, //249, 23, 20, 227};
    //// c = rs.encode(k, 8);
    //// c = rs.randomTR(c,4);
    int[] bits = gf.symboltobits(keiretu);
    finalBits = new BitArray(bits, 208);
    //

    QRCode qrCode = new QRCode();

    qrCode.setECLevel(ecLevel);
    qrCode.setMode(mode);
    qrCode.setVersion(version);

    // Choose the mask pattern and set to "qrCode".
    int dimension = version.getDimensionForVersion();
    ByteMatrix matrix = new ByteMatrix(dimension, dimension);
    // int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
    return chooseMaskByBlank(finalBits, ecLevel, version, matrix);
  }

  public static QRCode encode(String content,
      ErrorCorrectionLevel ecLevel,
      Map<EncodeHintType, ?> hints, int[] keiretu) throws WriterException {

    // Determine what character encoding has been specified by the caller, if any
    String encoding = DEFAULT_BYTE_MODE_ENCODING;
    boolean hasEncodingHint = hints != null && hints.containsKey(EncodeHintType.CHARACTER_SET);
    if (hasEncodingHint) {
      encoding = hints.get(EncodeHintType.CHARACTER_SET).toString();
    }

    // Pick an encoding mode appropriate for the content. Note that this will not
    // attempt to use
    // multiple modes / segments even if that were more efficient. Twould be nice.
    Mode mode = chooseMode(content, encoding);

    // This will store the header information, like mode and
    // length, as well as "header" segments like an ECI segment.
    BitArray headerBits = new BitArray();

    // Append ECI segment if applicable
    if (mode == Mode.BYTE && (hasEncodingHint || !DEFAULT_BYTE_MODE_ENCODING.equals(encoding))) {
      CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding);
      if (eci != null) {
        appendECI(eci, headerBits);
      }
    }

    // (With ECI in place,) Write the mode marker
    appendModeInfo(mode, headerBits);

    // Collect data within the main segment, separately, to count its size if
    // needed. Don't add it to
    // main payload yet.
    BitArray dataBits = new BitArray();
    appendBytes(content, mode, dataBits, encoding);

    Version version;
    if (hints != null && hints.containsKey(EncodeHintType.QR_VERSION)) {
      int versionNumber = Integer.parseInt(hints.get(EncodeHintType.QR_VERSION).toString());
      version = Version.getVersionForNumber(versionNumber);
      int bitsNeeded = calculateBitsNeeded(mode, headerBits, dataBits, version);
      if (!willFit(bitsNeeded, version, ecLevel)) {
        throw new WriterException("Data too big for requested version");
      }
    } else {
      version = recommendVersion(ecLevel, mode, headerBits, dataBits);
    }

    BitArray headerAndDataBits = new BitArray();
    headerAndDataBits.appendBitArray(headerBits);
    // Find "length" of main segment and write it
    int numLetters = mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length();
    appendLengthInfo(numLetters, version, mode, headerAndDataBits);
    // Put data together into the overall payload
    headerAndDataBits.appendBitArray(dataBits);

    Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
    int numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords();

    // Terminate the bits properly.
    terminateBits(numDataBytes, headerAndDataBits);
    // System.out.println(mode.getBits());
    // System.out.println(version.getTotalCodewords());
    // System.out.println(numDataBytes);
    // System.out.println(ecBlocks.getNumBlocks());
    // Interleave data bits with error correction code.
    BitArray finalBits = interleaveWithECBytes(headerAndDataBits,
        version.getTotalCodewords(),
        numDataBytes,
        ecBlocks.getNumBlocks());
    // for(int i=0;i<finalBits.getBitArray().length;i++) {
    // System.out.print(finalBits.getBitArray()[i]+", ");
    // }
    // System.out.println();
    GF gf = new GF(0x011D, 256, 0);
    RS rs = new RS(gf);

    int[] c = gf.bitstosymbol(finalBits.getBitArray());
    // System.out.println(c.length);
    // for(int i=0;i<c.length;i++) {
    // System.out.print(c[i]+", ");
    // }
    // System.out.println();
    int[] a = { 162, 179, 140, 4, 55, 0, 40, 84, 55, 136, 55, 136, 55, 136, 55, 136, 196, 136, 55, 136, 95, 193, 35,
        150, 19, 25 };
    // int[] bits = gf.symboltobits(a);
    GFpoly poly = new GFpoly(gf, c);
    // for(int i=0;i<7;i++) {
    // System.out.print(poly.substitute(gf.getaTable(i))+" ");
    // }
    // System.out.println();
    // 32, 35, 156, 63, 160, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17,
    // 236, 17, 130, 136, 123, 44, 61, 151, 10
    int[] k = { 32, 35, 155, 63, 160, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17 };
    int[] b = rs.encode(k, 8);
    // 5, 210, 12, 221, 142, 141, 202, 158, 157, 0, 122, 253, 122, 113, 122, 87,
    // 223, 114, 122, 2, 137, 61, 214, 43, 143, 176,
    // int[] bits = gf.symboltobits(b);
    // for(int i=0;i<bits.length;i++) {
    // System.out.print(bits[i]+", ");
    // }
    //
    // //-63323132, 931673861, 931673992, 94229896, -1840414189, -1404478543, 40080,
    // 7
    // //481531908, 931673856, 931673992, -1191012728, 1162377190, 1274563720, 57620
    // //int[] fbit = new int[8*version.getTotalCodewords()];
    //
    // GF gf = new GF(0x011D,256,0);
    // int[] abebits = {481531908, 931673856, 931673992, -1191012728, 1162377190,
    // 1274563720, 57620};
    //
    // int[] fbit = new int[finalBits.getBitArray().length];
    // for(int i=0;i<finalBits.getBitArray().length;i++) {
    // finalBits.getBitArray()[i] = finalBits.getBitArray()[i] ^abebits[i];
    // }
    // System.out.println(finalBits.getBitArray().length);
    // int[] bit = gf.bitstobit(finalBits.getBitArray());
    // for(int i=0;i<bit.length;i++) {
    // System.out.print(bit[i]+", ");
    // }
    // GF gf = new GF(0x011D,256,0);
    // RS rs = new RS(gf);
    // int[] k = new int[19];//44
    // k[0] = 1;
    // int[] c = new int[26];
    // //191, 62, 48, 125, 86, 217, 174, 235, 80, 93, 96, 238, 90, 91, 160, 58, 111,
    // 92, 100, 164, 35, 28, 209, 92, 98, 122,
    // //45, 62, 126, 125, 92, 244, 174, 235, 80, 93, 204, 238, 175, 91, 160, 58,
    // 111, 92, 100, 164, 35, 33, 209, 92, 206, 122,
    int[] clog = { 32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236, 15, 236, 17, 9, 17, 236, 4, 142, 111, 249,
        23, 20, 227 };
    for (int i = 0; i < c.length; i++) {
      c[i] = gf.getaTable(clog[i]);
    }

    // int[] f = {32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236, 15, 236,
    // 17, 9, 17, 236, 4, 142, 111, //249, 23, 20, 227};
    //// c = rs.encode(k, 8);
    //// c = rs.randomTR(c,4);
    int[] bits = gf.symboltobits(keiretu);
    finalBits = new BitArray(bits, 208);
    //

    QRCode qrCode = new QRCode();

    qrCode.setECLevel(ecLevel);
    qrCode.setMode(mode);
    qrCode.setVersion(version);

    // Choose the mask pattern and set to "qrCode".
    int dimension = version.getDimensionForVersion();
    ByteMatrix matrix = new ByteMatrix(dimension, dimension);
    // int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
    int maskPattern = chooseMaskByBlank(finalBits, ecLevel, version, matrix);
    if (mask != -1) {
      maskPattern = mask;
    } else {
      mask = maskPattern;
    }

    // System.out.println("mask = " + maskPattern);;
    // maskpattern = chooseMaskByBlank(finalBits, ecLevel, version, matrix);
    qrCode.setMaskPattern(maskPattern);

    // Build the matrix and set it to "qrCode".
    MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, matrix);
    byte[][] test = matrix.getArray();
    // System.out.println();
    // System.out.println("bitarray（マスクパターン付加済み）");
    // for(int i=0;i<test.length;i++) {
    // for(int j=0;j<test[0].length;j++) {
    // System.out.print(test[i][j]+" ");
    // }
    // System.out.println();
    // }
    int[] sy = matrixToRow(matrix);
    qrCode.setMatrix(matrix);

    return qrCode;
  }

  public static QRCode[] blackOutEncode(String content,
      ErrorCorrectionLevel ecLevel,
      Map<EncodeHintType, ?> hints, int[] keiretu, int maskpattern) throws WriterException {

    // Determine what character encoding has been specified by the caller, if any
    String encoding = DEFAULT_BYTE_MODE_ENCODING;
    boolean hasEncodingHint = hints != null && hints.containsKey(EncodeHintType.CHARACTER_SET);
    if (hasEncodingHint) {
      encoding = hints.get(EncodeHintType.CHARACTER_SET).toString();
    }

    // Pick an encoding mode appropriate for the content. Note that this will not
    // attempt to use
    // multiple modes / segments even if that were more efficient. Twould be nice.
    Mode mode = chooseMode(content, encoding);

    // This will store the header information, like mode and
    // length, as well as "header" segments like an ECI segment.
    BitArray headerBits = new BitArray();

    // Append ECI segment if applicable
    if (mode == Mode.BYTE && (hasEncodingHint || !DEFAULT_BYTE_MODE_ENCODING.equals(encoding))) {
      CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding);
      if (eci != null) {
        appendECI(eci, headerBits);
      }
    }

    // (With ECI in place,) Write the mode marker
    appendModeInfo(mode, headerBits);

    // Collect data within the main segment, separately, to count its size if
    // needed. Don't add it to
    // main payload yet.
    BitArray dataBits = new BitArray();
    appendBytes(content, mode, dataBits, encoding);

    Version version;
    if (hints != null && hints.containsKey(EncodeHintType.QR_VERSION)) {
      int versionNumber = Integer.parseInt(hints.get(EncodeHintType.QR_VERSION).toString());
      version = Version.getVersionForNumber(versionNumber);
      int bitsNeeded = calculateBitsNeeded(mode, headerBits, dataBits, version);
      if (!willFit(bitsNeeded, version, ecLevel)) {
        throw new WriterException("Data too big for requested version");
      }
    } else {
      version = recommendVersion(ecLevel, mode, headerBits, dataBits);
    }

    BitArray headerAndDataBits = new BitArray();
    headerAndDataBits.appendBitArray(headerBits);
    // Find "length" of main segment and write it
    int numLetters = mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length();
    appendLengthInfo(numLetters, version, mode, headerAndDataBits);
    // Put data together into the overall payload
    headerAndDataBits.appendBitArray(dataBits);

    Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
    int numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords();

    // Terminate the bits properly.
    terminateBits(numDataBytes, headerAndDataBits);
    // System.out.println(mode.getBits());
    // System.out.println(version.getTotalCodewords());
    // System.out.println(numDataBytes);
    // System.out.println(ecBlocks.getNumBlocks());
    // Interleave data bits with error correction code.
    BitArray finalBits = interleaveWithECBytes(headerAndDataBits,
        version.getTotalCodewords(),
        numDataBytes,
        ecBlocks.getNumBlocks());
    for (int i = 0; i < finalBits.getBitArray().length; i++) {
      // System.out.print(finalBits.getBitArray()[i]+", ");
    }
    // System.out.println();
    GF gf = new GF(0x011D, 256, 0);
    RS rs = new RS(gf);

    int[] c = gf.bitstosymbol(finalBits.getBitArray());
    // System.out.println(c.length);
    for (int i = 0; i < c.length; i++) {
      // System.out.print(c[i]+", ");
    }
    // System.out.println();
    int[] a = { 162, 179, 140, 4, 55, 0, 40, 84, 55, 136, 55, 136, 55, 136, 55, 136, 196, 136, 55, 136, 95, 193, 35,
        150, 19, 25 };
    // int[] bits = gf.symboltobits(a);
    GFpoly poly = new GFpoly(gf, c);
    for (int i = 0; i < 7; i++) {
      // System.out.print(poly.substitute(gf.getaTable(i))+" ");
    }
    // System.out.println();
    // 32, 35, 156, 63, 160, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17,
    // 236, 17, 130, 136, 123, 44, 61, 151, 10
    int[] k = { 32, 35, 155, 63, 160, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17 };
    int[] b = rs.encode(k, 8);
    // 5, 210, 12, 221, 142, 141, 202, 158, 157, 0, 122, 253, 122, 113, 122, 87,
    // 223, 114, 122, 2, 137, 61, 214, 43, 143, 176,
    // int[] bits = gf.symboltobits(b);
    // for(int i=0;i<bits.length;i++) {
    // System.out.print(bits[i]+", ");
    // }
    //
    //// -63323132, 931673861, 931673992, 94229896, -1840414189, -1404478543, 40080,
    // 7
    //// 481531908, 931673856, 931673992, -1191012728, 1162377190, 1274563720, 57620
    //// int[] fbit = new int[8*version.getTotalCodewords()];
    //
    // GF gf = new GF(0x011D,256,0);
    // int[] abebits = {481531908, 931673856, 931673992, -1191012728, 1162377190,
    // 1274563720, 57620};
    //
    // int[] fbit = new int[finalBits.getBitArray().length];
    // for(int i=0;i<finalBits.getBitArray().length;i++) {
    // finalBits.getBitArray()[i] = finalBits.getBitArray()[i] ^abebits[i];
    // }
    // System.out.println(finalBits.getBitArray().length);
    // int[] bit = gf.bitstobit(finalBits.getBitArray());
    // for(int i=0;i<bit.length;i++) {
    // System.out.print(bit[i]+", ");
    // }
    // GF gf = new GF(0x011D,256,0);
    // RS rs = new RS(gf);
    // int[] k = new int[19];//44
    // k[0] = 1;
    // int[] c = new int[26];
    //// 191, 62, 48, 125, 86, 217, 174, 235, 80, 93, 96, 238, 90, 91, 160, 58, 111,
    // 92, 100, 164, 35, 28, 209, 92, 98, 122,
    //// 45, 62, 126, 125, 92, 244, 174, 235, 80, 93, 204, 238, 175, 91, 160, 58,
    // 111, 92, 100, 164, 35, 33, 209, 92, 206, 122,
    int[] clog = { 32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236, 15, 236, 17, 9, 17, 236, 4, 142, 111, 249,
        23, 20, 227 };
    for (int i = 0; i < c.length; i++) {
      c[i] = gf.getaTable(clog[i]);
    }

    // int[] f = {32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236, 15, 236,
    // 17, 9, 17, 236, 4, 142, 111, //249, 23, 20, 227};
    //// c = rs.encode(k, 8);
    //// c = rs.randomTR(c,4);
    int[] bits = gf.symboltobits(keiretu);
    finalBits = new BitArray(bits, 208);
    //

    QRCode qrCodea = new QRCode();
    QRCode qrCodeb = new QRCode();

    qrCodea.setECLevel(ecLevel);
    qrCodea.setMode(mode);
    qrCodea.setVersion(version);
    qrCodeb.setECLevel(ecLevel);
    qrCodeb.setMode(mode);
    qrCodeb.setVersion(version);

    // Choose the mask pattern and set to "qrCode".
    int dimension = version.getDimensionForVersion();
    ByteMatrix matrixa = new ByteMatrix(dimension, dimension);
    // int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
    // int maskPattern = chooseMaskByBlank(finalBits, ecLevel, version, matrix);
    int maskPattern = maskpattern;
    // System.out.println("mask = " + maskPattern);
    ;
    // maskpattern = chooseMaskByBlank(finalBits, ecLevel, version, matrix);
    qrCodea.setMaskPattern(maskPattern);
    qrCodeb.setMaskPattern(maskPattern);

    // Build the matrix and set it to "qrCode".
    MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, matrixa);
    ByteMatrix matrixb = new ByteMatrix(dimension, dimension);
    MatrixUtil.buildMatrixWithoutDate(ecLevel, version, maskPattern, matrixb);
    // byte[][] test = matrix.getArray();
    // System.out.println();
    // System.out.println("bitarray（マスクパターン付加済み1）");
    // for(int i=0;i<test.length;i++) {
    // for(int j=0;j<test[0].length;j++) {
    // System.out.print(test[i][j]+" ");
    // }
    // System.out.println();
    // }

    int[] sa = matrixToRow(matrixa);
    int[] sb = matrixToRow(matrixb);
    int[] Finalbits = gf.symboltobits(sa);
    int[] bitswd = gf.symboltobits(sb);
    finalBits = new BitArray(Finalbits, 208);
    BitArray Bitswd = new BitArray(bitswd, 208);
    ByteMatrix fainalmatrix = new ByteMatrix(dimension, dimension);
    ByteMatrix fainalmatrixwd = new ByteMatrix(dimension, dimension);
    MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, fainalmatrix);
    MatrixUtil.buildMatrixWithoutDate(ecLevel, version, maskPattern, fainalmatrixwd);
    // test = fainalmatrix.getArray();
    // System.out.println("bitarray（マスクパターン付加済み2）");
    // for(int i=0;i<test.length;i++) {
    // for(int j=0;j<test[0].length;j++) {
    // System.out.print(test[i][j]+" ");
    // }
    // System.out.println();
    // }
    qrCodea.setMatrix(fainalmatrix);
    qrCodeb.setMatrix(fainalmatrixwd);
    // qrCode.setMatrix(fainalmatrixwd);
    QRCode[] qrcode = { qrCodea, qrCodeb };
    return qrcode;
  }

  /**
   * Decides the smallest version of QR code that will contain all of the provided
   * data.
   *
   * @throws WriterException if the data cannot fit in any version
   */
  private static Version recommendVersion(ErrorCorrectionLevel ecLevel,
      Mode mode,
      BitArray headerBits,
      BitArray dataBits) throws WriterException {
    // Hard part: need to know version to know how many bits length takes. But need
    // to know how many
    // bits it takes to know version. First we take a guess at version by assuming
    // version will be
    // the minimum, 1:
    int provisionalBitsNeeded = calculateBitsNeeded(mode, headerBits, dataBits, Version.getVersionForNumber(1));
    Version provisionalVersion = chooseVersion(provisionalBitsNeeded, ecLevel);

    // Use that guess to calculate the right version. I am still not sure this works
    // in 100% of cases.
    int bitsNeeded = calculateBitsNeeded(mode, headerBits, dataBits, provisionalVersion);
    return chooseVersion(bitsNeeded, ecLevel);
  }

  private static int calculateBitsNeeded(Mode mode,
      BitArray headerBits,
      BitArray dataBits,
      Version version) {
    return headerBits.getSize() + mode.getCharacterCountBits(version) + dataBits.getSize();
  }

  /**
   * @return the code point of the table used in alphanumeric mode or
   *         -1 if there is no corresponding code in the table.
   */
  static int getAlphanumericCode(int code) {
    if (code < ALPHANUMERIC_TABLE.length) {
      return ALPHANUMERIC_TABLE[code];
    }
    return -1;
  }

  public static Mode chooseMode(String content) {
    return chooseMode(content, null);
  }

  /**
   * Choose the best mode by examining the content. Note that 'encoding' is used
   * as a hint;
   * if it is Shift_JIS, and the input is only double-byte Kanji, then we return
   * {@link Mode#KANJI}.
   */
  private static Mode chooseMode(String content, String encoding) {
    if ("Shift_JIS".equals(encoding) && isOnlyDoubleByteKanji(content)) {
      // Choose Kanji mode if all input are double-byte characters
      return Mode.KANJI;
    }
    boolean hasNumeric = false;
    boolean hasAlphanumeric = false;
    for (int i = 0; i < content.length(); ++i) {
      char c = content.charAt(i);
      if (c >= '0' && c <= '9') {
        hasNumeric = true;
      } else if (getAlphanumericCode(c) != -1) {
        hasAlphanumeric = true;
      } else {
        return Mode.BYTE;
      }
    }
    if (hasAlphanumeric) {
      return Mode.ALPHANUMERIC;
    }
    if (hasNumeric) {
      return Mode.NUMERIC;
    }
    return Mode.BYTE;
  }

  private static boolean isOnlyDoubleByteKanji(String content) {
    byte[] bytes;
    try {
      bytes = content.getBytes("Shift_JIS");
    } catch (UnsupportedEncodingException ignored) {
      return false;
    }
    int length = bytes.length;
    if (length % 2 != 0) {
      return false;
    }
    for (int i = 0; i < length; i += 2) {
      int byte1 = bytes[i] & 0xFF;
      if ((byte1 < 0x81 || byte1 > 0x9F) && (byte1 < 0xE0 || byte1 > 0xEB)) {
        return false;
      }
    }
    return true;
  }

  private static int chooseMaskPattern(BitArray bits,
      ErrorCorrectionLevel ecLevel,
      Version version,
      ByteMatrix matrix) throws WriterException {

    int minPenalty = Integer.MAX_VALUE; // Lower penalty is better.
    int bestMaskPattern = -1;
    // We try all mask patterns to choose the best one.
    for (int maskPattern = 0; maskPattern < QRCode.NUM_MASK_PATTERNS; maskPattern++) {
      MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
      int penalty = calculateMaskPenalty(matrix);
      if (penalty < minPenalty) {
        minPenalty = penalty;
        bestMaskPattern = maskPattern;
      }
    }
    return bestMaskPattern;
  }

  private static Version chooseVersion(int numInputBits, ErrorCorrectionLevel ecLevel) throws WriterException {
    for (int versionNum = 1; versionNum <= 40; versionNum++) {
      Version version = Version.getVersionForNumber(versionNum);
      if (willFit(numInputBits, version, ecLevel)) {
        return version;
      }
    }
    throw new WriterException("Data too big");
  }

  /**
   * @return true if the number of input bits will fit in a code with the
   *         specified version and
   *         error correction level.
   */
  private static boolean willFit(int numInputBits, Version version, ErrorCorrectionLevel ecLevel) {
    // In the following comments, we use numbers of Version 7-H.
    // numBytes = 196
    int numBytes = version.getTotalCodewords();
    // getNumECBytes = 130
    Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
    int numEcBytes = ecBlocks.getTotalECCodewords();
    // getNumDataBytes = 196 - 130 = 66
    int numDataBytes = numBytes - numEcBytes;
    int totalInputBytes = (numInputBits + 7) / 8;
    return numDataBytes >= totalInputBytes;
  }

  /**
   * Terminate bits as described in 8.4.8 and 8.4.9 of JISX0510:2004 (p.24).
   */
  static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
    int capacity = numDataBytes * 8;
    if (bits.getSize() > capacity) {
      throw new WriterException("data bits cannot fit in the QR Code" + bits.getSize() + " > " +
          capacity);
    }
    for (int i = 0; i < 4 && bits.getSize() < capacity; ++i) {
      bits.appendBit(false);
    }
    // Append termination bits. See 8.4.8 of JISX0510:2004 (p.24) for details.
    // 終端パターンのこと
    // If the last byte isn't 8-bit aligned, we'll add padding bits.
    int numBitsInLastByte = bits.getSize() & 0x07;
    if (numBitsInLastByte > 0) {
      for (int i = numBitsInLastByte; i < 8; i++) {
        bits.appendBit(false);
      }
    }
    // 埋め草コード(11101100, 00010001を交互に埋める)
    // If we have more space, we'll fill the space with padding patterns defined in
    // 8.4.9 (p.24).
    int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
    for (int i = 0; i < numPaddingBytes; ++i) {
      bits.appendBits((i & 0x01) == 0 ? 0xEC : 0x11, 8);
    }
    if (bits.getSize() != capacity) {
      throw new WriterException("Bits size does not equal capacity");
    }
  }

  /**
   * Get number of data bytes and number of error correction bytes for block id
   * "blockID". Store
   * the result in "numDataBytesInBlock", and "numECBytesInBlock". See table 12 in
   * 8.5.1 of
   * JISX0510:2004 (p.30)
   */
  static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes,
      int numDataBytes,
      int numRSBlocks,
      int blockID,
      int[] numDataBytesInBlock,
      int[] numECBytesInBlock) throws WriterException {
    if (blockID >= numRSBlocks) {
      throw new WriterException("Block ID too large");
    }
    // numRsBlocksInGroup2 = 196 % 5 = 1
    int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
    // numRsBlocksInGroup1 = 5 - 1 = 4
    int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
    // numTotalBytesInGroup1 = 196 / 5 = 39
    int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
    // numTotalBytesInGroup2 = 39 + 1 = 40
    int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
    // numDataBytesInGroup1 = 66 / 5 = 13
    int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
    // numDataBytesInGroup2 = 13 + 1 = 14
    int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
    // numEcBytesInGroup1 = 39 - 13 = 26
    int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
    // numEcBytesInGroup2 = 40 - 14 = 26
    int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
    // Sanity checks.
    // 26 = 26
    if (numEcBytesInGroup1 != numEcBytesInGroup2) {
      throw new WriterException("EC bytes mismatch");
    }
    // 5 = 4 + 1.
    if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
      throw new WriterException("RS blocks mismatch");
    }
    // 196 = (13 + 26) * 4 + (14 + 26) * 1
    if (numTotalBytes != ((numDataBytesInGroup1 + numEcBytesInGroup1) *
        numRsBlocksInGroup1) +
        ((numDataBytesInGroup2 + numEcBytesInGroup2) *
            numRsBlocksInGroup2)) {
      throw new WriterException("Total bytes mismatch");
    }

    if (blockID < numRsBlocksInGroup1) {
      numDataBytesInBlock[0] = numDataBytesInGroup1;
      numECBytesInBlock[0] = numEcBytesInGroup1;
    } else {
      numDataBytesInBlock[0] = numDataBytesInGroup2;
      numECBytesInBlock[0] = numEcBytesInGroup2;
    }
  }

  /**
   * Interleave "bits" with corresponding error correction bytes. On success,
   * store the result in
   * "result". The interleave rule is complicated. See 8.6 of JISX0510:2004 (p.37)
   * for details.
   */
  static BitArray interleaveWithECBytes(BitArray bits,
      int numTotalBytes,
      int numDataBytes,
      int numRSBlocks) throws WriterException {

    // "bits" must have "getNumDataBytes" bytes of data.
    if (bits.getSizeInBytes() != numDataBytes) {
      throw new WriterException("Number of bits and data bytes does not match");
    }

    // Step 1. Divide data bytes into blocks and generate error correction bytes for
    // them. We'll
    // store the divided data bytes blocks and error correction bytes blocks into
    // "blocks".
    int dataBytesOffset = 0;
    int maxNumDataBytes = 0;
    int maxNumEcBytes = 0;

    // Since, we know the number of reedsolmon blocks, we can initialize the vector
    // with the number.
    Collection<BlockPair> blocks = new ArrayList<>(numRSBlocks);

    for (int i = 0; i < numRSBlocks; ++i) {
      int[] numDataBytesInBlock = new int[1];
      int[] numEcBytesInBlock = new int[1];
      getNumDataBytesAndNumECBytesForBlockID(
          numTotalBytes, numDataBytes, numRSBlocks, i,
          numDataBytesInBlock, numEcBytesInBlock);

      int size = numDataBytesInBlock[0];
      // System.out.println(size);
      byte[] dataBytes = new byte[size];
      bits.toBytes(8 * dataBytesOffset, dataBytes, 0, size);
      byte[] ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
      blocks.add(new BlockPair(dataBytes, ecBytes));

      maxNumDataBytes = Math.max(maxNumDataBytes, size);
      maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
      dataBytesOffset += numDataBytesInBlock[0];
    }
    if (numDataBytes != dataBytesOffset) {
      throw new WriterException("Data bytes does not match offset");
    }

    BitArray result = new BitArray();

    // First, place data blocks.
    for (int i = 0; i < maxNumDataBytes; ++i) {
      for (BlockPair block : blocks) {
        byte[] dataBytes = block.getDataBytes();
        if (i < dataBytes.length) {
          result.appendBits(dataBytes[i], 8);
        }
      }
    }
    // Then, place error correction blocks.
    for (int i = 0; i < maxNumEcBytes; ++i) {
      for (BlockPair block : blocks) {
        byte[] ecBytes = block.getErrorCorrectionBytes();
        if (i < ecBytes.length) {
          result.appendBits(ecBytes[i], 8);
        }
      }
    }
    if (numTotalBytes != result.getSizeInBytes()) { // Should be same.
      throw new WriterException("Interleaving error: " + numTotalBytes + " and " +
          result.getSizeInBytes() + " differ.");
    }

    return result;
  }

  // 0xFFは255を16進数で表したもの
  // ＆はbit論理積

  static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
    int numDataBytes = dataBytes.length;
    int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
    for (int i = 0; i < numDataBytes; i++) {
      toEncode[i] = dataBytes[i] & 0xFF;
    }
    new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);

    byte[] ecBytes = new byte[numEcBytesInBlock];
    for (int i = 0; i < numEcBytesInBlock; i++) {
      ecBytes[i] = (byte) toEncode[numDataBytes + i];
    }
    return ecBytes;
  }

  /**
   * Append mode info. On success, store the result in "bits".
   */
  static void appendModeInfo(Mode mode, BitArray bits) {
    bits.appendBits(mode.getBits(), 4);
  }

  /**
   * Append length info. On success, store the result in "bits".
   */
  static void appendLengthInfo(int numLetters, Version version, Mode mode, BitArray bits) throws WriterException {
    int numBits = mode.getCharacterCountBits(version);
    if (numLetters >= (1 << numBits)) {
      throw new WriterException(numLetters + " is bigger than " + ((1 << numBits) - 1));
    }
    bits.appendBits(numLetters, numBits);
  }

  /**
   * Append "bytes" in "mode" mode (encoding) into "bits". On success, store the
   * result in "bits".
   */
  static void appendBytes(String content,
      Mode mode,
      BitArray bits,
      String encoding) throws WriterException {
    switch (mode) {
      case NUMERIC:
        appendNumericBytes(content, bits);
        break;
      case ALPHANUMERIC:
        appendAlphanumericBytes(content, bits);
        break;
      case BYTE:
        append8BitBytes(content, bits, encoding);
        break;
      case KANJI:
        appendKanjiBytes(content, bits);
        break;
      default:
        throw new WriterException("Invalid mode: " + mode);
    }
  }

  static void appendNumericBytes(CharSequence content, BitArray bits) {
    int length = content.length();
    int i = 0;
    while (i < length) {
      int num1 = content.charAt(i) - '0';
      if (i + 2 < length) {
        // Encode three numeric letters in ten bits.
        int num2 = content.charAt(i + 1) - '0';
        int num3 = content.charAt(i + 2) - '0';
        bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
        i += 3;
      } else if (i + 1 < length) {
        // Encode two numeric letters in seven bits.
        int num2 = content.charAt(i + 1) - '0';
        bits.appendBits(num1 * 10 + num2, 7);
        i += 2;
      } else {
        // Encode one numeric letter in four bits.
        bits.appendBits(num1, 4);
        i++;
      }
    }
  }

  static void appendAlphanumericBytes(CharSequence content, BitArray bits) throws WriterException {
    int length = content.length();
    int i = 0;
    while (i < length) {
      int code1 = getAlphanumericCode(content.charAt(i));
      if (code1 == -1) {
        throw new WriterException();
      }
      if (i + 1 < length) {
        int code2 = getAlphanumericCode(content.charAt(i + 1));
        if (code2 == -1) {
          throw new WriterException();
        }
        // Encode two alphanumeric letters in 11 bits.
        bits.appendBits(code1 * 45 + code2, 11);
        i += 2;
      } else {
        // Encode one alphanumeric letter in six bits.
        bits.appendBits(code1, 6);
        i++;
      }
    }
  }

  static void append8BitBytes(String content, BitArray bits, String encoding)
      throws WriterException {
    byte[] bytes;
    try {
      bytes = content.getBytes(encoding);
    } catch (UnsupportedEncodingException uee) {
      throw new WriterException(uee);
    }
    for (byte b : bytes) {
      bits.appendBits(b, 8);
    }
  }

  static void appendKanjiBytes(String content, BitArray bits) throws WriterException {
    byte[] bytes;
    try {
      bytes = content.getBytes("Shift_JIS");
    } catch (UnsupportedEncodingException uee) {
      throw new WriterException(uee);
    }
    int length = bytes.length;
    for (int i = 0; i < length; i += 2) {
      int byte1 = bytes[i] & 0xFF;
      int byte2 = bytes[i + 1] & 0xFF;
      int code = (byte1 << 8) | byte2;
      int subtracted = -1;
      if (code >= 0x8140 && code <= 0x9ffc) {
        subtracted = code - 0x8140;
      } else if (code >= 0xe040 && code <= 0xebbf) {
        subtracted = code - 0xc140;
      }
      if (subtracted == -1) {
        throw new WriterException("Invalid byte sequence");
      }
      int encoded = ((subtracted >> 8) * 0xc0) + (subtracted & 0xff);
      bits.appendBits(encoded, 13);
    }
  }

  private static void appendECI(CharacterSetECI eci, BitArray bits) {
    bits.appendBits(Mode.ECI.getBits(), 4);
    // This is correct for values up to 127, which is all we need now.
    bits.appendBits(eci.getValue(), 8);
  }

}
