package com.geekplus.framework.shiro;

import com.geekplus.common.domain.LoginUser;
import com.geekplus.common.util.ServletUtil;
import com.geekplus.webapp.system.entity.SysMenu;
import com.geekplus.webapp.system.entity.SysRole;
import com.geekplus.webapp.system.entity.SysUser;
import com.geekplus.webapp.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 2022/4/24 3:53 下午
 * description: 做什么的？
 */
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Value("${geekplus.name}")
    private String appName;

    @Value("${token.expireTime}")
    private long expireTime;

    @Resource
    private LoginUserTokenService userTokenService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisSessionDao redisSessionDao;

    // 确保每个用户一个session
    protected final ConcurrentHashMap<Object, Object> sessionUserMap = new ConcurrentHashMap<Object, Object>();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        LoginUser sysUser = (LoginUser) principalCollection.getPrimaryPrincipal();
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<SysRole> roleList=(List<SysRole>) ServletUtil.getSession().getAttribute("roles");
        List<SysMenu> menuList=(List<SysMenu>) ServletUtil.getSession().getAttribute("menus");

        //Set roleSet=userRolePermList.stream().map(map->map.get("roleKey").toString()).collect(Collectors.toSet());
        //Set permSet=userRolePermList.stream().map(map->map.get("permName").toString()).collect(Collectors.toSet());
        Set roleSet2=roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
        Set permSet2=menuList.stream().map(SysMenu::getPerms).collect(Collectors.toSet());
        //permSet2.remove("");
        simpleAuthorizationInfo.addRoles(roleSet2);
        simpleAuthorizationInfo.addStringPermissions(permSet2);
        log.info("验证当前Subject用户为：{} 所属角色：{} ||| {}", sysUser.getUserName(),roleSet2,permSet2);
//        userRolePermList.stream().forEach(rolePermMap -> {
//        });
//        numbersList.stream().distinct().collect(Collectors.toList());
//        for (Map<String, Object> rolePermMap : userRolePermList) {
//            //添加角色
//            simpleAuthorizationInfo.addRole(rolePermMap.get("roleKey").toString());
//            //添加权限
//            simpleAuthorizationInfo.addStringPermission(rolePermMap.get("permName").toString());
//        }
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("####################### 执行Shiro权限认证 #####################");
        // 第一种方式
        // 获取用户输入的账号和密码(一般只需要获取账号就可以)
        String oldSessionName = (String)authenticationToken.getPrincipal();
        String username = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());
        //System.out.println("oldSession:"+oldSessionName);
        System.out.println("用户："+username+"密码："+password);
        // 第二种方式（推荐第一种）c
        // UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        // String username = usernamePasswordToken.getUsername();
        // String password = new String(usernamePasswordToken.getPassword());
        SysUser sUser=new SysUser();
        sUser.setUserName(username);
        //sUser.setPassword(password);不需要密码查询，先用用户名查询出来数据，获得密码，然后和传入的密码交给shiro去做一个验证
        System.out.println("*************###################***************");
        // 通过username从数据库中查找 User对象，如果找到则进行验证
        LoginUser sysUser = sysUserService.selectUserBy(sUser);
        userTokenService.setUserAgent(sysUser);
        // 判断账号是否存在
        if(sysUser == null){
            throw new UnknownAccountException();
        }
        if(sysUser.getStatus().equals("1")){// 用户状态为1，表示该用户被禁用了
            throw new DisabledAccountException();
        }
        // 用户互踢
        Session session = SecurityUtils.getSubject().getSession();
        //String newsesId = session.getId().toString();
        /** session共享，账号互踢 */
        //String singleSessionId = redisTemplate.opsForValue().get(redisSessionDao.);
        if(sessionUserMap.get(oldSessionName) != null) {
            Session oldSession = (Session)sessionUserMap.get(oldSessionName);
            // 移除存储的之前用户名和sesion的键值对
            sessionUserMap.remove(oldSessionName);
            // 销毁之前session
            redisSessionDao.delete(oldSession);
            // 确保每次登录之后将将用户名和session存进hashmap中
            //sessionUserMap.put(username, session);
        }
        // 确保每次登录之后将将用户名和session存进hashmap中
        sessionUserMap.put(username, session);
//        if (!StringUtils.isEmpty(oldsesid) && !newsesId.equals(oldsesid)) {
//            Session oldSession = (Session) redisTemplate.opsForValue().get(oldsesid);
//            try {
//                redisSessionDao.delete(oldSession);
//                oldSession.stop();
//            } catch (Exception e) {
//                log.info("{} session stop success", session);
//            }
//            redisTemplate.delete(oldsesid);
//            log.debug("del session,oldsessionid:{}, newsessionid:{}", oldsesid, newsesId);
//        }
        session.setTimeout(expireTime*1000);// 半个小时
        // 进行验证
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                // 用户名
                sysUser,
                // 密码
                sysUser.getPassword(),
                // 盐值
                ByteSource.Util.bytes("plus"),//sysUser.getUserId().toString()
                getName()
        );
        log.info("***************************##########****************************");
        List<SysRole> roles = sysRoleService.getRolesByUserId(sysUser.getUserId().toString());
        List<SysMenu> menus = sysMenuService.getSysMenuByRoles(roles);
        sysUser.setSysRoleList(roles);
        Set set = new HashSet();
        set.addAll(menus);
        menus.clear();
        menus.addAll(set);
        //session.setAttribute("roleName", roles.stream().map(SysRole::getRoleName).collect(Collectors.toSet()));
        session.setAttribute("roles", roles);
        //session.setAttribute("menuName", menus.stream().map(SysMenu::getMenuName).collect(Collectors.toSet()));
        session.setAttribute("menus", menus);
        session.setAttribute("userId", sysUser.getUserId());
        session.setAttribute("userName", sysUser.getUserName());
        session.setAttribute("nickName", sysUser.getNickName());
        session.setAttribute("loginIp", sysUser.getLoginIp());
        session.setAttribute("loginTime", sysUser.getLoginTime());
        session.setAttribute("browser",sysUser.getBrowser());
        session.setAttribute("os",sysUser.getOs());
//        session.setAttribute("userInfo",sysUser);
        //redisTemplate.opsForValue().set(appName + ":user:" +sysUser.getUserName(), newsesId, expireTime, TimeUnit.SECONDS);
        log.info("SysUser:"+authenticationInfo.getPrincipals()+"Info:"+authenticationInfo.getPrincipals().getPrimaryPrincipal());
        return authenticationInfo;
    }

    public static void main(String[] args) {
        String hashAlgorithmName = "MD5";
        String credentials = "123456";
        int hashIterations = 1024;
        ByteSource credentialsSalt = ByteSource.Util.bytes("plus");
        Object obj = new SimpleHash(hashAlgorithmName, credentials, credentialsSalt, hashIterations);
        System.out.println(obj);
    }
}
