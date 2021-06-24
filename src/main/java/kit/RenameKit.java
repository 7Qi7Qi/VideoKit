package kit;

import enums.FilePrefixEnum;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "ConstantConditions"})
public class RenameKit {

    //final 可以调用改对象的任何方法，但不能指向其他实例化对象
    private static final List<String> customWords = new ArrayList<>();
    private static final List<String> customRegex = new ArrayList<>();

    private static final Map<String, List<String>> customName2File = new HashMap<String, List<String>>() {{
        put("words.txt", customWords);
        put("regex.txt", customRegex);
    }};
    public static Logger logger = LoggerFactory.getLogger(RenameKit.class);

    public File customRename(File input) {
        String fileName = input.getName();
        String output = fixCustomWords(fileName);
        //why reference not take effect
        if (output.equals(fileName)) {
            return input;
        }
        return renameFileInFolder(input, output);
    }

    public void addCustomWords(String... paths) {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                try (
                        FileInputStream is = new FileInputStream(file);
                        BufferedReader bf = new BufferedReader(new InputStreamReader(is, "GBK"))
                ) {
                    List<String> toFileName = customName2File.get(file.getName());
                    String word;
                    while ((word = bf.readLine()) != null) {
                        toFileName.add(word);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.warn("{} not exist, custom words add nothing", file.getAbsolutePath());
            }
        }
    }

    protected String fixCustomWords(String str) {
        if (customWords.size() > 0) {
            for (String word : customWords) {
                str = str.replace(word, "");
            }
        }
        return fixRegexWords(str);
    }

    protected String fixRegexWords(String str) {
        if (customRegex.size() > 0) {
            for (String regex : customRegex) {
                //regex replace method
                str = str.replaceAll(regex, "");
            }
        }
        return handleCF(str);
    }

    public String handleCF(String input) {
        String cfName = FilePrefixEnum.CF.getName();
        if (input.startsWith(cfName) || input.startsWith(cfName.toLowerCase())) {
            int index = 3;
            for (int i = 3; i < input.length(); i++) {
                if (Character.isDigit(input.charAt(i))) {
                    index = i;
                    break;
                }
            }
            input = FilePrefixEnum.CF.getCode() + input.substring(index);
        }
        return input;
    }

    public File renameFile(File input, File output) {
        if (output.exists()) {
            logger.warn("{} already exists, {} rename failed", output.getAbsolutePath(),
                    input.getAbsolutePath());
            return input;
        }
        return input.renameTo(output) ? output : input;
    }

    public File renameFileInFolder(File input, String output) {
        return renameFile(input, new File(input.getParent(), output.trim()));
    }

    public void renameFolderAndFile(File file) {
        String fileName = file.getName();
        if (file.exists()) {
            File output = renameFileInFolder(file, fixCustomWords(fileName));
            if (output.isDirectory()) {
                Arrays.stream(Objects.requireNonNull(output.listFiles()))
                        .forEach(this::renameFolderAndFile);
            }
        }
    }

    /**
     * traverse the path and rename the file in folder to folder_name
     */
    public void renameToParent(String path) {
        File file = new File(path);
        if (file.exists()) {
            Arrays.stream(file.listFiles()).parallel().forEach(e -> {
                String folderName = e.getName();
                int i = 0;
                for (File listFile : e.listFiles()) {
                    renameFileInFolder(listFile,
                            folderName + "_" + i++ + StringUtils
                                    .substringAfterLast(listFile.getName(), "."));
                }
            });
        }
    }

    /**
     * Find the first file with specific name in folder
     *
     * @param fileName  file wanted
     * @param directory find file in the directory
     * @return Pair<Boolean, String> : <Result, FilePath>
     */
    public static Pair<Boolean, String> findSpecificFile(@NotNull String fileName,
            String directory) {
        if (StringUtils.isBlank(directory)) {
            directory = System.getProperty("user.dir");
        }
        File dirFile = new File(directory);
        if (dirFile.exists()) {
            if (dirFile.isDirectory()) {
                File[] filesInFolder = dirFile.listFiles();
                //The execution time of the stream is twice that of the for loop
                for (File file : filesInFolder) {
                    Pair<Boolean, String> subResult = findSpecificFile(fileName,
                            file.getAbsolutePath());
                    if (subResult.getKey()) {
                        return subResult;
                    }
                }
            } else {
                if (dirFile.getName().equals(fileName)) {
                    return new Pair<>(Boolean.TRUE, dirFile.getAbsolutePath());
                }
            }
        }
        return new Pair<>(Boolean.FALSE, null);
    }

    public static String readFile(File file) {
        String str = "";
        try {
            str = FileUtils.readFileToString(file);
        }catch (IOException e) {
            logger.error("{} read file failed", file.getAbsolutePath());
        }
        return str;
    }
}
