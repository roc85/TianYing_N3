package com.xyxl.tianyingn3.database;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.io.Serializable;

/**
 * Created by rocgoo on 2017/11/9 下午1:45.
 * Function：联系人数据库
 */

public class Contact_DB extends SugarRecord implements Serializable{
//    @Column(name = "contract_ID", unique = true)
//    @Expose
//    private long contractId; //联系人id号

    @Expose
    private String contactName; //联系人姓名

    @Expose
    private String password; //联系人密码

    @Expose
    private String head; //联系人头像

    @Expose
    private String bdNum; //联系人北斗号

    @Expose
    private String phone; //联系人手机号

    @Expose
    private int contactType; //联系人类型

    @Expose
    private String contactOwner; //联系人所属

    @Expose
    private String remark; //联系人备注

//    public long getContractId() {
//        return contractId;
//    }
//
//    public void setContractId(long contractId) {
//        this.contractId = contractId;
//    }

    public String getContactOwner() {
        return contactOwner;
    }

    public void setContactOwner(String contactOwner) {
        this.contactOwner = contactOwner;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contractName) {
        this.contactName = contractName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBdNum() {
        return bdNum;
    }

    public void setBdNum(String bdNum) {
        this.bdNum = bdNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contractType) {
        this.contactType = contractType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
