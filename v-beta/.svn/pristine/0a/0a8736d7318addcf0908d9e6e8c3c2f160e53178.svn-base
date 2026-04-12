package com.dsc.spos.json.utils;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Json 轉換工具
 * @author Xavier
 *
 */
public final class ParseJson {



	//private Gson gson=new Gson();
	//做成单例 ,避免Gson内存泄漏
	private static final Gson gson=new Gson();
	public ParseJson() 
	{
		/*
		GsonBuilder gsonBuilder = new GsonBuilder();
		//		gsonBuilder.registerTypeAdapter(JsonMaster.class, new JsonMasterSerializer());
		//		gsonBuilder.registerTypeAdapter(List.class, new ResDatasSerializer());
		this.gson = gsonBuilder.create();
		*/
	}


	public <T> String beanToJson(T obj) {
		return gson.toJson(obj);
	}

	public <T> T jsonToBean(String json, TypeToken<T> obj) 
	{		
		T o=null;

		try 
		{
			Type jsonType = obj.getType();

			//去除掉特別符號及字元, 參考：http://stackoverflow.com/questions/11484353/gson-throws-malformedjsonexception
			StringReader sr=new StringReader(json);
			JsonReader reader = new JsonReader(sr);


			reader.setLenient(true);
			o = gson.fromJson(reader, jsonType);

			//
			sr.close();
			sr=null;
			//
			reader.close();
			reader=null;
		} 
		catch (IOException e) 
		{
			
		}

		return o;
	}

	//	private static class ResDatasSerializer implements JsonSerializer<List<Map<String, Object>>> {
	//
	//		@Override
	//		public JsonElement serialize(List<Map<String, Object>> data, Type type, JsonSerializationContext jsonSerializationContext) {
	//			JsonObject elementContents = new JsonObject();
	//
	//			JsonArray elemData = new JsonArray();
	//
	//			for (Map<String, Object> row : data) {
	//				JsonObject elementRow = new JsonObject();
	//				String[] colums = row.keySet().toArray(new String[0]);
	//				for(String col : colums) {
	//					if (row.get(col) instanceof Integer) {
	////						elementRow.addProperty(col, this.getIntValue(row.get(col)));
	//						elementRow.add(col, new JsonPrimitive(this.getIntValue(row.get(col))));
	//						//System.out.println(col + "_int...");
	//					} else if (row.get(col) instanceof Double) {
	////						elementRow.addProperty(col, this.getDoubleValue(row.get(col)));
	//						elementRow.add(col, new JsonPrimitive(this.getDoubleValue(row.get(col))));
	//						//System.out.println(col + "double...");
	//					} else {
	//						elementRow.addProperty(col, this.getStringValue(row.get(col)));
	//						//System.out.println(col + "_String...");
	//					}
	//				}
	//				elemData.add(elementRow);
	//			}
	//			elementContents.add("datas", elemData);
	//
	//			return elementContents;
	//		}
	//
	//		private Integer getIntValue(Object obj) {
	//			if (obj == null) {
	//				return new Integer(0);
	//			} else {
	//				return new Integer(1);
	//			}
	//		}
	//
	//		private Double getDoubleValue(Object obj) {
	//			if (obj == null) {
	//				return new Double(0);
	//			} else {
	//				return new Double(1);
	//			}
	//		}
	//
	//		private String getStringValue(Object obj) {
	//			return obj == null ? "" : obj.toString();
	//		}
	//	}



	//	private static class JsonMasterSerializer implements JsonSerializer<JsonMaster> {
	//
	//		@Override
	//		public JsonElement serialize(JsonMaster jm, Type arg1, JsonSerializationContext jsc) {
	//			final JsonObject jo = new JsonObject();
	//			
	//			JsonArray detailResult = new JsonArray();
	//			//處理 detail.
	//			List<JsonDetail> details = jm.getDetails();
	//			for (JsonDetail jd : details) {
	//				
	//				JsonObject joDetail = new JsonObject();
	//				
	//				//設定 detail 的欄位
	//				joDetail.addProperty("AA", jd.getDetailName());
	//				
	//				//產生 content 的資料
	//				JsonArray result = new JsonArray();
	//				List<Map<String, String>> contents = jd.getContents();
	//				for (Map<String, String> row : contents) {
	//					JsonObject m = new JsonObject();
	//					String[] keys = row.keySet().toArray(new String[0]);
	//					for (String k : keys) {
	//						m.addProperty(k, row.get(k));
	//					}
	//					result.add(m);
	//				}
	//				joDetail.add("Content", result);
	//				detailResult.add(joDetail);
	//			}
	//			
	//
	//			jo.add("Detail", detailResult);
	////			return new JsonPrimitive(result.toString());
	//			return jo;
	//		}
	//	}
	//	
	//	public class JsonMasterDeserializer implements JsonDeserializer<JsonMaster> {
	//
	//		@Override
	//		public JsonMaster deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
	////			arg0.getAsJsonObject();
	//			return null;
	//		}
	//		
	//	}
}
