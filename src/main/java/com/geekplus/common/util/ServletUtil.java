package com.geekplus.common.util;

import com.geekplus.common.core.text.Convert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 客户端工具类
 *
 * @Author: geekplus
 * @CreateTime: 2022
 */
public class ServletUtil {
    /**
     * 请求格式为json
     */
    private static final String APPLICATION_JSON = "application/json";
    /**
     * 请求格式为xml
     */
    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    /**
     * json后缀
     */
    private static final String JSON_SUFFIX = ".json";
    /**
     * xml后缀
     */
    private static final String XML_SUFFIX = ".xml";
    /**
     * 索引越界
     */
    private static final int INDEX_NOT_FOUND = -1;


    /**
     * 获取String参数
     *
     * @param name 参数名
     * @return String 参数值
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return String 参数值
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     *
     * @param name 参数名
     * @return Integer 参数值
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name), null);
    }

    /**
     * 获取Integer参数
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return Integer 参数值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取Attributes
     *
     * @return ServletRequestAttributes
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setContentType(APPLICATION_JSON);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request 客户端请求
     * @return boolean
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains(APPLICATION_JSON)) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains(XML_HTTP_REQUEST)) {
            return true;
        }

        String uri = request.getRequestURI();
        if (Convert.inStringIgnoreCase(uri, JSON_SUFFIX, XML_SUFFIX)) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        return Convert.inStringIgnoreCase(ajax, "json", "xml");
    }
}
