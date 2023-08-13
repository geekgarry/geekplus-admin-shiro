package com.geekplus.webapp.common.monitor.service.impl;

import com.geekplus.common.domain.LoginUser;
import com.geekplus.common.util.AddressUtil;
import com.geekplus.common.util.ServletUtil;
import com.geekplus.common.util.StringUtils;
import com.geekplus.webapp.common.monitor.entity.SysUserOnline;
import com.geekplus.webapp.common.monitor.service.ISysUserOnlineService;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;

/**
 * 在线用户 服务层处理
 *
 * @author
 */
@Service
public class SysUserOnlineServiceImpl implements ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByIpaddr(String sessionId,String ipaddr, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getLoginIp()))
        {
            return loginUserToUserOnline(sessionId,user);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByUserName(String sessionId,String userName, LoginUser user)
    {
        if (StringUtils.equals(userName, user.getUserName()))
        {
            return loginUserToUserOnline(sessionId,user);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     *
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByInfo(String sessionId,String ipaddr, String userName, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getLoginIp()) && StringUtils.equals(userName, user.getUserName()))
        {
            return loginUserToUserOnline(sessionId,user);
        }
        return null;
    }

//    final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getRequest().getHeader("User-Agent"));
//    final String ip = IPUtils.getIp(ServletUtil.getRequest());
//    String address = AddressUtil.getRealAddressByIP(ip);
//    StringBuilder s = new StringBuilder();
//        s.append(getBlock(ip));
//        s.append(address);
//        s.append(getBlock(loginUser.getUserName()));
//        s.append(getBlock(status));
//        s.append(getBlock(msg));
//    // 打印信息到日志
//        log.info(s.toString(), msg);
//    // 获取客户端操作系统
//    String os = userAgent.getOperatingSystem().getName();
//    // 获取客户端浏览器
//    String browser = userAgent.getBrowser().getName();

    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    @Override
    public SysUserOnline loginUserToUserOnline(String sessionId,LoginUser user)
    {
        //UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getRequest().getHeader("User-Agent"));
        String ip=user.getLoginIp();
        String address = AddressUtil.getRealAddressByIP(ip);
        //StringBuilder s = new StringBuilder();
        // 获取客户端操作系统
        //String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        //String browser = userAgent.getBrowser().getName();
        if (StringUtils.isNull(user) || StringUtils.isNull(user))
        {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(sessionId);
        sysUserOnline.setUserName(user.getUserName());
        sysUserOnline.setIpaddr(ip);
        sysUserOnline.setLoginLocation(address);
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime().getTime());
        if (StringUtils.isNotNull(user))
        {
            sysUserOnline.setDeptName(user.getDeptName());
        }
        return sysUserOnline;
    }
}
