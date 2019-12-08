package Utils;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class env_properties {
    protected static final env_properties init = new env_properties();
    protected static Properties env;

    protected env_properties(String str) {
    }

    public env_properties() {
        String confFile="configure.properties";

        env = new Properties();
        FileInputStream FIS;
        InputStream pom;
        String path;
        try {
            String classresource=this.getClass().getResource(this.getClass().getSimpleName() + ".class").toString();
            if (classresource.startsWith("file:")) {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                String myPom = System.getProperty("user.dir") + File.separator + "pom.xml";
                try {
                    Model model = reader.read(new FileReader(myPom));
                    env.put("version", model.getVersion());
                } catch (Exception e) {

                }

                FIS = new FileInputStream(new File(confFile));
                env.load(FIS);
            } else if (classresource.startsWith("jar:")) {
                path = System.getProperty("java.class.path");
                int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
                int lastIndex = path.lastIndexOf(File.separator) + 1;

                String jarPath=path.substring(firstIndex);

                path = jarPath.substring(0, lastIndex) + confFile;
                FIS = new FileInputStream(path);
                env.load(FIS);
                try {
                    JarFile jar=new JarFile(jarPath);
                    ZipEntry je=jar.getEntry("META-INF/maven/zhzm/DBDF/pom.properties");
                    pom =jar.getInputStream(je);
                    env.load(pom);
                    pom.close();
                } catch (IOException e) {
                    env.put("version","");
                }
            } else {
                throw new IOException();
            }
            FIS.close();
        } catch (FileNotFoundException ex) {
            System.out.println("环境配置文件不存在！");
        } catch (IOException ex) {
            System.out.println("环境配置文件无法读取！");
        } finally //可以通过finally加载默认参数，防止程序崩溃
        {
//            loadDefaultProp();
        }
    }

    private void loadDefaultProp() {
//        if (!env.containsKey("toDB")) {
//            env.setProperty("toDB", "sql");
//        }
    }

    public static String getEnvironment(String envstring) {
        return init.env.getProperty(envstring).strip();
    }

    public static void setEnvironment(String envName, String envstring) {
        init.env.setProperty(envName, envstring);
    }

    public static Properties getJDBCEnv() {
        return init.env;
    }
}
