package com.dsc.spos.thirdpart.youzan;

/**
 * 解析有赞返回的RefundReason
 * @author LN 08546
 */
public enum YouZanRefundReasonEnum{
	
	 /**
	 * 
	 */
	_00001("COMMON_CONSENSUS", "买/卖双方协商一致"),
	_00002("COMMON_BUYWRONG", "买错/多买/不想要"),
	_00003("COMMON_QUALITY", "商品质量问题"),
	_00004("COMMON_NOTRECEIVE", "未收到货品"),
	_00005("COMMON_OTHER", "其他"),
	_00006("REFUND_QUALITY", "质量问题"),
	_00007("REFUND_BUYWRONG", "拍错/多拍/不喜欢"),
	_00008("REFUND_INCONFORMITY", "商品描述不符"),
	_00009("REFUND_FAKE", "假货"),
	_00010("REFUND_SENDWRONG", "商家发错货"),
	_00011("REFUND_GOODSLESS", "商品破损/少件"),
	_00012("REFUND_OTHER", "其他"),
	_00013("RETURNSNOT_BUYWRONG", "多买/买错/不想要"),
	_00014("RETURNSNOT_NULLEXPRESS", "快递无记录"),
	_00015("RETURNSNOT_GOODSLESS", "少货/空包裹"),
	_00016("RETURNSNOT_NOTEXPRESS_ONTIME", "未按约定时间发货"),
	_00017("RETURNSNOT_NOTRECEIVE", "快递一直未送达"),
	_00018("RETURNSNOT_OTHER", "快递一直未送达"),
	_00019("RETURNS_GOODSLESS", "商品破损/少件"),
	_00020("RETURNS_SENDWRONG", "商家发错货"),
	_00021("RETURNS_INCONFORMITY", "商品描述不符"),
	_00022("RETURNS_BUYWRONG", "拍错/多拍/不喜欢"),
	_00023("RETURNS_QUALITY", "质量问题"),
	_00024("RETURNS_FAKE", "假货"),
	_00025("RETURNS_OTHER", "其他"),
	_00026("CASH_BACK_REFUND", "返现退款"),
	_00027("HOTEL_REJECT_ORDER_REFUND", "酒店拒单退款"),
	_00028("GROUP_DEDUCT_STOCK_FAILED_REFUND", "拼团订单扣库存失败退款"),
	_00029("ORDER_CLOSE_REFUND", "订单关闭退款"),
	_00030("PEER_PAY_EXPIRE_REFUND", "代付过期退款"),
	_00031("PEER_PAY_OVER_PAY_REFUND", "代付超付退款"),
	_00032("CATERING_REJECT_ORDER_REFUND", "外卖拒单退款"),
	_00033("REGIMENT_NOT_MASS_REFUND", "拼团未成团退款"),
	_00034("PAT_ERROR_OR_MORE_OR_DOT_NO_GOODS", "拍错/多拍/不喜欢"),
	_00035("CONSULT_OK_NO_GOODS", "协商一致退款"),
	 
	;
	
	// 普通方法  
    public static String getName(String code) {  
        for (YouZanRefundReasonEnum retCode : YouZanRefundReasonEnum.values()) {  
            if (retCode.getCode().equals(code)) {  
                return retCode.message;  
            }  
        }  
        return null;  
    }
    
	private String code;
	
    private String message;
    
    private YouZanRefundReasonEnum(String code, String message)
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
