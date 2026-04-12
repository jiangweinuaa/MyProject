package com.dsc.spos.model;
import com.dsc.spos.orm.ORMField;

public class PLATFORM_SREGISTER 
{
  //序列号
	@ORMField(ColumnLength = 20,ColumnType = "NVARCHAR2",ColumnNullable ="N",ColumnPrimarykey = "Y",Comment="序列号")
	private String SNUMBER;
	public String getSNUMBER() {
		return SNUMBER;
	}
	public void setSNUMBER(String sNUMBER) {
		SNUMBER = sNUMBER;
	}
	public String getSNAME() {
		return SNAME;
	}
	public void setSNAME(String sNAME) {
		SNAME = sNAME;
	}
	public String getREGISTERTYPE() {
		return REGISTERTYPE;
	}
	public void setREGISTERTYPE(String rEGISTERTYPE) {
		REGISTERTYPE = rEGISTERTYPE;
	}
	public int getTOT_COUNT() {
		return TOT_COUNT;
	}
	public void setTOT_COUNT(int tOT_COUNT) {
		TOT_COUNT = tOT_COUNT;
	}
	public String getTERMINALLICENCE() {
		return TERMINALLICENCE;
	}
	public void setTERMINALLICENCE(String tERMINALLICENCE) {
		TERMINALLICENCE = tERMINALLICENCE;
	}
	//客户名称
	@ORMField(ColumnLength = 200,ColumnType = "NVARCHAR2",ColumnDefaultValue="'ALL'",Comment="客户名称")
	private String SNAME;
  //注册类型
	@ORMField(ColumnLength = 10,ColumnType = "NVARCHAR2",Comment="注册类型")
	private String REGISTERTYPE;
  //注册总数量
	@ORMField(ColumnType = "INTEGER",ColumnDefaultValue="0",Comment="总注册数")
	private int TOT_COUNT;
  //最新注册号码
	@ORMField(ColumnLength = 40,ColumnType = "NVARCHAR2",Comment="最新注册号")
	private String TERMINALLICENCE;
	
}
