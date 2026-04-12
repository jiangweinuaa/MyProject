package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 修改商品品类 2018-10-18
 * 
 * @author yuanyy
 *
 */
public class DCP_GoodsCategoryUpdateReq extends JsonBasicReq {

private levelRequest request;
	
	public levelRequest getRequest() {
		return request;
	}

	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String category;
		//private String categoryType;
		/*易成：
		0-普通分类
		1-财务分类
		cosmos：
		5.商品品类,
		6.商品属性-产地分类,
		7.商品属性-价格带,
		8.商品属性-品牌,
		9.商品属性-系列，
		A.型别,
		B.功能,
		C其它属性一，
		D.其它属性二，
		E其它属性三，
		F.其它属性四，
		G.其它属性五，
		H.其它属性六，
		I.商品属性七，
		J.其它属性八，
		K.其它属性九，
		L.其它属性十   
		M.其它属性十一  
		N.其它属性十二*/
		private String upCategory;
		private String categoryLevel;
		//private String downCategoryQty;
		private String topCategory;
		private String status;
        private String preFixCode;

		private List<level1Elm> categoryName_lang;
		
		private String categoryImage;
		

		public String getCategoryImage() {
			return categoryImage;
		}

		public void setCategoryImage(String categoryImage) {
			this.categoryImage = categoryImage;
		}

		public String getCategory() {
			return category;
		}
	
		public void setCategory(String category) {
			this.category = category;
		}
	
		public String getUpCategory() {
			return upCategory;
		}
	
		public void setUpCategory(String upCategory) {
			this.upCategory = upCategory;
		}
	
		public String getCategoryLevel() {
			return categoryLevel;
		}
	
		public void setCategoryLevel(String categoryLevel) {
			this.categoryLevel = categoryLevel;
		}
	
		public String getTopCategory() {
			return topCategory;
		}
	
		public void setTopCategory(String topCategory) {
			this.topCategory = topCategory;
		}
	
		public String getStatus() {
			return status;
		}
	
		public void setStatus(String status) {
			this.status = status;
		}

	public List<level1Elm> getCategoryName_lang() {
		return categoryName_lang;
	}

	public void setCategoryName_lang(List<level1Elm> categoryName_lang) {
		this.categoryName_lang = categoryName_lang;
	}


        public String getPreFixCode() {
            return preFixCode;
        }

        public void setPreFixCode(String preFixCode) {
            this.preFixCode = preFixCode;
        }
    }
	

	

	public class level1Elm {
		private String langType;
		private String name;
		

		public String getLangType() {
			return langType;
		}

		public void setLangType(String langType) {
			this.langType = langType;
		}

		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}

		

	}

}
