package com.yudianbank.tms.controller;

import com.yudianbank.tms.model.vo.ErrorInfo;
import com.yudianbank.tms.model.vo.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一的异常处理类
 * 即把@ControllerAdvice注解内部使用@ExceptionHandler、@InitBinder、@ModelAttribute注解的方法
 * 应用到所有的 @RequestMapping注解的方法。
 *
 * @author Song Lea
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ErrorInfo httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String url = getRequestUri();
        LOGGER.warn("请求的方式不对(POST/GET)  接口地址:{},异常内容:{}", url, ex);
        return ResponseData.ExceptionEnum.EXCEPTION_METHOD_NOT_SUPPORTED.getResult(url);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public ErrorInfo servletRequestBindingExceptionHandler(ServletRequestBindingException ex) {
        String url = getRequestUri();
        LOGGER.warn("请求的参数不完整  接口地址:{},异常内容:{}", url, ex);
        return ResponseData.ExceptionEnum.EXCEPTION_LACK_PARAMETER.getResult(url);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ErrorInfo methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String url = getRequestUri();
        LOGGER.warn("请求方法参数格式不匹配  接口地址:{},异常内容:{}", url, ex);
        return ResponseData.ExceptionEnum.EXCEPTION_ARGUMENT_TYPE_MISMATCH.getResult(url);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ErrorInfo httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String url = getRequestUri();
        LOGGER.warn("请求的MIME类型不支持  接口地址:{},异常内容:{}", url, ex);
        return ResponseData.ExceptionEnum.EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED.getResult(url);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ErrorInfo httpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String url = getRequestUri();
        LOGGER.warn("请求的MINE类型不接受  接口地址:{},异常内容:{}", url, ex);
        return ResponseData.ExceptionEnum.EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE.getResult(url);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo defaultExceptionHandler(Exception ex) {
        String url = getRequestUri();
        LOGGER.warn("代码出现异常  接口地址:{},异常内容:{}", url, ex);
        return ResponseData.ExceptionEnum.EXCEPTION_SYSTEM_BUSY.getResult(url);
    }

    // 取默认的请求
    private String getRequestUri() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes());
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        return httpServletRequest.getRequestURI();
    }
}
