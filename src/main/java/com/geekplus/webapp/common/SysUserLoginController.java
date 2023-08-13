/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 6/18/23 23:01
 * description: 做什么的？
 */
package com.geekplus.webapp.common;

import com.geekplus.common.constant.Constant;
import com.geekplus.common.core.controller.BaseController;
import com.geekplus.common.domain.LoginUser;
import com.geekplus.common.domain.Result;
import com.geekplus.common.enums.ApiExceptionEnum;
import com.geekplus.common.myexception.ApiException;
import com.geekplus.common.util.IPUtils;
import com.geekplus.common.util.LogUtil;
import com.geekplus.common.redis.RedisUtil;
import com.geekplus.common.util.ServletUtil;
import com.geekplus.webapp.system.entity.SysMenu;
import com.geekplus.webapp.system.service.LoginUserTokenService;
import com.geekplus.webapp.system.service.SysLoginLogService;
import com.geekplus.webapp.system.service.SysMenuService;
import com.geekplus.webapp.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserLoginController extends BaseController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private RedisUtil redisUtil;

    @Value("${geekplus.name}")
    private String appName;

    @PostMapping("/login")
    public Result login(@RequestBody LoginUser loginUser){
        //添加用户认证信息
        Subject subject = SecurityUtils.getSubject();
        String validateCode=loginUser.getValidateCode();
        String validateKey=loginUser.getValidateKey();
        //log.info("数据："+validateKey+"验证码："+validateCode);
        //自己系统的密码加密方式 ,这里简单示例一下MD5
        //String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        //Object salt=ByteSource.Util.bytes("userId");
        //Object md5Password=new SimpleHash("md5", password, salt, 1024);
        if(validateCode==null||validateCode.equals("")){
            LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL, ApiExceptionEnum.CODE_IS_NULL.getMsg());
            throw new ApiException(ApiExceptionEnum.CODE_IS_NULL);
        }else if(redisUtil.get(validateKey)==null||redisUtil.get(validateKey).equals("")){
            LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL,ApiExceptionEnum.CODE_IS_EXPIRE.getMsg());
            throw new ApiException(ApiExceptionEnum.CODE_IS_EXPIRE);
        }
        UsernamePasswordToken upToken = new UsernamePasswordToken(loginUser.getUserName(), loginUser.getPassword());
        if(validateCode==redisUtil.get(validateKey)||validateCode.equalsIgnoreCase(redisUtil.get(validateKey).toString())){
            //else if(sysUser.getPassword().equals(sysUser1.getPassword())){
            //  LogUtil.recordLoginInfo(sysUser,ConstValue.LOGIN_FAIL,ApiExceptionEnum.LOGIN_USERNAME_ERROR.getMsg());
            //  throw new ApiException(ApiExceptionEnum.LOGIN_USERNAME_ERROR);
            //}
//            SysUser sysUser1 = sysUserService.selectUserBy(sysUser);
//            if(sysUser1.getStatus().equals("0")){
//                LogUtil.recordLoginInfo(sysUser, ConstValue.LOGIN_FAIL,ApiExceptionEnum.LOGIN_DISABLED_ERROR.getMsg());
//                throw new ApiException(ApiExceptionEnum.LOGIN_DISABLED_ERROR);
//            }
            try {
                sysUserService.updateSysUserByUserName(loginUser);
                //进行验证，AuthenticationException可以catch到,但是AuthorizationException因为我们使用注解方式,是catch不到的,所以后面使用全局异常捕抓去获取
                subject.login(upToken);
                //    final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getRequest().getHeader("User-Agent"));
                String ip = IPUtils.getIp(ServletUtil.getRequest());
                loginUser.setLoginIp(ip);
                LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_SUCCESS,"登录成功");
                redisUtil.del(validateKey);
            } catch (UnknownAccountException e) {
//                subject.logout();
                LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL,ApiExceptionEnum.LOGIN_USERNAME_ERROR.getMsg());
                e.printStackTrace();
                log.info("打印异常信息{}",e.getMessage());
                throw new ApiException(ApiExceptionEnum.LOGIN_USERNAME_ERROR);
            } catch (DisabledAccountException e) {
//                subject.logout();
                LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL,ApiExceptionEnum.LOGIN_DISABLED_ERROR.getMsg());
                e.printStackTrace();
                log.info("打印异常信息{}",e.getMessage());
                throw new ApiException(ApiExceptionEnum.LOGIN_DISABLED_ERROR);
            } catch (IncorrectCredentialsException e){
                LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL,ApiExceptionEnum.LOGIN_PASSWORD_ERROR.getMsg());
                e.printStackTrace();
                log.info("打印异常信息{}",e.getMessage());
                throw new ApiException(ApiExceptionEnum.LOGIN_PASSWORD_ERROR);
            } catch (AuthenticationException e) {
//                subject.logout();
                LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL,ApiExceptionEnum.LOGIN_FAIL.getMsg());
                e.printStackTrace();
                throw new ApiException(ApiExceptionEnum.LOGIN_FAIL);
            }
        }else {
            LogUtil.recordLoginInfo(loginUser, Constant.LOGIN_FAIL,ApiExceptionEnum.CODE_ERROR.getMsg());
            throw new ApiException(ApiExceptionEnum.CODE_ERROR);
        }
        //SysUser sUser = (SysUser)subject.getPrincipal();
        log.info("主机:"+subject.getSession());
        Map<String, Object> res = new HashMap<>();
        res.put("token", subject.getSession().getId());
        //LoginUser sysUser = (LoginUser) subject.getSession().getAttribute("userInfo");
        res.put("userName", subject.getSession().getAttribute("userName"));
        //res.put("nickName", subject.getSession().getAttribute("nickName"));
        return Result.success(res);
    }

    @GetMapping("/getMenu")
    public Result getMenuList(){
        LoginUser sysUser= (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser==null||sysUser.equals("")){
            return Result.error(ApiExceptionEnum.NO_USER_ID);
        }
        Map<String,Object> map=new HashMap<>();
        log.info("=========================>"+sysUser.getUserId());
        List<SysMenu> menuList=sysMenuService.getMenuTreeByUserId(sysUser.getUserId());
        map.put("userName", sysUser.getUserName());
        map.put("nickName", sysUser.getNickName());
        map.put("userId", sysUser.getUserId());
        map.put("avatar", sysUser.getAvatar());
        map.put("menuList", menuList);
        return Result.success(map);
    }

    /* 登出操作 */
    @PostMapping("/logout")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
        return Result.success();
    }

    /**
     * 未登录
     */
    @RequestMapping(value = "/unLogin", method = RequestMethod.GET)
    public void notLogin() {
        throw new ApiException(ApiExceptionEnum.LOGIN_AUTH);
    }

    /**
     * 无权限访问
     */
    @RequestMapping(value = "/unAuth", method = RequestMethod.GET)
    public void notRole() {
        throw new ApiException(ApiExceptionEnum.PERMISSION);
    }
}
