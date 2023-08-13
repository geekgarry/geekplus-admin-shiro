package com.geekplus.webapp.system.mapper;

import com.geekplus.common.domain.LoginUser;
import com.geekplus.webapp.system.entity.SysUser;

import java.util.List;

/**
 * 系统用户表 系统用户表
 * Created by CodeGenerator on 2023/06/18.
 */

public interface SysUserMapper {

    /**
    * 增加
    * @param sysUser
    * @return 系统用户表
    */
    Integer insertSysUser(SysUser sysUser);

    /**
    * 批量增加
    * @param sysUserList
    * @return
    */
    public int batchInsertSysUserList(List<SysUser> sysUserList);

    /**
    * 删除
    * @param userId
    */
    Integer deleteSysUserById(Long userId);

    /**
    * 批量删除
    */
    Integer deleteSysUserByIds(Long[] userIds);

    /**
    * 修改
    * @param sysUser
    */
    Integer updateSysUser(SysUser sysUser);

    Integer updateSysUserByUserName(SysUser sysUser);

    /**
    * 批量修改魔偶几个字段
    * @param userIds
    */
    Integer batchUpdateSysUserList(Long[] userIds);

    /**
    * 查询全部
    */
    List<SysUser> selectSysUserList(SysUser sysUser);

    /**
     * 查询全部
     */
    SysUser selectSysUserByPassword(SysUser sysUser);

    /**
    * 查询全部,联合查询使用
    */
    List<SysUser> selectUnionSysUserList(SysUser sysUser);

    /**
    * 根据Id查询单条数据
    */
    SysUser selectSysUserById(Long userId);

    /** 根据用户名查询用户的角色和菜单*/
    //List<Map<String,Object>> getRoleMenuByUsername(SysUser sysUser);

    LoginUser selectSysUser(SysUser sysUser);

    Integer updateUserAvatar(SysUser sysUser);
}
