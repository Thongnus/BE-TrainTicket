package com.example.betickettrain.anotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAction {
    String action();              // CREATE, UPDATE, DELETE, etc.
    String entity();              // booking, trip, ticket...
    String description() default ""; // Mô tả tùy chỉnh (tùy chọn)
}
