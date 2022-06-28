package enums;

import java.util.Arrays;

/**
 * @author https://ffmpeg.org/ffmpeg.html
 */
@SuppressWarnings("unused")
public enum FFMPEGEnum {

    /**
     * format => $ffmpeg {global param} {input file param} -i {input file} {output file param} {output
     * file}
     *
     * @link http://www.ruanyifeng.com/blog/2020/01/ffmpeg.html
     *       bundle command with double quote to  avoid filePath which contains spaces. -c specific
     *       encoders ; -c copy direct copy, not encode again
     * @notice FFmpeg cannot edit existing files in-place. input should not be same as output Path
     */
//  -threads 4 -preset ultrafast
    FFMPEG_PATH("FFMPEG System Environment Path", "ffmpeg "),
    FFPROBE_PATH("FFPROBE System Environment Path", "ffprobe "),
    SIMPLE_CLIP("IN-START-END-OUT", " -ss %s -i \"%s\" -to %s -c copy \"%s\""),
    CREATE_COVER("TIME-IN-OUT", " -ss %s -i \"%s\" -vframes 1 -q:v 1 \"%s\""),
    REPLACE_COVER("IN-PIC-OUT",
            " -i \"%s\" -i \"%s\" -map 1 -map 0 -c copy -disposition:0 attached_pic -y \"%s\""),
    MERGE_VIDEO("MERGE VIDEO", " -i \"concat:%s\" -c copy %s"),

    ;

    private final String cmd;

    FFMPEGEnum(String parameter, String cmd) {
        this.cmd = cmd;
    }

    public String normalCMD() {
        if (Arrays.asList(FFMPEGEnum.FFMPEG_PATH, FFPROBE_PATH).contains(this)) {
            return this.cmd;
        }
        return FFMPEG_PATH.cmd + this.cmd;
    }

    public String forceCoverCMD() {
        if (Arrays.asList(FFMPEGEnum.FFMPEG_PATH, FFPROBE_PATH).contains(this)) {
            return this.cmd;
        }
        return FFMPEG_PATH.cmd + " -y " + this.cmd;
    }

}
