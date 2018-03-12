package com.yudianbank.tms.controller;

import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.configure.interceptor.UserLoginInterceptor;
import com.yudianbank.tms.model.vo.ResponseData;
import com.yudianbank.tms.util.IdentifyCodeUtil;
import com.yudianbank.tms.util.ProjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录Controller层
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/")
@Api(value = "UserLoginController", description = "用户登录界面API", hidden = true)
public class UserLoginController {

    private static final String VERIFICATION_CODE_NAME = "VerificationCode";

    private EnvVariableConfig envVariableConfig;

    public UserLoginController() {
    }

    @Autowired
    public UserLoginController(EnvVariableConfig envVariableConfig) {
        Assert.notNull(envVariableConfig, "UserLoginController.envVariableConfig must be not null!");
        this.envVariableConfig = envVariableConfig;
    }

    // 登录界面
    @RequestMapping(value = "api/tms/schedule/sign/index", method = RequestMethod.GET)
    @ApiOperation(value = "登录界面", hidden = true)
    public String index() {
        return "login";
    }

    // 登录界面
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "登录界面(主页)", hidden = true)
    public String home() {
        return "login";
    }


    // 用户登录请求
    @ResponseBody
    @RequestMapping(value = "api/tms/schedule/sign/in", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录请求", hidden = true)
    public String signIn(HttpServletRequest request, HttpServletResponse response, String loginUsername,
                         String loginPassword, String code) {
        if (loginUsername == null || "".equals(loginUsername.trim()))
            return ResponseData.NO_USER_NAME;
        if (StringUtils.isEmpty(loginPassword))
            return ResponseData.NO_PASSWORD;
        if (StringUtils.isEmpty(code))
            return ResponseData.NO_VERIFICATION_CODE;
        // 获取存放在session中的验证码并验证
        String sessionCode = (String) request.getSession().getAttribute(VERIFICATION_CODE_NAME);
        if (sessionCode == null || !code.equals(sessionCode.toLowerCase()))
            return ResponseData.ERROR_VERIFICATION_CODE;
        // 密码进行验证
        String defaultPassword = envVariableConfig.getDefaultPassword();
        String defaultUser = envVariableConfig.getDefaultUser();
        if (loginUsername.trim().equals(defaultUser) && loginPassword.equals(defaultPassword)) {
            Cookie cookie = new Cookie(UserLoginInterceptor.DEFAULT_COOKIE_NAME,
                    ProjectUtil.encoderByMd5(defaultPassword));
            cookie.setMaxAge(2 * 60 * 60);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "success";
        }
        return ResponseData.ERROR_USER_OR_PASSWORD;
    }

    // 用户登出请求
    @RequestMapping(value = "api/tms/schedule/sign/out", method = RequestMethod.GET)
    @ApiOperation(value = "用户登出请求", hidden = true)
    public String signOut(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(UserLoginInterceptor.DEFAULT_COOKIE_NAME,
                ProjectUtil.encoderByMd5(envVariableConfig.getDefaultPassword()));
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:" + request.getContextPath() + UserLoginInterceptor.LOGIN_URL;
    }

    // 取验证码的请求
    @RequestMapping(value = "api/tms/schedule/sign/getIdentifyCode", method = RequestMethod.GET)
    @ApiOperation(value = "取验证码的请求", hidden = true)
    public void getIdentifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        // 禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        IdentifyCodeUtil code = new IdentifyCodeUtil(100, 30, 4, 10);
        // 保存于session中
        request.getSession().setAttribute(VERIFICATION_CODE_NAME, code.getCode());
        code.write(response.getOutputStream());
    }

    // 获取登录用户名
    public String getDefaultUser() {
        return envVariableConfig.getDefaultUser();
    }
}