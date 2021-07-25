package main;

import com.alibaba.fastjson.JSONObject;
import enums.FileEnum;
import enums.ProcessTypeEnum;
import enums.VideoSuffixEnum;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kit.RenameKit;
import kit.VideoKit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ConstantConditions")
public class OtherKitsService {

    public static final Logger logger = LoggerFactory.getLogger(OtherKitsService.class);

    public static void mainVideoFix(String opt, String path) {
        VideoKit videoKit = new VideoKit();
        ProcessTypeEnum typeEnum = ProcessTypeEnum.getEnumBySequence(opt);
        if (typeEnum == null) {
            System.out.println("Unknown Operation");
        } else {
            videoKit.batchProcess(path, typeEnum);
        }
    }

    public static void tidyVideos(String path) {
        List<String> videoTypes = VideoSuffixEnum.getAllExtensions();
        RenameKit renameKit = new RenameKit();
        File mainPath = new File(path);
        if (mainPath.exists()) {
            File exportFile = new File(mainPath, FileEnum.DEFAULT_EXPORT_FILE.getName());
            try (
                    FileWriter fw = new FileWriter(exportFile);
                    BufferedWriter bw = new BufferedWriter(fw)
            ) {
                File[] listFiles = mainPath.listFiles();
                for (File folder : listFiles) {
                    if (folder.isDirectory()) {
                        File[] files = folder.listFiles();
                        Optional<File> fileOptional = Arrays.stream(files)
                                .filter(e -> isSpecificFile(videoTypes, e.getName()))
                                .findAny();
                        if (fileOptional.isPresent()) {
                            File video = fileOptional.get();
                            JSONObject jsonObject = getJSONObject(
                                    new File(folder.getAbsolutePath(), "project.json"));
                            String videoName = jsonObject.getString("file");
                            String description = jsonObject.getString("description");
                            if (StringUtils.isNotBlank(description)) {
                                bw.write(StringUtils.join(videoName, "--->   ", description, "\n"));
                            }
                            logger.info("get video name from project.json {}", videoName);
                            renameKit.renameFile(video, new File(path, videoName));
                        }
                    }
                }
                if (!exportFile.exists()) {
                    if (!exportFile.createNewFile()) {
                        logger.info("create export file {} failed", exportFile.getPath());
                    }
                }
            } catch (IOException e) {
                logger.error("file write occur error: " + e.getMessage());
            }
        } else {
            logger.warn("{} does not exist", path);
        }
    }

    public static JSONObject getJSONObject(File json) {
        if (json.exists()) {
            String jsonString = RenameKit.readFile(json);
            return JSONObject.parseObject(jsonString);
        } else {
            return null;
        }
    }

    public static boolean isSpecificFile(List<String> types, String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return types.contains(extension.toLowerCase());
    }
}
