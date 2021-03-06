package org.bupt.scaffold.mis.interceptor;

import org.bupt.common.constant.ErrorConsts;
import org.bupt.common.constant.OauthConsts;
import org.bupt.common.util.token.Identity;
import org.bupt.common.util.token.TokenUtil;
import org.bupt.scaffold.mis.constant.EnvConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录认证拦截器（判断token有效性）
 * Created by ken on 2017/6/8.
 */
public class TokenCheckInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenCheckInterceptor.class);

    @Autowired
    private EnvConsts envConsts;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {

        logger.info("进入TokenCheckInterceptor");

        // 验证token的有效性
        try {

            String accessToken = request.getHeader(OauthConsts.KEY_TOKEN);
            Identity identity = TokenUtil.parseToken(accessToken, envConsts.TOKEN_API_KEY_SECRET);

            //把identity存入session中(其中包含用户名、角色、过期时间戳等)
            request.getSession().setAttribute(OauthConsts.KEY_IDENTITY, identity);

            logger.info("应用={}: token通过认证", identity.getClientId());
            return true;

        } catch (Exception e) {
            logger.info("token无效, 原因为: {}", e.getMessage());
            logger.info("正转向认证失败控制器");
            response.sendRedirect("/api/error/oauth/" + ErrorConsts.OAUTH_CODE_TOKEN_INVALID);

            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {

    }
}
