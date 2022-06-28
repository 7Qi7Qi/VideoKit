package enums;

public enum ProcessTypeEnum {

    FIX_DOWNLOAD("1","一站式处理下载的视频", ""),
    FIX_NAME("2", "文件名去除自定义多余字段", "rename"),
    CUT_VIDEO("3", "根据文件夹名称剪辑片头", ""),
    BUILD_LATEST_COVER("4", "生成最新的视频封面", "cover"),
    MERGE_VIDEO("5", "文件夹视频合并", ""),

    BUILD_COVER_PIC(null,"生成目录下所有视频封面", "cover"),
    MOVE_PRETREAT(null,"视频根据封面移动预处理", ""),
    ;

    private final String sequence;
    private final String msg;
    private final String folder;

    ProcessTypeEnum(String sequence, String msg, String folder) {
        this.sequence = sequence;
        this.msg = msg;
        this.folder = folder;
    }

    public String getFolder() {
        return this.folder;
    }

    public static ProcessTypeEnum getEnumBySequence(String sequence) {
        for (ProcessTypeEnum value : ProcessTypeEnum.values()) {
            if (sequence.equals(value.sequence)) {
                return value;
            }
        }
        return null;
    }
}
