package enums;

public enum FileEnum {

    /**
     * FileOutputEnum: according to file type return folder name
     */
    SEPARATOR("\\"),
    VIDEO_FOLDER("clip"),
    LATEST_COVER("latest"),
    OLD_VIDEO("trash"),
    COVER_FOLDER("screenshot"),
    ORIGINAL_FILE("original"),

    TXT_FILE(".txt"),
    DEFAULT_EXPORT_FILE("document.txt");

    private final String name;

    public String getName() {
        if (this == SEPARATOR) {
            return this.name;
        }
        return this.name + SEPARATOR.name;
    }

    FileEnum(String name) {
        this.name = name;
    }

}
