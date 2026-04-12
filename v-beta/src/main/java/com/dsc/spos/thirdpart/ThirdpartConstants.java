package com.dsc.spos.thirdpart;

/**
 * @author LN 08546
 */
public class ThirdpartConstants {
	
	//物流类型参考 SELECT * FROM DCP_FIXEDVALUE

	/**
	 * 物流—自配送
	 */
	public static String self_deliveryType = "1";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—顺丰同城
	 */
	public static String sftc_deliveryType = "2";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—百度
	 */
	public static String baidu_deliveryType = "3";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—达达配送
	 */
	public static String dada_deliveryType = "4";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—人人
	 */
	public static String renren_deliveryType = "5";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—闪送跑腿
	 */
	public static String ss_deliveryType = "6";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—黑猫宅急便(台湾用)
	 */
	public static String heimao_deliveryType = "9";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—新竹物流(台湾用)
	 */
	public static String xinzhu_deliveryType = "15";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—点我达
	 */
	public static String dianwoda_deliveryType = "20";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—管易云物流
	 */
	public static String gyy_deliveryType = "21";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—绿界(台湾用)
	 */
	public static String lvjie_deliveryType = "22";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—美团跑腿配送
	 */
	public static String pt_deliveryType = "23";//  DCP_ORDER.DELIVERYTYPE
	
	/**
	 * 物流—圆通物流配送
	 */
	public static String yto_deliveryType = "24";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—商有云管家
	 */
	public static String sy_deliveryType = "25";//  DCP_ORDER.DELIVERYTYPE

	/**
	 * 物流—快递鸟物流配送
	 */
	public static String kdn_deliveryType = "KDN";//  DCP_ORDER.DELIVERYTYPE


	/**
	 * 物流—餐道
	 */
	public static String cangdao_deliveryType = "28";//  DCP_ORDER.DELIVERYTYPE
	
	
	/**
	 * 连接超时时长(单位:毫秒)
	 */
	public static final int CONNECT_TIME_OUT = 10000;

	/**
	 * 读取超时时长(单位:毫秒)
	 */
	public static final int READ_TIME_OUT = 30000;
	
}
