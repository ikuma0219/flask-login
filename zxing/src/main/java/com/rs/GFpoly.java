package com.rs;

public class GFpoly {
	int[] coefficients;
	GF gf;

	public GFpoly(GF gf, int[] coefficients) {
		this.gf = gf;
		int length;
		int firstNotCoeff = -1;

		for (int i = 0; i < coefficients.length; i++) {
			if (coefficients[i] != 0) {
				firstNotCoeff = i;
				length = coefficients.length - i;
				this.coefficients = new int[length];
				System.arraycopy(coefficients, firstNotCoeff, this.coefficients, 0, length);
				i = coefficients.length;

			}
		}

		if (firstNotCoeff == -1) {
			this.coefficients = new int[] { 0 };
		}

	}

	GFpoly buildMonoPoly(int degree, int coefficient) {
		int monoCoeff[];
		monoCoeff = new int[degree + 1];
		monoCoeff[0] = coefficient;
		for (int i = 1; i <= degree; i++)
			monoCoeff[i] = 0;

		GFpoly poly = new GFpoly(this.gf, monoCoeff);

		return poly;

	}

	boolean hanbitpoly(GFpoly poly, int t) {//
		int[] longCoeff = new int[] { 0 };
		int[] shortCoeff = new int[] { 0 };
		int[] result = new int[] { 0 };
		int diff = 0;
		boolean hantei = true;
		int count = 0;

		if (this.coefficients.length != poly.coefficients.length) {

			if (this.coefficients.length > poly.coefficients.length) {
				diff = this.coefficients.length - poly.coefficients.length;
				for (int i = 0; i < diff; i++) {
					if (this.coefficients[i] != 0)
						count++;
				}
				longCoeff = this.coefficients;
				shortCoeff = poly.coefficients;
				result = new int[this.coefficients.length];
			}
			if (this.coefficients.length < poly.coefficients.length) {
				diff = -this.coefficients.length + poly.coefficients.length;
				longCoeff = poly.coefficients;
				shortCoeff = this.coefficients;
				result = new int[poly.coefficients.length];
			}
			System.arraycopy(longCoeff, 0, result, 0, diff);
			for (int i = diff; i < result.length; i++) {
				if (!GF.hanbit(shortCoeff[i - diff], longCoeff[i])
						&& this.coefficients.length < poly.coefficients.length) {
					count++;
				}
				if (!GF.hanbit(longCoeff[i], shortCoeff[i - diff])
						&& this.coefficients.length > poly.coefficients.length) {
					count++;
				}
			}
		}
		if (this.coefficients.length == poly.coefficients.length) {
			result = new int[this.coefficients.length];
			for (int i = 0; i < result.length; i++) {
				if (!GF.hanbit(this.coefficients[i], poly.coefficients[i])) {
					count++;
				}
			}
		}
		if (count > t)
			hantei = false;
		System.out.println(count);
		return hantei;

	}

	GFpoly addSubstract(GFpoly poly) {
		if (!this.gf.equals(poly.gf))
			throw new IllegalArgumentException("二つのガロア体が異なります");

		int[] longCoeff = new int[] { 0 };
		int[] shortCoeff = new int[] { 0 };
		int[] result = new int[] { 0 };
		int diff = 0;

		if (this.coefficients.length != poly.coefficients.length) {

			if (this.coefficients.length > poly.coefficients.length) {
				diff = this.coefficients.length - poly.coefficients.length;
				longCoeff = this.coefficients;
				shortCoeff = poly.coefficients;
				result = new int[this.coefficients.length];
			}
			if (this.coefficients.length < poly.coefficients.length) {
				diff = -this.coefficients.length + poly.coefficients.length;
				longCoeff = poly.coefficients;
				shortCoeff = this.coefficients;
				result = new int[poly.coefficients.length];
			}
			System.arraycopy(longCoeff, 0, result, 0, diff);
			for (int i = diff; i < result.length; i++) {
				result[i] = GF.addSubstract(longCoeff[i], shortCoeff[i - diff]);
			}

		}
		if (this.coefficients.length == poly.coefficients.length) {
			result = new int[this.coefficients.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = GF.addSubstract(this.coefficients[i], poly.coefficients[i]);
			}
		}
		GFpoly resultPoly = new GFpoly(gf, result);
		return resultPoly;

	}

	GFpoly multiply(GFpoly poly) {
		GFpoly resultPoly;
		int length;
		int[] resultCoeff;
		if (!this.gf.equals(poly.gf))
			throw new IllegalArgumentException("二つのガロア体が異なります");
		else {
			if (poly.coefficients[0] == 0 || this.coefficients[0] == 0) {
				resultPoly = new GFpoly(this.gf, new int[] { 0 });
				return resultPoly;
			}

			else {
				length = this.coefficients.length + poly.coefficients.length - 1;
				resultCoeff = new int[length];
				resultCoeff[0] = 0;
				for (int i = 0; i < this.coefficients.length; i++) {
					for (int j = 0; j < poly.coefficients.length; j++) {
						resultCoeff[i + j] = GF.addSubstract(
								this.gf.multiply(this.coefficients[i], poly.coefficients[j]), resultCoeff[i + j]);
					}
				}
				resultPoly = new GFpoly(this.gf, resultCoeff);
				return resultPoly;
			}
		}

	}

	GFpoly monoMultiply(int degree, int coefficient) {
		GFpoly resultPoly;
		int length;
		int[] resultCoeff;
		if (coefficient == 0) {
			resultPoly = new GFpoly(this.gf, new int[] { 0 });
			return resultPoly;
		} else {
			length = degree + this.coefficients.length;
			resultCoeff = new int[length];
			for (int i = 0; i < this.coefficients.length; i++) {
				resultCoeff[i] = this.gf.multiply(this.coefficients[i], coefficient);
			}
			for (int i = this.coefficients.length; i < resultCoeff.length; i++) {
				resultCoeff[i] = 0;
			}
			resultPoly = new GFpoly(this.gf, resultCoeff);
			return resultPoly;

		}
	}

	GFpoly differential() {
		int[] resultCoeff;
		resultCoeff = new int[this.coefficients.length - 1];
		if (this.coefficients.length == 1)
			return gf.getZero();
		else {
			if ((this.coefficients.length - 1) % 2 == 0) {
				for (int t = 0; 2 * t < this.coefficients.length; t++)
					this.coefficients[2 * t] = 0;
				for (int i = 0; i < this.coefficients.length - 1; i++)
					resultCoeff[i] = this.coefficients[i];

			}
			if ((this.coefficients.length - 1) % 2 == 1) {
				for (int t = 0; 2 * t + 1 < this.coefficients.length; t++)
					this.coefficients[2 * t + 1] = 0;
				for (int i = 0; i < this.coefficients.length - 1; i++)
					resultCoeff[i] = this.coefficients[i];

			}
			return new GFpoly(gf, resultCoeff);
		}

	}

	// poly % this
	GFpoly[] divide(GFpoly poly) {
		GFpoly remainder;
		GFpoly quotient;
		quotient = gf.getZero();
		int diff = 0;
		int reciprocal;
		int resp;
		remainder = poly;

		if (!this.gf.equals(poly.gf))
			throw new IllegalArgumentException("二つのガロア体が異なります");
		else {
			if (this.coefficients[0] == 0)
				throw new IllegalArgumentException("0では割れません");
			if (poly.coefficients[0] == 0)
				return new GFpoly[] { quotient, remainder };
			else {
				reciprocal = this.gf.reciprocal(this.coefficients[0]);
				resp = gf.multiply(reciprocal, poly.coefficients[0]);

				if (poly.coefficients.length >= this.coefficients.length) {
					diff = poly.coefficients.length - this.coefficients.length;
					while (remainder.coefficients.length >= this.coefficients.length) {

						remainder = remainder.addSubstract(this.monoMultiply(diff, resp));
						quotient = quotient.addSubstract(this.buildMonoPoly(diff, resp));
						resp = gf.multiply(reciprocal, remainder.coefficients[0]);
						diff = remainder.coefficients.length - this.coefficients.length;

					}

				}
				return new GFpoly[] { quotient, remainder };
			}
		}
	}

	public int substitute(int a) {
		int result;
		if (a == 0)
			return this.coefficients[this.coefficients.length - 1];
		if (a == 1) {
			result = this.coefficients[0];
			for (int i = 1; i < this.coefficients.length; i++)
				result = GF.addSubstract(result, this.coefficients[i]);
			return result;
		}

		result = this.coefficients[0];

		for (int i = 1; i < this.coefficients.length; i++) {
			result = GF.addSubstract(gf.multiply(a, result), coefficients[i]);
			// System.out.print(result+ " ");
		}

		return result;

	}// int result = coefficients[0];
		// int size = coefficients.length;
		// for (int i = 1; i < size; i++) {
		// result = GenericGF.addOrSubtract(field.multiply(a, result), coefficients[i]);
		// }
		// return result;

	public static void main(String[] args) {
		GF gf = new GF(0x011D, 256, 0);
		int[] coefficients;
		int[] coeff;
		int sum = 0;
		int[] p;
		int[] sind;
		GF gf4 = new GF(19, 16, 0);
		int[] sg = new int[4];
		int n = 26;
		int[] a = new int[n];
		int[] b = new int[n];
		int t = 3;

		a[0] = gf.getaTable(17);
		a[1] = gf.getaTable(119);
		a[2] = gf.getaTable(77);
		a[3] = gf.getaTable(249);
		a[4] = gf.getaTable(114);
		a[5] = gf.getaTable(29);
		a[6] = gf.getaTable(66);
		a[7] = gf.getaTable(34);
		a[8] = gf.getaTable(136);
		a[9] = gf.getaTable(70);
		a[10] = gf.getaTable(22);
		a[11] = gf.getaTable(229);
		a[12] = gf.getaTable(95);
		a[13] = gf.getaTable(209);
		a[14] = gf.getaTable(152);
		a[15] = gf.getaTable(93);
		a[16] = gf.getaTable(108);
		a[17] = gf.getaTable(76);
		a[18] = gf.getaTable(21);
		a[19] = gf.getaTable(23);
		a[20] = gf.getaTable(228);
		a[21] = gf.getaTable(46);
		a[22] = gf.getaTable(215);
		a[23] = gf.getaTable(188);
		a[24] = gf.getaTable(82);
		a[25] = gf.getaTable(189);
		GFpoly polya = new GFpoly(gf, a);

		b[0] = gf.getaTable(17);
		b[1] = gf.getaTable(28);
		b[2] = gf.getaTable(77);
		b[3] = gf.getaTable(249);
		b[4] = gf.getaTable(207);
		b[5] = gf.getaTable(29);
		b[6] = gf.getaTable(66);
		b[7] = gf.getaTable(34);
		b[8] = gf.getaTable(104);
		b[9] = gf.getaTable(70);
		b[10] = gf.getaTable(201);
		b[11] = gf.getaTable(229);
		b[12] = gf.getaTable(22);
		b[13] = gf.getaTable(209);
		b[14] = gf.getaTable(152);
		b[15] = gf.getaTable(93);
		b[16] = gf.getaTable(67);
		b[17] = gf.getaTable(76);
		b[18] = gf.getaTable(21);
		b[19] = gf.getaTable(23);
		b[20] = gf.getaTable(228);
		b[21] = gf.getaTable(46);
		b[22] = gf.getaTable(215);
		b[23] = gf.getaTable(188);
		b[24] = gf.getaTable(207);
		b[25] = gf.getaTable(83);
		GFpoly polyb = new GFpoly(gf, b);

		boolean hantei;

		hantei = polya.hanbitpoly(polyb, t);

		a = new int[n];
		b = new int[n];
		a[0] = gf.getaTable(54);
		b[0] = gf.getaTable(176);
		a[1] = gf.getaTable(27);
		b[1] = gf.getaTable(76);
		// a[2] = gf.getaTable(78);
		// b[2] = gf.getaTable(98);
		// a[3] = gf.getaTable(101);
		// b[3] = gf.getaTable(67);
		// a[1] = 1;
		// b[1] = 0;
		// a[2] = 2;
		// a[3] = 1;
		// b[3] = 3;
		polya = new GFpoly(gf, a);
		polyb = new GFpoly(gf, b);
		System.out.print(polya.hanbitpoly(polyb, t));

		// sg[0] = gf4.getaTable(7);
		// sg[1] = gf4.getaTable(7);
		// sg[3] = gf4.getaTable(2);
		// GFpoly poly = new GFpoly(gf4,sg);
		// System.out.print(" "+ poly.substitute(poly.gf.getaTable(5)));

		/*
		 * coefficients = new int[] {176,140,206,246};
		 * GFpoly poly = new GFpoly(gf,coefficients);
		 * for(int i=0;i<=gf.getSize()-1;i++) {
		 * System.out.print(" "+ poly.substitute(poly.gf.getaTable(i)));
		 * System.out.println("");
		 * }
		 * 
		 * 
		 * 
		 * sind = new int[6];
		 * //teisei 3 k 28
		 * coefficients = new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		 * 0, 0, 96, 27, 248, 12, 50, 80, 82};
		 * //174, 80, 38, 210, 122, 245, 63, 80, 164, 41, 183, 165, 238, 26, 13, 243,
		 * 118, 227, 130, 249, 245, 217, 241, 88, 187, 190, 101, 213, 105, 107, 107,
		 * 186, 156, 110 };
		 * p = new int[coefficients.length];
		 * coeff = new int[] {1,0,1};
		 * // GFpoly poly = new GFpoly(gf,coefficients);
		 * /*
		 * GFpoly po = new GFpoly(gf,coeff);
		 * GFpoly result;
		 * int length = poly.coefficients.length;
		 * //for(int i=0;i<poly.coefficients.length;i++)
		 * //sum = sum ^ poly.coefficients[i];
		 * for(int h=0;h<6;h++) {
		 * sum=0;
		 * for(int i=0;i<poly.coefficients.length;i++)
		 * p[i] = poly.coefficients[i];
		 * for(int i=0;i<p.length-1;i++) {
		 * 
		 * for(int d=0;d<p.length-1-i;d++) {
		 * System.out.print(p[0]+" ");
		 * p[i] = gf.multiply(p[i],gf.getaTable(h));
		 * 
		 * 
		 * }
		 * 
		 * }
		 * for(int i=0;i<p.length;i++)
		 * sum = sum^p[i];
		 * 
		 * sind[h] = sum;
		 * 
		 * }
		 * for(int i=0;i<sind.length;i++) {
		 * System.out.print("s["+ i + "]: "+ sind[i]);
		 * System.out.println("");
		 * }
		 */
		/*
		 * for(int i=0;i<coefficients.length;i++)
		 * System.out.print("α^"+ gf.getlogTable(coefficients[i]) + ", ");
		 * for(int i=1;i<=sind.length+1;i++) {
		 * System.out.print("s["+ i + "]: "+ poly.substitute(poly.gf.getaTable(i)));
		 * System.out.println("");
		 * }
		 * for(int h=0;h<6;h++)
		 * System.out.println(poly.gf.getaTable(h));
		 * //187 50 200 30 198 128
		 * //for(int i=0;i<6;i++) System.out.println(poly.substitute(gf.getaTable(i)));
		 * String str;
		 * Integer st=null;
		 * str = String.valueOf(st);
		 */
		// l=5 161 9 238 156 211 208 98
		// int a[];
		// int b[];
		// a = new int[7];
		// b = new int[1];
		// b[0] = 1;
		// a[0] = gf.getaTable(161);
		// a[1] = gf.getaTable(9);
		// a[2] = gf.getaTable(238);
		// a[3] = gf.getaTable(156);
		// a[4] = gf.getaTable(211);
		// a[5] = gf.getaTable(208);
		// a[6] = gf.getaTable(98);
		// GFpoly lipoly = new GFpoly(gf,a);
		// GFpoly su = new GFpoly(gf,b);
		// for(int i=0;i<=5;i++)
		// su = su.multiply(lipoly);
		//
		// for(int i=0;i<su.coefficients.length;i++) {
		// System.out.print(gf.getlogTable(su.coefficients[i])+ " ");
		// }
		//
		//

		// System.out.print(str);
	}

}
