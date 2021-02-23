package com.huimv.android.basic.convert;

import java.lang.reflect.Type;

public abstract class BasicConvert<F, T> {
	private Class<F> fromCls;
	private Class<T> toCls;

	@SuppressWarnings("unchecked")
	public BasicConvert() {
		Type[] arguments = ((java.lang.reflect.ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments();
		fromCls = (Class<F>) arguments[0];
		toCls = (Class<T>) arguments[1];
	}

	public Class<F> getFromCls() {
		return fromCls;
	}

	public Class<T> getToCls() {
		return toCls;
	}

	public abstract T doCovert(Object value) throws Throwable;
}
