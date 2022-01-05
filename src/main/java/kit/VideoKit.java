package kit;

import enums.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import main.OtherKitsService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Jupiter
 * @date 2020-12-30 13:20:11
 */

@SuppressWarnings({"ConstantConditions"})
public class VideoKit {

    private final Logger logger = LoggerFactory.getLogger(VideoKit.class);
    private final static List<String> exFileList = Arrays.asList("words.txt", "regex.txt", "rename",
            "output", "cover", "ignore", "bin");
    private final static RenameKit renameKit = new RenameKit();
    private final static ThreadPoolKit poolKit = ThreadPoolKit.getInstance();

    public void batchProcess(String pathName, ProcessTypeEnum typeEnum) {
        File mainPath = new File(pathName);
        if (mainPath.exists() && "#".equals(mainPath.getName())) {

            File[] listFiles = new File(mainPath, typeEnum.getFolder()).listFiles();

            if (ProcessTypeEnum.FIX_NAME.equals(typeEnum)) {
                String wordsPath = mainPath + FileEnum.SEPARATOR.getName() + exFileList.get(0);
                String regexPath = mainPath + FileEnum.SEPARATOR.getName() + exFileList.get(1);
                //initialize custom words that you want to delete
                renameKit.addCustomWords(wordsPath, regexPath);
                Arrays.stream(listFiles).forEach(renameKit::renameForAll);
            } else if (ProcessTypeEnum.BUILD_COVER_PIC.equals(typeEnum)) {
                Arrays.stream(listFiles).forEach(this::captureCover);
            } else if (ProcessTypeEnum.BUILD_LATEST_COVER.equals(typeEnum)) {
                Arrays.stream(listFiles).forEach(this::createLatestCover);
            } else {
                List<File> allFiles = Arrays.stream(listFiles)
                        .filter(e -> e.isDirectory() && StringUtils.isNumeric(e.getName()))
                        .flatMap(e -> Arrays.stream(e.listFiles()))
                        .filter(File::isFile)
                        .collect(Collectors.toList());
                this.multiThread(allFiles);
                //this.oneStepService(allFiles.get(0));
            }
        } else {
            logger.warn("【{}】 does not exist or mainPath name is not #", mainPath);
        }
    }


    public boolean executeCommand(String command) {
        Process process;
        try {
            logger.info("【{}】", command);
            process = Runtime.getRuntime().exec(command);
            InputStream is = process.getErrorStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            // ensure command executed and execute down
            while (process.isAlive() && (line = reader.readLine()) != null) {
//                System.out.println(line);
            }
            return true;
        } catch (IOException e) {
            logger.warn("【{}】 occur exception => 【{}】", command, e.getMessage());
            return false;
        }
    }

    /***************************************VideoRelated**************************************/
    public void multiThread(List<File> list) {
        if (list.isEmpty()) {
            OtherKitsService.messagePrint("No File");
            return;
        }
        //Lists.partition(list, )
        List<Future<?>> futures = new ArrayList<>();
        for (File file : list) {
            Future<?> future = poolKit.submit(() -> oneStepService(file));
            futures.add(future);
        }
        //wait for all thread completed
        futures.forEach(future -> {
            try {
                future.get();
            }catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        });
        OtherKitsService.messagePrint("Finish Message");
    }

    /**
     * 1. handle file name, remove redundant string words in video name
     * 2. cut fix cover
     * 3. build the latest video cover
     */
    public void oneStepService(File file) {
        String parentFolder = new File(file.getParent()).getName();
        File handleName = renameKit.renameForFile(file);
        if (StringUtils.isNumeric(parentFolder)) {
            String coverCutVideo = cutFixedCover(handleName, Integer.parseInt(parentFolder));
            if (coverCutVideo != null) {
                String trash = createFolderIfAbsent(handleName, FileEnum.OLD_VIDEO.getName());
                renameKit.renameFile(handleName, new File(trash + handleName.getName()));
                String latestCover = createLatestCover(new File(coverCutVideo));
                if (latestCover != null) {
                    File out = move2Parent(new File(latestCover), 3);
                    String outPath = out.getParent() + FileEnum.OUTPUT.getName();
                    renameKit.renameFile(out, new File(outPath, out.getName()));
                }
            }
        }
    }

    /**
     * get video length
     * @param file video
     * @return video length in units of millisecond
     */
    public long getVideoLength(File file) {
        MultimediaObject object = new MultimediaObject(file);
        long duration = -1;
        try {
            MultimediaInfo info = object.getInfo();
//      VideoInfo video = info.getVideo();
            duration = info.getDuration();
        } catch (EncoderException e) {
            logger.warn("【{}】 get video length occur exception => 【{}】", file.getName(),
                    e.getMessage());
        }
        return duration;
    }

    public String cutFixedCover(File file, long startSecond) {
        if (file.isFile()) {
            return cutFixedSize(file, startSecond, getVideoLength(file));
        }
        return null;
    }

    public String cutFixedSize(@NotNull File file, long start, long end) {
        String outputPath =
                createFolderIfAbsent(file, FileEnum.VIDEO_FOLDER.getName()) + file.getName();
        if (fileNotExist(outputPath)) {
            String command = String.format(FFMPEGEnum.SIMPLE_CLIP.normalCMD(), start,
                    file.getAbsolutePath(), end, outputPath);
            if (executeCommand(command)) {
                logger.info("【{}】 cut cover start from 【{}】 seconds", file.getAbsolutePath(),
                        start);
                return outputPath;
            }
        } else {
            logger.warn("【{}】 outputPath existed ", outputPath);
        }
        return null;
    }


    /****************************************PictureRelated****************************************/
    public String captureCover(File file) {
        if (file.isDirectory()) {
            logger.warn("【{}】 capture cover failed, since parameter file is a directory",
                    file.getAbsolutePath());
        } else {
            String videoName = file.getName().substring(0, file.getName().lastIndexOf("."));
            String outputName = createFolderIfAbsent(file, FileEnum.COVER_FOLDER.getName());
            outputName += videoName + ".jpg";
            if (fileNotExist(outputName)) {
                String command = String.format(FFMPEGEnum.CREATE_COVER.normalCMD(), 6,
                        file.getAbsolutePath(), outputName);
                if (executeCommand(command)) {
                    logger.info("【{}】 capture cover succeed", file.getAbsolutePath());
                    return outputName;
                }
            } else {
                logger.warn("【{}】 outputPath existed ", outputName);
            }
        }
        return null;
    }

    public String createLatestCover(File file) {
        String cover = captureCover(file);
        return replaceCover(file, cover);
    }

    public String replaceCover(File file, String coverPath) {
        String outputPath =
                createFolderIfAbsent(file, FileEnum.LATEST_COVER.getName()) + file.getName();
        if (fileNotExist(outputPath)) {
            String command = String
                    .format(FFMPEGEnum.REPLACE_COVER.normalCMD(), file, coverPath, outputPath);
            if (executeCommand(command)) {
                logger.info("【{}】 replace cover", file.getAbsolutePath());
                return outputPath;
            }
        } else {
            logger.warn("【{}】 outputPath existed ", outputPath);
        }
        return null;
    }

    public Image getImageByPath(String filePath) {
        return Toolkit.getDefaultToolkit().getImage(filePath);
    }

    /***************************************DirectoryRelated***************************************/
    public File move2Parent(File input, int layer) {
        File output = input;
        for (int i = 0; i < layer; i++) {
            output = new File(new File(output.getParent()).getParent(), input.getName());
        }
        renameKit.renameFile(input, output);
        return output;
    }

    /**
     * @see org.apache.commons.io.FileUtils#deleteDirectory(File)
     */
    public void deleteDir(File dir) {
        if (dir.exists()) {
            if (dir.delete()) {
                logger.warn("Successfully delete the directory 【{}】", dir);
            } else {
                logger.warn("Failed to delete the directory 【{}】", dir);
            }
        } else {
            logger.warn("There is no 【{}】", dir);
        }

    }

    /**
     * directory with subFile can't delete directly, need delete recursively
     */
    public boolean deleteDirRecursive(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File subFile : files) {
                if (!deleteDirRecursive(subFile)) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public String createFolderIfAbsent(File sourceFile, String folder) {
        String outputPath = "";
        if (sourceFile.exists()) {
            if (sourceFile.isFile()) {
                outputPath = sourceFile.getParent() + FileEnum.SEPARATOR.getName() + folder;
            } else {
                outputPath = sourceFile.getAbsolutePath() + FileEnum.SEPARATOR.getName() + folder;
            }
            createFolderIfAbsent(outputPath);
        } else {
            if (sourceFile.mkdir()) {
                logger.info("【{}】 has been created first",
                        sourceFile.getAbsolutePath());
                outputPath = createFolderIfAbsent(sourceFile, folder);
            } else {
                logger.warn("fail to create 【{}】", sourceFile.getAbsolutePath());
            }
        }
        return outputPath;
    }

    protected void createFolderIfAbsent(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (file.mkdir()) {
                logger.info("succeed to create folder: 【{}】", file.getAbsolutePath());
            } else {
                logger.warn("fail to create folder: 【{}】", file.getAbsolutePath());
            }
        }
    }

    protected boolean fileNotExist(String filePath) {
        return !new File(filePath).exists();
    }

}
