package com.dsc.spos.model;

import com.dsc.spos.orm.ORMField;

public class PLATFORM_SREGISTERDETAIL 
{
	@ORMField(ColumnLength = 20,ColumnType = "NVARCHAR2",ColumnNullable ="N",Comment="客户编号")
	public String CUSTOMERNO;
  //注册号码
	@ORMField(ColumnLength = 40,ColumnType = "NVARCHAR2",ColumnNullable ="N",ColumnPrimarykey = "Y",Comment="注册号")
	private String TERMINALLICENCE;
  //注册类型0正式 1测试
	@ORMField(ColumnLength = 10,ColumnType = "NVARCHAR2",ColumnDefaultValue="0",Comment="注册类型 0正式 1测试")
	private String REGISTERTYPE;
  //0、云中台1、门店管理2、会员3、云POS
	@ORMField(ColumnLength = 10,ColumnType = "NVARCHAR2",ColumnDefaultValue="0",ColumnNullable ="N",ColumnPrimarykey = "Y",Comment="产品类型")
	private String PRODUCTTYPE;
//注册数量
	@ORMField(ColumnType = "INTEGER",ColumnDefaultValue ="0",Comment="注册数量")
	private int SCOUNT;
//注册开始时间
	@ORMField(ColumnLength = 8,ColumnType = "NVARCHAR2",Comment="注册开始时间")
	private String BDATE;
	//注册结束时间
	@ORMField(ColumnLength = 8,ColumnType = "NVARCHAR2",Comment="注册结束时间")
	private String EDATE;
	@ORMField(ColumnLength = 20,ColumnType = "NVARCHAR2",ColumnDefaultValue="to_char(systimestamp, 'yyyymmddhh24missff3')",Comment="系统时间")
	private String SDATETIME;
	@ORMField(ColumnLength = 200,ColumnType = "NVARCHAR2",Comment="备注")
	private String MEMO;
	public String getCUSTOMERNO() {
		return CUSTOMERNO;
	}
	public void setCUSTOMERNO(String cUSTOMERNO) {
		CUSTOMERNO = cUSTOMERNO;
	}
	public String getTERMINALLICENCE() {
		return TERMINALLICENCE;
	}
	public void setTERMINALLICENCE(String tERMINALLICENCE) {
		TERMINALLICENCE = tERMINALLICENCE;
	}
	public String getREGISTERTYPE() {
		return REGISTERTYPE;
	}
	public void setREGISTERTYPE(String rEGISTERTYPE) {
		REGISTERTYPE = rEGISTERTYPE;
	}
	public String getPRODUCTTYPE() {
		return PRODUCTTYPE;
	}
	public void setPRODUCTTYPE(String pRODUCTTYPE) {
		PRODUCTTYPE = pRODUCTTYPE;
	}
	public int getSCOUNT() {
		return SCOUNT;
	}
	public void setSCOUNT(int sCOUNT) {
		SCOUNT = sCOUNT;
	}
	public String getBDATE() {
		return BDATE;
	}
	public void setBDATE(String bDATE) {
		BDATE = bDATE;
	}
	public String getEDATE() {
		return EDATE;
	}
	public void setEDATE(String eDATE) {
		EDATE = eDATE;
	}
	public String getSDATETIME() {
		return SDATETIME;
	}
	public void setSDATETIME(String sDATETIME) {
		SDATETIME = sDATETIME;
	}
	public String getMEMO() {
		return MEMO;
	}
	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}
	
	
}

