package com.bilibili.utils;

/**
 * @author xck
 */
public class Constant {

    public static final String PATH = System.getProperty("user.dir") + "/files/";

    public static String USER_REDIS_PREFIX = "user:";

    public static String USER_DEFAULT_PASSWORD = "123456";

    public static String REGISTER_SUCCESS = "注册成功";

    public static String PHONE_EXISTS_ERROR = "手机号已存在";
    public static String PHONE_ERROR = "请输入正确的手机号";
    public static String PHONE_NOT_INPUT_ERROR = "请输入手机号";

    public static String USER_EXISTS_ERROR = "用户名已存在";

    public static String SMS_ERROR = "验证码错误或失效";
    public static String SMS_NOT_FAILURE_ERROR = "短信还未失效, 请稍后重试";
    public static String SMS_SUCCESS = "发送成功";

    public static String LOGIN_ERROR = "用户名或密码错误";

    public static String FILE_NOT_FOUND = "文件不存在";


    public static Integer VIDEO_TYPE = 1;
    public static Integer DYNAMIC_TYPE = 0;
    public static Integer TEXT_TYPE = 2;

    public static String SEARCH_USER_PREFIX = "ID:";

    public static Integer LAST = 1;
    public static Integer NOT_LAST = 0;


    public static Integer LINK_TYPE = 1;
    public static Integer SEND_TEXT_TYPE = 2;
    public static Integer SEND_IMAGE_TYPE = 3;

    public static Integer NOT_PUBLIC_FAVORITES = 0;
    public static Integer PUBLIC_FAVORITES = 1;

    public static Integer DELETE = 1;
    public static Integer NOT_DELETE = 0;

}
