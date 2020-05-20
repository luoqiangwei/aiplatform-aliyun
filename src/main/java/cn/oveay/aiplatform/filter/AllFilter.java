package cn.oveay.aiplatform.filter;

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
@WebFilter("/*")
public class AllFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getRequestURI().equals("/") || request.getRequestURI().equals("/index")
            || request.getRequestURI().equals("/login") || request.getRequestURI().equals("/register")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (request.getAttribute("user") != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ((HttpServletResponse) servletResponse).sendRedirect("/login");
        }
    }
}
