package com.dsc.spos.waimai.dada;

public class DadaBaseModel {
	
  private String source_id;
  private String v;
  private String format;
  private String app_key;
  private String app_secret;
  private String body;
  private String timestamp;
  private String signature;
  
  public void setSource_id(String source_id) {
       this.source_id = source_id;
   }
   public String getSource_id() {
       return source_id;
   }

  public void setV(String v) {
       this.v = v;
   }
   public String getV() {
       return v;
   }

  public void setFormat(String format) {
       this.format = format;
   }
   public String getFormat() {
       return format;
   }

  public void setApp_key(String app_key) {
       this.app_key = app_key;
   }
   public String getApp_key() {
       return app_key;
   }

  public void setBody(String body) {
       this.body = body;
   }
   public String getBody() {
       return body;
   }

  public void setTimestamp(String timestamp) {
       this.timestamp = timestamp;
   }
   public String getTimestamp() {
       return timestamp;
   }

  public void setSignature(String signature) {
       this.signature = signature;
   }
   public String getSignature() {
       return signature;
   }
public String getApp_secret()
{
	return app_secret;
}
public void setApp_secret(String app_secret)
{
	this.app_secret = app_secret;
}
	
	
}
