package kit;

import enums.VideoSuffixEnum;
import java.io.File;
import java.util.*;
import java.util.function.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class UnusedKit {

    public static final Logger logger = LoggerFactory.getLogger(UnusedKit.class);

    public static void testOutputKit(Consumer<String> function) {

        for (int i = 0; i < 20; i++) {
            function.accept(i + " ======> " + new Date());
        }
    }

    public static void testLogger() {
        logger.debug("logger debug 测试测试" + new Date());
        logger.info("logger info " + new Date());
        logger.warn("logger warn " + new Date());
        logger.error("logger error " + new Date());
    }


    public File correctFile(File input) {
        String fileName = input.getName();
        fileName = fileName.substring(0, fileName.length() - 3) + "." + fileName
                .substring(fileName.length() - 3);
        return renameFile(input, new File(input.getParent(), fileName));
    }

    public File renameFile(File input, File output) {
        if (input.renameTo(output)) {
            logger.info("rename to {} ", input.getName());
            return output;
        } else {
            logger.warn("failed rename to {}", input.getName());
            return input;
        }
    }

    /****************************FilterFileTypes***************************************/
    protected static List<File> filterVideoFiles(File file) {
        return filterFiles(file, val -> VideoSuffixEnum.getAllExtensions().stream().anyMatch(val::endsWith));
    }

    protected static List<File> filterAllVideoFiles(File file) {
        return filterAllFiles(file, val -> VideoSuffixEnum.getAllExtensions().stream().anyMatch(val::endsWith), true);
    }
    protected static List<File> filterFiles(File file, Predicate<String> judgeType) {
        ArrayList<File> fileList = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : Objects.requireNonNull(files)) {
                    fileList.addAll(filterAllFiles(f, judgeType, false));
                }
            } else {
                if (judgeType.test(file.getName())) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }
    protected static List<File> filterAllFiles(File file, Predicate<String> judgeType, Boolean flag) {
        ArrayList<File> fileList = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory() && flag) {
                File[] files = file.listFiles();
                for (File f : Objects.requireNonNull(files)) {
                    fileList.addAll(filterAllFiles(f, judgeType, true));
                }
            } else {
                if (judgeType.test(file.getName())) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }
}
