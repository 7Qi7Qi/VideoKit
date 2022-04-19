package enums;

import java.util.*;
import java.util.stream.Collectors;

public enum VideoSuffixEnum {

    AVI("avi"),
    FLV("flv"),
    MKV("mkv"),
    MOV("mov"),
    MP4("mp4"),
    MPEG("mpeg"),
    RMVB("rmvb"),
    TS("ts"),
    WEBM("webm"),
    WMV("wmv");

    private final String extension;
    VideoSuffixEnum(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static Set<String> getAllExtensions() {
        return Arrays.stream(VideoSuffixEnum.values()).map(VideoSuffixEnum::getExtension).collect(Collectors.toSet());
    }
}
