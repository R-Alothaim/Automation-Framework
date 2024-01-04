package util;

import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
public @interface JiraPolicy {
	boolean logTicketReady();
}
