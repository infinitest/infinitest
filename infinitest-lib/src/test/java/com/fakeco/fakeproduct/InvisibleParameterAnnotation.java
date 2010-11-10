package com.fakeco.fakeproduct;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(PARAMETER)
public @interface InvisibleParameterAnnotation
{
    // marker
}
