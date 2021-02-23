package com.huimv.android.basic.util;

import android.util.Log;

import com.huimv.android.basic.convert.BasicConvert;
import com.huimv.android.basic.convert.JSONConvert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSONObject 的一些通用操作
 * 
 * @author ye
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JSONUtil {
	private static final String TAG = "JsonUtil";

	public static List toListMap(JSONArray jsonArray) {
		List result = null;
		if (jsonArray != null) {
			result = new ArrayList();
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					Object obj = jsonArray.get(i);
					if (obj == null) {
						continue;
					}
					if (JSONObject.class.isInstance(obj)) {
						Map map = toMap((JSONObject) obj);
						if (map != null) {
							result.add(map);
						}
					} else if (JSONArray.class.isInstance(obj)) {
						List list = toListMap((JSONArray) obj);
						if (list != null) {
							result.add(list);
						}
					}

				} catch (JSONException e) {
					Log.e(TAG, e.getMessage(), e);
				}

			}
		}
		return result;
	}

	public static String getString(JSONObject json, String key,
			String defaultValue) {
		String result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getString(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		if (result == null && defaultValue != null) {
			result = defaultValue;
		}
		return result;
	}

	public static double getDouble(JSONObject json, String key,
			double defaultValue) {
		Double result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getDouble(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static int getInt(JSONObject json, String key, int defaultValue) {
		Integer result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getInt(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static long getLong(JSONObject json, String key, long defaultValue) {
		Long result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getLong(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static boolean getBoolean(JSONObject json, String key,
			boolean defaultValue) {
		Boolean result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getBoolean(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static JSONArray getJSONArray(JSONObject json, String key) {
		JSONArray result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getJSONArray(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}

	public static <T> T[] getArray(JSONObject json, String key, Class<T> cls) {
		JSONArray jsonArray = getJSONArray(json, key);
		if (jsonArray != null) {
			return toArray(jsonArray, cls);
		} else {
			return null;
		}
	}

	public static <T> List<T> getList(JSONObject json, String key, Class<T> cls) {
		JSONArray jsonArray = getJSONArray(json, key);
		if (jsonArray != null) {
			return toList(jsonArray, cls);
		} else {
			return null;
		}
	}

	public static JSONObject getJSONObject(JSONObject json, String key) {
		JSONObject result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.getJSONObject(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}

	public static JSONObject getJSONObject(JSONArray jsonArray, int position) {
		JSONObject result = null;
		if (jsonArray != null && position < jsonArray.length()) {
			try {
				result = jsonArray.getJSONObject(position);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}

	public static Object get(JSONArray jsonArray, int position) {
		Object result = null;
		if (jsonArray != null && position < jsonArray.length()) {
			try {
				result = jsonArray.get(position);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}

	public static Object get(JSONObject json, String key) {
		Object result = null;
		if (json != null && json.has(key) && !json.isNull(key)) {
			try {
				result = json.get(key);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}

	public static <T> T getBean(JSONObject json, String key, Class<T> cls) {
		JSONObject value = getJSONObject(json, key);
		return toBean(value, cls);
	}

	public static <T> List<T> toList(JSONArray jsonArr, Class<T> cls) {
		if (jsonArr == null || cls == null) {
			return null;
		}
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < jsonArr.length() && !jsonArr.isNull(i); i++) {
			try {
				Object obj = jsonArr.get(i);
				T item = null;
				if (JSONObject.class.isInstance(obj)) {
					item = toBean((JSONObject) obj, cls);
				} else
				// if (JSONArray.class.isInstance(obj)) {
				// throw new RuntimeException("不支持多重数组");
				// } else
				{
					item = (T) obj;
				}
				result.add(item);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}

	public static <T> T[] toArray(JSONArray jsonArr, Class<T> cls) {
		if (jsonArr == null || cls == null) {
			return null;
		}
		List<T> list = toList(jsonArr, cls);
		T[] arr = (T[]) Array.newInstance(cls, list.size());
		return list.toArray(arr);
	}

	public static Map toMap(JSONObject json) {
		Map map = null;
		if (json != null && !json.equals(JSONObject.NULL)) {
			map = new HashMap();
			Iterator iterator = json.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				try {
					map.put(key, json.get(key));
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
		return map;
	}

	public static <T> T toBean(JSONObject json, Class<T> cls) {
		if (json == null || cls == null) {
			return null;
		}
		T instance = null;
		try {
			instance = cls.newInstance();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
		List<Field> fields = BeanTools.getAllFieldsList(cls);
		// Field[] fields = BeanTools.getAllFieldsArr(cls);
		for (Field field : fields) {
			String fieldName = field.getName();
			if (!json.has(fieldName) || json.isNull(fieldName)) {
				continue;
			}
			field.setAccessible(true);
			Class<?> fieldClass = field.getType();
			BasicConvert<?, ?> covert = null;
			Object value = null;
			try {
				JSONConvert jSONConvert = field
						.getAnnotation(JSONConvert.class);
				if (jSONConvert != null) {
					Class<?> covertCls = jSONConvert.covertCls();
					if (BasicConvert.class.isAssignableFrom(covertCls)) {
						covert = (BasicConvert<?, ?>) covertCls.newInstance();
						fieldClass = covert.getFromCls();
					}
				}

				if (Integer.TYPE == fieldClass || Integer.class == fieldClass) {
					value = Integer.valueOf(json.getInt(fieldName));

				} else if (Long.TYPE == fieldClass || Long.class == fieldClass) {
					value = Long.valueOf(json.getLong(fieldName));

				} else if (Double.TYPE == fieldClass
						|| Double.class == fieldClass) {
					value = Double.valueOf(json.getDouble(fieldName));

				} else if (Boolean.TYPE == fieldClass
						|| Boolean.class == fieldClass) {
					value = Boolean.valueOf(json.getBoolean(fieldName));

				} else if (Float.TYPE == fieldClass
						|| Float.class == fieldClass) {
					value = Float.valueOf((float) json.getDouble(fieldName));

				} else if (Short.TYPE == fieldClass
						|| Short.class == fieldClass) {
					value = Short.valueOf((short) json.getInt(fieldName));

				} else if (String.class == fieldClass) {
					value = json.getString(fieldName);

				} else if (Map.class.isAssignableFrom(fieldClass)) {
					value = toMap(json.getJSONObject(fieldName));

				} else if (List.class.isAssignableFrom(fieldClass)) {
					JSONArray jsonArr = json.getJSONArray(fieldName);
					Class<?> compType = GenericsUtils
							.getFieldGenericType(field);
					value = toList(jsonArr, compType);

				} else if (fieldClass.isArray()) {
					JSONArray jsonArr = json.getJSONArray(fieldName);
					Class<?> compType = fieldClass.getComponentType();
					value = toArray(jsonArr, compType);

				} else {
					value = json.get(fieldName);
					if (JSONObject.class.isInstance(value)) {
						value = toBean((JSONObject) value, fieldClass);
					}

				}
				if (covert != null) {
					value = covert.doCovert(value);
				}
				field.set(instance, value);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
			}

		}
		return instance;
	}

	public static Object doCovert(Object remote, Class<?> resultBodyClass) {
		if (remote instanceof JSONArray) {
			JSONArray jsonArr = (JSONArray) remote;
			return JSONUtil.toList(jsonArr, resultBodyClass);
		} else if (remote instanceof JSONObject) {
			JSONObject json = (JSONObject) remote;
			return JSONUtil.toBean(json, resultBodyClass);
		} else {
			return null;
		}
	}
	// public statist<Field> getAllFields(Class<?> cls) {
	// List<Field> result = new ArrayList<Field>();
	// do {
	// Field[] fields = cls.getDeclaredFields();
	// if (fields != null) {
	// result.addAll(Arrays.asList(fields));
	// }
	// } while ((cls = cls.getSuperclass()) != null);
	// return result;

	// Class superCls = cls.getSuperclass();
	// if (superCls == null) {
	// return fields;
	// } else {
	// Field[] parentFields = superCls.getDeclaredFields();
	// if (parentFields == null || parentFields.length <= 0) {
	// return fields;
	// }
	// Field[] newArrs = new Field[fields.length + parentFields.length];
	// System.arraycopy(fields, 0, newArrs, 0, fields.length);
	// System.arraycopy(parentFields, 0, newArrs, fields.length,
	// parentFields.length);
	// return newArrs;
	// }
	// }
}
