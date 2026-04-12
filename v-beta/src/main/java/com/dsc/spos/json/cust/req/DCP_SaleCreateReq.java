package com.dsc.spos.json.cust.req;

import java.util.List;


import com.dsc.spos.json.JsonBasicReq;

public class DCP_SaleCreateReq extends JsonBasicReq {

	private List<level1Elm> sale;	
	private String keyid;

	public List<level1Elm> getSale() {
		return sale;
	}
	public void setSale(List<level1Elm> sale) {
		this.sale = sale;
	}
	public String getKeyid() {
		return keyid;
	}
	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}
	public  class level1Elm
	{
		private String sale_id;
		private String shopId;
		private String sale_no;
		private String machine_no;
		private String business_date;
		private String class_no;
		private String creator;
		private String tot_qty;
		private String tot_amt;
		private String tot_uamt;
		private String doc_type;
		private String source_type;
		private String source_no;
		private String sdate;
		private String stime;
		private String create_date;
		private String create_time;
		private String order_id;		
		private String pc_specrece;
		private String pc_saletype;
		private List<level2Goods> sale_goods_detail;
		private List<level2Pay> sale_pay_detail;
		public String getSale_id() {
			return sale_id;
		}
		public void setSale_id(String sale_id) {
			this.sale_id = sale_id;
		}

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getSale_no() {
			return sale_no;
		}
		public void setSale_no(String sale_no) {
			this.sale_no = sale_no;
		}
		public String getMachine_no() {
			return machine_no;
		}
		public void setMachine_no(String machine_no) {
			this.machine_no = machine_no;
		}
		public String getBusiness_date() {
			return business_date;
		}
		public void setBusiness_date(String business_date) {
			this.business_date = business_date;
		}
		public String getClass_no() {
			return class_no;
		}
		public void setClass_no(String class_no) {
			this.class_no = class_no;
		}
		public String getCreator() {
			return creator;
		}
		public void setCreator(String creator) {
			this.creator = creator;
		}
		public String getTot_qty() {
			return tot_qty;
		}
		public void setTot_qty(String tot_qty) {
			this.tot_qty = tot_qty;
		}
		public String getTot_amt() {
			return tot_amt;
		}
		public void setTot_amt(String tot_amt) {
			this.tot_amt = tot_amt;
		}
		public String getTot_uamt() {
			return tot_uamt;
		}
		public void setTot_uamt(String tot_uamt) {
			this.tot_uamt = tot_uamt;
		}
		public String getDoc_type() {
			return doc_type;
		}
		public void setDoc_type(String doc_type) {
			this.doc_type = doc_type;
		}
		public String getSource_type() {
			return source_type;
		}
		public void setSource_type(String source_type) {
			this.source_type = source_type;
		}
		public String getSource_no() {
			return source_no;
		}
		public void setSource_no(String source_no) {
			this.source_no = source_no;
		}
		public String getSdate() {
			return sdate;
		}
		public void setSdate(String sdate) {
			this.sdate = sdate;
		}
		public String getStime() {
			return stime;
		}
		public void setStime(String stime) {
			this.stime = stime;
		}
		public String getCreate_date() {
			return create_date;
		}
		public void setCreate_date(String create_date) {
			this.create_date = create_date;
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getOrder_id() {
			return order_id;
		}
		public void setOrder_id(String order_id) {
			this.order_id = order_id;
		}		
		public String getPc_specrece() {
			return pc_specrece;
		}
		public void setPc_specrece(String pc_specrece) {
			this.pc_specrece = pc_specrece;
		}
		public String getPc_saletype() {
			return pc_saletype;
		}
		public void setPc_saletype(String pc_saletype) {
			this.pc_saletype = pc_saletype;
		}	
		public List<level2Goods> getSale_goods_detail() {
			return sale_goods_detail;
		}
		public void setSale_goods_detail(List<level2Goods> sale_goods_detail) {
			this.sale_goods_detail = sale_goods_detail;
		}
		public List<level2Pay> getSale_pay_detail() {
			return sale_pay_detail;
		}
		public void setSale_pay_detail(List<level2Pay> sale_pay_detail) {
			this.sale_pay_detail = sale_pay_detail;
		}	

	}

	public  class level2Goods
	{
		private String old_sale_detail_id;
		private String sale_detail_id;
		private String seq;
		private String plu_no;
		private String plu_barcode;
		private String scan_barcode;
		private String unit_no;
		private String old_price;
		private String price;
		private String qty;
		private String disc;
		private String amt;
		private String uamt;
		private String source_seq;
		private String is_scrap;
		private String is_add;
		public String getOld_sale_detail_id() {
			return old_sale_detail_id;
		}
		public void setOld_sale_detail_id(String old_sale_detail_id) {
			this.old_sale_detail_id = old_sale_detail_id;
		}
		public String getSale_detail_id() {
			return sale_detail_id;
		}
		public void setSale_detail_id(String sale_detail_id) {
			this.sale_detail_id = sale_detail_id;
		}
		public String getSeq() {
			return seq;
		}
		public void setSeq(String seq) {
			this.seq = seq;
		}
		public String getPlu_no() {
			return plu_no;
		}
		public void setPlu_no(String plu_no) {
			this.plu_no = plu_no;
		}
		public String getPlu_barcode() {
			return plu_barcode;
		}
		public void setPlu_barcode(String plu_barcode) {
			this.plu_barcode = plu_barcode;
		}
		public String getScan_barcode() {
			return scan_barcode;
		}
		public void setScan_barcode(String scan_barcode) {
			this.scan_barcode = scan_barcode;
		}
		public String getUnit_no() {
			return unit_no;
		}
		public void setUnit_no(String unit_no) {
			this.unit_no = unit_no;
		}
		public String getOld_price() {
			return old_price;
		}
		public void setOld_price(String old_price) {
			this.old_price = old_price;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getDisc() {
			return disc;
		}
		public void setDisc(String disc) {
			this.disc = disc;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getUamt() {
			return uamt;
		}
		public void setUamt(String uamt) {
			this.uamt = uamt;
		}
		public String getSource_seq() {
			return source_seq;
		}
		public void setSource_seq(String source_seq) {
			this.source_seq = source_seq;
		}
		public String getIs_scrap() {
			return is_scrap;
		}
		public void setIs_scrap(String is_scrap) {
			this.is_scrap = is_scrap;
		}
		public String getIs_add() {
			return is_add;
		}
		public void setIs_add(String is_add) {
			this.is_add = is_add;
		}			
	}

	public  class level2Pay
	{
		private String sale_pay_id;
		private String seq;
		private String pay_code;
		private String pay_type;
		private String erp_pay_code;
		private String pay_sernum;
		private String serial_no;
		private String pay;
		private String extra_amt;
		private String changed_amt;
		private String is_verification;
		private String exrate;
		private String input_amt;
		private String pc_bankname;
		public String getSale_pay_id() {
			return sale_pay_id;
		}
		public void setSale_pay_id(String sale_pay_id) {
			this.sale_pay_id = sale_pay_id;
		}
		public String getSeq() {
			return seq;
		}
		public void setSeq(String seq) {
			this.seq = seq;
		}
		public String getPay_code() {
			return pay_code;
		}
		public void setPay_code(String pay_code) {
			this.pay_code = pay_code;
		}
		public String getPay_type() {
			return pay_type;
		}
		public void setPay_type(String pay_type) {
			this.pay_type = pay_type;
		}
		public String getErp_pay_code() {
			return erp_pay_code;
		}
		public void setErp_pay_code(String erp_pay_code) {
			this.erp_pay_code = erp_pay_code;
		}
		public String getPay_sernum() {
			return pay_sernum;
		}
		public void setPay_sernum(String pay_sernum) {
			this.pay_sernum = pay_sernum;
		}
		public String getSerial_no() {
			return serial_no;
		}
		public void setSerial_no(String serial_no) {
			this.serial_no = serial_no;
		}
		public String getPay() {
			return pay;
		}
		public void setPay(String pay) {
			this.pay = pay;
		}
		public String getExtra_amt() {
			return extra_amt;
		}
		public void setExtra_amt(String extra_amt) {
			this.extra_amt = extra_amt;
		}
		public String getChanged_amt() {
			return changed_amt;
		}
		public void setChanged_amt(String changed_amt) {
			this.changed_amt = changed_amt;
		}
		public String getIs_verification() {
			return is_verification;
		}
		public void setIs_verification(String is_verification) {
			this.is_verification = is_verification;
		}
		public String getExrate() {
			return exrate;
		}
		public void setExrate(String exrate) {
			this.exrate = exrate;
		}
		public String getInput_amt() {
			return input_amt;
		}
		public void setInput_amt(String input_amt) {
			this.input_amt = input_amt;
		}
		public String getPc_bankname() {
			return pc_bankname;
		}
		public void setPc_bankname(String pc_bankname) {
			this.pc_bankname = pc_bankname;
		}	
	}
}
