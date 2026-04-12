package com.dsc.spos.waimai.jddj;

import java.util.List;

public class OSkuMainResultList {
	
	 private int count;
   private String result;
   private List<OSkuMain> skuMains;//最后赋值上去
   
   public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }

   public void setResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }
		public List<OSkuMain> getSkuMains() {
			return skuMains;
		}
		public void setSkuMains(List<OSkuMain> skuMains) {
			this.skuMains = skuMains;
		}


}
