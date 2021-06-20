package main;

import com.alibaba.fastjson.JSONObject;
import enums.ProcessTypeEnum;
import enums.VideoSuffixEnum;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kit.RenameKit;
import kit.VideoKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ConstantConditions")
public class VideoKitService {

    public static final Logger logger = LoggerFactory.getLogger(VideoKitService.class);

    public static void mainVideoFix(String opt, String path) {
        VideoKit videoKit = new VideoKit();
        ProcessTypeEnum typeEnum = ProcessTypeEnum.getEnumBySequence(opt);
        if (typeEnum == null) {
            System.out.println("Unknown Operation");
        }else {
            videoKit.batchProcess(path, typeEnum);
        }
    }

    public static void tidyVideos(String path) {
        List<String> videoTypes = VideoSuffixEnum.getAllExtensions();
        RenameKit renameKit = new RenameKit();
        File mainPath = new File(path);
        if (mainPath.exists()) {
            File[] listFiles = mainPath.listFiles();
            for (File folder : listFiles) {
                if (folder.isDirectory()) {
                    File[] files = folder.listFiles();
                    Optional<File> fileOptional = Arrays.stream(files)
                            .filter(e -> videoTypes.contains(e.getName().substring(e.getName().lastIndexOf(".")+1)))
                            .findAny();
                    if (fileOptional.isPresent()) {
                        File video = fileOptional.get();
                        String videoName = getVideoName(
                                new File(folder.getAbsolutePath(), "project.json"));
                        renameKit.renameFile(video, new File(path, videoName));
                    }
                }
            }

        }else {
            logger.warn("{} does not exist", path);
        }
    }

    public static String getVideoName(File json) {
        if (json.exists()) {
            String jsonString = RenameKit.readFile(json);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            return jsonObject.getString("file");
        }else {
            return null;
        }
    }
}
