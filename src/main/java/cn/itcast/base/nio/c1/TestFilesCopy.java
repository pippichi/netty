package cn.itcast.base.nio.c1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        String source = "E:\\zk\\工业大数据\\supIBD\\app-service\\app-service\\src\\main\\java";
        String target = "E:\\zk\\工业大数据\\supIBD\\app-service-R\\app-service-R\\src\\main\\java";
        Files.walk(Paths.get(source)).forEach(path -> {
           try {
               String targetDir = path.toString().replace(source, target);
               if (Files.isDirectory(path) && !Files.exists(Paths.get(targetDir))) {
                   Files.createDirectory(Paths.get(targetDir));
               }
           } catch (IOException e) {}
        });
    }
}
