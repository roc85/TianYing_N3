package com.xyxl.tianyingn3.database;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.xyxl.tianyingn3.bean.BdCardBean;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

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

    /***************************************************************************************************/

    /**
     * Gets id via address.
     *
     * @param num the num
     * @return the id via address
     */
    public static long getIdViaAddress(String num)
    {
        num = BdCardBean.FormatCardNum(num);
        List<Contact_DB> tmpList = Contact_DB.find(Contact_DB.class,"bd_Num = ? ",num);
        if(tmpList.size()>0)
        {
            return tmpList.get(0).getId();
        }
        else
        {
            return -1;
        }
    }

    /**
     * Gets id via phone.
     *
     * @param num the num
     * @return the id via phone
     */
    public static long getIdViaPhone(String num)
    {
        num = BdCardBean.FormatCardNum(num);
        List<Contact_DB> tmpList = Contact_DB.find(Contact_DB.class,"phone = ? ",num);
        if(tmpList.size()>0)
        {
            return tmpList.get(0).getId();
        }
        else
        {
            return -1;
        }
    }


    /**
     * Gets id via name.
     *
     * @param name the name
     * @return the id via name
     */
    public static long getIdViaName(String name)
    {
        List<Contact_DB> tmpList = Contact_DB.find(Contact_DB.class,"contact_Name = ?",name);
        if(tmpList.size()>0)
        {
            return tmpList.get(0).getId();
        }
        else
        {
            return -1;
        }
    }


    /**
     * Gets name via id.
     *
     * @param _id         the id
     * @param defaultName the default name
     * @return the name via id
     */
    public static String getNameViaId(long _id, String defaultName) {
        if (_id < 0) {
            return defaultName;
        } else {
            try {
                Contact_DB tmp = Contact_DB.findById(Contact_DB.class, _id);
                if (tmp == null) {
                    return defaultName;
                } else {
                    return tmp.getContactName();
                }
            } catch (Exception e) {
                return defaultName;
            }

        }

    }

    //重复判断
    public static boolean HaveSameNum(String num)
    {
        if(TextUtils.isEmpty(num))
        {
            return false;
        }
        return Contact_DB.find(Contact_DB.class,"bd_Num = ?",BdCardBean.FormatCardNum(num)).size()>0?true:false;
    }
    public static boolean HaveSameName(String name)
    {
        if(TextUtils.isEmpty(name))
        {
            return false;
        }
        return Contact_DB.find(Contact_DB.class,"contact_Name = ?",name).size()>0?true:false;
    }
    public static boolean HaveSamePhone(String phone)
    {
        if(TextUtils.isEmpty(phone))
        {
            return false;
        }
        return Contact_DB.find(Contact_DB.class,"phone = ?",phone).size()>0?true:false;
    }
}
