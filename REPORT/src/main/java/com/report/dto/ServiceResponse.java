package com.report.dto;

import java.io.Serializable;

/**
 * 统一服务响应封装
 * 格式：{"datas":{},"success":true,"serviceStatus":"000","serviceDescription":"xxx","sign":{}}
 */
public class ServiceResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回数据
     */
    private T datas;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 服务状态码 (000 表示成功)
     */
    private String serviceStatus;

    /**
     * 服务描述
     */
    private String serviceDescription;

    /**
     * 总记录数 (分页用)
     */
    private int totalRecords;

    /**
     * 总页数 (分页用)
     */
    private int totalPages;

    /**
     * 当前页码 (分页用)
     */
    private int pageNumber;

    /**
     * 每页大小 (分页用)
     */
    private int pageSize;

    /**
     * 签名信息
     */
    private SignInfo sign;

    public ServiceResponse() {
        this.success = true;
        this.serviceStatus = "000";
        this.serviceDescription = "服务执行成功";
    }

    public static <T> ServiceResponse<T> success(T datas) {
        ServiceResponse<T> response = new ServiceResponse<>();
        response.setDatas(datas);
        return response;
    }

    public static <T> ServiceResponse<T> success(T datas, String description) {
        ServiceResponse<T> response = new ServiceResponse<>();
        response.setDatas(datas);
        response.setServiceDescription(description);
        return response;
    }

    public static <T> ServiceResponse<T> error(String status, String description) {
        ServiceResponse<T> response = new ServiceResponse<>();
        response.setSuccess(false);
        response.setServiceStatus(status);
        response.setServiceDescription(description);
        return response;
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public SignInfo getSign() {
        return sign;
    }

    public void setSign(SignInfo sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "datas=" + datas +
                ", success=" + success +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", serviceDescription='" + serviceDescription + '\'' +
                ", sign=" + sign +
                '}';
    }
}
