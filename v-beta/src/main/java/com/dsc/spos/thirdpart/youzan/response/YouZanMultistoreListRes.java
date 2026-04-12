package com.dsc.spos.thirdpart.youzan.response;

import java.util.ArrayList;
import java.util.List;

/**
 *requestDTO全部为密文，具体的加密方式参考相应章节
入参：
{
    "service":"ORGANIZATION_LIST_QUERY_BIZ",
    "version":"v1",
    "targetSystem":"digiwin",
    "appId":"95908110_digiwin",
    "requestBody":{
        "pageSize":20,
        "pageIndex":1
    }
}
 
出参：
{
    "success":true,
    "errorCode":200,
    "errorMsg":"无错误信息",
    "responseDTO":{
        "total":1000,
        "data":[
            {
                "storeId":916200,
                "storeName":"文三店",
                "extendMap":{
 
                }
            },
            {
                "storeId":916201,
                "storeName":"西溪店店",
                "extendMap":{
 
                }
            }
        ]
    }
}
 */
public class YouZanMultistoreListRes extends YouZanBasicRes  {

	public YouZanMultistoreListRes() {
		
	}
	
	private ResponseDTO responseDTO=new ResponseDTO();
	public ResponseDTO getResponseDTO() {
		return responseDTO;
	}

	public void setResponseDTO(ResponseDTO responseDTO) {
		this.responseDTO = responseDTO;
	}

	public class ResponseDTO{
		private String total;
		private List<Item> data=new ArrayList<Item>();
		public String getTotal() {
			return total;
		}
		public void setTotal(String total) {
			this.total = total;
		}
		public List<Item> getData() {
			return data;
		}
		public void setData(List<Item> data) {
			this.data = data;
		}
		
		public class Item{
			private String storeId;
			private String storeName;
			private ExtendMap extendMap;
			public String getStoreId() {
				return storeId;
			}
			public void setStoreId(String storeId) {
				this.storeId = storeId;
			}
			public String getStoreName() {
				return storeName;
			}
			public void setStoreName(String storeName) {
				this.storeName = storeName;
			}
			public ExtendMap getExtendMap() {
				return extendMap;
			}
			public void setExtendMap(ExtendMap extendMap) {
				this.extendMap = extendMap;
			}
			
			public class ExtendMap{
				
			}
		}
	}
	
		
}
