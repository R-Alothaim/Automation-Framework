package util;

import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {
	String value();
}

