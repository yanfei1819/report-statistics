package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * Created by chengtianren on 2017/8/22.
 */
public class TmsSmsReceiverVO implements Serializable {

    private static final long serialVersionUID = -1631848498609575684L;

    private String name;
    private String mobile;

    public TmsSmsReceiverVO() {
    }

    public TmsSmsReceiverVO(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


}
