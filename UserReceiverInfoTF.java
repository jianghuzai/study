package com.business.pt.user.intf;

import com.business.consts.SysStaticDataEnum;
import com.business.pt.sys.intf.SysOperatorTF;
import com.business.pt.user.service.UserReceiverInfoSV;
import com.business.pt.user.service.UserSV;
import com.business.pt.user.vo.UserDataInfo;
import com.business.pt.user.vo.UserReceiverInfo;
import com.business.pt.user.vo.out.DriverAndReceiverInfoOut;
import com.framework.core.SysContexts;
import com.framework.core.inter.vo.Pagination;

import java.util.Date;

public class UserReceiverInfoTF implements IUserReceiverInfoTF {

    private UserReceiverInfoSV  userReceiverInfoSV;
    private UserReceiverInfoSV getUserReceiverInfoSV() {
        if (null == userReceiverInfoSV) {
            userReceiverInfoSV = (UserReceiverInfoSV) SysContexts.getBean("userReceiverInfoSV");
        }
        return userReceiverInfoSV;
    }

    private UserSV userSV;
    private UserSV getUserSV() {
        if (null == userSV) {
            userSV = (UserSV) SysContexts.getBean("userSV");
        }
        return userSV;
    }

    private SysOperatorTF sysOperatorTF;

    private SysOperatorTF getSysOperatorTF() {
        if (null == sysOperatorTF) {
            sysOperatorTF = (SysOperatorTF) SysContexts.getBean("sysOperatorTF");
        }
        return sysOperatorTF;
    }

    @Override
    public Pagination<DriverAndReceiverInfoOut> queryDriverAndReceiverInfo(String phone, String linkman) throws Exception {
        return getUserReceiverInfoSV().queryDriverAndReceiverInfo(phone, linkman, false);
    }

    @Override
    public Pagination<DriverAndReceiverInfoOut> queryDriverAndReceiverInfo(String phone, String linkman, boolean includeDriver) throws Exception {
        return getUserReceiverInfoSV().queryDriverAndReceiverInfo(phone, linkman, includeDriver);
    }

    @Override
    public UserReceiverInfo getUserReceiverInfo(String phone) {
        UserDataInfo userDataInfo = getUserSV().getUserDataInfoByMoblile(phone, false);
        if (null == userDataInfo) {
            return null;
        }
        return getUserReceiverInfoSV().getUserReceiverInfo(userDataInfo.getUserId());
    }

    @Override
    public UserReceiverInfo getUserReceiverInfo(long userId) {
        return getUserReceiverInfoSV().getUserReceiverInfo(userId);
    }

    @Override
    public void createUserReceiverInfo(String phone) throws Exception {
        createUserReceiverInfo(phone,"代收", "代收人");
    }

    @Override
    public void createUserRecevierInfo(String phone, String receiverName) throws Exception {
        createUserReceiverInfo(phone,receiverName, receiverName);
    }

    @Override
    public void createUserReceiverInfo(String phone, String receiverName, String linkman) throws Exception {
        UserDataInfo userDataInfo = getUserSV().getUserDataInfoByMoblile(phone, false);
        if (null == userDataInfo) {
            userDataInfo = new UserDataInfo();
            userDataInfo.setLinkman(linkman);
            userDataInfo.setMobilePhone(phone);
            userDataInfo.setSourceFlag(0);
            getUserSV().save(userDataInfo);

            getSysOperatorTF().insertSysOperator(phone, userDataInfo.getUserId(),linkman, 1, null, null,
                    SysStaticDataEnum.VERIFY_STS.UNAUTHORIZED, null);
        }

        UserReceiverInfo userReceiverInfo = new UserReceiverInfo();
        userReceiverInfo.setReceiverName(receiverName);
        userReceiverInfo.setOpId(SysContexts.getCurrentOperator().getOperId());
        userReceiverInfo.setCreateDate(new Date());
        userReceiverInfo.setUpdateDate(new Date());
        userReceiverInfo.setUserId(userDataInfo.getUserId());

        getUserReceiverInfoSV().save(userReceiverInfo);
    }

    @Override
    public void updateUserReceiverInfo(long userId, String receiverName) {
        UserReceiverInfo userReceiverInfo = getUserReceiverInfoSV().getUserReceiverInfo(userId);
        if (null == userReceiverInfo) {
            return;
        }

        userReceiverInfo.setReceiverName(receiverName);
        userReceiverInfo.setUpdateDate(new Date());
        getUserReceiverInfoSV().update(userReceiverInfo);
    }
}
