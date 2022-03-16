import enums.ProcessTypeEnum;
import java.io.File;
import kit.FFmpegKit;
import kit.VideoKit;
import main.OtherKitsService;
import org.junit.Before;
import org.junit.Test;

public class FunctionTest {

    public static File file;
    @Before
    public void init() {
        String path = "G:\\#";
        file = new File(path);
    }

    @Test
    public void simpleTest() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String substring = file.getParent().substring(file.getParent().lastIndexOf('\\') + 1);
        }
        long end1 = System.currentTimeMillis();
        System.out.println("end1-start = " + (end1 - start));
        for (int i = 0; i < 100000; i++) {
            String name = new File(file.getParent()).getName();
        }
        long end2 = System.currentTimeMillis();
        System.out.println("(end2-end1) = " + (end2 - end1));
    }

    @Test
    public void multiThreading() {
        long start = System.currentTimeMillis();
        VideoKit videoKit = new VideoKit();
        videoKit.batchProcess(file.getPath(), ProcessTypeEnum.FIX_DOWNLOAD);
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void main(String[] args) {
        FFmpegKit kit = FFmpegKit.getInstance();
        String input = "";
        String output = "";
        kit.createJobToCut(input, output, "10");
        long start = System.currentTimeMillis();
        kit.getVideoLength(input);
        long l = System.currentTimeMillis() - start;
        System.out.println(l);

    }

    @Test
    public void messagePrint2Test() {
        OtherKitsService.messagePrint2("");
    }
}
