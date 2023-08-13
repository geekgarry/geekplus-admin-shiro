/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 2022/4/26 10:21 下午
 * description: 做什么的？
 */
package com.geekplus.webapp.system.service;

import com.geekplus.common.domain.LoginUser;
import com.geekplus.common.util.ServletUtil;
import com.geekplus.webapp.system.entity.SysRole;
import com.geekplus.webapp.system.entity.SysUser;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class LoginUserTokenService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysRoleService sysRoleService;

    public SysUser getUserInfo(){
        //Session session= SecurityUtils.getSubject().getSession();
        LoginUser sysUserInfo = (LoginUser) SecurityUtils.getSubject().getPrincipal();;
        if(sysUserInfo.getUserId()!=null){
            //Long userId=Long.parseLong(session.getAttribute("userId").toString());
            SysUser sysUser = sysUserService.selectSysUserById(sysUserInfo.getUserId());
            List<SysRole> sysRoles=sysRoleService.getRolesByUserId(sysUserInfo.getUserId().toString());
            sysUser.setSysRoleList(sysRoles);
            return sysUser;
        }
        return null;
    }

    /**
     *设置用户代理相关请求头信息，浏览器和操作系统
     */
    public void setUserAgent(LoginUser loginUser){
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getRequest().getHeader("User-Agent"));
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        loginUser.setBrowser(browser);
        loginUser.setOs(os);
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(){
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
    }

//    public SysUser updateUserInfo(SysUser sysUser){
//        SysUser sysUser1=getUserInfo();
//    }

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
//    public String login(String username, String password, String code, String uuid)
//    {
//        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
//        String captcha = redisCache.getCacheObject(verifyKey);
//        redisCache.deleteObject(verifyKey);
//        if (captcha == null)
//        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
//            throw new CaptchaExpireException();
//        }
//        if (!code.equalsIgnoreCase(captcha))
//        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
//            throw new CaptchaException();
//        }
//        // 用户验证
//        Authentication authentication = null;
//        try
//        {
//            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
//            authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        }
//        catch (Exception e)
//        {
//            if (e instanceof BadCredentialsException)
//            {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
//                throw new UserPasswordNotMatchException();
//            }
//            else
//            {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
//                throw new CustomException(e.getMessage());
//            }
//        }
//        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
//        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
//        // 生成token
//        return tokenService.createToken(loginUser);
//    }
}
