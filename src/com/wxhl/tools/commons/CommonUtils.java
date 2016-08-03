package com.wxhl.tools.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

/**
 * 小小工具
 */
public class CommonUtils {

	/**
	 * 返回一个不重复的字符串
	 *
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

	/**
	 * 把map转换成对象
	 *
	 * @param map
	 * @param clazz
	 * @return 把Map转换成指定类型
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T toBean(Map map, Class<T> clazz) {
		try {
			/*
             * 1. 通过参数clazz创建实例
			 * 2. 使用BeanUtils.populate把map的数据封闭到bean中
			 */
			T bean = clazz.newInstance();
			Converter conv = new Converter() {
				@Override
				public Object convert(Class type, Object value) {
					if (value == null) {
						return null;//如果要转换成值为null，那么直接返回null
					}
					if (!(value instanceof String)) {//如果要转换的值不是String，那么就不转换了，直接返回
						return value;
					}
					String val = (String) value;//把值转换成String

					// 使用SimpleDateFormat进行转换
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						return sdf.parse(val);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}
			};
			ConvertUtils.register(conv, java.util.Date.class);
			BeanUtils.populate(bean, map);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
