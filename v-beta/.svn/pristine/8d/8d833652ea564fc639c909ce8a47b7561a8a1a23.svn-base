package com.dsc.spos.utils;

import java.math.BigDecimal;

/**
 * 对BigDecimal类的封装操作
 * 
 * @author robbin.zhang
 * @date 2016/06/17 12:30
 */
public class BigDecimalUtils {
	
	// 默认除法运算精度  
    private static final int DEF_DIV_SCALE = 10;  
  
  
    /** 
     * 提供精确的加法运算。 
     *  
     * @param v1 被加数 
     * @param v2 加数 
     * @return 两个参数的和 
     */  
    public static double add(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.add(b2).doubleValue();  
    }  
    
    /**
     * 多个double加法运算
     * @param args
     * @return
     */
    public static double addMultipleValue(double...args) {
    	 if (args.length < 1) {  
             throw new IllegalArgumentException("The scale must be a positive integer or zero");  
         }  
    	 BigDecimal result = new BigDecimal(Double.toString(args[0]));
    	 for (int i = 1; i < args.length; i++) {
    		result = result.add(new BigDecimal(Double.toString(args[i])));
		}
    	return result.doubleValue();
    }
  
    /** 
     * 提供精确的减法运算。 
     *  
     * @param v1 被减数 
     * @param v2 减数 
     * @return 两个参数的差 
     */  
    public static double sub(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.subtract(b2).doubleValue();  
    }  
    
	/**
	 * 多个double减法运算
	 * @param args
	 * @return
	 */
	public static double sub(double... args) {

		if (args.length < 2) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal result = new BigDecimal(Double.toString((args[0])));
		for (int i = 1; i < args.length; i++) {
			result = result.subtract(new BigDecimal(Double.toString(args[i])));
		}
		return result.doubleValue();
	}
    
    /** 
     * 提供精确的乘法运算。 
     *  
     * @param v1 被乘数 
     * @param v2 乘数 
     * @return 两个参数的积 
     */  
    public static double mul(double v1, double v2) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.multiply(b2).doubleValue();  
    }  
    
    
    /** 
     * 提供精确的乘法运算。 
     *  
     * @param v1 被乘数 
     * @param v2 乘数 
     * @return 两个参数的积 
     */  
    public static double mul(double v1, double v2, int scale) {  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.multiply(b2).setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();  
    }  
  
    /** 
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。 
     *  
     * @param v1 被除数 
     * @param v2 除数 
     * @return 两个参数的商 
     */  
    public static double div(double v1, double v2) {  
        return div(v1, v2, DEF_DIV_SCALE);  
    }  
  
    /** 
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
     *  
     * @param v1 被除数 
     * @param v2 除数 
     * @param scale 表示表示需要精确到小数点以后几位。 
     * @return 两个参数的商 
     */  
    public static double div(double v1, double v2, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        }  
        BigDecimal b1 = new BigDecimal(Double.toString(v1));  
        BigDecimal b2 = new BigDecimal(Double.toString(v2));  
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }  
  
    /** 
     * 提供精确的小数位四舍五入处理。 
     *  
     * @param v 需要四舍五入的数字 
     * @param scale 小数点后保留几位 
     * @return 四舍五入后的结果 
     */  
    public static double round(double v, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        }  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }
    
    public static BigDecimal round(BigDecimal v, int scale) {  
        if (scale < 0) {  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        }  
        BigDecimal one = new BigDecimal("1");  
        return v.divide(one, scale, BigDecimal.ROUND_HALF_UP);  
    }
    
 	/**
 	 * 判断 a 是否大于等于 b
 	 * 
 	 * @param a
 	 * @param b
 	 * @return a&gt;=b 返回true, a&lt;b 返回false
 	 */
 	public static boolean greaterThanOrEqualTo(double a, double b) {
 		BigDecimal v1 = BigDecimal.valueOf(a);
 		BigDecimal v2 = BigDecimal.valueOf(b);
 		if (v1.compareTo(v2) >= 0) {
 			return true;
 		}
 		return false;
 	}
 	
 	/**
 	 * 判断 a 是否大于 b
 	 * 
 	 * @param a
 	 * @param b
 	 * @return a&gt;b 返回true, a&lt;=b 返回 false
 	 */
 	public static boolean bigger(double a, double b) {
 		BigDecimal v1 = BigDecimal.valueOf(a);
 		BigDecimal v2 = BigDecimal.valueOf(b);
 		if (v1.compareTo(v2) == 1) {
 			return true;
 		}
 		return false;
 	}
 	
 	public static boolean equal(double a, double b) {
 		BigDecimal v1 = BigDecimal.valueOf(a).setScale(6,BigDecimal.ROUND_HALF_UP);
 		BigDecimal v2 = BigDecimal.valueOf(b).setScale(6,BigDecimal.ROUND_HALF_UP);
 		if (v1.compareTo(v2) == 0) {
 			return true;
 		}
 		return false;
 	}
 	
 	
	public static String subZeroAndDot(String s) {
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");
			s = s.replaceAll("[.]$", "");
		}
		return s;
	}
	
   public static int toInt(double v) {  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        return b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
   }
   
   /** 
    * 判断double是否是整数 
    * @param obj 
    * @return 
    */  
   public static boolean isIntegerForDouble(double obj) {  
       double eps = 1e-10;  // 精度范围  
       return obj-Math.floor(obj) < eps;  
   }  
   
   public static void main(String[] args) {
	   System.out.println(isIntegerForDouble(129.1)); // 287
	   System.out.println(isIntegerForDouble(129.000000001)); // 287
	   System.out.println(isIntegerForDouble(129.00)); // 287
	   System.out.println(isIntegerForDouble(129.)); // 287
   }
	  
}  