package com.rs;

public final class GF {
	private final int[] aTable;
	private final int[] logTable;
	private final int prim, size, l;
	int[] coefficients = new int[] { 0 };
	GFpoly zero;
	GFpoly one;

	public GF(int prim, int size, int l) {
		this.prim = prim;
		this.size = size;
		this.l = l;
		aTable = new int[size];
		logTable = new int[size];
		int x;
		x = 1;
		for (int i = 0; i < size; i++) {
			aTable[i] = x;
			logTable[aTable[i]] = i;
			x = 2 * x;

			if (x >= size)
				x = x ^ prim;

		}
		zero = new GFpoly(this, new int[] { 0 });
		one = new GFpoly(this, new int[] { 1 });

	}

	public int[] tobit(int[] a) {
		int[] bit = new int[8 * a.length];
		int bitrow = 0;
		for (int ii = 0; ii < a.length; ii++) {
			int suma = a[ii];
			for (int i = 0; i < 8; i++) {
				if (suma >= pow(2, 7 - i)) {
					bit[bitrow] = 1;
					suma = suma - pow(2, 7 - i);
				}
				bitrow++;
			}
		}
		return bit;
	}

	int[] shiroloca(int a) {
		int[] kuro = vectol(a);
		int[] shiro = new int[kuro.length];
		for (int i = 0; i < shiro.length; i++) {
			if (kuro[i] == 0)
				shiro[i] = 1;
		}
		return shiro;
	}

	// 16777216, 0, 0, 0, 3715, -588029755, 0,
	public int[] tobits(int[] a) {
		int[] bits = new int[(a.length * 8 + 31) / 32];
		int[] bit = tobit(a);
		int sum = 0;
		int jyou = 31;
		int bitsrow = 0;
		// System.out.println(bit.length);
		// for(int i=0;i<bit.length;i++) {
		// System.out.print(bit[i]+", ");
		// }
		// System.out.println();
		for (int i = 0; i < bit.length; i++) {
			int leftbit = bit.length - i;
			if (jyou == 31 && leftbit < 32) {
				jyou = leftbit - 1;
				// System.out.println(jyou);
				// System.out.println(i);
			}
			if (bit[i] == 1) {
				sum = sum + pow(2, jyou);
			}
			jyou--;
			if (jyou < 0) {
				jyou = 31;
				bits[bitsrow] = sum;
				sum = 0;
				bitsrow++;
			}

		}
		return bits;
	}

	public static int[] bitstobit(int[] bits) {
		int count = 0;
		int[] newbits = new int[bits.length];
		for (int i = 0; i < newbits.length; i++) {
			newbits[i] = bits[i];
		}
		int[] bit = new int[208];
		int[] lastbit = new int[16];

		count = 0;
		for (int i = 0; i < bits.length - 1; i++) {
			// System.out.print(bits[i]+", ");
			for (int j = 31; j >= 0; j--) {
				long hikaku = longpow(2, j);
				if (newbits[i] <= 0) {
					if (newbits[i] >= pow(2, j) && newbits[i] != 0) {
						// if(i==1&&j==31) {
						// System.out.print(hikaku);
						// System.out.print(0>=longpow(2,j));
						// }
						// System.out.print(newbits[i]+" ");
						// System.out.println(pow(2,j));
						newbits[i] = newbits[i] - pow(2, j);
						bit[count] = 1;
					}

				}
				if (newbits[i] > 0) {
					if (newbits[i] >= longpow(2, j) && newbits[i] != 0) {
						// if(i==1&&j==31) {
						// System.out.print(hikaku);
						// System.out.print(0>=longpow(2,j));
						// }
						newbits[i] = newbits[i] - pow(2, j);
						bit[count] = 1;
					}

				}
				// System.out.print(bit[count]+", ");
				// if(bit[count]==1) {
				// System.out.println(j);
				// }
				count++;

			}

			// System.out.println();
		}
		// System.out.println();
		count = 192;
		for (int j = 15; j >= 0; j--) {
			if (newbits[bits.length - 1] <= 0)
				if (newbits[bits.length - 1] >= pow(2, j)) {
					// if(i==1&&j==31) {
					// System.out.print(hikaku);
					// System.out.print(0>=longpow(2,j));
					// }
					newbits[bits.length - 1] = newbits[bits.length - 1] - pow(2, j);
					bit[count] = 1;
				}
			if (newbits[bits.length - 1] > 0)
				if (newbits[bits.length - 1] >= longpow(2, j)) {
					// if(i==1&&j==31) {
					// System.out.print(hikaku);
					// System.out.print(0>=longpow(2,j));
					// }
					newbits[bits.length - 1] = newbits[bits.length - 1] - pow(2, j);
					bit[count] = 1;
				}
			// System.out.print(bit[count]+", ");
			count++;
		}
		// System.out.println();
		count = 0;
		int cou = 0;
		int[] result = new int[bit.length];
		int leftlength = bit.length;
		boolean owari = false;
		for (int i = 0; i < bit.length && !owari; i++) {
			result[i] = bit[31 + (32 * count) - cou];
			leftlength = leftlength - 1;
			cou++;
			if (cou == 32) {
				cou = 0;
				count++;
			}
			if (leftlength <= 16) {
				for (int j = i + 1; j < bit.length; j++) {
					result[j] = bit[bit.length - 1 - j + i + 1];
					// System.out.print(result[j]+", ");
				}
				// System.out.println(owari);
				owari = true;
			}
		} // 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1,
		return result;
	}

	public int[] bitstosymbol(int[] bits) {
		int[] bit = bitstobit(bits);
		// for(int i=0;i<bit.length;i++) {
		// System.out.print(bit[i]+", ");
		// }
		// System.out.println();
		int[] symbol = new int[bit.length / 8];
		int count = 0;
		for (int i = 0; i < bit.length; i++) {
			int beki = i;
			while (beki >= 8) {
				beki = beki - 8;
			}
			count = i / 8;
			if (bit[i] != 0) {
				symbol[count] = symbol[count] + pow(2, 7 - beki);
			}
		}
		return symbol;
	}

	public static int[] bittosymbol(int[] bit) {
		// for(int i=0;i<bit.length;i++) {
		// System.out.print(bit[i]+", ");
		// }
		// System.out.println();
		int[] symbol = new int[bit.length / 8];
		int count = 0;
		for (int i = 0; i < bit.length; i++) {
			int beki = i;
			while (beki >= 8) {
				beki = beki - 8;
			}
			count = i / 8;
			if (bit[i] != 0) {
				symbol[count] = symbol[count] + pow(2, 7 - beki);
			}
		}
		return symbol;
	}

	static boolean bit(int a, int e) {
		int[] bita = new int[8];
		int[] bite = new int[8];
		int suma = a;
		int sume = e;
		boolean result = true;

		for (int i = 0; i < 8; i++) {
			if (suma >= pow(2, 7 - i)) {
				bita[i] = 1;
				suma = suma - pow(2, 7 - i);
			}
			if (sume >= pow(2, 7 - i)) {
				bite[i] = 1;
				sume = sume - pow(2, 7 - i);
			}
		}

		for (int i = 0; i < bita.length; i++) {
			if (bita[i] == 1 && bite[i] == 1)
				result = false;
		}
		return result;
	}

	static int[] vectol(int a) {
		int[] bita = new int[8];
		int suma = a;
		for (int i = 0; i < 8; i++) {
			if (suma >= pow(2, 7 - i)) {
				bita[i] = 1;
				suma = suma - pow(2, 7 - i);
			}
		}
		return bita;
	}

	public int[] symboltobits(int[] c) {
		int count = 0;
		int[] bita = new int[32];
		int[] bits = new int[(c.length * 8) / 32 + 1];
		int le = c.length * 8;
		int num = 0;
		for (int i = 1; i <= c.length; i++) {
			int[] bit = vectol(c[i - 1]);

			for (int ii = 0; ii < bit.length; ii++) {
				bita[31 - count] = bit[ii];
				count++;
			}
			if (count == 32) {
				count = 0;
				int sum = 0;
				for (int ii = 0; ii < bita.length; ii++) {
					if (bita[ii] != 0)
						sum = sum + pow(2, 31 - ii);
				}
				bits[num] = sum;
				le = le - 32;
				num++;
				bita = new int[32];
			}
			if (i == c.length && (c.length * 8) % 32 != 0) {
				// System.out.println(le);
				int sum = 0;
				for (int ii = 0; ii < bita.length; ii++) {
					if (bita[ii] != 0)
						sum = sum + pow(2, 31 - ii);
					// System.out.print(bita[ii]+" ");
				}

				bits[num] = sum;
			}
		}
		return bits;
	}

	static int symbolweight(int a) {
		int weight = 0;
		int[] bita = vectol(a);
		for (int i = 0; i < bita.length; i++) {
			if (bita[i] == 1) {
				weight++;
			}
		}
		return weight;

	}

	static boolean hanbit(int a, int b) {
		int e = a ^ b;
		int[] bita = new int[8];
		int[] bite = new int[8];
		int suma = a;
		int sume = e;
		boolean result = true;

		for (int i = 0; i < 8; i++) {
			if (suma >= pow(2, 7 - i)) {
				bita[i] = 1;
				suma = suma - pow(2, 7 - i);
			}
			if (sume >= pow(2, 7 - i)) {
				bite[i] = 1;
				sume = sume - pow(2, 7 - i);
			}
		}

		for (int i = 0; i < bita.length; i++) {
			if (bita[i] == 1 && bite[i] == 1)
				result = false;
		}
		return result;

	}

	static long longpow(int a, int b) {// aのb乗
		long result = 1;
		for (int i = 0; i < b; i++) {
			result = result * a;
		}
		return result;
	}

	static int pow(int a, int b) {// aのb乗
		int result = 1;
		for (int i = 0; i < b; i++) {
			result = result * a;
		}
		return result;
	}

	GFpoly getZero() {
		return zero;
	}

	GFpoly getOne() {
		return one;
	}

	int getpow(int i, int n) {
		int a;
		if (i == 0 && n > 0) {
			return 0;
		}
		if (i == 0 && n == 0) {
			return 1;
		}
		if (i == 1) {
			return 1;
		} else {
			a = getlogTable(i);
			a = a * n;

			// System.out.println(a);
			a = a % (getSize() - 1);
			return getaTable(a);
		}
	}

	int getSize() {
		return size;
	}

	public int getaTable(int i) {
		return aTable[i];
	}

	int getlogTable(int i) {
		return logTable[i];
	}

	int getL() {
		return l;
	}

	public static int addSubstract(int a, int b) {
		return a ^ b;
	}

	int multiply(int a, int b) {
		if (a == 0 || b == 0)
			return 0;
		else
			return aTable[(logTable[a] + logTable[b]) % (size - 1)];
	}

	int reciprocal(int a) {
		if (a == 0)
			throw new IllegalArgumentException("");
		else
			return aTable[size - 1 - logTable[a]];
	}

	// a / b c: bの逆数
	int divide(int a, int b) {
		if (b == 0)
			throw new IllegalArgumentException("0では割れない");
		else {
			int c = aTable[size - 1 - logTable[b]];
			return multiply(a, c);
		}
	}

	public static void main(String[] args) {//

		GF gf = new GF(0x011D, 256, 0);
		RS rs = new RS(gf);
		int[] k = new int[19];
		k[0] = 1;
		int[] c = rs.encode(k, 8);
		for (int i = 0; i < c.length; i++) {
			System.out.print(c[i] + ", ");
		}
		System.out.println();
		int[] bits = gf.symboltobits(c);
		for (int i = 0; i < bits.length; i++) {
			System.out.print(bits[i] + ", ");
		}
		System.out.println();
		int[] cc = gf.bitstosymbol(bits);
		for (int i = 0; i < cc.length; i++) {
			System.out.print(cc[i] + ", ");
		}
		// int[] bits = gf.tobits(c);
		// System.out.println(bits.length);
		// for(int i=0;i<bits.length;i++) {
		// System.out.print(bits[i]+", ");
		// }
		/*
		 * for(int i=0;i<=gf.getSize();i++) {
		 * 
		 * System.out.println(gf.getaTable(i));
		 * }
		 */
		// int d = 16;
		// int o;
		// o = (d-1)/2;
		// int[] bit = new int[8];
		// for(int i=0;i<gf.getSize()-1;i++) {
		// System.out.print("α^"+i);
		// System.out.print(" ");
		// bit = GF.vectol(gf.getaTable(i));
		// for(int j=0;j<bit.length;j++) {
		// System.out.print(bit[j]+" ");
		// }
		//
		// System.out.println();
		// }

		// System.out.println(2*o);
		// for(int i=0;i<gf.getSize()-1;i++)
		// System.out.println(gf.getlogTable(gf.getaTable(i)));
		// System.out.println(gf.getlogTable(77));

	}

}
