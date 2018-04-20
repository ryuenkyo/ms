package com.mingsoft.cms.util;

import java.util.Arrays;

import cn.hutool.core.util.ArrayUtil;


/**
 * 
 * @ClassName:  ArrysUtil   
 * @Description:TODO(数组工具类)   
 * @author: 铭飞开发团队
 * @date:   2018年4月20日 下午17:02:00   
 *     
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public class ArrysUtil {
	/**
	 * 降序排序
	 * @param str
	 * @param delimiter
	 * 			分隔符
	 * @return
	 */
	public static String sort(String str,String delimiter){
		String[] articleTypeArrays = str.split(delimiter);
		Arrays.sort(articleTypeArrays);
		return ArrayUtil.join(articleTypeArrays, delimiter);
	}
}
