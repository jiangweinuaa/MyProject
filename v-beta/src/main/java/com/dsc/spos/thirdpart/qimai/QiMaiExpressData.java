package com.dsc.spos.thirdpart.qimai;

public class QiMaiExpressData {

	public QiMaiExpressData() {
		// TODO Auto-generated constructor stub
	}
	//物流数据来源渠道类型。1=快递鸟；2=快递100；3=管易云；
	private String type="3";
	//物流公司编码。根据传递的物流数据来源渠道类型对应的编码传递。相关编码按照对应渠道标准编码传递。
	private String code;
	//物流公司ID
	//1=顺丰快递；2=圆通快递；3=韵达快递；4=中通快递；5=天天快递；6=中国邮政；7=EMS；8=百世汇通；9=申通快递；10=德邦快递；13=中铁快运；15=安能物流；16=优速快递；17=国通快递；19=京东物流；20=全一快递；21=远成；22=信丰；23=速尔快递；24=品骏快递；25=龙邦速运；26=极兔物流；27=特急送；28=宅急送；29=丹鸟物流；30=耀飞快递；31=众邮快递
	private String id; 
	//物流公司名称
	private String name;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	

}
