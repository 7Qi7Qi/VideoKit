package kit;

import enums.FilePrefixEnum;
import enums.VideoSuffixEnum;
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

    public void renameForAll(File file) {
        if (file.isFile()) {
            renameForFile(file);
        }else {
            List<String> extensions = VideoSuffixEnum.getAllExtensions();
            File[] files = file.listFiles();
            for (File input : files) {
                Pair<String, String> result = this.renameInnerOpt(input, extensions);
                String nameAfterFix = result.getValue();
                if (nameAfterFix != null) {
                    renameFileInFolder(input, nameAfterFix, 1);
                }
                if ("torrent".equals(result.getKey())) {
                    if (!input.delete()) {
                        logger.info("【{}】 fail to delete", input);
                    }
                }
            }
        }
    }

    public File renameForFile(File input) {
        Pair<String, String> result = this.renameInnerOpt(input, null);
        if ( result.getValue() != null) {
            renameFileInFolder(input,  result.getValue());
        }
        return input;
    }

    /**
     *
     * @param input file wanted to rename
     * @param types rename file that in types
     * @return extension , fileName
     */
    private Pair<String, String> renameInnerOpt(File input, List<String> types) {
        String inputName = input.getName();
        int dotIndex = inputName.lastIndexOf('.');
        String fileName = inputName.substring(0, dotIndex);
        String extension = inputName.substring(dotIndex + 1).toLowerCase();
        String output = fileName;
        if (types == null || types.contains(extension)) {
            output = seriesRename(fileName);
        }
        if (output.equals(fileName)) {
            return new Pair<>(extension, null);
        }
        return new Pair<>(extension, output + "." + extension);
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
                logger.warn("【{}】 not exist, custom words add nothing", file.getAbsolutePath());
            }
        }
    }

    public String seriesRename(String input) {
        //RemoveCustomWord
        if (customWords.size() > 0) {
            for (String word : customWords) {
                input = input.replace(word, "");
            }
        }
        //RemoveRegexWord
        if (customRegex.size() > 0) {
            for (String regex : customRegex) {
                //regex replace method
                input = input.replaceAll(regex, "");
            }
        }
        //HandleCF
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
        //DeletePrefixNumber
        if (input.matches("[0-9]{1,3}[\\D]+")) {
            int start = 0;
            for (int i = 0; i < Math.min(5, input.length()); i++) {
                char c = input.charAt(i);
                if (c <= '9' && c >=  '0' || c == '-') {
                    start = i;
                }
            }
            input = input.substring(start + 1);
        }
        //HandleFH
        if (input.toLowerCase().matches("[\\w]{3,4}-[\\d]{3,4}(-c)?")) {
            input = input.toUpperCase();
        }
        return input;
    }



    public File renameFile(File input, File output) {
        if (output.exists()) {
            logger.warn("【{}】 already exists, 【{}】 rename failed", output.getAbsolutePath(),
                    input.getAbsolutePath());
            File duplicate = new File(output.getParent() + "/duplicate");
            if (!duplicate.exists()) {
                duplicate.mkdir();
            }
            return renameFile(input, new File(duplicate, output.getName()));
        }
        boolean result = input.renameTo(output);
        if (!result) {
            logger.error("【{}】 rename to 【{}】 failed", input, output);
        }else {
            logger.info("【{}】 rename to 【{}】 ", input, output);
        }
        return result ? output : input;
    }

    public File renameFileInFolder(File input, String outputFileName) {
        return renameFileInFolder(input, outputFileName, 0);

    }

    /**
     *
     * @param in input file
     * @param out output file name
     * @param layer 0 -> no move, 1 -> move to parent ... etc.
     */
    public File renameFileInFolder(File in, String out, Integer layer) {
        File parent = in;
        for (int i = 0; i <= layer; i++) {
            parent = new File(parent.getParent());
        }
        return renameFile(in, new File(parent, out.trim()));
    }

    public void renameFolderAndFile(File file) {
        String fileName = file.getName();
        if (file.exists()) {
            String nameAfterFix = seriesRename(fileName);
            if (!fileName.equals(nameAfterFix)) {
                File output = renameFileInFolder(file, nameAfterFix);
                if (output.isDirectory()) {
                    Arrays.stream(Objects.requireNonNull(output.listFiles()))
                            .forEach(this::renameFolderAndFile);
                }
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
            str = FileUtils.readFileToString(file, "UTF-8");
        }catch (IOException e) {
            logger.error("【{}】 read file failed", file.getAbsolutePath());
        }
        return str;
    }
}
