package kit;

public class ProgressBarKit {

    private static final int PROGRESS_SIZE = 50;
    private final int RATE;
    private final int SPEED;
    private final long interval;

    private String finish;
    private String unFinish;
    private String target;

    public static class Builder {

        private int rate = 2;
        private int speed = 1;
        private long interval = 50L;

        public Builder(){}

        public Builder rate(int rate) {
            this.rate = rate;
            return this;
        }
        public Builder speed(int speed) {
            this.speed = speed;
            return this;
        }
        public Builder waitMs(int interval) {
            this.interval = interval;
            return this;
        }

        public ProgressBarKit builder() {
            return new ProgressBarKit(this);
        }
    }

    private ProgressBarKit(Builder builder) {
        this.RATE = builder.rate;
        this.SPEED = builder.speed;
        this.interval = builder.interval;
        this.refresh();
    }

    public void refresh() {
        System.out.print("Progress:");
        unFinish = getNChar(PROGRESS_SIZE, '─');
        finish = getNChar(0, '█');
        target = String.format("%3d%%[%s%s]", 0, finish, unFinish);
        System.out.print(target);
    }

    public void printProgress(int index) {
        unFinish = getNChar(PROGRESS_SIZE - index / RATE * SPEED, '─');
        finish = getNChar(index / RATE * SPEED, '█');
        if (index * SPEED > 100) {
            return;
        }
        target = String.format("%3d%%├%s%s┤", index * SPEED, finish, unFinish);
        System.out.print(getNChar(PROGRESS_SIZE + 6, '\b') + target);
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private String getNChar(int num, char ch){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
