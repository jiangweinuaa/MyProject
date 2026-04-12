package com.dsc.spos.waimai.jddj;
import java.util.List;

public class OResultList {

  private int pageNo;
  private int pageSize;
  private int maxPageSize;
  private long totalCount;
  private List<OrderInfoDTO> resultList;
  private int totalPage;
  private int page;
 
  
  public void setPageNo(int pageNo) {
       this.pageNo = pageNo;
   }
   public int getPageNo() {
       return pageNo;
   }

  public void setPageSize(int pageSize) {
       this.pageSize = pageSize;
   }
   public int getPageSize() {
       return pageSize;
   }

  public void setMaxPageSize(int maxPageSize) {
       this.maxPageSize = maxPageSize;
   }
   public int getMaxPageSize() {
       return maxPageSize;
   }

  public void setTotalCount(long totalCount) {
       this.totalCount = totalCount;
   }
   public long getTotalCount() {
       return totalCount;
   }

  public void setResultList(List<OrderInfoDTO> resultList) {
       this.resultList = resultList;
   }
   public List<OrderInfoDTO> getResultList() {
       return resultList;
   }

  public void setTotalPage(int totalPage) {
       this.totalPage = totalPage;
   }
   public int getTotalPage() {
       return totalPage;
   }

  public void setPage(int page) {
       this.page = page;
   }
   public int getPage() {
       return page;
   }

}