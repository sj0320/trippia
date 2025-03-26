package com.trippia.travel.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)  // 파라미터에서만 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
@Documented
public @interface CurrentUser {
}
