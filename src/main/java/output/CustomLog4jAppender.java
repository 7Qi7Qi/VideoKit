package output;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class CustomLog4jAppender extends AppenderSkeleton {

    private String account;

    @Override
    protected void append(LoggingEvent loggingEvent) {
        System.out.println(account + " ===>"+ loggingEvent.getMessage());
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
