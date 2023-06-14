package com.rs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.MatrixUtil;

public class RS {
	private GF gf;
	private GFpoly generater;
	int kainasi;
	public boolean correct;
	int[] sindrome;
	int[] sigma;
	int[] location;
	int[] magnitude;
	int[] Z;
	int hannigai;
	int[] randome;
	int[] erV;
	List<int[]> elist = new ArrayList<>();
	int[] rankh;
	int kaisuu = 0;
	int tanni = 0;
	int[] simhist;
	int[] bunnpu;
	int[] cisum;
	int sikou = 0;
	int[] omega;
	public int[][] ciii;// 誤訂正させられる符号語
	int[][] cix;// 誤訂正させられる符号語
	public int cicount = 0;// 誤訂正させられる符号語のカウント

	public RS(GF gf) {
		this.gf = gf;
		generater = new GFpoly(gf, new int[] { 1 });
	}

	GFpoly generatedPolynominal(int degree) {
		generater = new GFpoly(gf, new int[] { 1 });

		for (int i = 0; i < degree; i++) {
			generater = generater.multiply(new GFpoly(gf, new int[] { 1, gf.getaTable(gf.getL() + i) }));
		}
		return generater;
	}

	public int[] encode(int[] a, int d) {// 距離d = n - k + 1
		GFpoly info;
		GFpoly[] product;
		GFpoly remainder;
		GFpoly quotient;
		GFpoly result;
		GFpoly poly;
		int[] resultCoeff;
		int diff;
		int length;

		info = new GFpoly(gf, a);
		length = a.length + d - 1;// length = info.coefficients.length;,訂正
		info = info.monoMultiply(d - 1, 1);
		poly = this.generatedPolynominal(d - 1);
		resultCoeff = new int[length];
		product = poly.divide(info);
		quotient = product[0];
		remainder = product[1];
		System.arraycopy(a, 0, resultCoeff, 0, a.length);
		diff = resultCoeff.length - remainder.coefficients.length;
		for (int i = length; i < diff; i++) {
			resultCoeff[i] = 0;
		}
		System.arraycopy(remainder.coefficients, 0, resultCoeff, diff, remainder.coefficients.length);

		return resultCoeff;
	}

	public int[] decode(int[] a, int d) {// d>=3
		GFpoly poly;
		GFpoly aRemainder;
		GFpoly bRemainder;
		GFpoly dxbRemainder;
		GFpoly quotient;
		GFpoly[] aProduct = { gf.getZero(), gf.getZero() };
		GFpoly bProduct;
		GFpoly result;
		int[] s;// シンドローム
		GFpoly S;// シンドローム多項式
		GFpoly aRem;
		GFpoly bRem;
		int[] r;// 誤り位置多項式解
		int[] e;// 誤り
		int f = 0;
		// this.kainasi = f;
		boolean correct = false;
		// this.correct = correct;
		int kekka[];
		int length;
		int t;
		t = (d - 1) / 2;
		// this.kainasi = 0;
		// this.hannigai = 0;

		s = new int[d - 1];
		poly = new GFpoly(gf, a);
		for (int i = 0; i < d - 1; i++)
			s[d - 1 - 1 - i] = poly.substitute(gf.getaTable(gf.getL() + i));
		// sindrome = s;
		S = new GFpoly(gf, s);
		int ri = -1;
		int bi = -1;

		// System.out.print("ui");
		if (S.coefficients[0] == 0)
			return a;// シンドロームが全て0
		else {// ユークリッド復号法
			aRemainder = S;
			aRem = poly.buildMonoPoly(d - 1, 1);
			// System.out.println("R "+ ri);
			// for(int hh=0;hh<aRem.coefficients.length;hh++) {
			// System.out.print(gf.getlogTable(aRem.coefficients[hh])+" ");
			// }
			ri++;
			// System.out.println();
			// System.out.println("R "+ ri);
			// for(int hh=0;hh<aRemainder.coefficients.length;hh++) {
			// System.out.print(gf.getlogTable(aRemainder.coefficients[hh])+" ");
			// }
			ri++;
			// System.out.println();
			bRemainder = gf.getOne();
			bRem = gf.getZero();
			// System.out.println("B "+ bi);
			// for(int hh=0;hh<bRem.coefficients.length;hh++) {
			// System.out.print(gf.getlogTable(bRem.coefficients[hh])+" ");
			// }
			bi++;
			// System.out.println();
			// System.out.println("B "+ bi);
			// for(int hh=0;hh<bRemainder.coefficients.length;hh++) {
			// System.out.print(gf.getlogTable(bRemainder.coefficients[hh])+" ");
			// }
			bi++;
			// System.out.println();
			quotient = gf.getZero();
			bProduct = gf.getZero();
			while (aRemainder.coefficients.length - 1 > t - 1) {

				aProduct = aRemainder.divide(aRem);
				aRem = aRemainder;
				quotient = aProduct[0];
				aRemainder = aProduct[1];

				bProduct = bRem.addSubstract(bRemainder.multiply(quotient));
				/*
				 * System.out.print("bProduct");
				 * for(int i=0;i<bProduct.coefficients.length;i++)
				 * System.out.print(bProduct.coefficients[i]+" ");
				 * System.out.println("");
				 */
				bRem = bRemainder;
				bRemainder = bProduct;

				// System.out.println("R "+ ri);
				// for(int hh=0;hh<aRem.coefficients.length;hh++) {
				// System.out.print(gf.getlogTable(aRem.coefficients[hh])+" ");
				// }
				// System.out.println();
				// ri++;
				// System.out.println("R "+ ri);
				// for(int hh=0;hh<aRemainder.coefficients.length;hh++) {
				// System.out.print(gf.getlogTable(aRemainder.coefficients[hh])+" ");
				// }
				// ri++;
				// System.out.println();
				// System.out.println("B "+ bi);
				// for(int hh=0;hh<bRem.coefficients.length;hh++) {
				// System.out.print(gf.getlogTable(bRem.coefficients[hh])+" ");
				// }
				// bi++;
				// System.out.println();
				// System.out.println("B "+ bi);
				// for(int hh=0;hh<bRemainder.coefficients.length;hh++) {
				// System.out.print(gf.getlogTable(bRemainder.coefficients[hh])+" ");
				// }
				// bi++;
				// System.out.println();
				/*
				 * System.out.print("bRemainder");
				 * for(int i=0;i<bRemainder.coefficients.length;i++)
				 * System.out.print(bRemainder.coefficients[i]+" ");
				 * System.out.println("");
				 */

			}

			/*
			 * System.out.print("σ: ");
			 * for(int i=0;i<bRemainder.coefficients.length;i++)
			 * System.out.print(bRemainder.coefficients[i]+ " ");
			 * System.out.println("");
			 * /*
			 * System.out.print("Z: ");
			 * for(int i=0;i<aRemainder.coefficients.length;i++)
			 * System.out.print(aRemainder.coefficients[i]+ " ");
			 * System.out.println("");
			 */
			// System.out.print("uu");

			// Z = new int[aRemainder.coefficients.length];
			// sigma = new int[bRemainder.coefficients.length];
			// location = new int[bRemainder.coefficients.length-1];

			// if(sigma.length-1>t) {
			// correct=false;
			// this.correct=false;
			// return a;
			// }

			if (bRemainder.substitute(0) != 0) {

				int B0 = gf.reciprocal(bRemainder.substitute(0));
				bRemainder = bRemainder.monoMultiply(0, B0);
				aRemainder = aRemainder.monoMultiply(0, B0);
			} else {
				correct = false;
				// this.correct = false;
				return a;
			}

			// Z = new int[aRemainder.coefficients.length];
			// sigma = new int[bRemainder.coefficients.length];
			// hannigai = 0;

			// for(int i=0;i<aRemainder.coefficients.length;i++)
			// this.Z[i] = aRemainder.coefficients[i];
			// for(int i=0;i<bRemainder.coefficients.length;i++)
			// this.sigma[i] = bRemainder.coefficients[i];

			if (bRemainder.coefficients.length > aRemainder.coefficients.length
					&& bRemainder.coefficients.length - 1 <= t) {
				r = new int[bRemainder.coefficients.length - 1];
				// location = new int[bRemainder.coefficients.length-1];
				int j = 0;
				int n = a.length;
				for (int i = 0; i < gf.getSize() - 1; i++) {// 変更したint i=0;i<a.length;i++

					if (bRemainder.substitute(gf.getaTable(i)) == 0) {// bRemainder.substitute(gf.reciprocal(gf.getaTable(i)))==0
						if (i == 0 || i >= gf.getSize() - 1 - n + 1) {
							if (i == 0) {
								r[j] = 0;
								// this.location[j]=r[j];
								j = j + 1;
							} else {
								r[j] = gf.getSize() - 1 - i;
								// System.out.print("r["+ j + "]" + r[j] + " ");
								// this.location[j] = r[j];
								j = j + 1;
							}

						} else {
							// System.out.print("誤りの位置が符号長の範囲外");
							// hannigai = 1;
							correct = false;
							// this.correct = correct;
							return a;
						}
					}

				}

				// System.out.println("");
				if (j != bRemainder.coefficients.length - 1) {
					f = 1;
					// this.kainasi = f;
					// System.out.print("位置多項式が解けない ");
					/*
					 * for(int i=0;i<a.length;i++)
					 * System.out.print(a[i] + " ");
					 */
					correct = false;
					// this.correct = correct;

					return a;

				} else {

					e = new int[r.length];
					dxbRemainder = bRemainder.differential();

					/*
					 * System.out.print("σ': ");
					 * for(int i=0;i<dxbRemainder.coefficients.length;i++)
					 * System.out.print(dxbRemainder.coefficients[i]+ " ");
					 * System.out.println("");
					 */
					// magnitude = new int[r.length];
					// omega = new int[aRemainder.coefficients.length];
					// for(int i=0;i<omega.length;i++) {
					// omega[i] = aRemainder.coefficients[i];
					// }
					for (int i = 0; i < r.length; i++) {
						int q = gf.getSize() - 1 - r[i];
						// System.out.print("a^"+d + " " );
						// System.out.print("are"+aRemainder.substitute(gf.getaTable(gf.getSize()-1-r[i]))
						// + " " );
						// System.out.print("dxbre"+dxbRemainder.substitute(gf.getaTable(gf.getSize()-1-r[i]))
						// + " " );
						e[i] = gf.divide(aRemainder.substitute(gf.getaTable(gf.getSize() - 1 - r[i])),
								dxbRemainder.substitute(gf.getaTable(gf.getSize() - 1 - r[i])));// gf.divide(aRemainder.substitute(gf.reciprocal(gf.getaTable(r[i]))),dxbRemainder.substitute(gf.reciprocal(gf.getaTable(r[i]))))
						// System.out.print("e["+ i + "]" + e[i] + " ");
						if (this.gf.getL() == 0) {
							// System.out.println("getL()=0");
							e[i] = gf.multiply(e[i], gf.getaTable(r[i]));
						}
						// magnitude[i] = e[i];
					}
					int[] errorv = new int[a.length];
					for (int i = 0; i < r.length; i++) {
						errorv[r[i]] = e[i];
					}
					// this.erV = new int[n];
					// for(int i=0;i<r.length;i++) {
					// erV[i] = errorv[i];
					// }
					// System.out.println("");
					result = new GFpoly(gf, a);
					/*
					 * System.out.println("first result");
					 * for(int u=0;u<result.coefficients.length;u++)
					 * System.out.print(result.coefficients[u]+" ");
					 * System.out.println("");
					 */

					for (int i = 0; i < r.length; i++) {
						result = result.addSubstract(poly.buildMonoPoly(r[i], e[i]));

						/*
						 * System.out.println("result");
						 * for(int u=0;u<result.coefficients.length;u++)
						 * System.out.print(result.coefficients[u]+" ");
						 * 
						 * System.out.println("");
						 */
					}
					// System.out.println("result");
					correct = true;
					// this.correct = correct;
					kekka = new int[a.length];

					if (result.coefficients.length != a.length) {
						length = -result.coefficients.length + a.length;
						for (int i = 0; i < length; i++)
							kekka[i] = 0;
						for (int i = 0; i < result.coefficients.length; i++)
							kekka[length + i] = result.coefficients[i];

						return kekka;

					}

					return result.coefficients;
				}

			}

			// this.correct = false;
			return a;

			/*
			 * try {
			 * //出力先を作成する
			 * FileWriter fw = new
			 * FileWriter("C:\\Users\\野村　知也\\Downloads\\大学\\RSProgramEx\\result.txt", true);
			 * //※１
			 * PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			 * 
			 * //内容を指定する
			 * pw.print("位置多項式が解けない ");
			 * 
			 * //ファイルに書き出す
			 * pw.close();
			 * 
			 * //終了メッセージを画面に出力する
			 * System.out.println("出力が完了しました。");
			 * 
			 * } catch (IOException ex) {
			 * //例外時処理
			 * ex.printStackTrace();
			 * }
			 */

		}

	}

	List<int[]> generateS(int ε) {
		List<int[]> slist = new ArrayList<>();

		int[] s = new int[2 * ε];
		slist.add(s);
		for (int i = 0; i < s.length; i++) {
			s[i] = gf.getaTable(0);
		}
		slist.add(s);
		s = new int[2 * ε];
		int check = 0;
		int[] kuri = new int[s.length];
		for (int i = 1; i < kuri.length; i++) {
			kuri[i] = -1;
		}
		while (check == 0) {
			s = new int[2 * ε];
			check = 1;
			kuri[0]++;
			for (int i = 0; i < kuri.length; i++) {
				if (kuri[i] == gf.getSize() - 1) {
					kuri[i + 1]++;
					kuri[i] = -1;
				}
			}
			for (int i = 0; i < s.length; i++) {
				if (kuri[i] != -1)
					s[i] = gf.getaTable(kuri[i]);
				else {
					s[i] = 0;
				}
				// System.out.print(gf.getlogTable(s[i])+ " ");
			}
			// System.out.println();
			slist.add(s);

			for (int i = 0; i < s.length; i++) {
				if (kuri[i] != gf.getSize() - 2)
					check = 0;
			}
		}
		return slist;
	}

	int[] kakudecode(int[] a, int[] s) {
		GFpoly poly;
		GFpoly aRemainder;
		GFpoly bRemainder;
		GFpoly dxbRemainder;
		GFpoly quotient;
		GFpoly[] aProduct = { gf.getZero(), gf.getZero() };
		GFpoly bProduct;
		GFpoly result;
		GFpoly S;// シンドローム多項式
		GFpoly aRem;
		GFpoly bRem;
		// List<int[]> clist = new ArrayList<>();
		int[] r;// 誤り位置多項式解
		int[] e;// 誤り
		int[] re;
		int f = 0;
		this.kainasi = f;
		boolean correct = true;
		this.correct = correct;
		int kekka[];
		int length;
		int t;
		int d = s.length + 1;
		t = s.length / 2;
		this.kainasi = 0;
		this.hannigai = 0;
		this.erV = new int[a.length];
		S = new GFpoly(gf, s);
		poly = new GFpoly(gf, a);
		aRemainder = S;
		aRem = poly.buildMonoPoly(d - 1, 1);
		// for(int i=0;i<S.coefficients.length;i++)
		// System.out.print(gf.getlogTable(S.coefficients[i])+" ");
		// System.out.println("");

		// for(int i=0;i<aRem.coefficients.length;i++)
		// System.out.print(aRem.coefficients[i]+" ");
		// System.out.println("");

		bRemainder = gf.getOne();
		bRem = gf.getZero();
		quotient = gf.getZero();
		bProduct = gf.getZero();

		// for(int i=0;i<bRemainder.coefficients.length;i++)
		// System.out.print(bRemainder.coefficients[i]+" ");
		// System.out.println("");
		// System.out.println(t+ex);

		while (aRemainder.coefficients.length - 1 >= t) {
			aProduct = aRemainder.divide(aRem);
			aRem = aRemainder;
			quotient = aProduct[0];
			aRemainder = aProduct[1];

			bProduct = bRem.addSubstract(bRemainder.multiply(quotient));
			// System.out.print("bProduct");
			// for(int i=0;i<bProduct.coefficients.length;i++)
			// System.out.print(bProduct.coefficients[i]+" ");
			// System.out.println("");
			bRem = bRemainder;
			bRemainder = bProduct;
			/*
			 * System.out.print("bRemainder");
			 * for(int i=0;i<bRemainder.coefficients.length;i++)
			 * System.out.print(bRemainder.coefficients[i]+" ");
			 * System.out.println("");
			 */

		}
		// System.out.print(t+ex);
		if (bRemainder.coefficients.length - 1 <= t) {
			for (int i = 0; i < s.length; i++)
				System.out.print(gf.getlogTable(s[i]) + " ");
			System.out.println("");
			System.out.print("σ: ");
			for (int i = 0; i < bRemainder.coefficients.length; i++)
				System.out.print(gf.getlogTable(bRemainder.coefficients[i]) + " ");
			System.out.println("");
		}
		/*
		 * System.out.print("Z: ");
		 * for(int i=0;i<aRemainder.coefficients.length;i++)
		 * System.out.print(aRemainder.coefficients[i]+ " ");
		 * System.out.println("");
		 */
		// System.out.print("uu");

		Z = new int[aRemainder.coefficients.length];
		sigma = new int[bRemainder.coefficients.length];
		location = new int[bRemainder.coefficients.length - 1];

		// if(sigma.length-1>t) {
		// correct=false;
		// this.correct=false;
		// return a;
		// }

		if (bRemainder.substitute(0) != 0) {
			int B0 = gf.reciprocal(bRemainder.substitute(0));
			bRemainder = bRemainder.monoMultiply(0, B0);
			aRemainder = aRemainder.monoMultiply(0, B0);
		}
		// else {
		// correct = false;
		// this.correct = correct;
		// return a;
		// }
		Z = new int[aRemainder.coefficients.length];
		sigma = new int[bRemainder.coefficients.length];
		hannigai = 0;
		if (bRemainder.coefficients.length > aRemainder.coefficients.length
				&& bRemainder.coefficients.length - 1 <= t) {
			for (int i = 0; i < aRemainder.coefficients.length; i++)
				this.Z[i] = aRemainder.coefficients[i];
			for (int i = 0; i < bRemainder.coefficients.length; i++)
				this.sigma[i] = bRemainder.coefficients[i];

			r = new int[bRemainder.coefficients.length - 1];
			location = new int[bRemainder.coefficients.length - 1];
			int j = 0;
			int n = a.length;
			for (int i = 0; i < gf.getSize() - 1; i++) {// 変更したint i=0;i<a.length;i++
				if (bRemainder.substitute(gf.getaTable(i)) == 0) {// bRemainder.substitute(gf.reciprocal(gf.getaTable(i)))==0
					if (i == 0 || i >= gf.getSize() - 1 - n + 1) {
						if (i == 0) {
							r[j] = 0;
							this.location[j] = r[j];
							j = j + 1;
						} else {
							r[j] = gf.getSize() - 1 - i;
							// System.out.print("r["+ j + "]" + r[j] + " ");
							this.location[j] = r[j];
							j = j + 1;
						}

					} else {
						// System.out.print("誤りの位置が符号長の範囲外");
						hannigai = 1;
						correct = false;
						this.correct = correct;
					}
				}

			}

			// System.out.println("");
			if (j != bRemainder.coefficients.length - 1) {
				f = 1;
				this.kainasi = f;
				if (bRemainder.coefficients.length - 1 <= t) {
					System.out.print("位置多項式が解けない ");
					System.out.println();
				}
				/*
				 * for(int i=0;i<a.length;i++)
				 * System.out.print(a[i] + " ");
				 */
				correct = false;
				this.correct = correct;
			}
			if (correct) {

				e = new int[r.length];
				dxbRemainder = bRemainder.differential();

				/*
				 * System.out.print("σ': ");
				 * for(int i=0;i<dxbRemainder.coefficients.length;i++)
				 * System.out.print(dxbRemainder.coefficients[i]+ " ");
				 * System.out.println("");
				 */
				magnitude = new int[r.length];

				for (int i = 0; i < r.length; i++) {
					int q = gf.getSize() - 1 - r[i];
					// System.out.print("a^"+d + " " );
					// System.out.print("are"+aRemainder.substitute(gf.getaTable(gf.getSize()-1-r[i]))
					// + " " );
					// System.out.print("dxbre"+dxbRemainder.substitute(gf.getaTable(gf.getSize()-1-r[i]))
					// + " " );
					e[i] = gf.divide(aRemainder.substitute(gf.getaTable(gf.getSize() - 1 - r[i])),
							dxbRemainder.substitute(gf.getaTable(gf.getSize() - 1 - r[i])));// gf.divide(aRemainder.substitute(gf.reciprocal(gf.getaTable(r[i]))),dxbRemainder.substitute(gf.reciprocal(gf.getaTable(r[i]))))
					// System.out.print("e["+ i + "]" + e[i] + " ");
					if (this.gf.getL() == 0) {
						// System.out.println("getL()=0");
						e[i] = gf.multiply(e[i], gf.getaTable(r[i]));
					}
					magnitude[i] = e[i];
				}
				int[] errorv = new int[a.length];
				for (int i = 0; i < r.length; i++) {
					errorv[r[i]] = e[i];
				}
				for (int i = 0; i < errorv.length; i++) {
					erV[i] = errorv[i];
				}
				GFpoly epoly = new GFpoly(gf, new int[] { 0 });
				for (int i = 0; i < r.length; i++) {
					epoly = epoly.addSubstract(poly.buildMonoPoly(r[i], e[i]));
				}
				elist.add(epoly.coefficients);
				// System.out.println("");
				result = new GFpoly(gf, a);
				/*
				 * System.out.println("first result");
				 * for(int u=0;u<result.coefficients.length;u++)
				 * System.out.print(result.coefficients[u]+" ");
				 * System.out.println("");
				 */

				for (int i = 0; i < r.length; i++) {
					result = result.addSubstract(poly.buildMonoPoly(r[i], e[i]));

					/*
					 * System.out.println("result");
					 * for(int u=0;u<result.coefficients.length;u++)
					 * System.out.print(result.coefficients[u]+" ");
					 * 
					 * System.out.println("");
					 */
				}
				// System.out.println("result");
				correct = true;
				this.correct = correct;
				kekka = new int[a.length];

				if (result.coefficients.length != a.length) {
					length = -result.coefficients.length + a.length;
					for (int i = 0; i < length; i++)
						kekka[i] = 0;
					for (int i = 0; i < result.coefficients.length; i++)
						kekka[length + i] = result.coefficients[i];
					this.correct = true;

					return kekka;

				} else {
					// System.out.println(clist.size());
					this.correct = true;
					return result.coefficients;
				}
			}

			/*
			 * try {
			 * //出力先を作成する
			 * FileWriter fw = new
			 * FileWriter("C:\\Users\\野村　知也\\Downloads\\大学\\RSProgramEx\\result.txt", true);
			 * //※１
			 * PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			 * 
			 * //内容を指定する
			 * pw.print("位置多項式が解けない ");
			 * 
			 * //ファイルに書き出す
			 * pw.close();
			 * 
			 * //終了メッセージを画面に出力する
			 * System.out.println("出力が完了しました。");
			 * 
			 * } catch (IOException ex) {
			 * //例外時処理
			 * ex.printStackTrace();
			 * }
			 */

		}
		this.correct = false;

		return a;
	}

	// boolean hantei(int[] a, int[] b, int t) {
	// int d=0;
	// for(int i=0;i<a.length;i++) {
	// if(a[i]!=b[i]) {
	// d++;
	// }
	// }
	// int[] aj = new int[d];
	// int[] bj = new int[d];
	// int[] combi = new int[d];
	// for(int i=0;i<a.length;i++) {
	// if(a[i]!=b[i]) {
	// aj[i] = a[i];
	// bj[i] = b[i];
	// }
	// }
	// int[] aa = new int[d-t];
	// int[] bb = new int[d-t];
	//
	// for(int i=0;i<d-t;i++) {
	// combi[i] = 1;
	// }
	// int iti = d-t;
	// for(int i=0;i<=d-t;i++) {
	// combi[i] = 1;
	// }
	// combi[d-t-1] = 0;
	//
	// int counta=0;
	// int countb=0;
	// boolean syuuryou = false;
	// boolean owa = false;
	// for(int i=0;counta<d-t&&!syuuryou;i++) {
	// counta = 0;
	// if(combi[i]==1) {
	// counta++;
	// }
	// if(counta==d-t) {
	// combi[i]=0;
	// combi[i+1]=1;
	// i=0;
	// counta=0;
	// }
	// if(combi[d-1]==1) {
	// int v=0;
	// countb=0;
	// for(int ii=0;v==0;ii++) {
	// if(combi[d-1-ii]!=1) {
	// v=1;
	// }
	// else{
	// countb++;
	// combi[d-1-ii]=0;
	// }
	// }
	// if(countb==d-t) {
	// syuuryou = true;
	// }
	// owa = false;
	// for(int ii=0;!syuuryou&&counta<d-t||owa;ii++) {
	// if(combi[ii]==1) {
	// counta++;
	// }
	// if(d-t-countb==counta) {
	// combi[ii]=0;
	// for(int j=ii+1;j<=ii+countb;j++) {
	// combi[j]=1;
	// }
	// owa = true;
	// }
	// }
	// }
	// }
	//
	// int check;
	// for(int i=0;i<combi.length;i++) {
	// if(combi[i]!=1)
	// check=i;
	// }
	//
	//
	//
	// }

	List<int[]> exdecode(int[] a, int d, int ε) {// d>=3
		GFpoly poly;
		GFpoly aRemainder;
		GFpoly bRemainder;
		GFpoly dxbRemainder;
		GFpoly quotient;
		GFpoly[] aProduct = { gf.getZero(), gf.getZero() };
		GFpoly bProduct;
		GFpoly result;
		int[] s;// シンドローム
		GFpoly S;// シンドローム多項式
		GFpoly aRem;
		GFpoly bRem;
		int[] c;
		List<int[]> clist = new ArrayList<>();
		elist = new ArrayList<>();
		int[] r;// 誤り位置多項式解
		int[] e;// 誤り
		int[] re;
		int f = 0;
		this.kainasi = f;
		boolean correct = true;
		this.correct = correct;
		int kekka[];
		int length;
		int t;
		t = (d - 1) / 2;
		this.kainasi = 0;
		this.hannigai = 0;

		s = new int[d - 1];
		poly = new GFpoly(gf, a);
		for (int i = 0; i < d - 1; i++)
			s[d - 1 - 1 - i] = poly.substitute(gf.getaTable(gf.getL() + i));
		sindrome = s;
		S = new GFpoly(gf, s);
		// System.out.print("ui");
		if (S.coefficients[0] == 0) {
			clist.add(a);
			return clist;// シンドロームが全て0
		}
		// re = decode(a,d);
		// if(this.correct) return re;
		else {// ユークリッド復号法

			for (int ex = 1; ex <= ε; ex++) {
				// List<int[]> slist = new ArrayList<>();
				// slist = generateS(ex);
				clist = new ArrayList<>();
				int[] exs = new int[d + 1 + 2 * (ex - 1)];
				for (int i = 0; i < s.length; i++) {
					exs[exs.length - 1 - i] = s[d - 1 - 1 - i];
				}
				int count = 0;
				int[] es = new int[2 * ex];
				c = kakudecode(a, exs);
				if (this.correct)
					clist.add(c);
				count++;
				for (int i = 0; i < es.length; i++) {
					es[i] = gf.getaTable(0);
				}

				for (int i = s.length; i < exs.length; i++) {
					exs[exs.length - 1 - i] = es[i - s.length];
				}

				c = kakudecode(a, exs);
				if (this.correct)
					clist.add(c);
				count++;
				es = new int[2 * ex];
				int check = 0;
				int[] kuri = new int[2 * ex];
				for (int i = 1; i < kuri.length; i++) {
					kuri[i] = -1;
				}
				while (check == 0) {
					// s = new int[2*ε];
					check = 1;
					kuri[0]++;
					for (int i = 0; i < kuri.length; i++) {
						if (kuri[i] == gf.getSize() - 1) {
							kuri[i + 1]++;
							kuri[i] = -1;
						}
					}
					for (int i = 0; i < es.length; i++) {
						if (kuri[i] != -1)
							es[i] = gf.getaTable(kuri[i]);
						else {
							es[i] = 0;
						}
						// System.out.print(gf.getlogTable(s[i])+ " ");
					}
					// System.out.println();
					for (int i = s.length; i < exs.length; i++) {
						exs[exs.length - 1 - i] = es[i - s.length];
					}
					c = kakudecode(a, exs);
					if (this.correct)
						clist.add(c);
					count++;

					for (int i = 0; i < es.length; i++) {
						if (kuri[i] != gf.getSize() - 2)
							check = 0;
					}
				}
				if (clist.size() == 1) {
					return clist;
				}
			}
		}
		if (clist.size() > 1) {
			System.out.println("復号できず");
			return clist;
		} else {
			clist.add(a);
			System.out.println("復号できず");
			return clist;
		}
	}

	public int[] randomR(int l) {
		Random random = new Random();
		int[] rand;
		rand = new int[l];
		for (int i = 0; i < l; i++) {
			int r;
			r = random.nextInt(gf.getSize());
			rand[i] = r;
		}
		return rand;
	}

	public int[] randomTR(int en[], int t) {
		Random random = new Random();
		int j = 0;
		int e = 0;
		int[] eror;
		int[] y;
		y = new int[t];
		for (int i = 0; i < y.length; i++)
			y[i] = -1;
		int x = -1;
		int[] result;
		int k;
		boolean diff;
		k = 0;
		eror = new int[en.length];
		result = new int[en.length];

		boolean enonzero = true;

		while (k < t) {
			enonzero = true;
			diff = true;
			j = random.nextInt(en.length);
			e = random.nextInt(gf.getSize());
			if (e == 0)
				enonzero = false;

			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;
			if (diff && enonzero) {
				eror[j] = e;
				y[k] = j;
				k = k + 1;
				// System.out.print("k: "+ k + " ");
			}

		}
		this.randome = eror;
		GFpoly evec = new GFpoly(gf, eror);
		GFpoly c = new GFpoly(gf, en);
		GFpoly re = c.addSubstract(evec);
		int difflen = 0;
		if (result.length > re.coefficients.length) {
			difflen = result.length - re.coefficients.length;

		}
		for (int i = difflen; i < result.length; i++) {
			result[i] = re.coefficients[i - difflen];
		}
		return result;
	}

	int[] allRandom(int[] en) {
		Random random = new Random();
		int j = 0;
		int e = 0;
		int[] eror;
		int[] y;
		int t = random.nextInt(26) + 1;
		y = new int[t];
		for (int i = 0; i < y.length; i++)
			y[i] = -1;
		int x = -1;
		int[] result;
		int k;
		boolean diff;
		k = 0;
		eror = new int[en.length];
		result = new int[en.length];

		boolean enonzero = true;

		while (k < t) {
			enonzero = true;
			diff = true;
			j = random.nextInt(en.length);
			e = random.nextInt(gf.getSize());
			if (e == 0)
				enonzero = false;

			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;
			if (diff && GF.bit(en[j], e) && enonzero) {
				eror[j] = e;
				y[k] = j;
				k = k + 1;
				// System.out.print("k: "+ k + " ");
			}

		}
		this.randome = eror;
		GFpoly evec = new GFpoly(gf, eror);
		GFpoly c = new GFpoly(gf, en);
		GFpoly re = c.addSubstract(evec);
		int difflen = 0;
		if (result.length > re.coefficients.length) {
			difflen = result.length - re.coefficients.length;

		}
		for (int i = difflen; i < result.length; i++) {
			result[i] = re.coefficients[i - difflen];
		}
		return result;
	}

	int[] random(int[] en, int t) {
		Random random = new Random();
		int j = 0;
		int e = 0;
		int[] eror;
		int[] y;
		y = new int[t];
		for (int i = 0; i < y.length; i++)
			y[i] = -1;
		int x = -1;
		int[] result;
		int k;
		boolean diff;
		k = 0;
		eror = new int[en.length];
		result = new int[en.length];

		boolean enonzero = true;

		while (k < t) {
			enonzero = true;
			diff = true;
			j = random.nextInt(en.length);
			e = random.nextInt(gf.getSize());
			if (e == 0)
				enonzero = false;

			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;
			if (diff && GF.bit(en[j], e) && enonzero) {
				eror[j] = e;
				y[k] = j;
				k = k + 1;
				// System.out.print("k: "+ k + " ");
			}

		}
		this.randome = eror;
		GFpoly evec = new GFpoly(gf, eror);
		GFpoly c = new GFpoly(gf, en);
		GFpoly re = c.addSubstract(evec);
		int difflen = 0;
		if (result.length > re.coefficients.length) {
			difflen = result.length - re.coefficients.length;

		}
		for (int i = difflen; i < result.length; i++) {
			result[i] = re.coefficients[i - difflen];
		}
		return result;
	}

	int[] jrandomTR(int[] en, int[] jl, int t) {
		Random random = new Random();
		int j = 0;
		int e = 0;
		int[] eror;
		int[] y;
		y = new int[t];
		for (int i = 0; i < y.length; i++)
			y[i] = -1;
		int x = -1;
		int[] result;
		int k;
		boolean diff;
		k = 0;
		eror = new int[en.length];
		result = new int[en.length];

		boolean enonzero = true;
		// System.out.println(jl.length);

		while (k < t) {
			enonzero = true;
			diff = true;

			j = random.nextInt(en.length);

			e = random.nextInt(gf.getSize());
			if (e == 0)
				enonzero = false;

			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;

			for (int f = 0; f < jl.length; f++)
				if (jl[f] == j)
					diff = false;
			// if(diff && GF.bit(en[j],e) && enonzero)
			// System.out.println(diff && GF.bit(en[j],e) && enonzero);
			if (diff && enonzero) {
				eror[j] = e;
				y[k] = j;
				k = k + 1;
				// System.out.print("k: "+ k + " ");
			}
			// System.out.println("roop");

		}
		this.randome = eror;
		GFpoly evec = new GFpoly(gf, eror);
		GFpoly c = new GFpoly(gf, en);
		GFpoly re = c.addSubstract(evec);
		int difflen = 0;
		if (result.length > re.coefficients.length) {
			difflen = result.length - re.coefficients.length;

		}
		for (int i = difflen; i < result.length; i++) {
			result[i] = re.coefficients[i - difflen];
		}
		return result;
	}

	int[] jrandom(int[] en, int[] jl, int t) {
		Random random = new Random();
		int j = 0;
		int e = 0;
		int[] eror;
		int[] y;
		y = new int[t];
		for (int i = 0; i < y.length; i++)
			y[i] = -1;
		int x = -1;
		int[] result;
		int k;
		boolean diff;
		k = 0;
		eror = new int[en.length];
		result = new int[en.length];

		boolean enonzero = true;
		// System.out.println(jl.length);

		while (k < t) {
			enonzero = true;
			diff = true;

			j = random.nextInt(en.length);

			e = random.nextInt(gf.getSize());
			if (e == 0)
				enonzero = false;

			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;

			for (int f = 0; f < jl.length; f++)
				if (jl[f] == j)
					diff = false;
			// if(diff && GF.bit(en[j],e) && enonzero)
			// System.out.println(diff && GF.bit(en[j],e) && enonzero);
			if (diff && GF.bit(en[j], e) && enonzero) {
				eror[j] = e;
				y[k] = j;
				k = k + 1;
				// System.out.print("k: "+ k + " ");
			}
			// System.out.println("roop");

		}
		this.randome = eror;
		GFpoly evec = new GFpoly(gf, eror);
		GFpoly c = new GFpoly(gf, en);
		GFpoly re = c.addSubstract(evec);
		int difflen = 0;
		if (result.length > re.coefficients.length) {
			difflen = result.length - re.coefficients.length;

		}
		for (int i = difflen; i < result.length; i++) {
			result[i] = re.coefficients[i - difflen];
		}
		return result;
	}

	int[] symbolsweight(int[] a) {
		int[] weight = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			weight[i] = GF.symbolweight(a[i]);
		}
		return weight;
	}

	GFpoly elect(int[] a, int d, int[] b) {
		int t = (d - 1) / 2;
		int eweight = 0;
		int[] weight = symbolsweight(a);
		int[] error = new int[a.length];
		int minimum = 1;
		while (eweight < d - t) {
			for (int i = 0; i < weight.length; i++) {
				if (minimum == weight[i] && b[i] == 0) {
					error[i] = a[i];
					eweight++;
				}
			}
			minimum++;
		}
		return new GFpoly(gf, error);
	}

	int[] gotei(int[] a, int d, int[] b) {// aの非零d-t個のシンボル（シンボル重みが小さい）を誤りeaとしてb+eaをかえす
		int t = (d - 1) / 2;
		int[] kekka = new int[a.length];
		int diff = 0;
		GFpoly error = elect(a, d, b);
		GFpoly result = new GFpoly(gf, b);
		result = result.addSubstract(error);
		if (kekka.length > result.coefficients.length) {
			diff = kekka.length - result.coefficients.length;
		}
		for (int i = diff; i < kekka.length - diff; i++) {
			kekka[i] = result.coefficients[i - diff];
		}
		return kekka;
	}

	Integer[] randomDisap(int en[], int t, int disap) {
		Random random = new Random();
		int j = 0;
		int e = 0;
		int[] y;
		y = new int[t + disap];
		for (int i = 0; i < y.length; i++)
			y[i] = -1;
		int x = -1;
		Integer[] result;
		int k;
		boolean diff;
		k = 0;
		result = new Integer[en.length];

		for (int i = 0; i < en.length; i++)
			result[i] = en[i];

		while (k < t) {
			diff = true;
			j = random.nextInt(en.length);
			e = random.nextInt(gf.getSize());
			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;
			if (diff && result[j] != e) {
				result[j] = e;
				y[k] = j;
				k = k + 1;
				// System.out.print("k: "+ k + " ");
			}
		}
		k = 0;
		while (k < disap) {
			diff = true;
			j = random.nextInt(en.length);
			for (int f = 0; f < y.length; f++)
				if (y[f] == j)
					diff = false;
			if (diff) {
				result[j] = null;
				y[k] = j;
				k = k + 1;
			}
		}

		return result;
	}

	int[][] parity(int d, int n) throws WriterException {
		int[][] parity = new int[d - 1][n];
		int substi = 0;
		int count = 0;
		for (int i = 0; i < parity.length; i++) {
			count = 0;
			for (int j = 0; j < parity[0].length; j++) {
				parity[i][j] = gf.getpow(gf.getaTable(substi), count);
				count++;
			}
			substi++;
		}
		boolean test = true;
		if (!test)
			try {
				// 出力先を作成する
				FileWriter fw = new FileWriter("C:\\Users\\野村　知也\\Downloads\\大学\\RSProgramEx\\testparity.txt", true); // ※１
				PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
				// 内容を指

				for (int ii = 0; ii < parity.length; ii++) {
					for (int j = 0; j < parity[0].length; j++) {
						if (parity[ii][j] == 1)
							pw.print("0 ");

						if (parity[ii][j] != 1)
							pw.print(gf.getlogTable(parity[ii][j]) + " ");
					}
					pw.println();
				}
				pw.println();
				// ファイルに書き出す
				pw.close();

				// 終了メッセージを画面に出力する
				// System.out.println("出力が完了しました。");

			} catch (IOException ex) {
				// 例外時処理
				ex.printStackTrace();
			}
		return parity;
	}

	int coeffmatrix(int[][] a, int[] b, int[] c, int[] e) throws WriterException {
		// (m*n)*(n*1) = (m*1) 拡大係数行列の作成 要素が全て0の列は除く
		int[][] result = new int[a.length][b.length + 1];
		int kaikaisu = 0;
		// int[] e = new int[b.length];
		int[] loacation;
		int m = 0;
		Random random = new Random();
		for (int i = 0; i < b.length; i++)
			if (b[i] != 0)
				m++;
		location = new int[m];
		m = 0;
		for (int i = 0; i < b.length; i++)
			if (b[i] != 0) {
				location[m] = i;
				m++;
			}

		// e[1] = 1;
		// e[1] = gf.getaTable(137);
		// e[7] = gf.getaTable(209);
		// e[15] = gf.getaTable(122);
		// e[21] = gf.getaTable(169);

		int tyoufuku = 0;
		for (int i = 0; i < b.length; i++) {
			if (e[i] != 0 && b[i] != 0)
				tyoufuku++;
		}
		int ew = 0;
		for (int i = 0; i < e.length; i++) {
			if (e[i] != 0)
				ew++;
		}

		int basicweight = ew + m - tyoufuku;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				result[i][j] = gf.multiply(a[i][j], b[b.length - 1 - j]);
			}
		}
		location = new int[m];
		m = 0;
		for (int i = 0; i < b.length; i++) {
			if (b[b.length - 1 - i] != 0) {
				location[m] = b.length - 1 - i;
				m++;
			}
		}
		for (int i = 0; i < a.length; i++) {
			result[i][b.length] = c[i];
		}
		int count = 0;
		int[] row;
		for (int j = 0; j < result[0].length; j++) {
			if (result[0][j] != 0) {
				count++;

			}
		}
		int l = 0;
		for (int i = 0; i < b.length; i++) {
			if (b[i] != 0)
				l++;
		}
		int[][] nresult = new int[result.length][count];
		row = new int[count];
		count = 0;
		for (int j = 0; j < result[0].length; j++) {
			if (result[0][j] != 0) {
				row[count] = j;
				count++;

			}
		}
		for (int j = 0; j < row.length; j++) {
			for (int i = 0; i < result.length; i++) {
				nresult[i][j] = result[i][row[j]];
			}
		}

		int is = 0;
		int ra = 0;
		int shift = 0;
		int ddg = 0;
		int[][] Q = new int[nresult.length][nresult[0].length];
		for (int i = 0; i < nresult.length; i++) {
			for (int j = 0; j < nresult[0].length; j++) {
				Q[i][j] = nresult[i][j];
			}
		}

		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\matrix.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指
		// /*
		// for (int[] x : Q)
		// {
		// for (int yy : x)
		// {
		// pw.print(yy + " ");
		// }
		// pw.println();
		// }
		// pw.println();
		// */
		//
		// for(int ii=0;ii<Q.length;ii++) {
		// for(int j=0;j<Q[0].length;j++) {
		// if(Q[ii][j]==1)
		// pw.print("1 ");
		//
		// if(Q[ii][j]!=1)
		// pw.print(gf.getlogTable(Q[ii][j])+ " ");
		// }
		// pw.println();
		// }
		// pw.println();
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// // System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }

		// Q = matrixSort(Q);
		int shortlength = 0;
		if (Q.length >= Q[0].length) {
			shortlength = Q[0].length;
		}
		if (Q.length <= Q[0].length) {
			shortlength = Q.length;
		}

		for (int i = 0; i < shortlength - shift; i++) {
			int p;
			if (Q[i][i + shift] == 0 && is == 0) {
				// Q = matrixSort(Q);
				ddg = 0;
				for (int sg = i; sg < Q.length; sg++) {
					if (Q[sg][i] != 0 && ddg != 1) {
						for (int ssg = 0; ssg < Q[0].length; ssg++) {
							int oo;
							oo = Q[sg][ssg];
							Q[sg][ssg] = Q[i][ssg];
							Q[i][ssg] = oo;
						}

						ddg = 1;
					}
				}

				if (ddg == 0) {
					// System.out.println(is);
					is = 1;
					ra = i;
				}

			}
			if (Q[i][i + shift] != 0 && is == 0) {
				p = Q[i][i + shift];
				for (int j = 0; j < Q[0].length; j++) {
					Q[i][j] = gf.divide(Q[i][j], p);
				}

				for (int j = 0; j < Q.length; j++) {
					if (i != j) {
						int d = Q[j][i + shift];

						for (int g = i + shift; g < Q[0].length - shift; g++) {
							Q[j][g] = gf.addSubstract(Q[j][g], gf.multiply(d, Q[i][g]));
						}
					}
				}

			}
			if (is == 1) {
				is = 0;
				i--;
				shift++;
			}
			if (i == shortlength - shift - 1) {

				// try {
				// //出力先を作成する
				// FileWriter fw = new FileWriter("C:\\Users\\野村
				// 知也\\Downloads\\大学\\RSProgramEx\\matrix.txt", true); //※１
				// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
				//
				// //内容を指
				// /*
				// for (int[] x : Q)
				// {
				// for (int yy : x)
				// {
				// pw.print(yy + " ");
				// }
				// pw.println();
				// }
				// pw.println();
				// */
				// for(int ii=0;ii<b.length;ii++) {
				// pw.print(b[ii]+" ");
				// }
				// pw.println();
				// for(int ii=0;ii<Q.length;ii++) {
				// for(int j=0;j<Q[0].length;j++) {
				// if(Q[ii][j]==1)
				// pw.print("1 ");
				//
				// if(Q[ii][j]!=1)
				// pw.print(gf.getlogTable(Q[ii][j])+ " ");
				// }
				// pw.println();
				// }
				// pw.println();
				// //ファイルに書き出す
				// pw.close();
				//
				// //終了メッセージを画面に出力する
				// // System.out.println("出力が完了しました。");
				//
				// } catch (IOException ex) {
				// //例外時処理
				// ex.printStackTrace();
				// }
				//

			}

			// System.out.print(fff);

		}

		int rank = 0;
		boolean syuuryou = false;
		// System.out.println(Q[0].length);
		for (int i = 0; i < Q.length; i++) {
			syuuryou = false;
			for (int j = 0; j < Q[0].length && !syuuryou; j++) {
				if (Q[i][j] != 0) {
					rank++;
					syuuryou = true;
				}
			}
		}
		if (Q[rank - 1][rank - 1] == 1)
			tanni++;
		// System.out.println(kaisuu);
		rankh[rank]++;

		boolean kai = false;
		if (rank < Q[0].length) {
			kai = true;
		}
		boolean hantei = true;
		int diffleng = 0;
		int ranj = 0;
		for (int j = 0; j < Q[0].length; j++) {
			if (Q[rank - 1][j] != 0) {
				diffleng = Q[0].length - 1 - j;
				ranj = j;
				break;
			}
		}
		boolean kau = false;
		boolean end = false;
		if (diffleng > 0)
			for (int i = 0; i < rank; i++) {
				end = false;
				kau = false;
				for (int j = 0; j < Q[0].length && !end; j++) {
					if (Q[i][j] != 0) {
						for (int jj = j + 1; jj < Q[0].length; jj++) {
							if (Q[i][jj] != 0) {
								kau = true;
							}
						}
						end = true;

					}
				}
				if (!kau)
					break;
			}
		if (diffleng <= 0) {
			kau = true;
			kai = false;
		}

		if (rank < Q.length && rank == Q[0].length - 1) {
			for (int i = rank; i < Q.length; i++) {
				if (Q[i][Q[0].length - 1] != 0) {
					hantei = false;
					break;
				}
			}
		}

		for (int j = 0; j < Q[0].length; j++) {
			if (Q[rank - 1][j] != 0) {
				if (j == Q[0].length - 1) {
					hantei = false;
				}
				break;
			}
		}
		if (!hantei) {
			kai = false;
		}

		if (rank == Q[0].length - 1 && Q[rank - 1][Q[0].length - 2] == 1) {
			for (int i = 0; i < rank; i++) {
				if (Q[i][Q[0].length - 1] == 0)
					kai = false;
			}
		}

		int sum = 0;
		int basicloca = Q[0].length - 1;
		int rowlocation = 0;
		boolean same = true;
		boolean owari = true;
		int jyoukenclear = 0;
		sikou++;
		// System.out.println(sikou);
		// if((!kai||!kau))
		// if(rank==0)
		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\testmatrix.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指
		// /*
		// for (int[] x : Q)
		// {
		// for (int yy : x)
		// {
		// pw.print(yy + " ");
		// }
		// pw.println();
		// }
		// pw.println();
		// */
		// pw.print(kai);
		// pw.print(" "+kau);
		// for(int i=0;i<b.length;i++) {
		// pw.print(b[i]+" ");
		// }
		// pw.println();
		// for(int ii=0;ii<Q.length;ii++) {
		// for(int j=0;j<Q[0].length;j++) {
		// if(Q[ii][j]==1)
		// pw.print("1 ");
		//
		// if(Q[ii][j]!=1)
		// pw.print(gf.getlogTable(Q[ii][j])+ " ");
		// }
		// pw.println();
		// }
		// pw.println();
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// // System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }

		if (kai && kau) {
			// if(rank==0)
			// try {
			// //出力先を作成する
			// FileWriter fw = new FileWriter("C:\\Users\\野村
			// 知也\\Downloads\\大学\\RSProgramEx\\truetestmatrix.txt", true); //※１
			// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			// //内容を指
			//
			// /*
			// for (int[] x : Q)
			// {
			// for (int yy : x)
			// {
			// pw.print(yy + " ");
			// }
			// pw.println();
			// }
			// pw.println();
			// */
			// for(int i=0;i<b.length;i++) {
			// pw.print(b[i]+" ");
			// }
			// pw.println();
			// for(int ii=0;ii<Q.length;ii++) {
			// for(int j=0;j<Q[0].length;j++) {
			// if(Q[ii][j]==1)
			// pw.print("1 ");
			//
			// if(Q[ii][j]!=1)
			// pw.print(gf.getlogTable(Q[ii][j])+ " ");
			// }
			// pw.println();
			// }
			// pw.println();
			// //ファイルに書き出す
			// pw.close();
			//
			// //終了メッセージを画面に出力する
			// // System.out.println("出力が完了しました。");
			//
			// } catch (IOException ex) {
			// //例外時処理
			// ex.printStackTrace();
			// }
			kaisuu++;
			int jyouken = 0;
			int weightsum = 0;

			// System.out.println(kaisuu);
			// if(rank==Q[0].length-2) {
			//
			// for(int i=0;i<Q[0].length-1;i++) {
			// int[] jadd = new int[Q[0].length-1];
			// int[] shiro = gf.shiroloca(e[location[i]]);
			// for(int j=0;j<shiro.length;j++) {
			// jadd = new int[Q[0].length-1];
			// jyouken = 0;
			// weightsum=0;
			// if(shiro[j]==1) {
			// jadd[i] = gf.getaTable(j);
			// for(int ii=0;ii<Q[0].length-1;ii++) {
			// if(ii!=i) {
			// if(i!=Q[0].length-2) {
			// int gyaku = gf.addSubstract(gf.multiply(Q[i][i],
			// jadd[i]),Q[i][Q[0].length-1]);
			// jadd[Q[0].length-2] = gf.divide(gyaku, Q[i][Q[0].length-2]);
			// if(ii<7)
			// jadd[ii] =
			// gf.addSubstract(gf.multiply(jadd[Q[0].length-2],Q[ii][Q[0].length-2]),
			// Q[ii][Q[0].length-1]);
			// if(ii>=7)
			// jadd[ii] =
			// gf.addSubstract(gf.multiply(jadd[Q[0].length-2],Q[ii-1][Q[0].length-2]),
			// Q[ii-1][Q[0].length-1]);
			//
			// if(gf.bit(jadd[ii],e[location[ii]])&&jadd[ii]!=0) {
			// jyouken++;
			// weightsum = weightsum + gf.symbolweight(jadd[ii]);
			// //System.out.print(jyouken+" ");
			// }
			// }else {
			// jadd[Q[0].length-3] = gf.addSubstract(gf.multiply(Q[Q.length-1][i],
			// jadd[i]),Q[Q.length-1][Q[0].length-1]);
			// if(ii<7)
			// jadd[ii] =
			// gf.addSubstract(gf.multiply(jadd[Q[0].length-2],Q[ii][Q[0].length-2]),
			// Q[ii][Q[0].length-1]);
			// if(ii>=7)
			// jadd[ii] =
			// gf.addSubstract(gf.multiply(jadd[Q[0].length-2],Q[ii-1][Q[0].length-2]),
			// Q[ii-1][Q[0].length-1]);
			// if(gf.bit(jadd[ii],e[location[ii]])&&jadd[ii]!=0) {
			// jyouken++;
			// weightsum = weightsum + gf.symbolweight(jadd[ii]);
			// //System.out.print(jyouken+" ");
			// }
			// }
			//
			// }
			// }
			// if(jyouken>=4) {
			// int[] coeff = new int[b.length];
			// for(int y=0;y<jadd.length;y++)
			// coeff[location[y]] = jadd[y];
			// System.out.print("jyouken "+jyouken);
			// System.out.println(" "+weightsum);
			// if(weightsum<=5) {
			// jyoukenclear++;
			// }
			//// for(int p=0;p<coeff.length;p++) {
			//// System.out.print(gf.getlogTable(coeff[p])+", ");
			////
			//// }
			//// System.out.println();
			//// System.out.print("元系列: ");
			//// for(int p=0;p<coeff.length;p++) {
			//// System.out.print(gf.getlogTable(e[p])+", ");
			//// }
			//// System.out.println();
			// }
			// }
			//
			// }
			//
			// }
			//
			// }
			// memo
			if (rank == Q[0].length - 1) {
				jyouken = 0;
				int weight = 0;
				boolean nonze = true;
				int[] maskc = mask(e);// 格納する系列にマスク処理した系列
				ConcurrentHashMap hints = new ConcurrentHashMap();
				int ver = 1;
				// //�G���[�������x���w��
				hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				hints.put(EncodeHintType.QR_VERSION, ver);
				Version version = Version.getVersionForNumber(ver);
				int dimension = version.getDimensionForVersion();
				ByteMatrix matrix = new ByteMatrix(dimension, dimension);
				int[] bits = gf.symboltobits(e);
				BitArray finalbits = new BitArray(bits, 208);
				int maskpattern = Encoder.chooseMaskByBlank(finalbits, ErrorCorrectionLevel.L, version, matrix);
				MatrixUtil.buildMatrix(finalbits, ErrorCorrectionLevel.L, version, maskpattern, matrix);
				int[] etest = Encoder.matrixToRow(matrix);

				System.out.println("検証");
				for (int p = 0; p < etest.length; p++) {
					System.out.print(etest[p] + " ");
				}
				System.out.println();

				int[] jadd = new int[Q[0].length - 1];
				int[] symw = new int[Q[0].length - 1];
				for (int ii = 0; ii < rank; ii++) {
					jadd[ii] = Q[ii][Q[0].length - 1];
					if (gf.bit(jadd[ii], e[location[ii]])) {
						symw[jyouken] = gf.symbolweight(jadd[ii]);
						jyouken++;
						weight = weight + gf.symbolweight(jadd[ii]);
					}
					if (jadd[ii] == 0) {
						nonze = false;
						break;
					}
				}
				if (jyouken >= Q[0].length - 1 - 3 && nonze) {
					if (jyouken > Q[0].length - 1 - 3) {
						while (jyouken > Q[0].length - 1 - 3) {
							int max = symw[0];
							for (int ii = 1; ii < jyouken; ii++) {
								if (max < symw[ii])
									max = symw[ii];
							}
							for (int ii = 0; ii < jyouken; ii++) {
								if (max == symw[ii]) {
									symw[ii] = 0;
									break;
								}
							}
							weight = weight - max;
							jyouken--;

						}
					}
					if (weight <= 8) {
						System.out.println("omomi:" + weight);
						cisum[weight]++;
						int[] coeff = new int[b.length];
						for (int y = 0; y < jadd.length; y++)
							coeff[location[y]] = jadd[y];
						for (int p = 0; p < coeff.length; p++) {
							System.out.print(gf.getlogTable(coeff[p]) + ", ");

						}
						System.out.println();
						System.out.print("元系列: ");
						for (int p = 0; p < coeff.length; p++) {
							System.out.print(gf.getlogTable(e[p]) + ", ");
						}
						System.out.println();
						int[] ci = new int[e.length];
						for (int ii = 0; ii < e.length; ii++) {

							ci[ii] = gf.addSubstract(coeff[ii], e[ii]);
							System.out.print(gf.getlogTable(ci[ii]) + ", ");
						}
						System.out.println();

						for (int ii = 0; ii < ci.length; ii++) {
							ciii[cicount][ii] = ci[ii];
						}
						cicount++;

						GFpoly cipoly = new GFpoly(gf, ci);
						int[] s = new int[7];
						for (int i = 0; i < 7; i++) {
							s[7 - 1 - i] = cipoly.substitute(gf.getaTable(gf.getL() + i));
						}
						GFpoly S = new GFpoly(gf, s);
						// System.out.print("ui");
						if (S.coefficients[0] != 0) {
							System.out.println("ng");
						}
						System.out.println();
						System.out.println();
					}
				}
			}
			if (rank <= Q[0].length - 2) {
				// System.out.println("通貨 ");
				int[] es = new int[Q[0].length - 1 - rank];
				int weight = 0;
				int[] maskc = mask(e);// 格納する系列にマスク処理した系列
				ConcurrentHashMap hints = new ConcurrentHashMap();
				int ver = 1;
				// //�G���[�������x���w��
				hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				hints.put(EncodeHintType.QR_VERSION, ver);
				Version version = Version.getVersionForNumber(ver);
				int dimension = version.getDimensionForVersion();
				ByteMatrix matrix = new ByteMatrix(dimension, dimension);
				int[] bits = gf.symboltobits(e);
				BitArray finalbits = new BitArray(bits, 208);
				int maskpattern = Encoder.chooseMaskByBlank(finalbits, ErrorCorrectionLevel.L, version, matrix);
				MatrixUtil.buildMatrix(finalbits, ErrorCorrectionLevel.L, version, maskpattern, matrix);
				int[] etest = Encoder.matrixToRow(matrix);

				System.out.println("検証");
				for (int p = 0; p < etest.length; p++) {
					System.out.print(etest[p] + " ");
				}
				System.out.println();
				// for(int p=0;p<maskc.length;p++) {
				// System.out.print(gf.getlogTable(maskc[p])+" ");
				// }
				// System.out.println();
				boolean nonze = true;
				for (int ii = 0; ii < es.length; ii++) {
					es[ii] = gf.getaTable(0);
				}
				int[] jadd = new int[Q[0].length - 1];
				int[] symw = new int[Q[0].length - 1];
				for (int ii = Q[0].length - 2; ii >= rank; ii--) {
					jadd[ii] = es[Q[0].length - 2 - ii];
					if (gf.bit(jadd[ii], maskc[location[ii]])) {
						symw[jyouken] = gf.symbolweight(jadd[ii]);
						jyouken++;
						weight = weight + gf.symbolweight(jadd[ii]);
					}

				}

				for (int ii = 0; ii < rank; ii++) {
					int add = 0;
					for (int jj = rank; jj < Q[0].length - 1; jj++) {
						add = gf.addSubstract(gf.multiply(Q[ii][jj], jadd[jj]), add);
					}
					add = gf.addSubstract(add, Q[ii][Q[0].length - 1]);
					if (add == 0) {
						nonze = false;
						break;
					}
					jadd[ii] = add;
					if (gf.bit(jadd[ii], maskc[location[ii]])) {
						symw[jyouken] = gf.symbolweight(jadd[ii]);
						jyouken++;
						weight = weight + gf.symbolweight(jadd[ii]);
					}
				}
				// System.out.println(jyouken);
				if (jyouken >= Q[0].length - 1 - 3 && nonze) {
					if (jyouken > Q[0].length - 1 - 3) {
						while (jyouken > Q[0].length - 1 - 3) {
							int max = symw[0];
							for (int ii = 1; ii < jyouken; ii++) {
								if (max < symw[ii])
									max = symw[ii];
							}
							for (int ii = 0; ii < jyouken; ii++) {
								if (max == symw[ii]) {
									symw[ii] = 0;
									break;
								}
							}
							weight = weight - max;
							jyouken--;

						}

					}

					if (weight <= 8) {
						System.out.println("omomi:" + weight);
						cisum[weight]++;
						int[] coeff = new int[b.length];
						for (int y = 0; y < jadd.length; y++)
							coeff[location[y]] = jadd[y];
						for (int p = 0; p < coeff.length; p++) {
							System.out.print(gf.getlogTable(coeff[p]) + ", ");

						}
						System.out.println();
						System.out.print("元系列: ");
						for (int p = 0; p < coeff.length; p++) {
							System.out.print(gf.getlogTable(e[p]) + ", ");
						}
						System.out.println();
						int[] ci = new int[e.length];
						for (int ii = 0; ii < e.length; ii++) {

							ci[ii] = gf.addSubstract(coeff[ii], e[ii]);
							System.out.print(gf.getlogTable(ci[ii]) + ", ");
						}
						System.out.println();
						for (int ii = 0; ii < ci.length; ii++) {
							ciii[cicount][ii] = ci[ii];
						}
						cicount++;
						GFpoly cipoly = new GFpoly(gf, ci);
						int[] s = new int[7];
						for (int i = 0; i < 7; i++) {
							s[7 - 1 - i] = cipoly.substitute(gf.getaTable(gf.getL() + i));
						}
						GFpoly S = new GFpoly(gf, s);
						// System.out.print("ui");
						if (S.coefficients[0] != 0) {
							System.out.println("ng");
						}
						System.out.println();
						for (int i = 1; i < cisum.length; i++) {
							System.out.print(" 塗りつぶす数" + i);

							System.out.print(": " + cisum[i]);
						}
						System.out.println();
					}
				}

				es = new int[Q[0].length - 1 - rank];
				weight = 0;
				nonze = true;
				jyouken = 0;
				int check = 0;
				int[] kuri = new int[Q[0].length - 1 - rank];
				for (int ii = 1; ii < kuri.length; ii++) {
					kuri[ii] = 0;
				}
				// System.out.println("check");
				while (check == 0) {
					// s = new int[2*ε];
					// System.out.println("check");
					check = 1;
					kuri[0]++;
					for (int ii = 0; ii < kuri.length; ii++) {
						if (kuri[ii] == gf.getSize() - 1) {
							kuri[ii + 1]++;
							kuri[ii] = 0;
						}
					}

					for (int ii = 0; ii < es.length; ii++) {
						if (kuri[ii] != -1)
							es[ii] = gf.getaTable(kuri[ii]);
						// System.out.print(gf.getlogTable(s[i])+ " ");
					}
					// for(int ii=0;ii<es.length;ii++) {
					// System.out.print(gf.getlogTable(es[ii])+" ");
					// }
					// System.out.println();
					weight = 0;
					nonze = true;
					jyouken = 0;
					jadd = new int[Q[0].length - 1];
					symw = new int[Q[0].length - 1];
					for (int ii = Q[0].length - 2; ii >= rank; ii--) {// int ii=rank;ii>=Q[0].length-1-es.length;ii--
						jadd[ii] = es[Q[0].length - 2 - ii];
						if (gf.bit(jadd[ii], maskc[location[ii]])) {
							symw[jyouken] = gf.symbolweight(jadd[ii]);
							jyouken++;
							weight = weight + gf.symbolweight(jadd[ii]);
						}

					}

					for (int ii = 0; ii < rank; ii++) {
						int add = 0;
						for (int jj = rank; jj < Q[0].length - 1; jj++) {
							add = gf.addSubstract(gf.multiply(Q[ii][jj], jadd[jj]), add);
						}
						add = gf.addSubstract(add, Q[ii][Q[0].length - 1]);
						if (add == 0) {
							nonze = false;
							break;
						}
						jadd[ii] = add;
						if (gf.bit(jadd[ii], maskc[location[ii]])) {
							symw[jyouken] = gf.symbolweight(jadd[ii]);
							jyouken++;
							weight = weight + gf.symbolweight(jadd[ii]);
						}
					}
					// System.out.println(jyouken);
					if (jyouken >= Q[0].length - 1 - 3 && nonze) {
						if (jyouken > Q[0].length - 1 - 3) {
							while (jyouken > Q[0].length - 1 - 3) {
								int max = symw[0];
								for (int ii = 1; ii < jyouken; ii++) {
									if (max < symw[ii])
										max = symw[ii];
								}
								for (int ii = 0; ii < jyouken; ii++) {
									if (max == symw[ii]) {
										symw[ii] = 0;
										break;
									}
								}
								weight = weight - max;
								jyouken--;

							}
						}
						if (weight <= 8) {// weight 塗りつぶす数
							System.out.println("omomi:" + weight);
							int[] coeff = new int[b.length];
							cisum[weight]++;
							for (int y = 0; y < jadd.length; y++)
								coeff[location[y]] = jadd[y];
							for (int p = 0; p < coeff.length; p++) {
								System.out.print(gf.getlogTable(coeff[p]) + ", ");

							}
							System.out.println();
							System.out.print("元系列: ");
							for (int p = 0; p < coeff.length; p++) {
								System.out.print(gf.getlogTable(e[p]) + ", ");
							}
							System.out.println();
							int[] ci = new int[e.length];
							for (int ii = 0; ii < e.length; ii++) {

								ci[ii] = gf.addSubstract(coeff[ii], e[ii]);
								System.out.print(gf.getlogTable(ci[ii]) + ", ");
							}

							// 追加
							for (int ii = 0; ii < ci.length; ii++) {
								ciii[cicount][ii] = ci[ii];
							}
							cicount++;
							//
							System.out.println();
							GFpoly cipoly = new GFpoly(gf, ci);
							int[] s = new int[7];
							for (int i = 0; i < 7; i++) {
								s[7 - 1 - i] = cipoly.substitute(gf.getaTable(gf.getL() + i));
							}
							GFpoly S = new GFpoly(gf, s);
							// System.out.print("ui");
							if (S.coefficients[0] != 0) {
								System.out.println("ng");
							}
							System.out.println();
							for (int i = 1; i < cisum.length; i++) {
								System.out.print(" 塗りつぶす数" + i);

								System.out.print(": " + cisum[i]);
							}
							System.out.println();
						}
					}

					for (int ii = 0; ii < es.length; ii++) {
						if (kuri[ii] != gf.getSize() - 2)
							check = 0;
					}

				}
			}

			if (sum == 0 && rank == 0) {
				sum = 1;
				if (b[0] != 0) {
					simhist[gf.getlogTable(Q[0][rank])]++;
				} else {
					simhist[256]++;
				}
				int[] jjadd = new int[Q[0].length - 1];
				for (int ii = 0; ii > jjadd.length; ii++)
					jjadd[ii] = Q[ii][Q[0].length - 1];
				int wa = -1;
				int wc = 0;
				int[] coeff = new int[b.length];
				for (int y = 0; y < jjadd.length; y++)
					coeff[location[y]] = jjadd[y];
				for (int y = 0; y < b.length; y++) {
					if (e[y] != 0 && b[y] != 0) {
						wa = GF.addSubstract(e[y], coeff[y]);
						if (wa == 0)
							wc++;
					}
				}
				bunnpu[basicweight - wc]++;
			}
			// System.out.println(cisum);
		}

		return sum;
	}

	// public int[] generateErrorv2(int[] c)throws
	// WriterException{//下記と同じ，各白モジュールに当確率で誤りを加えてたのに対し，各シンボルに当確率で白→黒となるランダムな誤りをくわえる
	// ConcurrentHashMap hints = new ConcurrentHashMap();
	// Random random = new Random();
	// int ver = 1;
	//// //�G���[�������x���w��
	// hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	// hints.put(EncodeHintType.QR_VERSION, ver);
	// Version version = Version.getVersionForNumber(ver);
	// int dimension = version.getDimensionForVersion();
	// ByteMatrix matrix = new ByteMatrix(dimension, dimension);
	// int[] bits = gf.symboltobits(c);
	// BitArray finalbits = new BitArray(bits,208);
	// int maskpattern = Encoder.chooseMaskByBlank(finalbits,ErrorCorrectionLevel.L
	// , version ,matrix);
	// //System.out.println("mask: "+maskpattern);
	// MatrixUtil.buildMatrix(finalbits,ErrorCorrectionLevel.L ,
	// version,maskpattern,matrix);
	// int[] etest = Encoder.matrixToRow(matrix);
	//
	// int ayamarisymbolnum = random.nextInt(8)+1;
	//
	//
	// }
	public int[] generateError(int[] c) throws WriterException {// cにマスク付加したもので白⇒黒となる誤りをランダム生成
		ConcurrentHashMap hints = new ConcurrentHashMap();
		Random random = new Random();
		int ver = 1;
		// //�G���[�������x���w��
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.QR_VERSION, ver);
		Version version = Version.getVersionForNumber(ver);
		int dimension = version.getDimensionForVersion();
		ByteMatrix matrix = new ByteMatrix(dimension, dimension);
		int[] bits = gf.symboltobits(c);
		BitArray finalbits = new BitArray(bits, 208);
		int maskpattern = Encoder.chooseMaskByBlank(finalbits, ErrorCorrectionLevel.L, version, matrix);
		// System.out.println("mask: "+maskpattern);
		MatrixUtil.buildMatrix(finalbits, ErrorCorrectionLevel.L, version, maskpattern, matrix);
		int[] etest = Encoder.matrixToRow(matrix);
		// System.out.println("検証（マスクパターン付加済みmatrix→symbol→bit→誤り位置bit→）");
		// for(int i=0;i<etest.length;i++) {
		// System.out.print(etest[i]+" ");
		// }
		// System.out.println();
		int[] bit = gf.tobit(etest);
		// 検証用{

		// for(int i=0;i<bit.length;i++) {
		// System.out.print(bit[i]);
		// }
		// System.out.println();
		// }//ここまで正解
		int blankcount = 0;
		for (int i = 0; i < bit.length; i++) {
			if (bit[i] != 1)
				blankcount++;
		}

		int[] blanklocation = new int[blankcount];
		blankcount = 0;

		for (int i = 0; i < bit.length; i++) {
			if (bit[i] == 0) {
				blanklocation[blankcount] = i;
				blankcount++;
			}
		}
		// int numblackout = random.nextInt(8)+1;
		int numblackout = 8;
		// System.out.println("blc:"+ numblackout);
		int[] errorlocation = new int[numblackout];
		int blackcount = 0;
		while (numblackout > blackcount) {
			int candidateEL = random.nextInt(blankcount);
			boolean check = true;
			for (int i = 0; i < blackcount; i++) {
				if (errorlocation[i] == blanklocation[candidateEL]) {
					check = false;
					break;
				}
			}
			if (check) {
				errorlocation[blackcount] = blanklocation[candidateEL];
				blackcount++;
			}
		}
		int[] error = new int[bit.length];
		for (int i = 0; i < errorlocation.length; i++) {
			error[errorlocation[i]] = 1;
		}
		int ayamarinum = 0;
		int[] errorsymbol = gf.bittosymbol(error);
		for (int i = 0; i < errorsymbol.length; i++) {
			if (errorsymbol[i] != 0) {
				ayamarinum++;
			}
		}
		// System.out.println("誤りの重み： "+ayamarinum);
		// 検証用{
		// for(int i=0;i<bit.length;i++) {
		// System.out.print(error[i]);
		// }
		// System.out.println();
		// }

		return error;
	}

	int[] mask(int[] c) {
		// maskp = 2,ver = 1 ecl = L
		int[][] bit = new int[26][8];
		for (int i = 1; i <= bit.length; i++) {
			int[] vectol = gf.vectol(c[i - 1]);
			for (int j = 0; j < vectol.length; j++) {
				bit[i - 1][j] = vectol[j];
				if (i == 4 || i == 5 || i == 6 || i == 13 || i == 14 || i == 15 || i == 16 || i == 17 || i == 25) {
					if (7 - j == 7 || 7 - j == 5 || 7 - j == 3 || 7 - j == 1) {

						boolean owa = false;
						if (bit[i - 1][j] == 1) {
							bit[i - 1][j] = 0;
							owa = true;
						}

						if (bit[i - 1][j] == 0 && !owa)
							bit[i - 1][j] = 1;
					}
				}
				if (i == 7 || i == 8 || i == 9 || i == 18 || i == 19 || i == 20 || i == 21 || i == 22 || i == 26) {
					if (7 - j == 6 || 7 - j == 4 || 7 - j == 2 || 7 - j == 0) {

						boolean owa = false;
						if (bit[i - 1][j] == 1) {
							bit[i - 1][j] = 0;
							owa = true;
						}

						if (bit[i - 1][j] == 0 && !owa)
							bit[i - 1][j] = 1;
					}
				}
			}
		}
		int[] result = new int[26];
		for (int i = 1; i <= bit.length; i++) {
			for (int j = 7; j >= 0; j--) {
				if (bit[i - 1][7 - j] == 1)
					result[i - 1] = result[i - 1] + gf.pow(2, j);
			}
		}
		// for(int p=0;p<c.length;p++) {
		// System.out.print(gf.getlogTable(c[p])+" ");
		// }
		// System.out.println();
		// for(int p=0;p<result.length;p++) {
		// System.out.print(gf.getlogTable(result[p])+" ");
		// }
		//
		// System.out.println();
		return result;
	}

	public int sindserch(int d, int[] c, int l) throws WriterException {// d:最小重み c:長さn(符号長)の系列 v: vH^t =
																		// cH^tを満たす重みlのベクトル
		tanni = 0;
		int count = 0;
		if (l >= d)
			rankh = new int[d];
		if (l < d)
			rankh = new int[l + 1 + 1];
		kaisuu = 0;
		simhist = new int[257];
		bunnpu = new int[71];
		cisum = new int[9];
		ciii = new int[20000][c.length];
		sikou = 0;
		int[] sind = new int[d - 1];
		int n = c.length;
		int[] b;
		int sum = 0;
		GFpoly cpoly = new GFpoly(gf, c);
		for (int i = 0; i < sind.length; i++) {
			sind[i] = cpoly.substitute(gf.getaTable(i));
		}
		int[] combi = new int[n];
		for (int i = 0; i < l; i++) {
			combi[i] = 1;
		}
		// combi[n-m-1] = 0;
		// for(int ii=0;ii<combi.length;ii++) {
		// System.out.print(combi[ii]+ " ");
		// }
		// System.out.println();
		b = new int[n];
		for (int ii = 0; ii < combi.length; ii++) {
			if (combi[ii] == 1) {
				b[ii] = 1;
			}
		}
		sum = sum + coeffmatrix(parity(d, n), b, sind, c);
		count++;
		// System.out.println(count);
		int counta = 0;
		int countb = 0;
		boolean syuuryou = false;
		boolean owa = false;
		for (int i = 0; !syuuryou; i++) {// counta<l&&
			// System.out.print(i+" ");
			// System.out.println(counta);
			if (combi[i] == 1) {
				counta++;
			}
			if (counta == l) {
				combi[i] = 0;
				combi[i + 1] = 1;
				i = -1;
				counta = 0;
				// for(int ii=0;ii<combi.length;ii++) {
				// System.out.print(combi[ii]+ " ");
				// }
				// System.out.println();
				b = new int[n];
				for (int ii = 0; ii < combi.length; ii++) {
					if (combi[ii] == 1) {
						b[ii] = 1;
					}
				}
				sum = sum + coeffmatrix(parity(d, n), b, sind, c);
				count++;
				// System.out.println(count);
			}
			if (combi[n - 1] == 1) {
				int v = 0;
				countb = 0;
				counta = 0;
				for (int ii = 0; v == 0; ii++) {
					if (combi[n - 1 - ii] != 1) {
						v = 1;
					} else {
						countb++;
						combi[n - 1 - ii] = 0;
					}
				}
				if (countb == l) {
					syuuryou = true;
				}
				// for(int ii=0;ii<combi.length;ii++) {
				// System.out.print(combi[ii]+ " ");
				// }
				// System.out.println();
				owa = false;
				for (int ii = 0; (!syuuryou && counta < l) || !owa; ii++) {
					// System.out.print(ii+" ");
					// System.out.print(l-countb+" ");
					// System.out.println(counta);
					if (combi[ii] == 1) {
						counta++;
					}
					if (l - countb == counta) {
						combi[ii] = 0;
						for (int j = ii + 1; j <= ii + 1 + countb; j++) {
							combi[j] = 1;
						}
						owa = true;
					}
				}
				counta = 0;
				i = -1;
				// for(int ii=0;ii<combi.length;ii++) {
				// System.out.print(combi[ii]+ " ");
				// }
				// System.out.println();
				b = new int[n];
				for (int ii = 0; ii < combi.length; ii++) {
					if (combi[ii] == 1) {
						b[ii] = 1;
					}
				}
				sum = sum + coeffmatrix(parity(d, n), b, sind, c);
				count++;
				// System.out.println(count);
				countb = 0;
				// counta=0;
				v = 0;
				for (int ii = 0; v == 0; ii++) {
					if (combi[n - 1 - ii] != 1) {
						v = 1;
					} else {
						countb++;
						// combi[n-m-1-ii]=0;
					}
				}
				if (countb == l) {
					syuuryou = true;
				}
			}

		}
		// System.out.println(cisum);
		return sum;
	}

	// int sindmisco(int d,int[] c,int l){//重みdの符号語の非零シンボルをm個選択し，シンドロームを計算
	// 非零シンボルの位置が重複せず，シンドロームの値が一致するl個の非零シンボル
	// int[] sind = new int[d-1];
	// int n = c.length;
	// int[] b;
	// int sum = 0;
	// GFpoly cpoly = new GFpoly(gf,c);
	// for(int i=0;i<sind.length;i++){
	// sind[i] = cpoly.substitute(gf.getaTable(i));
	// }
	// int m=0;
	// for(int i=0;i<c.length;i++) {
	// if(c[i]!=0) {
	// m++;
	// }
	// }
	// int[] location = new int[n-m];
	// int loca=0;
	// for(int i=0;i<c.length;i++) {
	// if(c[i]==0) {
	// location[loca] = i;
	// loca++;
	// }
	// }
	// int[] a = new int[n];
	// int[] combi = new int[n-m];
	// for(int i=0;i<l;i++) {
	// combi[i] = 1;
	// }
	// //combi[n-m-1] = 0;
	//// for(int ii=0;ii<combi.length;ii++) {
	//// System.out.print(combi[ii]+ " ");
	//// }
	//// System.out.println();
	// b = new int[n];
	// for(int ii=0;ii<combi.length;ii++) {
	// if(combi[ii]==1) {
	// b[location[ii]]=1;
	// }
	// }
	// sum = sum + coeffmatrix(parity(d,n), b, sind);
	//
	// int counta=0;
	// int countb=0;
	// boolean syuuryou = false;
	// boolean owa = false;
	// for(int i=0;!syuuryou;i++) {//counta<l&&
	// //System.out.print(i+" ");
	// //System.out.println(counta);
	// if(combi[i]==1) {
	// counta++;
	// }
	// if(counta==l) {
	// combi[i]=0;
	// combi[i+1]=1;
	// i=-1;
	// counta=0;
	//// for(int ii=0;ii<combi.length;ii++) {
	//// System.out.print(combi[ii]+ " ");
	//// }
	//// System.out.println();
	// b = new int[n];
	// for(int ii=0;ii<combi.length;ii++) {
	// if(combi[ii]==1) {
	// b[location[ii]]=1;
	// }
	// }
	// sum = sum + coeffmatrix(parity(d,n), b, sind);
	//
	// }
	// if(combi[n-m-1]==1) {
	// int v=0;
	// countb=0;
	// counta=0;
	// for(int ii=0;v==0;ii++) {
	// if(combi[n-m-1-ii]!=1) {
	// v=1;
	// }
	// else{
	// countb++;
	// combi[n-m-1-ii]=0;
	// }
	// }
	// if(countb==l) {
	// syuuryou = true;
	// }
	//// for(int ii=0;ii<combi.length;ii++) {
	//// System.out.print(combi[ii]+ " ");
	//// }
	//// System.out.println();
	// owa = false;
	// for(int ii=0;(!syuuryou&&counta<l)||!owa;ii++) {
	// //System.out.print(ii+" ");
	// //System.out.print(l-countb+" ");
	// //System.out.println(counta);
	// if(combi[ii]==1) {
	// counta++;
	// }
	// if(l-countb==counta) {
	// combi[ii]=0;
	// for(int j=ii+1;j<=ii+1+countb;j++) {
	// combi[j]=1;
	// }
	// owa = true;
	// }
	// }
	// counta = 0;
	// i=-1;
	//// for(int ii=0;ii<combi.length;ii++) {
	//// System.out.print(combi[ii]+ " ");
	//// }
	//// System.out.println();
	// b = new int[n];
	// for(int ii=0;ii<combi.length;ii++) {
	// if(combi[ii]==1) {
	// b[location[ii]]=1;
	// }
	// }
	// sum = sum + coeffmatrix(parity(d,n), b, sind);
	//
	// countb=0;
	// //counta=0;
	// v=0;
	// for(int ii=0;v==0;ii++) {
	// if(combi[n-m-1-ii]!=1) {
	// v=1;
	// }
	// else{
	// countb++;
	// //combi[n-m-1-ii]=0;
	// }
	// }
	// if(countb==l) {
	// syuuryou = true;
	// }
	// }
	//
	// }
	//
	// return sum;
	// }

	int sindmiscoj(int d, int[] c, int l) throws WriterException {// 重みdの符号語の非零シンボルをm個選択し，シンドロームを計算，シンドロームの値が一致するl個の非零シンボル
		int[] sind = new int[d - 1];
		int n = c.length;
		int[] b;
		int sum = 0;
		int csousuu = 0;
		int combikazu = 0;
		tanni = 0;
		if (l >= d)
			rankh = new int[d];
		if (l < d)
			rankh = new int[l + 1 + 1];
		kaisuu = 0;
		simhist = new int[257];
		bunnpu = new int[71];
		GFpoly cpoly = new GFpoly(gf, c);
		for (int i = 0; i < sind.length; i++) {
			sind[i] = cpoly.substitute(gf.getaTable(i));
		}
		int m = 0;
		for (int i = 0; i < c.length; i++) {
			if (c[i] != 0) {
				m++;
			}
		}
		int diff = 0;
		int keisann;
		boolean syuu = false;
		while (!syuu) {
			if (l - diff - 7 < 0) {
				keisann = diff + 7 - l + diff;
			} else {
				keisann = l - diff - 7 + diff;
			}

			if (keisann <= 3) {
				break;
			} else {
				diff--;
			}
		}
		int li = l;
		diff = 0;
		for (int p = 0; p <= diff; p++) {
			int[] location = new int[n - m];
			// System.out.println(p);
			// System.out.println(combikazu);
			combikazu = 0;
			li = l - p;
			int loca = 0;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == 0) {
					location[loca] = i;
					loca++;
				}
			}
			int[] a = new int[n];
			int[] combi = new int[n - m];
			for (int i = 0; i < li; i++) {
				combi[i] = 1;
			}
			// int[] comp = new int[m];
			int locap = 0;
			int[] locationp = new int[m];
			for (int i = 0; i < c.length; i++) {
				if (c[i] != 0) {
					locationp[locap] = i;
					locap++;
				}
			}
			// combi[n-m-1] = 0;
			// for(int ii=0;ii<combi.length;ii++) {
			// System.out.print(combi[ii]+ " ");
			// }
			// System.out.println();
			combikazu++;
			if (p > 0) {
				System.out.println("通貨");
				int[] com = new int[m];
				if (p == 1) {
					for (int jj = 0; jj < com.length; jj++) {
						com = new int[m];
						com[jj] = 1;
						b = new int[n];
						for (int ii = 0; ii < combi.length; ii++) {
							if (combi[ii] == 1) {
								b[location[ii]] = 1;
							}
						}
						for (int ii = 0; ii < com.length; ii++) {
							if (com[ii] == 1) {
								b[locationp[ii]] = 1;
							}
						}
						sum = sum + coeffmatrix(parity(d, n), b, sind, c);
						csousuu++;
					}
				}
				if (p > 1) {
					System.out.println("通");
					com = new int[m];
					for (int i = 0; i < p; i++) {
						com[i] = 1;
					}
					b = new int[n];
					for (int ii = 0; ii < combi.length; ii++) {
						if (combi[ii] == 1) {
							b[location[ii]] = 1;
						}
					}
					for (int ii = 0; ii < com.length; ii++) {
						if (com[ii] == 1) {
							b[locationp[ii]] = 1;
						}
					}
					sum = sum + coeffmatrix(parity(d, n), b, sind, c);
					csousuu++;
					int counta = 0;
					int countb = 0;
					boolean syuuryou = false;
					boolean owa = false;
					for (int i = 0; !syuuryou; i++) {// counta<l&&
						// System.out.print(i+" ");
						// System.out.println(counta);
						if (com[i] == 1) {
							counta++;
						}
						if (counta == p) {
							com[i] = 0;
							com[i + 1] = 1;
							i = -1;
							counta = 0;
							// for(int ii=0;ii<com.length;ii++) {
							// System.out.print(com[ii]+ " ");
							// }
							// System.out.println();
							b = new int[n];
							for (int ii = 0; ii < combi.length; ii++) {
								if (combi[ii] == 1) {
									b[location[ii]] = 1;
								}
							}
							for (int ii = 0; ii < com.length; ii++) {
								if (com[ii] == 1) {
									b[locationp[ii]] = 1;
								}
							}
							sum = sum + coeffmatrix(parity(d, n), b, sind, c);
							csousuu++;

						}
						if (com[m - 1] == 1) {
							int v = 0;
							countb = 0;
							counta = 0;
							for (int ii = 0; v == 0; ii++) {
								if (com[m - 1 - ii] != 1) {
									v = 1;
								} else {
									countb++;
									com[m - 1 - ii] = 0;
								}
							}
							if (countb == p) {
								syuuryou = true;
							}
							// for(int ii=0;ii<combi.length;ii++) {
							// System.out.print(combi[ii]+ " ");
							// }
							// System.out.println();
							owa = false;
							for (int ii = 0; (!syuuryou && counta < p) || !owa; ii++) {
								// System.out.print(ii+" ");
								// System.out.print(l-countb+" ");
								// System.out.println(counta);
								if (com[ii] == 1) {
									counta++;
								}
								if (p - countb == counta) {
									com[ii] = 0;
									for (int j = ii + 1; j <= ii + 1 + countb; j++) {
										com[j] = 1;
									}
									owa = true;
								}
							}
							counta = 0;
							i = -1;
							// for(int ii=0;ii<com.length;ii++) {
							// System.out.print(com[ii]+ " ");
							// }
							// System.out.println();
							b = new int[n];
							for (int ii = 0; ii < combi.length; ii++) {
								if (combi[ii] == 1) {
									b[location[ii]] = 1;
								}
							}
							for (int ii = 0; ii < com.length; ii++) {
								if (com[ii] == 1) {
									b[locationp[ii]] = 1;
								}
							}
							sum = sum + coeffmatrix(parity(d, n), b, sind, c);
							csousuu++;
							countb = 0;
							// counta=0;
							v = 0;
							for (int ii = 0; v == 0; ii++) {
								if (com[m - 1 - ii] != 1) {
									v = 1;
								} else {
									countb++;
									// combi[n-m-1-ii]=0;
								}
							}
							if (countb == p) {
								syuuryou = true;
							}
						}

					}
				}
			}

			if (p == 0) {
				System.out.println("貨");
				b = new int[n];
				for (int ii = 0; ii < combi.length; ii++) {
					if (combi[ii] == 1) {
						b[location[ii]] = 1;
					}
				}
				sum = sum + coeffmatrix(parity(d, n), b, sind, c);
				csousuu++;
			}

			int counta = 0;
			int countb = 0;
			boolean syuuryou = false;
			boolean owa = false;

			for (int i = 0; !syuuryou; i++) {// counta<l&&
				// System.out.print(i+" ");
				// System.out.println(counta);
				if (combi[i] == 1) {
					counta++;
				}
				if (counta == li) {
					combi[i] = 0;
					combi[i + 1] = 1;
					i = -1;
					counta = 0;
					// for(int ii=0;ii<combi.length;ii++) {
					// System.out.print(combi[ii]+ " ");
					// }
					// System.out.println();
					combikazu++;
					if (p > 0) {

						int[] com = new int[m];
						if (p == 1) {
							for (int jj = 0; jj < com.length; jj++) {
								com = new int[m];
								com[jj] = 1;
								b = new int[n];
								for (int ii = 0; ii < combi.length; ii++) {
									if (combi[ii] == 1) {
										b[location[ii]] = 1;
									}
								}
								for (int ii = 0; ii < com.length; ii++) {
									if (com[ii] == 1) {
										b[locationp[ii]] = 1;
									}
								}
								sum = sum + coeffmatrix(parity(d, n), b, sind, c);
								csousuu++;
							}
						}
						if (p > 1) {
							com = new int[m];
							for (int ii = 0; ii < p; ii++) {
								com[ii] = 1;
							}
							b = new int[n];
							for (int ii = 0; ii < combi.length; ii++) {
								if (combi[ii] == 1) {
									b[location[ii]] = 1;
								}
							}
							for (int ii = 0; ii < com.length; ii++) {
								if (com[ii] == 1) {
									b[locationp[ii]] = 1;
								}
							}
							sum = sum + coeffmatrix(parity(d, n), b, sind, c);
							csousuu++;

							int countap = 0;
							int countbp = 0;
							boolean syuuryoup = false;
							boolean owap = false;
							for (int ip = 0; !syuuryoup; ip++) {// counta<l&&
								// System.out.print(i+" ");
								// System.out.println(counta);
								if (com[ip] == 1) {
									countap++;
								}
								if (countap == p) {
									com[ip] = 0;
									com[ip + 1] = 1;
									ip = -1;
									countap = 0;
									// for(int ii=0;ii<combi.length;ii++) {
									// System.out.print(combi[ii]+ " ");
									// }
									// System.out.println();
									b = new int[n];
									for (int ii = 0; ii < combi.length; ii++) {
										if (combi[ii] == 1) {
											b[location[ii]] = 1;
										}
									}
									for (int ii = 0; ii < com.length; ii++) {
										if (com[ii] == 1) {
											b[locationp[ii]] = 1;
										}
									}
									sum = sum + coeffmatrix(parity(d, n), b, sind, c);
									csousuu++;
								}
								if (com[m - 1] == 1) {
									int v = 0;
									countbp = 0;
									countap = 0;
									for (int ii = 0; v == 0; ii++) {
										if (com[m - 1 - ii] != 1) {
											v = 1;
										} else {
											countbp++;
											com[m - 1 - ii] = 0;
										}
									}
									if (countbp == p) {
										syuuryoup = true;
									}
									// for(int ii=0;ii<combi.length;ii++) {
									// System.out.print(combi[ii]+ " ");
									// }
									// System.out.println();
									owap = false;
									for (int ii = 0; (!syuuryoup && countap < p) || !owap; ii++) {
										// System.out.print(ii+" ");
										// System.out.print(l-countb+" ");
										// System.out.println(counta);
										if (com[ii] == 1) {
											countap++;
										}
										if (p - countbp == countap) {
											com[ii] = 0;
											for (int j = ii + 1; j <= ii + 1 + countbp; j++) {
												com[j] = 1;
											}
											owap = true;
										}
									}
									countap = 0;
									ip = -1;
									// for(int ii=0;ii<combi.length;ii++) {
									// System.out.print(combi[ii]+ " ");
									// }
									// System.out.println();
									b = new int[n];
									for (int ii = 0; ii < combi.length; ii++) {
										if (combi[ii] == 1) {
											b[location[ii]] = 1;
										}
									}
									for (int ii = 0; ii < com.length; ii++) {
										if (com[ii] == 1) {
											b[locationp[ii]] = 1;
										}
									}
									sum = sum + coeffmatrix(parity(d, n), b, sind, c);
									csousuu++;
									countbp = 0;
									// counta=0;
									v = 0;
									for (int ii = 0; v == 0; ii++) {
										if (com[m - 1 - ii] != 1) {
											v = 1;
										} else {
											countbp++;
											// combi[n-m-1-ii]=0;
										}
									}
									if (countbp == p) {
										syuuryoup = true;
									}
								}

							}
						}
					}

					if (p == 0) {
						b = new int[n];
						for (int ii = 0; ii < combi.length; ii++) {
							if (combi[ii] == 1) {
								b[location[ii]] = 1;
							}
						}
						sum = sum + coeffmatrix(parity(d, n), b, sind, c);
						csousuu++;
					}

				}
				if (combi[n - m - 1] == 1) {
					int v = 0;
					countb = 0;
					counta = 0;
					for (int ii = 0; v == 0; ii++) {
						if (combi[n - m - 1 - ii] != 1) {
							v = 1;
						} else {
							countb++;
							combi[n - m - 1 - ii] = 0;
						}
					}
					if (countb == li) {
						syuuryou = true;
					}
					// for(int ii=0;ii<combi.length;ii++) {
					// System.out.print(combi[ii]+ " ");
					// }
					// System.out.println();
					owa = false;
					for (int ii = 0; (!syuuryou && counta < li) || !owa; ii++) {
						// System.out.print(ii+" ");
						// System.out.print(l-countb+" ");
						// System.out.println(counta);
						if (combi[ii] == 1) {
							counta++;
						}
						if (li - countb == counta) {
							combi[ii] = 0;
							for (int j = ii + 1; j <= ii + 1 + countb; j++) {
								combi[j] = 1;
							}
							owa = true;
						}
					}
					counta = 0;
					i = -1;
					// for(int ii=0;ii<combi.length;ii++) {
					// System.out.print(combi[ii]+ " ");
					// }
					// System.out.println();
					combikazu++;
					if (p > 0) {

						int[] com = new int[m];
						if (p == 1) {
							for (int jj = 0; jj < com.length; jj++) {
								com = new int[m];
								com[jj] = 1;
								b = new int[n];
								for (int ii = 0; ii < combi.length; ii++) {
									if (combi[ii] == 1) {
										b[location[ii]] = 1;
									}
								}
								for (int ii = 0; ii < com.length; ii++) {
									if (com[ii] == 1) {
										b[locationp[ii]] = 1;
									}
								}
								sum = sum + coeffmatrix(parity(d, n), b, sind, c);
								csousuu++;
							}
						}
						if (p > 1) {
							com = new int[m];
							for (int ii = 0; ii < p; ii++) {
								com[ii] = 1;
							}
							b = new int[n];
							for (int ii = 0; ii < combi.length; ii++) {
								if (combi[ii] == 1) {
									b[location[ii]] = 1;
								}
							}
							for (int ii = 0; ii < com.length; ii++) {
								if (com[ii] == 1) {
									b[locationp[ii]] = 1;
								}
							}
							sum = sum + coeffmatrix(parity(d, n), b, sind, c);
							csousuu++;
							int countap = 0;
							int countbp = 0;
							boolean syuuryoup = false;
							boolean owap = false;
							for (int ip = 0; !syuuryoup; ip++) {// counta<l&&
								// System.out.print(i+" ");
								// System.out.println(counta);
								if (com[ip] == 1) {
									countap++;
								}
								if (countap == p) {
									com[ip] = 0;
									com[ip + 1] = 1;
									ip = -1;
									countap = 0;
									// for(int ii=0;ii<combi.length;ii++) {
									// System.out.print(combi[ii]+ " ");
									// }
									// System.out.println();
									b = new int[n];
									for (int ii = 0; ii < combi.length; ii++) {
										if (combi[ii] == 1) {
											b[location[ii]] = 1;
										}
									}
									for (int ii = 0; ii < com.length; ii++) {
										if (com[ii] == 1) {
											b[locationp[ii]] = 1;
										}
									}
									sum = sum + coeffmatrix(parity(d, n), b, sind, c);
									csousuu++;
								}
								if (com[m - 1] == 1) {
									int vp = 0;
									countbp = 0;
									countap = 0;
									for (int ii = 0; vp == 0; ii++) {
										if (com[m - 1 - ii] != 1) {
											vp = 1;
										} else {
											countbp++;
											com[m - 1 - ii] = 0;
										}
									}
									if (countbp == p) {
										syuuryoup = true;
									}
									// for(int ii=0;ii<combi.length;ii++) {
									// System.out.print(combi[ii]+ " ");
									// }
									// System.out.println();
									owap = false;
									for (int ii = 0; (!syuuryoup && countap < p) || !owap; ii++) {
										// System.out.print(ii+" ");
										// System.out.print(l-countb+" ");
										// System.out.println(counta);
										if (com[ii] == 1) {
											countap++;
										}
										if (p - countbp == countap) {
											com[ii] = 0;
											for (int j = ii + 1; j <= ii + 1 + countbp; j++) {
												com[j] = 1;
											}
											owap = true;
										}
									}
									countap = 0;
									ip = -1;
									// for(int ii=0;ii<combi.length;ii++) {
									// System.out.print(combi[ii]+ " ");
									// }
									// System.out.println();
									b = new int[n];
									for (int ii = 0; ii < combi.length; ii++) {
										if (combi[ii] == 1) {
											b[location[ii]] = 1;
										}
									}
									for (int ii = 0; ii < com.length; ii++) {
										if (com[ii] == 1) {
											b[locationp[ii]] = 1;
										}
									}
									sum = sum + coeffmatrix(parity(d, n), b, sind, c);
									csousuu++;
									countbp = 0;
									// counta=0;
									vp = 0;
									for (int ii = 0; vp == 0; ii++) {
										if (com[m - 1 - ii] != 1) {
											vp = 1;
										} else {
											countbp++;
											// combi[n-m-1-ii]=0;
										}
									}
									if (countbp == p) {
										syuuryoup = true;
									}
								}

							}
						}
					}

					if (p == 0) {
						b = new int[n];
						for (int ii = 0; ii < combi.length; ii++) {
							if (combi[ii] == 1) {
								b[location[ii]] = 1;
							}
						}
						sum = sum + coeffmatrix(parity(d, n), b, sind, c);
						csousuu++;
					}

					countb = 0;
					// counta=0;
					v = 0;
					for (int ii = 0; v == 0; ii++) {
						if (combi[n - m - 1 - ii] != 1) {
							v = 1;
						} else {
							countb++;
							// combi[n-m-1-ii]=0;
						}
					}
					if (countb == li) {
						syuuryou = true;
					}
				}

			}
		}
		System.out.println(csousuu);
		System.out.print("kaisuu: ");
		System.out.println(kaisuu);
		for (int i = 0; i < rankh.length; i++) {
			System.out.print(i + ": ");
			System.out.print(rankh[i] + " ");
		}
		System.out.println();
		System.out.print("tanni: ");
		System.out.println(tanni);
		for (int i = 0; i < bunnpu.length; i++) {
			System.out.print(i + ": ");
			System.out.print(bunnpu[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < simhist.length; i++) {
			System.out.print(i + ": ");
			System.out.print(simhist[i] + " ");
		}
		System.out.println();
		int bunnpugoukei = 0;
		int simhistgoukei = 0;
		for (int i = 0; i < bunnpu.length; i++) {
			bunnpugoukei = bunnpugoukei + bunnpu[i];
		}
		for (int i = 0; i < simhist.length; i++) {
			simhistgoukei = simhistgoukei + simhist[i];
		}
		System.out.print("simhistgoukei: ");
		System.out.println(simhistgoukei);
		System.out.print("bunnpugoukei: ");
		System.out.println(bunnpugoukei);
		System.out.print("p: ");
		System.out.println(diff);

		return sum;
	}

	int[] onePointTwoAllError(int[] c) {
		Random random = new Random();
		int[] error = new int[c.length];
		int[] result = new int[c.length];
		for (int i = 0; i < error.length; i++) {
			if (random.nextInt(2) == 1) {
				boolean end = false;
				while (!end) {
					error[i] = random.nextInt(256);
					if (error[i] != c[i])
						end = true;
				}
			}
			result[i] = GF.addSubstract(c[i], error[i]);
		}
		return result;
	}

	int[] allSymbolError(int[] c) {
		Random random = new Random();
		int[] error = new int[c.length];
		int[] result = new int[c.length];
		for (int i = 0; i < error.length; i++) {

			error[i] = random.nextInt(256);

			result[i] = GF.addSubstract(c[i], error[i]);
		}
		return result;
	}

	public static void main(String[] args) {// 44,28
		GF gf16 = new GF(0x011D, 256, 0);// 0x011D,256,0
		GF gf4 = new GF(19, 16, 1);
		int[] jyouhou = new int[15];
		int dt = 7;
		jyouhou[1] = gf4.getaTable(4);
		jyouhou[3] = gf4.getaTable(0);
		jyouhou[4] = gf4.getaTable(10);
		jyouhou[5] = gf4.getaTable(14);
		jyouhou[6] = gf4.getaTable(4);
		jyouhou[7] = gf4.getaTable(10);
		jyouhou[8] = gf4.getaTable(7);
		jyouhou[10] = gf4.getaTable(14);
		jyouhou[11] = gf4.getaTable(4);
		jyouhou[12] = gf4.getaTable(6);
		jyouhou[13] = gf4.getaTable(9);
		jyouhou[14] = gf4.getaTable(6);

		RS rs = new RS(gf16);
		RS rrs = new RS(gf4);
		int[] rresult = rrs.decode(jyouhou, dt);
		int[] sind = rrs.sindrome;
		int[] sigma = rrs.sigma;
		int[] mag = rrs.magnitude;
		List<int[]> elist = rrs.elist;
		System.out.println("答え");
		for (int i = 0; i < rresult.length; i++) {
			System.out.print(gf4.getlogTable(rresult[i]) + " ");
		}
		System.out.println("");
		System.out.println();
		for (int i = 0; i < sind.length; i++) {
			System.out.print(gf4.getlogTable(sind[i]) + " ");
		}
		System.out.println("");
		for (int i = 0; i < sigma.length; i++) {
			System.out.print(gf4.getlogTable(sigma[i]) + " ");
		}
		System.out.println("");
		for (int i = 0; i < mag.length; i++) {
			System.out.print(gf4.getlogTable(mag[i]) + " ");
		}
		System.out.println();
		for (int i = 0; i < rrs.location.length; i++) {
			System.out.print(rrs.location[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < rrs.omega.length; i++) {
			System.out.print(gf4.getlogTable(rrs.omega[i]) + " ");
		}
		System.out.println();
		// for(int i=0;i<elist.get(0).length;i++) {
		// System.out.print(gf4.getlogTable(elist.get(0)[i])+" ");
		// }
		// GFpoly poly;
		// List<int[]> clist = new ArrayList<>();
		// List<int[]> errorlist = new ArrayList<>();
		// int n=26;
		// int k=19;
		// int d=n-k+1;
		// int a[];
		// int c[];
		// int ε=1;
		// int t = (d-1)/2;
		//
		// int j = t+ε;
		// int co=0;
		// int fa=0;
		// //int ayamari=0;
		// int[] result = new int[n];
		// int[] error;
		// int teisei=0;
		// int kennsyutu = 0;
		// boolean de;
		// int kurikaesi = 0;
		// int goteisei = 0;
		// int[] y = new int[n];
		// c= new int[n];
		//
		// a = new int[k];
		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\エラー.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		// FileWriter kfw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\結果.txt", true); //※１
		// PrintWriter kpw = new PrintWriter(new BufferedWriter(kfw));
		// FileWriter gfw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\誤訂正.txt", true); //※１
		// PrintWriter gpw = new PrintWriter(new BufferedWriter(gfw));
		// FileWriter ifw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\目的.txt", true); //※１
		// PrintWriter ipw = new PrintWriter(new BufferedWriter(ifw));
		//
		// //内容を指定する
		//
		// int[] keiretu = new int[n];
		// int defayamari = 5;
		// // keiretu = rs.randomR(n);
		// //a = rs.randomR(k);
		// //c = rs.encode(a,d);
		// //c = keiretu;
		// kpw.println();
		// kpw.print("符号語: ");
		// //kpw.println("元系列");
		// gpw.println("符号語");
		// //gpw.println("元系列");
		// ipw.println("符号語");
		// //ipw.println("元系列");
		// for(int i=0;i<c.length;i++) {
		// kpw.print(gf16.getlogTable(c[i])+" ");
		// gpw.print(gf16.getlogTable(c[i])+" ");
		// ipw.print(gf16.getlogTable(c[i])+" ");
		// }
		// int jd=0;
		// //y = rs.random(c, 3);
		// //y = rs.randomTR(c, 4);
		// y[1] = gf16.getaTable(137);
		// y[7] = gf16.getaTable(209);
		// y[15] = gf16.getaTable(122);
		// y[21] = gf16.getaTable(169);
		// kpw.println();
		// gpw.println();
		// ipw.println();
		// kpw.print("固定した誤りベクトル: ");
		//
		//
		// gpw.println("固定した誤りベクトル");
		// ipw.println("固定した誤りベクトル");
		//// for(int i=0;i<rs.randome.length;i++) {
		//// kpw.print(gf16.getlogTable(rs.randome[i])+" ");
		//// gpw.print(gf16.getlogTable(rs.randome[i])+" ");
		//// ipw.print(gf16.getlogTable(rs.randome[i])+" ");
		//// }
		//
		// gpw.println();
		// ipw.println();
		// kpw.println();
		// for(int i=0;i<y.length;i++) {
		// if(y[i]!=c[i])
		// jd++;
		// }
		// int[] jl = new int[jd];
		// jd = 0;
		// for(int i=0;i<y.length;i++) {
		// if(y[i]!=c[i]) {
		// jl[jd] = i;
		// jd++;
		// }
		// }
		//
		// for(int ayamari=7;ayamari<10;ayamari++) {
		// kurikaesi = 0;
		// teisei=0;
		// goteisei=0;
		// kennsyutu=0;
		// gpw.println();
		// ipw.println();
		// gpw.println();
		// gpw.print("誤りの数:"+ayamari);
		// gpw.println();
		// while(kurikaesi<700000000) {
		// //y = rs.randomTR(c, ayamari);
		// //y = rs.random(keiretu, ayamari);
		// //y = rs.random(c, ayamari);
		// int[] yy = new int[n];
		// //yy = rs.jrandom(y,jl,ayamari);
		// yy = rs.jrandomTR(y,jl,ayamari);
		// result = rs.decode(yy,d);
		// poly = new GFpoly(gf16,result);
		// if(rs.correct) {
		// for(int i=0;i<d-1;i++) {
		// if(poly.substitute(gf16.getaTable(i))!=0)
		// pw.println("pe");
		// }
		// de = false;
		// for(int i=0;i<c.length;i++) {
		// if(c[i]!=result[i]) {
		// de = true;
		// }
		// }
		// if(de) {
		// for(int i=0;i<result.length;i++) {
		// gpw.print(gf16.getlogTable(result[i])+" ");
		// }
		// gpw.println();
		// gpw.println("誤りベクトル");
		//// for(int i=0;i<rs.randome.length;i++)
		//// gpw.print(gf16.getlogTable(rs.randome[i])+" ");
		//// gpw.println();
		// goteisei++;
		// GFpoly poy = new GFpoly(gf16,result);
		// GFpoly poc = new GFpoly(gf16,c);
		// boolean hantei = false;
		// hantei = poc.hanbitpoly(poy, t);
		// if(hantei) {
		//// ipw.println("誤りベクトル");
		//// for(int i=0;i<rs.randome.length;i++)
		//// ipw.print(gf16.getlogTable(rs.randome[i])+" ");
		//// ipw.println();
		// for(int i=0;i<result.length;i++) {
		// ipw.print(gf16.getlogTable(result[i])+" ");
		// }
		// ipw.println();
		// ipw.println();
		// }
		// }
		// else {
		// teisei++;
		// }
		// }
		// else {
		// kennsyutu++;
		// }
		// //if(kurikaesi%1000000==0) {
		// System.out.print("ayamari: "+ayamari);
		// System.out.print(" 試行回数: "+kurikaesi);
		// System.out.println();
		// //}
		// kurikaesi++;
		// }
		//
		// kpw.println();
		// kpw.print("誤りの個数:"+ ayamari);
		// kpw.println();
		// kpw.print("試行回数:"+ kurikaesi);
		// kpw.print(" 訂正:"+ teisei);
		// kpw.print(" 誤訂正:"+ goteisei);
		// kpw.print(" 誤り検出:"+ kennsyutu);
		// kpw.println();
		// kpw.println();
		// }
		//
		// pw.close();
		// kpw.close();
		// gpw.close();
		// ipw.close();
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }
		//
		// //拡張シンドローム復号法
		// /*
		// a[0]=2;
		// a[1]=1;
		//
		// c =rs.encode(a,d);
		// for(int i=0;i<c.length;i++) {
		// System.out.print(c[i]+", ");
		// }
		//
		// */
		//// c[10]=gf16.getaTable(221);
		//// c[5]=gf16.getaTable(105);
		//// c[11]=gf16.getaTable(15);
		//// c[12]=gf16.getaTable(201);
		//// poly = new GFpoly(gf16,c);
		//// for(int ii=0;ii<9;ii++)
		//// System.out.print(gf16.getlogTable(poly.substitute(gf16.getaTable(ii)))+"
		// ");
		//// System.out.println("");
		////
		//// System.out.println("受信語");
		//// for(int i=0;i<c.length;i++)
		//// System.out.print(gf16.getlogTable(c[i])+" ");
		//// System.out.println("");
		//// clist = rs.exdecode(c,d,ε);
		//// if(clist.size()>0) {
		//// co++;
		//// for(int i=0;i<clist.size();i++) {
		//// System.out.println("符号語"+i);
		//// c = clist.get(i);
		//// //en = rs.encode(a, 3);
		////
		//// for(int ii=0;ii<c.length;ii++)
		//// System.out.print(gf16.getlogTable(c[ii])+" ");
		//// System.out.println("");
		////
		//// poly = new GFpoly(gf16,c);
		////
		//// for(int ii=0;ii<d-1;ii++)
		//// System.out.print(poly.substitute(gf16.getaTable(ii))+" ");
		//// System.out.println("");
		//// }
		//// }
		//
		//// List<int[]> falist = new ArrayList<>();
		//// a = rs.randomR(k);
		//// c = rs.encode(a, d);
		////
		//// for(int w=0;w<1000;w++) {
		//// y = rs.randomTR(c,j);
		//// System.out.println("受信語");
		//// for(int i=0;i<c.length;i++)
		//// System.out.print(gf16.getlogTable(y[i])+" ");
		//// System.out.println("");
		////
		//// clist = rs.exdecode(y,d,ε);
		//// errorlist = rs.elist;
		//// //c = rs.decode(c,d);
		//// //System.out.println(clist.size());
		//// if(clist.size()>0) {
		//// co++;
		//// for(int i=0;i<clist.size();i++) {
		//// System.out.println("符号語"+i);
		//// result = clist.get(i);
		//// //en = rs.encode(a, 3);
		////
		//// for(int ii=0;ii<c.length;ii++)
		//// System.out.print(gf16.getlogTable(result[ii])+" ");
		//// System.out.println("");
		////
		//// poly = new GFpoly(gf16,result);
		////
		//// for(int ii=0;ii<d-1;ii++)
		//// System.out.print(poly.substitute(gf16.getaTable(ii))+" ");
		//// System.out.println("");
		//// }
		//// if(clist.size()>1) {
		//// try {
		//// //出力先を作成する
		//// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\clist.txt", true); //※１
		//// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		////
		//// //内容を指定する
		//// pw.print("元の符号語: ");
		//// for(int ii=0;ii<c.length;ii++)
		//// pw.print(gf16.getlogTable(c[ii])+" ");
		//// pw.println("");
		//// pw.print("受信語: ");
		//// for(int ii=0;ii<c.length;ii++)
		//// pw.print(gf16.getlogTable(y[ii])+" ");
		//// pw.println("");
		//// for(int i=0;i<clist.size();i++) {
		//// pw.println("符号語"+i);
		//// result = clist.get(i);
		//// error = errorlist.get(i);
		//// //en = rs.encode(a, 3);
		////
		//// for(int ii=0;ii<c.length;ii++)
		//// pw.print(gf16.getlogTable(result[ii])+" ");
		//// pw.println("");
		////
		//// poly = new GFpoly(gf16,result);
		////
		//// for(int ii=0;ii<d-1;ii++)
		//// pw.print(poly.substitute(gf16.getaTable(ii))+" ");
		//// pw.println("");
		//// pw.print("誤りベクトル: ");
		//// for(int ii=0;ii<error.length;ii++)
		//// pw.print(gf16.getlogTable(error[ii])+" ");
		//// pw.println("");
		//// }
		//// //ファイルに書き出す
		//// pw.close();
		////
		//// //終了メッセージを画面に出力する
		//// System.out.println("出力が完了しました。");
		////
		//// } catch (IOException ex) {
		//// //例外時処理
		//// ex.printStackTrace();
		//// }
		//// }
		//// }
		//// else {
		//// fa++;
		//// falist.add(c);
		//// }
		//// }
		//// System.out.println("誤り: "+fa);
		//// if(falist.size()>0) {
		//// for(int i=0;i<falist.size();i++) {
		//// c = falist.get(i);
		//// System.out.println("誤り語");
		//// for(int ii=0;ii<c.length;ii++)
		//// System.out.print(gf16.getlogTable(c[ii])+" ");
		//// System.out.println("");
		//// }
		//// }
		//// System.out.println("正:" +co);
		// //拡張シンドローム復号法終
		//
		// //System.out.print(gf4.getlogTable(poly.substitute(gf4.getaTable(6)))+" ");
		// /*
		// int[] a = {2,2};
		// int[] b = {24, 163, 201, 174, 122, 160, 152, 141, 149, 98, 27, 220, 75, 157,
		// 45, 95, 227, 26, 42, 72, 78, 120, 216, 41, 112, 15, 104, 209};
		// GFpoly poly;
		// int code[];
		// int[] en;
		// int[] p;
		// int count=0;
		// poly = new GFpoly(gf16,rs.encode(b,3));
		// //for(int i=0;i<poly.coefficients.length;i++)
		// //System.out.print(poly.coefficients[i] + ", ");
		//
		//
		// /*
		// poly = rs.generatedPolynominal(16);
		// System.out.println("生成多項式");
		// for(int i=0;i<poly.coefficients.length;i++)
		// System.out.print(poly.coefficients[i]+" ");
		// System.out.println("");
		// /*
		// System.out.println("符号語Ca");
		// en = rs.encode(a, 3);
		// for(int i=0;i<en.length;i++)
		// System.out.print(en[i]+" ");
		// System.out.println("");
		//
		// System.out.println("符号語Cb");
		// en = rs.encode(b, 3);
		// for(int i=0;i<en.length;i++)
		// System.out.print(en[i]+" ");
		// System.out.println("");
		//
		// //System.out.println("受信語");
		// a = new int[]{8,0,0,0,0,10,9,0,11,0,0,3,5,9};
		// GFpoly test;
		// test = new GFpoly(gf,a);
		// code = rs.decode(a, 3);
		// //System.out.println(test.substitute(4));
		// for(int i=0;i<code.coefficients.length;i++)
		// System.out.print(code.coefficients[i]+" ");
		// System.out.println("");
		//
		// */
		//
		// /*
		// int rand[];
		// int R[];
		// int tennsu;
		// int distanse;
		// int ayamari;
		// int correction=0;
		// int miscorrection=0;
		// int detection=0;
		// int fgo=888;
		// int fgolength;
		// int[] z;
		// int falsemiscorrect;
		// GFpoly po;
		//
		// tennsu = 19;
		// distanse = 8;// t = (d-1)/2
		// int teisei = distanse;
		// ayamari = 0;
		// fgolength = tennsu + distanse-1;
		//
		//
		// p = new int[tennsu];
		// /*
		// rand = rs.randomR(tennsu);
		//
		// for(int i=0;i<tennsu;i++) {
		// p[i] = rand[i];
		// }
		// en = rs.encode(p,teisei);
		// */
		// //p = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		// //z = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		// 0, 0, 206, 156, 0, 0, 0, 0, 0, 210, 0, 0, 248, 0};
		// /*for(int i=0;i<p.length;i++)
		// p[i]=0;
		//
		// en = rs.encode(p,distanse);
		// //poly = new GFpoly(gf16,en);
		// System.out.println("符号語");
		// for(int i=0;i<en.length;i++)
		// System.out.print(en[i] + " " );
		// System.out.println("");
		//
		//
		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\result.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指定する
		// pw.print("("+fgolength+","+tennsu+")"+"RS符号 ");
		// pw.print("符号語： ");
		// for(int i=0;i<en.length;i++)
		// pw.print(en[i] + " " );
		// pw.println("");
		//
		// /* for(int i=0;i<2*teisei;i++) {
		// fgo = poly.substitute(rs.gf.getaTable(i+rs.gf.getL()));
		// int q;
		// q = i+rs.gf.getL();
		// pw.print("a^"+q+": "+ fgo+" ");
		// pw.println("");
		// }
		// */
		// //ファイルに書き出す
		// // pw.close();
		//
		// //終了メッセージを画面に出力する
		/// * System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }
		// int[] test;
		// test = new int[distanse-1+tennsu];
		// int[] test2;
		// test2 = new int[rs.gf.getSize()];
		//
		// //for(int v=14;v<16;v++) {
		// //ayamariの個数
		//
		// //ayamari = 1+v;
		//
		//
		// count=0;
		// correction=0;
		// miscorrection=0;
		// detection=0;
		// falsemiscorrect = 0;
		//
		// while(miscorrection==0) {
		// Random random = new Random();
		// ayamari = random.nextInt(79) + 21 ;
		// int[] miscorrectsind;
		// int[] erormag;
		// int[] erorlocation;
		// int kai=0;
		// int hanni=0;
		//
		// miscorrectsind = new int[distanse-1];
		//
		// test = new int[distanse-1+tennsu];
		// test2 = new int[rs.gf.getSize()];
		// for(int ff=0;ff<test2.length;ff++)
		// test2[ff]=0;
		// //for(int w=0;w<1000000000;w++) { //繰り返す回数
		//
		//
		// R = rs.randomTR(en, ayamari);
		// int u=0;
		// erormag = new int[ayamari];
		// erorlocation = new int[ayamari];
		// for(int i=0;i<R.length;i++) {
		// if(R[i]!=0) {
		// erormag[u] = R[i];
		// erorlocation[u] = i;
		// u = u+1;
		// }
		// }
		//
		// falsemiscorrect = 0;
		// for(int x=0;x<en.length;x++)
		// if(R[x]!=en[x]) {
		// test2[R[x]] = test2[R[x]] + 1;
		// test[x] = test[x] + 1;
		// }
		// code = rs.decode(R, distanse);
		//
		// count = 1+count;
		//
		// /*
		// System.out.println("受信語");
		// for(int i=0;i<R.length;i++)
		// System.out.print(R[i] + " " );
		// System.out.println("");
		// */
		// /* int t=0;
		// for(int i=0;i<en.length;i++)
		// if(en[i]!=code[i]) t = t + 1;
		// System.out.print("t: " + t +" ");
		// //System.out.println("");
		//
		// //for(int i=0;i<code.coefficients.length;i++)
		// //System.out.print(code.coefficients[i] + " " );
		// //System.out.println("");
		// System.out.print("count: "+ count+ " ");
		// //System.out.print("correct: "+ rs.correct);
		// //System.out.println("");
		// /*try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\t=9.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指定する
		// */
		// /*
		// for(int i=0;i<en.length;i++)
		// if(en[i]!=code[i]) t = t + 1;
		// */
		//
		//
		// // if(t==0&&rs.correct) {
		// /*
		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\correct.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指
		// pw.print("符号語： ");
		// for(int i=0;i<en.length;i++)
		// pw.print(en[i] + ", " );
		// pw.println("");
		//
		// pw.print("復号前： ");
		// for(int i=0;i<R.length;i++)
		// pw.print(R[i] + ", " );
		// pw.println("");
		//
		// pw.print("復号後： ");
		// for(int i=0;i<code.length;i++)
		// pw.print(code[i] + ", " );
		// pw.println("");
		//
		// pw.println("");
		// pw.print("σ： ");
		// for(int i=0;i<rs.sigma.length;i++)
		// pw.print(rs.sigma[i] + ", " );
		// pw.println("");
		// pw.print("Erorlocation： ");
		// for(int i=0;i<rs.location.length;i++)
		// pw.print(rs.location[i] + ", " );
		// pw.println("");
		// pw.print("Z： ");
		// for(int i=0;i<rs.Z.length;i++)
		// pw.print(rs.Z[i] + ", " );
		// pw.println("");
		// /*
		// po = new GFpoly(gf16,code);
		//
		//
		// pw.print("復号後のシンドローム: ");
		// for(int i=0;i<distanse-1;i++) {
		// pw.print("a^"+i+": " +po.substitute(po.gf.getaTable(i+po.gf.getL())) + " ");
		// miscorrectsind[i] = po.substitute(po.gf.getaTable(i+po.gf.getL()));
		// }
		// pw.println("");
		//
		//
		// for(int i=0;i<miscorrectsind.length;i++)
		// if(miscorrectsind[i]!=0)falsemiscorrect = 1;
		//
		//
		//
		// pw.println();
		//
		// pw.println("");
		// pw.println("");
		//
		//
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }
		// */
		// // correction = correction+1;
		// /*if(rs.sigma.length-1>t) {
		// correction = correction-1;
		// detection = detection + 1;
		// }*/
		// /* }
		// if(t!=0&&rs.correct) {
		// miscorrection = miscorrection+1;
		//
		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\miscorrect.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指
		// pw.print("試行回数: "+ count + " ");
		// pw.print("符号語： ");
		// for(int i=0;i<en.length;i++)
		// pw.print(en[i] + ", " );
		// pw.println("");
		// pw.print("誤り個数: "+ ayamari+ " ");
		// pw.println("");
		// pw.print("復号前： ");
		// for(int i=0;i<R.length;i++)
		// pw.print(R[i] + ", " );
		// pw.println("");
		//
		// pw.print("復号後： ");
		// for(int i=0;i<code.length;i++)
		// pw.print(code[i] + ", " );
		// pw.println("");
		//
		// po = new GFpoly(gf16,code);
		//
		//
		// pw.print("復号後のシンドローム: ");
		// for(int i=0;i<distanse-1;i++) {
		// int x=i+po.gf.getL();
		// pw.print("a^"+x+": " +po.substitute(po.gf.getaTable(i+po.gf.getL())) + " ");
		// miscorrectsind[i] = po.substitute(po.gf.getaTable(i+po.gf.getL()));
		// }
		// pw.println("");
		//
		// for(int i=0;i<miscorrectsind.length;i++)
		// if(miscorrectsind[i]!=0)falsemiscorrect = 1;
		//
		//
		//
		// pw.println();
		//
		// pw.println("");
		// pw.println("");
		//
		//
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }
		//
		// }
		//
		// if(!rs.correct) {
		// detection = detection + 1;
		// if(rs.hannigai==1) hanni=hanni+1;
		// if(rs.kainasi==1) kai = kai+1;
		// }
		// if(falsemiscorrect==1) {
		// try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\falsemiscorrect.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指定する
		// pw.print("符号語： ");
		// for(int i=0;i<en.length;i++)
		// pw.print(en[i] + ", " );
		// pw.println("");
		//
		// pw.print("復号前： ");
		// for(int i=0;i<R.length;i++)
		// pw.print(R[i] + ", " );
		// pw.println("");
		// pw.print("復号後： ");
		// for(int i=0;i<code.length;i++)
		// pw.print(code[i] + ", " );
		// pw.println("");
		//
		// po = new GFpoly(gf16,code);
		// detection = detection + 1;
		// miscorrection = miscorrection - 1;
		//
		// pw.print("復号後のシンドローム: ");
		// for(int i=0;i<distanse-1;i++) {
		// pw.print("a^"+(i+po.gf.getL())+": "
		// +po.substitute(po.gf.getaTable(i+po.gf.getL())) + " ");
		// miscorrectsind[i] = po.substitute(po.gf.getaTable(i+po.gf.getL()));
		// }
		// pw.println("");
		// pw.print("σ： ");
		// for(int i=0;i<rs.sigma.length;i++)
		// pw.print(rs.sigma[i] + ", " );
		// pw.println("");
		// pw.print("Erorlocation： ");
		// for(int i=0;i<rs.location.length;i++)
		// pw.print(rs.location[i] + ", " );
		// pw.println("");
		// pw.print("Z： ");
		// for(int i=0;i<rs.Z.length;i++)
		// pw.print(rs.Z[i] + ", " );
		// pw.println("");
		//
		// pw.println("");
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }
		// }
		//
		//
		// /* pw.print("t: " + t +" ");
		// pw.println("count: "+ count);
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }
		// */
		//
		//
		// //}
		// /*
		// System.out.print("c: "+ correction+ " ");
		// System.out.print("misc: "+ miscorrection + " ");
		// System.out.print("det: "+ detection + " ");
		// */
		// /*try {
		// //出力先を作成する
		// FileWriter fw = new FileWriter("C:\\Users\\野村
		// 知也\\Downloads\\大学\\RSProgramEx\\result.txt", true); //※１
		// PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		//
		// //内容を指定する
		// /*
		// pw.print("誤り個数: "+ ayamari+ " ");
		// pw.print("訂正: "+ correction+ " ");
		// pw.print("誤訂正: "+ miscorrection + " ");
		// pw.print("誤り検出: "+ detection + " ");
		// pw.print("符号長より大きい: "+ hanni + " ");
		// pw.print("解けない: "+ kai + " ");
		// /*
		// for(int m=0;m<test.length;m++)
		// pw.print("test"+"["+ m +"]: "+ test[m] +" ");
		// pw.println("");
		// for(int g=0;g<test2.length;g++)
		// pw.print("test2"+"["+ g +"]: "+ test2[g] +" ");
		// */
		// /*
		// pw.println("");
		//
		// //ファイルに書き出す
		// pw.close();
		//
		// //終了メッセージを画面に出力する
		// System.out.println("出力が完了しました。");
		//
		// } catch (IOException ex) {
		// //例外時処理
		// ex.printStackTrace();
		// }*/
		//
		// //}
		//
		// }
	}
}
