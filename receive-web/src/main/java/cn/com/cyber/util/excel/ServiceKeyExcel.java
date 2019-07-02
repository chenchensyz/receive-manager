package cn.com.cyber.util.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import javax.validation.constraints.NotNull;

public class ServiceKeyExcel extends BaseRowModel {

    @ExcelProperty(value = "接口后缀", index = 0)
    private String urlSuffix;

    @ExcelProperty(value = "请求方法", index = 1)
    private String method;

    @ExcelProperty(value = "请求编码格式", index = 2)
    private String contentType;

    @ExcelProperty(value = "接口名称", index = 3)
    private String serviceName;

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "ServiceKeyExcel{" +
                "urlSuffix='" + urlSuffix + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", method='" + method + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
