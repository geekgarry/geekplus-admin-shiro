/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 6/19/23 02:32
 * description: 做什么的？
 */
package com.geekplus.common.core.controller;

import com.geekplus.common.constant.HttpStatus;
import com.geekplus.common.domain.Result;
import com.geekplus.common.page.PageData;
import com.geekplus.common.page.PageDataInfo;
import com.geekplus.common.page.TableDataSupport;
import com.geekplus.common.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class BaseController {

    public static void startPage(){
        PageData pageDomain = TableDataSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtil.isNotNull(pageNum) && StringUtil.isNotNull(pageSize))
        {
//            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize);
        }
//        Integer pageNum=ServletUtil.getParameterToInt("pageNum");
//        Integer pageSize=ServletUtil.getParameterToInt("pageSize");
//        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected PageDataInfo getDataTable(List<?> list)
    {
        PageDataInfo rspData = new PageDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected Result toResult(int rows)
    {
        return rows > 0 ? Result.success() : Result.error();
    }
}
