package com.example.splitly.application.mapper.qualifier;

import org.mapstruct.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.CLASS)
public @interface WithoutRoleMapping {
}
