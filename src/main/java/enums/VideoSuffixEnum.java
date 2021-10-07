package enums;

import java.util.Arrays;
import java.util.List;
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
    WMV("wmv");

    private final String extension;
    VideoSuffixEnum(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static List<String> getAllExtensions() {
        return Arrays.stream(VideoSuffixEnum.values()).map(VideoSuffixEnum::getExtension).collect(Collectors.toList());
    }
}
