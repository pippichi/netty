package cn.itcast.netty.c1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        String source = "E:\\zk1";
        String target = "E:\\zk2";
        Files.walk(Paths.get(source)).forEach(path -> {
           try {
               String targetDir = path.toString().replace(source, target);
               if (Files.isDirectory(path)) {
                   Files.createDirectory(Paths.get(targetDir));
               } else if (Files.isRegularFile(path)) {
                   Files.copy(path, Paths.get(targetDir));
               }
           } catch (IOException e) {}
        });
    }
}
