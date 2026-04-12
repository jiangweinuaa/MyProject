package com.dsc.spos.thirdpart.youzan;

/**
 * 支付方式归类
 * @author LN 08546
 */
public enum YouZanPayTypeEnum{
	
	 /**
	 * 
	 */
	//微信支付：
	/**
	 * wxpay, 1
	 * tenpay, 5
	"wxpay_bigunsign", 10
	"wxpay_bigsign", 11
	"wxapppay", 13
	"wxwappay", 19
	"wx_hb", 20
	"barcode_wx", 29
	"wx_native", 72
	"of_weixin", 102
	"wx_barcode", 109
	"mark_pay_wxpay", 110
	 */
	_00001("1", "WEIXIN"),
	_00002("5", "WEIXIN"),
	_00003("10", "WEIXIN"),
	_00004("11", "WEIXIN"),
	_00005("13", "WEIXIN"),
	_00006("19", "WEIXIN"),
	_00007("20", "WEIXIN"),
	_00008("29", "WEIXIN"),
	_00009("72", "WEIXIN"),
	_00010("102", "WEIXIN"),
	_00011("109", "WEIXIN"),
	_00012("110", "WEIXIN"),
	
	/**
	 * 银行卡支付：
	"unionpay", 4
	"unionwap", 6
	"umpay", 8
	"baiduwap", 12
	"yzpay", 24
	"CREDIT_CARD_UNIONPAY", 36
	"DEBIT_CARD_UNIONPAY", 37
	"allin_swipecard", 113
	 */
	
	_10001("4", "YINHANG"),
	_10002("6", "YINHANG"),
	_10003("8", "YINHANG"),
	_10004("12", "YINHANG"),
	_10005("24", "YINHANG"),
	_10006("36", "YINHANG"),
	_10007("37", "YINHANG"),
	_10008("113", "YINHANG"),
	
	
	/**
	 * 支付宝支付：
	"aliwap", 2
	"alipay", 3
	"barcode_alipay", 30
	"of_alipay", 103
	"alipay_barcode", 108
	"mar_pay_alipay", 111
	 */
	_20001("2", "ALIPAY"),
	_20002("3", "ALIPAY"),
	_20003("30", "ALIPAY"),
	_20004("103", "ALIPAY"),
	_20005("108", "ALIPAY"),
	_20006("111", "ALIPAY"),
	 
	;
	
	// 普通方法  
    public static String getName(String code) {  
        for (YouZanPayTypeEnum retCode : YouZanPayTypeEnum.values()) {  
            if (retCode.getCode().equals(code)) {  
                return retCode.message;  
            }  
        }  
        return null;  
    }
    
	private String code;
	
    private String message;
    
    private YouZanPayTypeEnum(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
