package com.rest.kouponify.common;

import java.util.Map;

/**
 * Created by vijay on 2/15/16.
 * @param <T>
 */
public interface RowUnmapper<T> {
	Map<String, Object> mapColumns(T t);
}
