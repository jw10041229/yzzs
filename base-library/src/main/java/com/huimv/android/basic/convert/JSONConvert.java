package com.huimv.android.basic.convert;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface JSONConvert {
	// public abstract ConvertEnum;
	// CoverType covertType();

	Class<?> covertCls();

}
