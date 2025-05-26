package com.bonju.review.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MarkdownLengthValidator.class)
public @interface MarkdownLength {
  String message() default "내용은 500자를 초과할 수 없습니다.";
  int max() default 500;
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
