/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 7/15/23 08:31
 * description: 做什么的？
 */
package com.geekplus.webapp.common.monitor.controller;

import com.geekplus.common.annotation.Log;
import com.geekplus.common.constant.Constant;
import com.geekplus.common.core.controller.BaseController;
import com.geekplus.common.domain.LoginUser;
import com.geekplus.common.domain.Result;
import com.geekplus.common.enums.BusinessType;
import com.geekplus.common.page.PageDataInfo;
import com.geekplus.common.redis.RedisUtil;
import com.geekplus.common.util.StringUtils;
import com.geekplus.webapp.common.monitor.entity.SysUserOnline;
import com.geekplus.webapp.common.monitor.service.ISysUserOnlineService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 在线用户监控
 *
 * @author
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisUtil redisUtil;

    @RequiresPermissions("monitor:online:list")
    @GetMapping("/list")
    public PageDataInfo list(String ipaddr, String userName)
    {
        Collection<String> keys = redisUtil.keys(Constant.LOGIN_USER_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            Session session = (Session) redisUtil.get(key);
            String sessionId=session.getId().toString();
            LoginUser user = new LoginUser();
            user.setLoginIp(session.getAttribute("loginIp").toString());
            user.setUserName(session.getAttribute("userName").toString());
            user.setLoginTime((Date) session.getAttribute("loginTime"));
            user.setBrowser(session.getAttribute("browser").toString());
            user.setOs(session.getAttribute("os").toString());
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(ipaddr, user.getLoginIp()) && StringUtils.equals(userName, user.getUserName()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(sessionId, ipaddr, userName, user));
                }
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                if (StringUtils.equals(ipaddr, user.getLoginIp()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(sessionId, ipaddr, user));
                }
            }
            else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user))
            {
                if (StringUtils.equals(userName, user.getUserName()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(sessionId, userName, user));
                }
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(sessionId, user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 强退用户
     */
    //@RequiresPermissions("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public Result forceLogout(@PathVariable String tokenId)
    {
        redisUtil.del(Constant.LOGIN_USER_KEY + tokenId);
        return Result.success();
    }
}
