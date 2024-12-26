package com.johnpickup.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    public static void createEmptyTestFileIfMissing(String filename) {
        try {
        File file = new File(filename);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            System.out.println("Creating directory " + dir);
            Files.createDirectory(dir.toPath());
        }
        if (!file.exists()) {
            System.out.println("Creating file " + file);
            Files.createFile(file.toPath());
        }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
