package com.johnpickup.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static File projectRootDirectory(String year) {
        URL resource = FileUtils.class.getClassLoader().getResource(year);
        return new File(resource.getFile()).getParentFile().getParentFile().getParentFile().getParentFile();
    }

    public static File resourceDirectory(String year, String day) {
        return new File(new File(new File(projectRootDirectory(year), "resources"), year), day);
    }

    public static List<String> getInputFilenames(String year, String day) {
        File resourceDirectory = resourceDirectory(year, day);
        String prefix = resourceDirectory.getAbsolutePath() + "/" + day;
        createEmptyTestFileIfMissing(prefix + "-test.txt");
        createEmptyTestFileIfMissing(prefix + ".txt");
        createEmptyTestFileIfMissing( resourceDirectory.getAbsolutePath() + "/instructions.txt");
        return Arrays.stream(resourceDirectory.listFiles((dir, name) ->
                        name.endsWith(".txt") && !name.startsWith("instructions")))
                .map(file -> file.getAbsolutePath())
                .sorted(FileUtils::sortTestFilesFirst)
                .collect(Collectors.toList());
    }

    private static int sortTestFilesFirst(String filename1, String filename2) {
        boolean isTest1 = filename1.contains("test");
        boolean isTest2 = filename2.contains("test");
        if (isTest1 == isTest2) {
            return filename1.compareTo(filename2);
        }
        return isTest1 ? -1 : 1;
    }

    public static List<String> getInputFilenames(Object obj) {
        Class<?> enclosingClass = obj.getClass().getEnclosingClass();
        String day = enclosingClass.getSimpleName();
        return getInputFilenames(obj, day);
    }

    public static List<String> getInputFilenames(Object obj, String day) {
        Class<?> enclosingClass = obj.getClass().getEnclosingClass();
        String year = enclosingClass.getName().replace("com.johnpickup.aoc", "").replace("."+ day, "");
        return getInputFilenames(year, day);
    }

    private static void createEmptyTestFileIfMissing(String filename) {
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
