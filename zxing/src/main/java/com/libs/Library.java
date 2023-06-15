package com.libs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.rs.GF;
import com.rs.GFpoly;
import com.rs.RS;

public class Library {
	static private int num = 0;

	// // 系列を入力したら，そのQRコードと誤訂正させられる符号語の表を出力する関数を作りましょう．マスクパターンはとりま2固定
	// public static int[][] generateGraphAndQR(int[] keiretu, int maskpattern, int
	// d)
	// throws WriterException, IOException {// keiretu = 格納するシンボル列
	// GF gf = new GF(0x011D, 256, 0);
	// RS rs = new RS(gf);
	// Random ran = new Random();
	// int t = (d - 1) / 2;
	// int[][] ciii = new int[20000][keiretu.length];

	// int l = 6;// vの重み
	// while (rs.cicount < 20000) {
	// int sum = rs.sindserch(d, keiretu, l);
	// for (int i = 0; i < keiretu.length; i++) {
	// ciii[rs.cicount][i] = rs.ciii[rs.cicount][i];
	// }
	// l++;
	// }

	// int[][] secretci = new int[10000][keiretu.length];
	// int[] secretcilocation = new int[10000];
	// int secretcount = 0;
	// while (secretcount < 10000) {
	// boolean check = true;
	// secretcilocation[secretcount] = ran.nextInt(20000);
	// for (int i = 0; i < secretcount; i++) {
	// if (secretcilocation[i] == secretcilocation[secretcount])
	// check = false;
	// }
	// if (check) {
	// for (int i = 0; i < keiretu.length; i++) {
	// secretci[secretcount][i] = ciii[secretcilocation[secretcount]][i];
	// }
	// secretcount++;

	// }
	// }
	// // とりま2に固定⇒sindserchも2想定
	// // maskpatternも指定できるようにしたい
	// // ⇒ maskpatternを白い部分が多くなるように選択できれば良い
	// maskpattern = 2;
	// String source = "ABCDEFGHIJK";// これは何でもよい（出力に関係ない）
	// String encoding = "UTF-8";
	// String filePath = "testtt.png";
	// generateQR(source, keiretu, encoding, filePath);

	// return secretci;
	// }

	public static void generateOriginalQR(String source, int[] keiretu, String encoding, String filePath)
			throws WriterException, IOException {
		int version = 1;
		// 162, 179, 140, 4, 55, 0, 40, 84, 55, 136, 55, 136, 55, 136, 55, 136, 196,
		// 136, 55, 136, 95, 193, 35, 150, 19, 25,

		ConcurrentHashMap hints = new ConcurrentHashMap();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.QR_VERSION, version);
		hints.put(EncodeHintType.CHARACTER_SET, encoding);
		hints.put(EncodeHintType.MARGIN, 4);// 4

		int size = 17 + version * 4;
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix = writer.encode(source, BarcodeFormat.QR_CODE, size, size, hints, keiretu);
		BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
		ImageIO.write(image, "png", new File(filePath));
	}

	public static void generateErrorLocationQR(String source, int[] keiretu, String encoding, String filePath,
			int maskpattern) throws WriterException, IOException {//
		int version = 1;
		// 162, 179, 140, 4, 55, 0, 40, 84, 55, 136, 55, 136, 55, 136, 55, 136, 196,
		// 136, 55, 136, 95, 193, 35, 150, 19, 25,

		ConcurrentHashMap hints = new ConcurrentHashMap();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.QR_VERSION, version);
		hints.put(EncodeHintType.CHARACTER_SET, encoding);
		hints.put(EncodeHintType.MARGIN, 4);// 4

		int size = 17 + version * 4;
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix[] bitMatrix = writer.encode(source, BarcodeFormat.QR_CODE, size, size, hints, keiretu, maskpattern);
		BufferedImage image = MatrixToImageWriter.toBufferedImagekai(bitMatrix);
		ImageIO.write(image, "png", new File(filePath));
	}

	public int[] calcurateOnetime(int[] keiretu) throws WriterException, IOException {// 共通鍵から塗りつぶし位置を指定 and
																						// ワンタイムパスワードを計算
		int version = 1;
		ConcurrentHashMap hints = new ConcurrentHashMap();

		int size = 17 + version * 4;
		QRCodeWriter writer = new QRCodeWriter();
		boolean find = false;
		int[] onetime = new int[26];
		int shikounum = 0;
		num = 0;
		while (!find) {
			shikounum++;
			System.out.println("shikounum: " + shikounum);
			GF gf = new GF(0x011D, 256, 0);
			RS rs = new RS(gf);
			int[] kcode = rs.encode(keiretu, 8);
			int[] kyoutu = rs.randomTR(kcode, 4);
			int[] errorbit = rs.generateError(kyoutu);
			int[] error = gf.bittosymbol(errorbit);

			int[] y = new int[26];// 塗りつぶし後の系列

			for (int i = 0; i < y.length; i++) {
				y[i] = gf.addSubstract(kyoutu[i], error[i]);
			}
			int[] result = rs.decode(y, 8);
			GFpoly poly = new GFpoly(gf, result);
			boolean code = true;
			int[] sind = new int[7];
			for (int i = 0; i < sind.length; i++) {
				sind[i] = poly.substitute(gf.getaTable(i));
				if (sind[i] != 0) {
					code = false;
					break;
				}
			}

			if (code) {
				num = shikounum;
				find = true;
				int diffnum = 0;
				int[] preonetime = new int[26];
				for (int i = 0; i < result.length; i++) {
					if (y[i] != result[i])
						diffnum++;
				}

				// 変化するシンボルが2シンボル未満であれば送る誤り位置を調整
				if (diffnum < 2) {
					int esymbolnum = 0;
					for (int i = 0; i < error.length; i++) {
						if (error[i] != 0) {
							esymbolnum++;
						}
					}
					int[] errorloca = new int[esymbolnum];
					esymbolnum = 0;
					for (int i = 0; i < y.length; i++) {
						if (error[i] != 0) {
							errorloca[esymbolnum] = i;
							esymbolnum++;
						}
					}
					int[] deleteloca = new int[2 - diffnum];
					for (int i = 0; i < deleteloca.length; i++) {
						deleteloca[i] = -1;
					}
					Random random = new Random();
					for (int i = 0; i < 2 - diffnum; i++) {
						int dl = random.nextInt(esymbolnum);
						boolean tyoufuku = false;
						for (int j = 0; j < deleteloca.length; j++) {
							if (dl == deleteloca[j]) {
								tyoufuku = true;
								i--;
								break;
							}
						}
						if (!tyoufuku) {
							deleteloca[i] = errorloca[dl];
							error[errorloca[dl]] = 0;
						}
					}

					for (int i = 0; i < y.length; i++) {
						y[i] = gf.addSubstract(kyoutu[i], error[i]);
					}
				}

				// 変化したシンボルを導出
				for (int i = 0; i < preonetime.length; i++) {
					if (y[i] != result[i])
						preonetime[i] = result[i];
				}

				// 変化したシンボルのうち2シンボル（とりあえず1,2番目固定）
				int pcount = 0;
				for (int i = 0; i < onetime.length; i++) {
					if (pcount == 2)
						break;
					if (preonetime[i] != 0) {
						pcount++;
						onetime[i] = preonetime[i];
					}
				}

				String source = "ABCDEFGHIJK";
				String encoding = "UTF-8";
				String filePath = "errorLocation.png";

				hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				hints.put(EncodeHintType.QR_VERSION, version);
				hints.put(EncodeHintType.CHARACTER_SET, encoding);
				hints.put(EncodeHintType.MARGIN, 4);// 4

				int mask = Encoder.getMask(source, ErrorCorrectionLevel.L, hints, kyoutu);
				generateErrorLocationQR(source, error, encoding, filePath, mask);
			}
		}
		return onetime;
	}
}

// public static void main(String[] args) throws WriterException, IOException {
// // 動作テスト
// int sumnum = 0;
// long sumtime = 0;

// int testnum = 1;
// for (int i = 0; i < testnum; i++) {
// // 現在時刻を取得
// long long1 = System.nanoTime();

// System.out.println("開始時間:" + long1);

// // ここに実行時間を計測したい処理を記述
// // int[] keiretu = {32, 89, 205, 69, 42, 21, 112, 179, 213, 0, 236, 17, 236,
// 15,
// // 236, 17, 9, 17, 236, 4, 142, 111, 249, 23, 20, 227};
// GF gf = new GF(0x011D, 256, 0);
// RS rs = new RS(gf);
// int[] jyouhou = rs.randomR(19);
// int[] code = rs.encode(jyouhou, 8);
// int[] keiretu = rs.randomTR(code, 4);

// int[] onetime = calcurateOnetime(keiretu);

// // もう一度現在時刻を取得
// long long2 = System.nanoTime();

// System.out.println("終了時間:" + long2);

// // 差分を求める
// System.out.println("実行時間:" + (long2 - long1) + "ナノ秒");
// sumtime = sumtime + (long2 - long1);
// sumnum = sumnum + num;

// System.out.println("code↓");
// for (int j = 0; j < code.length; j++) {
// System.out.print(code[j] + " ");
// }
// System.out.println();
// }

// System.out.println("百回のワンタイムパスワードを計算");
// System.out.println();
// System.out.println("時間平均：" + sumtime / testnum);
// System.out.println("誤り探索数平均" + sumnum / testnum);

// }
// }