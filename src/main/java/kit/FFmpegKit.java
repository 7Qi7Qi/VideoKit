package kit;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//https://github.com/bramp/ffmpeg-cli-wrapper
public class FFmpegKit {

    protected final static Logger logger = LoggerFactory.getLogger(FFmpegKit.class);

    private static FFmpeg fFmpeg;
    private static FFprobe fFprobe;
    private static FFmpegExecutor executor;
    private static volatile FFmpegKit kit;

    private FFmpegKit(){}

    public static FFmpegKit getInstance() {
        if (kit == null) {
            synchronized (FFmpegKit.class) {
                if (kit == null) {
                    kit = new FFmpegKit();
                    try {
                        fFmpeg = new FFmpeg(new ProcessFunction() {
                            final ProcessFunction runner = new RunProcessFunction();
                            @Override
                            public Process run(List<String> args) throws IOException {
                                System.out.println(args);
                                return runner.run(args);
                            }
                        });
                        fFprobe = new FFprobe();
                        executor = new FFmpegExecutor(fFmpeg);
                    }catch (IOException e) {
                        throw new RuntimeException("ffmpeg getInstance failed, " + e.getMessage());
                    }
                }
            }
        }
        return kit;
    }

    public void createJob(FFmpegBuilder builder) {
        //FFmpegJob job = executor.createJob(builder,new ProgressListener() {
        //
        //    // Using the FFmpegProbeResult determine the duration of the input
        //    final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
        //
        //    @Override
        //    public void progress(Progress progress) {
        //        double percentage = progress.out_time_ns / duration_ns;
        //        System.out.printf(
        //                "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx%n",
        //                percentage * 100,
        //                progress.status,
        //                progress.frame,
        //                FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
        //                progress.fps.doubleValue(),
        //                progress.speed
        //        );
        //    }
        //});
        //job.run();
    }

    public void createJobToCut(String input, String output, String startTime, String endTime) {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .overrideOutputFiles(true)
                .addOutput(output)
                .addExtraArgs("-ss", startTime)
                .addExtraArgs("-to", endTime)
                .setAudioCodec("copy")
                .done();
        //this.createJob(builder);
        double duration_ns = this.getVideoLength(input)  * TimeUnit.SECONDS.toNanos(1);
        // Using the FFmpegProbeResult determine the duration of the input
        FFmpegJob job = executor.createJob(builder, progress -> {
            double percentage = progress.out_time_ns / duration_ns;
            System.out.printf(
                    "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx%n",
                    percentage * 100,
                    progress.status,
                    progress.frame,
                    FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                    progress.fps.doubleValue(),
                    progress.speed
            );
        });
        job.run();
    }

    public void createJobToCut(String input, String output, String startTime) {
        double videoLength = getVideoLength(input);
        if (videoLength == -1) {
            throw new RuntimeException("can't get video length, check video ===> " + input);
        }
        this.createJobToCut(input, output, startTime, String.valueOf(videoLength));
    }



    /**
     * get video length
     * @param path video path
     * @return video length in units of millisecond
     */
    public double getVideoLength(String path) {
        double duration = -1;
        try {
            FFmpegProbeResult probeResult = fFprobe.probe(path);
            FFmpegFormat result = probeResult.getFormat();
            duration = result.duration;
        } catch (IOException e) {
            logger.error("{} get media info error=> {}", path, e.getMessage());

        }
        return duration;
    }

}
