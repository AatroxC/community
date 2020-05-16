package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.model.entity.User;
import com.nowcoder.community.model.support.UserHolder;
import com.nowcoder.community.model.support.UserInfo;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CookieUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mafei007
 * @date 2020/4/3 23:19
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    private UserService userService;

    public LoginTicketInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ticket = CookieUtils.getValue(request, "ticket");

        // 查询登陆凭证
        UserInfo userInfo = userService.findUserInfo(ticket);

        if (userInfo != null) {
            // 存到线程局部变量
            UserHolder.set(userInfo);

            // 构建用户认证的结果，并存入SecurityContext, 以便于Security进行授权.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userInfo, userInfo.getPassword(), userService.getAutorities(userInfo));
            SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        // 添加到模板
        UserInfo userInfo = UserHolder.get();
        if (userInfo != null && modelAndView != null) {
            modelAndView.addObject("loginUser", userInfo);
        }

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
        SecurityContextHolder.clearContext();
    }
}
