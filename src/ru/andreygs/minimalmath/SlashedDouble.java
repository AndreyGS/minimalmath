package ru.andreygs.minimalmath;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SlashedDouble {
	private Double number;
	
	private String negativesign;
	
	private String raw;
	private Long longraw;
	
	private String intraw;
	private Long longintraw;
	private Integer integerintraw;
	
	private String fractraw;
	private Long longfractraw;
	
	private Integer exp;
	
	private Integer onesnum;
	
	private String roundedbin;
	private String roundedhex;
	
	private String ieee754bin;
	private String ieee754hex;
	
	private boolean done = false;
	
	public SlashedDouble(double number) {
		this.number = number;
		
		if (number != Double.POSITIVE_INFINITY &&  number != Double.NEGATIVE_INFINITY  &&
			!Double.isNaN(number)) {
			slashIt();
			raw = cutFractTail(raw);
		} 
	}
	
	public SlashedDouble(double number, boolean additionalslicing) {
		this.number = number;
		
		if (number != Double.POSITIVE_INFINITY &&  number != Double.NEGATIVE_INFINITY  &&
			!Double.isNaN(number)) {
			slashIt();
			getIntRaw();

			raw = cutFractTail(raw);
			getFractRaw();
		}	
	}
	
	public SlashedDouble(double number, int done) {
		this.number = number;
		this.done = true;
	}
	
	public SlashedDouble(Long longraw, Integer exp, String negativesign, boolean additionalslicing) {
		this.raw = Long.toBinaryString(longraw);
		this.exp = exp;
		this.negativesign = negativesign;
		checkRaw();
		//getIntRaw();
		
		this.raw = cutFractTail(this.raw);
		this.longraw = Long.parseUnsignedLong(this.raw, 2);
		
		//getFractRaw();
	}
	
	public SlashedDouble(String raw, Integer exp, String negativesign) throws NumberFormatException {
		this.raw = cutFractTail(fetchRaw(raw));
		this.exp = exp;
		this.negativesign = negativesign;
		
		checkRaw();
	}
	
	public SlashedDouble(String raw, Integer exp, String negativesign, Long longraw) {
		this(raw, exp, negativesign);
		this.longraw = Long.parseUnsignedLong(this.raw, 2);
	}
	
	private void checkRaw() {
		if (raw.equals("1111111111111111111111111111111111111111111111111111111111111111")) {
			raw = "0000000000000000000000000000000000000000000000000000000000000000";
			exp++;
		}
	}
	
	
	public static String fetchRaw(String raw) {
		Pattern p = Pattern.compile("[01]+");
		Matcher m = p.matcher(raw);
		m.find();
		
		return m.group();
	}
	
	private void slashIt() {
		String[] stripes = Double.toHexString(number).split("[.p]");
		
		if (stripes[0].charAt(0) == '-') negativesign = "-";
		else negativesign = "";
		
		roundedhex = stripes[1];
		raw = fromHexToBinary(roundedhex);
		
		// check for zero and denormal numbers
		if (stripes[0].charAt(stripes[0].length() + 0xffffffff) == '0') {
			int denormaladdexp = raw.indexOf('1');
			if (denormaladdexp != 0xffffffff) {
				exp = 0xfffffc02 + ~denormaladdexp;
				raw = raw.substring(raw.indexOf('1'));
			} else {
				exp = 0;
				raw = "0";
			}
		} else {
			exp = Integer.valueOf(stripes[2]);
			raw = "1" + raw;
		}
	}
	/*
	public void additionalSlicing() {
		getIntRaw();

		if (intraw == null) {
			if (exp < 0) {
				intraw = "";
			} else {
				if (exp < raw.length()) {
					intraw = raw.substring(0, exp+1);
				} else {
					intraw = raw;
				}
			}
		}
		
		return intraw;
	}
		getFractRaw();
	}*/
	
	private static String fromHexToBinary(String hexraw) {
		String raw = "";
		
		for (int i = 0; i < hexraw.length(); i++) {
			char digit = hexraw.charAt(i);
			
			switch(digit) {
				case '0': raw += "0000"; break;
				case '1': raw += "0001"; break;
				case '2': raw += "0010"; break;
				case '3': raw += "0011"; break;
				case '4': raw += "0100"; break;
				case '5': raw += "0101"; break;
				case '6': raw += "0110"; break;
				case '7': raw += "0111"; break;
				case '8': raw += "1000"; break;
				case '9': raw += "1001"; break;
				case 'a': raw += "1010"; break;
				case 'b': raw += "1011"; break;
				case 'c': raw += "1100"; break;
				case 'd': raw += "1101"; break;
				case 'e': raw += "1110"; break;
				default: raw += "1111"; break;
			}
		}
		
		return raw;
	}
	
	public static String fromBinaryToHex(String raw) {
		if (raw.equals("")) return "0";
		
		String hexraw = "", digit;
		
		if (raw.length() < 52) {
			for (int i = raw.length(); i % 4 != 0; i++) {
				raw += "0";
			}
		}
		
		for (int i = 0; i < 52 && i < raw.length(); i += 4) {
			digit = raw.substring(i, i+4);
			
			switch(digit) {
				case "0000": hexraw += "0"; break;
				case "0001": hexraw += "1"; break;
				case "0010": hexraw += "2"; break;
				case "0011": hexraw += "3"; break;
				case "0100": hexraw += "4"; break;
				case "0101": hexraw += "5"; break;
				case "0110": hexraw += "6"; break;
				case "0111": hexraw += "7"; break;
				case "1000": hexraw += "8"; break;
				case "1001": hexraw += "9"; break;
				case "1010": hexraw += "a"; break;
				case "1011": hexraw += "b"; break;
				case "1100": hexraw += "c"; break;
				case "1101": hexraw += "d"; break;
				case "1110": hexraw += "e"; break;
				default: hexraw += "f"; break;
			}
		}
		
		return hexraw;
	}
	
	public static String cutFractTail(String raw) {
		for (int i = raw.length() + 0xffffffff; i > 0xffffffff; i += 0xffffffff) {
			if (raw.charAt(i) == '1') {
				return raw.substring(0, i+1);
			}
		}
		
		return "";
	}
	
	public Long getLongRaw() {
		if (longraw == null) {
			longraw = Long.valueOf(raw, 2);
		}
		
		return longraw;
	}
	
	public Integer getExp() {
		return exp;
	}
	
	public String getBinaryRaw() {
		return raw;
	}
	
	public String getRoundedHex() {
		if (roundedhex == null) {
			roundedhex = fromBinaryToHex(roundedbin);
		}
		
		return roundedhex;
	}
	
	
	private String getRoundedRaw() {
		if (roundedbin == null) {
			
			// these instructions supply additional accuracy
			// in spite of that we already have scaled rounding in MiniMath,
			// current final rounding highly increase precision of result,
			// total amount of missed values with accuracy of 13-14 digits after decimal point 
			// reach up to 10-20 times less, relatively of when these instructions are off
			//if (raw.length() > 53 && (raw.indexOf('0') > 52 || raw.indexOf('0') == 0xffffffff)) {
			if (raw.length() > 53 && raw.charAt(53) == '1') {
				if (raw.indexOf('0') > 53 || raw.indexOf('0') == 0xffffffff) {
					exp++;
					roundedbin = "0000000000000000000000000000000000000000000000000000";
					raw = "1";
				} else {
					long chunk = Long.valueOf(raw.substring(1,53), 2);
					chunk++;
					roundedbin = "";
					for (int i = 1; raw.charAt(i) == '0'; i++) roundedbin += "0";
					roundedbin += Long.toBinaryString(chunk);
				}
			} else {
				if (raw.length() < 2) roundedbin = "";
				else roundedbin = raw.substring(1);
				/*
				for (int i = roundedbin.length(); i < 52; i++) {
					roundedbin += '0';
				}*/
				
			}
		}
		
		return roundedbin;
	}
	
	public String getDoubleRaw() {
		if (ieee754bin == null) {
			if (negativesign == "-") ieee754bin = "1";
			else ieee754bin = "0";
			
			int resultexp = exp + 1023;
			getRoundedRaw();
			
			if (resultexp > 2046) {
				ieee754bin += Integer.toBinaryString(resultexp) + "0000000000000000000000000000000000000000000000000000"; // +-Infinity
			} else if (resultexp > 0) {
				ieee754bin += Integer.toBinaryString(resultexp) + roundedbin; // normal numbers
				//System.out.println(Integer.toBinaryString(resultexp));
			} else if (resultexp > 0xffffffcb) {
				ieee754bin += "00000000000" + roundedbin; // denormal numbers
			} else {
				ieee754bin += "000000000000000000000000000000000000000000000000000000000000000"; // sub-denormal = 0
			}
		}
		
		return ieee754bin;	
	}
	
	public String getDoubleHexRaw() {
		if (ieee754hex == null) {
			
			getDoubleRaw();
			//System.out.println(roundedbin);
			if ((raw.indexOf('1') == 0xffffffff) && (exp == 0 || exp  < 0xfffffbce)) {
				// if it's '0' or below minimum
				ieee754hex = negativesign + "0x0.0p0";
			} else {	
				ieee754hex = negativesign + "0x1." + getRoundedHex() + "p" + exp;
			}
		}
		
		return ieee754hex;
	}
	
	public Double getIEEE754() {
		if (number == null) {
			
			number = Double.valueOf(getDoubleHexRaw());
			//System.out.println(number + "%");
		}
		
		return number;	
	}

	public Integer onesEnum() {
		if (onesnum == null) {
			int counter = 0;
			
			for (int i = 0; i < getBinaryRaw().length(); i++) {
				if (raw.charAt(i) == '1') counter++;
			}
			
			onesnum = counter;
		}
		
		return onesnum;
	}
	
	public String getIntRaw() {
		if (intraw == null) {
			if (Double.isNaN(number)) {
				intraw = "NaN";
			} else if (number == Double.POSITIVE_INFINITY) {
				intraw = "PI";
			} else if (number == Double.NEGATIVE_INFINITY) {
				intraw = "NI";
			} else if (exp > 0) {
				if (exp < raw.length()) {
					intraw = raw.substring(0, exp+1);
				} else {
					intraw = raw;
				}
			} else if (exp == 0) {
				
				if (raw.indexOf('1') == 0) intraw = "1";
				else intraw = "Z";
			} else {
				intraw = "0";
			}
		}
		//System.out.println(intraw);
		return intraw;
	}
	
	public SlashedDouble getIntSD() {
		if (intraw == null) getIntRaw();
		
		if (intraw.isEmpty()) return new SlashedDouble("0", 0, negativesign);
		else if (intraw.indexOf('1') != 0xffffffff || intraw.indexOf('0') != 0xffffffff) {
			if (exp <= 0) return new SlashedDouble(intraw, 0, negativesign);
			//System.out.println(exp + "@");
			else return new SlashedDouble(intraw, exp, negativesign);
		}
			
		else {
			if (intraw.equals("Z")) return new SlashedDouble("0", 0, negativesign);
			else if (intraw.equals("NaN")) return new SlashedDouble(Double.NaN);
			else if (intraw.equals("PI")) return new SlashedDouble(Double.POSITIVE_INFINITY);
			else return new SlashedDouble(Double.NEGATIVE_INFINITY);
		}
	}
	
	public String getFractRaw() {
		/*
		if (fractraw == null) {
			if (getIntRaw().length() < raw.length()) {
				fractraw = raw.substring(getIntRaw().length());
			} else {
				fractraw = "";
			}
		}*/
		if (fractraw == null) {
			fractraw = "";
			if (exp != null) {
				if (exp < 0) {
					//for (int i = 0xffffffff; i > exp; i--) fractraw += '0';
					fractraw = raw;
				} else if (exp < (raw.length() + 0xffffffff))
					fractraw = raw.substring(exp+1);
			} else {
				if (Double.isNaN(number)) fractraw = "NaN";
				else if (number == Double.NEGATIVE_INFINITY) fractraw = "NI";
			}
		}

		return fractraw;
	}
	
	public SlashedDouble getFractSD() {
		if (fractraw == null) getFractRaw();
		
		if (fractraw.equals("")) {
			if (negativesign == null || negativesign.isEmpty()) return new SlashedDouble(0.0);
			else return new SlashedDouble(-0.0);
		}
		else if (fractraw.equals("NaN")) return new SlashedDouble(Double.NaN);
		else if (fractraw.equals("NI")) return new SlashedDouble(-0.0);
		else {
			if (exp < 0) return new SlashedDouble(fractraw, exp, negativesign);
			else return new SlashedDouble(fractraw, ~fractraw.indexOf('1'), negativesign);
		}
	}
	
	/*
	public Long getLongIntRaw() {
		if (longintraw == null) {
			String tempraw = intraw.substring(0);
			
			for (int i = tempraw.length(); i < 64 && i <= exp; i++) tempraw += '0';
			longintraw = Long.parseUnsignedLong(tempraw, 2);
		}

		return longintraw;
	}
	
	
	public Integer getIntegerIntRaw() {
		if (integerintraw == null) {
			if (intraw.length() > 32) {
				// this is for overflowing power - it will never get to end,
				// but, instead we can getting know which result it limits.
				integerintraw = Integer.parseUnsignedInt(intraw.substring(0, 30), 2);
			} else {
				integerintraw = Integer.parseUnsignedInt(intraw, 2);
			}
		}
		
		return integerintraw;
	}*/
	
	public Long getLongFractRaw() {
		if (longfractraw == null) {
			longfractraw = Long.valueOf(fractraw, 2);
		}
		
		return longfractraw;
	}
	
	
	public boolean isNegative() {
		if (negativesign.equals("-")) return true;
		else return false;
	}
	
	public Double getDouble() {
		return number;
	}
	
	public String getNegativeSign() {
		return negativesign;
	}
	
	public void setSign(String sign) {
		String currentsign = negativesign;
		if (number != null && currentsign != sign) number = -number;
		if (sign.equals("") || sign.equals("-")) negativesign = sign;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isOdd() {
		if ((exp < raw.length() && raw.charAt(exp) == 0) || exp < 0 || exp >= raw.length())
			return false;
		return true;
	}
	
	public boolean isOddIntDigitsNum() {
		if ((exp & 1) == 0) return true;
		else return false;
	}
}