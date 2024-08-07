package com.imwj.design.controller;

import com.imwj.design.domain.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wj
 * @create 2023-06-05 16:27
 */
public class HelloWorldController {

    public UserInfo queryUserInfo( String userId) {

        // 做白名单拦截
        List<String> userList = new ArrayList<String>();
        userList.add("1001");
        userList.add("aaaa");
        userList.add("ccc");
        if (!userList.contains(userId)) {
            return new UserInfo("1111", "非白名单可访问用户拦截！");
        }

        return new UserInfo("虫虫:" + userId, 19, "天津市南开区旮旯胡同100号");
    }
}
