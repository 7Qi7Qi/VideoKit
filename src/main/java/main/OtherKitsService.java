package main;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import enums.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import kit.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ConstantConditions")
public class OtherKitsService {

    public static final Logger logger = LoggerFactory.getLogger(OtherKitsService.class);
    private static final ThreadPoolKit poolKit = ThreadPoolKit.getInstance();
    private static final char unitChar = '▄';

    public static void mainVideoFix(String opt, String path) {
        VideoKit videoKit = new VideoKit();
        ProcessTypeEnum typeEnum = ProcessTypeEnum.getEnumBySequence(opt);
        if (typeEnum == null) {
            alertAutoClose("Unknown Operation");
        } else {
            poolKit.submit(() -> videoKit.batchProcess(path, typeEnum));
        }
    }

    public static Result tidyVideos(String path) {
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
                            if (jsonObject == null) {
                                renameKit.renameFile(video, new File(path, video.getName()));
                            }else {
                                String videoName = jsonObject.getString("file");
//                                String description = jsonObject.getString("description");
                                String title = jsonObject.getString("title");
                                if (StringUtils.isNotBlank(title)) {
                                    bw.write(StringUtils.join(videoName, "--->   ", title, "\n"));
                                }
                                logger.info("get video name from project.json {}", videoName);
                                renameKit.renameFile(video, new File(path, videoName));
                            }
                        }
                    }
                }
                if (!exportFile.exists()) {
                    if (!exportFile.createNewFile()) {
                        logger.info("create export file {} failed", exportFile.getPath());
                    }
                }
                return Result.ok();
            } catch (IOException e) {
                logger.error("file write occur error: {}", e.getMessage());
                return Result.fail().message("file write occur error:" + e.getMessage());
            }
        } else {
            logger.warn("{} does not exist", path);
            return Result.fail().message(String.format("%s does not exist", path));
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

    public static Result lower2Upper(String path) {
        File mainPath = new File(path);
        if (mainPath.exists()) {
            File[] files = mainPath.listFiles();
            boolean tooLong = Arrays.stream(files).anyMatch(e -> e.getName().length() > 15);
            if (tooLong) {
                return Result.fail().message("有文件名字长度大于15 ");
            }

            for (File file : files) {
                if (file.isFile()) {
                    String inputName = file.getName();
                    int dotIndex = inputName.lastIndexOf('.');
                    String fileName = inputName.substring(0, dotIndex);
                    String extension = inputName.substring(dotIndex + 1).toLowerCase();

                    file.renameTo(new File(file.getParent(), fileName.toUpperCase()
                            + "." + extension));
                }
            }
            return Result.ok();
        }
        return Result.fail().message(String.format("%s路径不存在", mainPath.getPath()));
    }

    public static void alertAutoClose(String contentText) {
        if (contentText == null) {
            contentText = "操作完成";
        }
        Alert alert = new Alert(AlertType.INFORMATION, contentText, ButtonType.FINISH);
        alert.show();
        Thread autoClose = new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    if (alert.isShowing()) {
                        alert.close();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        autoClose.setDaemon(true);
        autoClose.start();
    }

    public static void messagePrint(String content) {
        String bound = StringUtils.repeat(unitChar, 60);
        System.out.println(bound);
        if (content.length() > bound.length()) {
            System.out.println(content);
        }else {
            int index = (int)((bound.length() - content.length()) * 1.3);
            String s = StringUtils.repeat(' ', index) + content + StringUtils.repeat(' ', index);
            System.out.println(s);
        }
        System.out.println(bound);
    }

    public static void messagePrint2(String msg) {
        String[] corners = {"═", "║", "║\n", "╔", "╗\n", "╚", "╝\n"};
        String splitLine = Strings.repeat(corners[0], 60);
        StringBuilder sb = new StringBuilder();
        sb.append(corners[3]).append(splitLine).append(corners[4]);
        sb.append(corners[1]).append(String.format("%144s", "")).append(corners[2]);
        for (String single : msg.split("\n")) {
            sb.append(corners[1])
                    .append(String.format("%70s", single))
                    .append(Strings.repeat(" ", 70))
                    .append(corners[2]);
        }
        sb.append(corners[1]).append(String.format("%144s", "")).append(corners[2]);
        sb.append(corners[5]).append(splitLine).append(corners[6]);
    }

}
