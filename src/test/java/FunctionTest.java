import enums.ProcessTypeEnum;
import java.io.File;
import kit.VideoKit;
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
        VideoKit videoKit = new VideoKit();
        videoKit.getVideoLength("G:\\迅雷\\#\\ignore\\[脸肿字幕组][720P][魔人]euphoria～地下の戦栗ゲーム、地上のス カトロ地狱。笑う黒幕は……幼なじみ！？编(2).mp4");
    }
}
