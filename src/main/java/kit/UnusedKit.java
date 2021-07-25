package kit;

import enums.VideoSuffixEnum;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class UnusedKit {

    public final Logger logger = LoggerFactory.getLogger(getClass());


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