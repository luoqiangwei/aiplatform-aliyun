package cn.oveay.aiplatform.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/20 17:06
 * 文件说明：
 */
@Slf4j
@WebFilter("/*")
public class AllFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.warn(request.getRequestedSessionId() + "doFilter URI: " + request.getRequestURI());
        if (request.getRequestURI().equals("/") || request.getRequestURI().equals("/index")
            || request.getRequestURI().startsWith("/login") || request.getRequestURI().startsWith("/register")
            || request.getRequestURI().startsWith("/css") || request.getRequestURI().startsWith("/images")
            || request.getRequestURI().startsWith("/scripts") || request.getRequestURI().startsWith("/check")
            || request.getRequestURI().startsWith("/test") || request.getRequestURI().startsWith("/verify")
            || request.getRequestURI().startsWith("/js") || request.getRequestURI().equals("/favicon.ico")
            || request.getRequestURI().startsWith("/img")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (request.getSession().getAttribute("user") != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ((HttpServletResponse) servletResponse).sendRedirect("/login");
        }
//        filterChain.doFilter(servletRequest, servletResponse);
    }
}
