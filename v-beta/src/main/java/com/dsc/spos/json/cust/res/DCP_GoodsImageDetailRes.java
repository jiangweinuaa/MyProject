package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_GoodsImageDetailRes extends JsonRes
{

	private level1Elm datas;
	public level1Elm getDatas()
	{
		return datas;
	}

	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}

	public class level1Elm
	{
		private String appType;
		private String pluNo;
		private String pluName;
		private String pluType;
		private String listImage;
		private String listImageUrl;
		private String symbolDisplay;
        private String dPic;
        private String dPicUrl;

        private List<levelSymbol> symbolList;//角标列表
		private List<levelProduct> prodImageList;//产品图片
		private List<levelSpec> specImageList;//规格图片
		private List<levelDetail> detailImageList;//图文详情
		public String getAppType() {
			return appType;
		}
		public void setAppType(String appType) {
			this.appType = appType;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getPluType() {
			return pluType;
		}
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getListImageUrl() {
			return listImageUrl;
		}
		public void setListImageUrl(String listImageUrl) {
			this.listImageUrl = listImageUrl;
		}
        public String getdPic() {
            return dPic;
        }
        public void setdPic(String dPic) {
            this.dPic = dPic;
        }
        public String getdPicUrl() {
            return dPicUrl;
        }
        public void setdPicUrl(String dPicUrl) {
            this.dPicUrl = dPicUrl;
        }
		public String getSymbolDisplay() {
			return symbolDisplay;
		}
		public void setSymbolDisplay(String symbolDisplay) {
			this.symbolDisplay = symbolDisplay;
		}
		public List<levelSymbol> getSymbolList() {
			return symbolList;
		}
		public void setSymbolList(List<levelSymbol> symbolList) {
			this.symbolList = symbolList;
		}
		public List<levelProduct> getProdImageList() {
			return prodImageList;
		}
		public void setProdImageList(List<levelProduct> prodImageList) {
			this.prodImageList = prodImageList;
		}
		public List<levelSpec> getSpecImageList() {
			return specImageList;
		}
		public void setSpecImageList(List<levelSpec> specImageList) {
			this.specImageList = specImageList;
		}
		public List<levelDetail> getDetailImageList() {
			return detailImageList;
		}
		public void setDetailImageList(List<levelDetail> detailImageList) {
			this.detailImageList = detailImageList;
		}



	}

	public class levelSymbol
	{
		private String symbolType ;//角标类型：1：新品2：热卖3：文本标签4：自定义
		private String symbolTag ;//自定义文本标签
		private String symbolImage ;//自定义图标名称
		private String symbolImageUrl ;//自定义图标url
		private String beginDate ;//开始日期YYYY-MM-DD
		private String endDate ;//截止日期YYYY-MM-DD		

		public String getSymbolImageUrl() {
			return symbolImageUrl;
		}
		public void setSymbolImageUrl(String symbolImageUrl) {
			this.symbolImageUrl = symbolImageUrl;
		}
		public String getSymbolType() {
			return symbolType;
		}
		public void setSymbolType(String symbolType) {
			this.symbolType = symbolType;
		}
		public String getSymbolTag() {
			return symbolTag;
		}
		public void setSymbolTag(String symbolTag) {
			this.symbolTag = symbolTag;
		}
		public String getSymbolImage() {
			return symbolImage;
		}
		public void setSymbolImage(String symbolImage) {
			this.symbolImage = symbolImage;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}



	}

	public class levelProduct
	{
		private String prodImage ;//产品图片名称
		private String prodImageUrl ;//产品图片url


		public String getProdImageUrl() {
			return prodImageUrl;
		}

		public void setProdImageUrl(String prodImageUrl) {
			this.prodImageUrl = prodImageUrl;
		}

		public String getProdImage() {
			return prodImage;
		}

		public void setProdImage(String prodImage) {
			this.prodImage = prodImage;
		}		
	}

	public class levelSpec
	{
		private String attrValueId1 ;//第一规格编码
		private String attrValueName ;//第一规格名称
		private String attrId1 ;//第一属性编码
		private String specImage ;//规格图片名称
		private String specImageUrl ;//规格图片url【DomainName拼接图片名称】
		
		public String getAttrValueId1()
		{
			return attrValueId1;
		}
		public void setAttrValueId1(String attrValueId1)
		{
			this.attrValueId1 = attrValueId1;
		}
		public String getAttrValueName()
		{
			return attrValueName;
		}
		public void setAttrValueName(String attrValueName)
		{
			this.attrValueName = attrValueName;
		}
		public String getAttrId1()
		{
			return attrId1;
		}
		public void setAttrId1(String attrId1)
		{
			this.attrId1 = attrId1;
		}
		public String getSpecImage()
		{
			return specImage;
		}
		public void setSpecImage(String specImage)
		{
			this.specImage = specImage;
		}
		public String getSpecImageUrl()
		{
			return specImageUrl;
		}
		public void setSpecImageUrl(String specImageUrl)
		{
			this.specImageUrl = specImageUrl;
		}
	}

	public class levelDetail
	{
		private String type ;//文件类型：TEXT IMAGE
		private String detailImage ;//详情图片名称，type为IMAGE时必传
		private String content ;//文本内容，type为TEXT时必传
		private String detailImageUrl ;//详情图片地址		

		public String getDetailImageUrl() {
			return detailImageUrl;
		}
		public void setDetailImageUrl(String detailImageUrl) {
			this.detailImageUrl = detailImageUrl;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getDetailImage() {
			return detailImage;
		}
		public void setDetailImage(String detailImage) {
			this.detailImage = detailImage;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}


	}

}
