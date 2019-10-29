package com.offcn.sellergoods.service;

import com.offcn.pojo.TbUser;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/10/26 15:03
 */
public interface UserService {

    TbUser findOne(String username);
}
