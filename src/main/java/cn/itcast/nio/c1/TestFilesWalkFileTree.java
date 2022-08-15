package cn.itcast.nio.c1;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFilesWalkFileTree {

    public static void main(String[] args) throws IOException {
        Files.walkFileTree(Paths.get("E:\\java\\netty-demo"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.endsWith("to.txt")) {
                    Files.delete(file);
                }
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void extracted() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("E:\\java\\netty-demo"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("==>" + dir);
                dirCount.getAndIncrement();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.getAndIncrement();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dir count:" + dirCount);
        System.out.println("file count:" + fileCount);
    }
}
