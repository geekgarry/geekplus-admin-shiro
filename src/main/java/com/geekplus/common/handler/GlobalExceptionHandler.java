package com.geekplus.common.handler;

import com.geekplus.common.enums.ApiExceptionEnum;
import com.geekplus.common.myexception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 * author     : geekplus
 * email      : geekcjj@gmail.com
 * date       : 2022/4/24 10:56 下午
 * description: 做什么的？
 */
//拦截异常注解 @ControllerAdvice表明这时一个异常处理类
//@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object handleHttpException(ServletException e) {
        log.error("ServletException: ", e);
        return new ApiException(ApiExceptionEnum.FAIL, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e) {
        log.error("Default Exception: ", e+"GEEKPLUS");
        return new ApiException(ApiExceptionEnum.SERVICE_ERROR);
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public Object handleFirstMallException(ApiException e) {
        log.error("ApiException: ", e);
        return new ApiException(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiException handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);
        return handleBindingResult(e.getBindingResult());
    }
    //参数类型错误
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: ", e);
        return new ApiException(ApiExceptionEnum.PARAM_ERROR, "参数"+e.getName() + ":" + "字段类型错误");
    }
    //参数缺少
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException: ", e);
        return new ApiException(ApiExceptionEnum.PARAM_ERROR, e.getMessage());
    }

    private ApiException handleBindingResult(BindingResult result) {
        //把异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError objectError : allErrors) {
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size() == 0) {
            return new ApiException(ApiExceptionEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        return new ApiException(ApiExceptionEnum.ILLEGAL_CALLBACK_REQUEST_ERROR, list.toString());
    }
}
