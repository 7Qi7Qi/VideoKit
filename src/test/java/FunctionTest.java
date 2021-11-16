import enums.ProcessTypeEnum;
import java.io.File;
import kit.VideoKit;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FunctionTest {

    public static File file;
    @Before
    public void init() {
        String path = "G:\\迅雷\\#";
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
        long start = System.currentTimeMillis();
        VideoKit videoKit = new VideoKit();
        videoKit.batchProcess("G:\\迅雷\\#", ProcessTypeEnum.FIX_DOWNLOAD);
        System.out.println(System.currentTimeMillis() - start);
    }
}
