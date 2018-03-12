package com.yudianbank.tms.model.vo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by chengtianren on 2017/8/27.
 */
public class SendSmsVO implements Serializable {

    private static final long serialVersionUID = -5162357364166498862L;

    private String functionCode; // 功能点
    private String productCode; // 产品
    private String partnerNo; // 合作方
    private Object[] variable; // 参数清单

    public Object[] getVariable() {
        return variable;
    }

    public void setVariable(Object[] variable) {
        this.variable = variable;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    @Override
    public String toString() {
        return "SendSmsVO{" +
                "functionCode='" + functionCode + '\'' +
                ", productCode='" + productCode + '\'' +
                ", partnerNo='" + partnerNo + '\'' +
                ", variable=" + Arrays.toString(variable) +
                '}';
    }
}
