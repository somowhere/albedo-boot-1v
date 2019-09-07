package com.albedo.java.util;

import com.albedo.java.common.config.ApplicationProperties;
import com.albedo.java.modules.sys.domain.Dict;
import com.albedo.java.modules.sys.service.DictService;
import com.albedo.java.util.domain.DictVm;
import com.albedo.java.util.spring.SpringContextHolder;
import com.albedo.java.vo.base.SelectResult;
import com.albedo.java.vo.sys.query.DictQuery;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据字典工具类 copyright 2014 albedo all right reserved author somewhere created on
 * 2015年1月27日 上午9:52:55
 */
public class DictUtil {
    public static final String CACHE_DICT_MAP = "dictMap";
    public static final String CACHE_DICT_LIST = "dictList";
    public static ApplicationProperties applicationProperties = SpringContextHolder.getBean(ApplicationProperties.class);
    public static DictService dictService = SpringContextHolder.getBean(DictService.class);
    public static CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);
    private static Map<String, String> dataMap = Maps.newHashMap();

    private static boolean cluster = applicationProperties.getCluster();

//    private static JmsMessagingTemplate jms = SpringContextHolder.getBean(JmsMessagingTemplate.class);

    /**
     * 清空ehcache中所有字典对象
     */
    public static void clearCache() {
        if (!cluster) {
            dataMap.remove(CACHE_DICT_MAP);
            dataMap.remove(CACHE_DICT_LIST);
        }
        RedisUtil.removeUser(CACHE_DICT_LIST);
        RedisUtil.removeUser(CACHE_DICT_MAP);
//        cacheManager.getCache(DictRepository.CACHE_BY_FIND_CITYS).clear();
//        cacheManager.getCache(DictRepository.CACHE_BY_FIND_PRODUCTS).clear();
//        cacheManager.getCache(DictRepository.CACHE_BY_FIND_ORGS).clear();
        // 通知app来取数据字典
//        JmsMessageInfo result = new JmsMessageInfo();
//        Map<String, List<SelectResult>> codes = dictService.findCodes(null);
//        List<Dict> codes = getDictList();
//        result.setData(codes);
//        result.setMessageType("SYNC_AUDIT_DICT");
//        result.setUuidCode(PublicUtil.getUUID());
//        result.setSendTime(System.currentTimeMillis());
//        jms.convertAndSend(BizConstants.APP_LISTENING_DESTINATION, JSON.toJSONString(result));
    }

    /**
     * 获取ehcache中所有字典对象
     */
    public static List<Dict> getDictList() {
        String dictListJson = null;
        List<Dict> dictList = null;
        if (cluster) {
            dictListJson = RedisUtil.getUserStr(CACHE_DICT_LIST);
        } else {
            dictListJson = dataMap.get(CACHE_DICT_LIST);
            if (PublicUtil.isEmpty(dictListJson))
                dictListJson = RedisUtil.getUserStr(CACHE_DICT_LIST);
        }

        if (PublicUtil.isEmpty(dictListJson)) {
            dictList = dictService.findAllByStatusOrderBySortAsc(Dict.FLAG_NORMAL);
            dictListJson = Json.toJSONString(dictList, Dict.F_PARENT, Dict.F_CREATOR, Dict.F_MODIFIER);
            RedisUtil.putUser(CACHE_DICT_LIST, dictListJson);
        }

        if (!cluster && PublicUtil.isNotEmpty(dictListJson)) {
            dataMap.put(CACHE_DICT_LIST, dictListJson);
        }
        if (PublicUtil.isNotEmpty(dictListJson) && PublicUtil.isEmpty(dictList)) {
            dictList = Json.parseArray(dictListJson, Dict.class);
        }

        return dictList;
    }

    /**
     * 获取ehcache中所有字典对象
     */
    public static Map<String, List<Dict>> getDictMap() {
        Map<String, List<Dict>> dictMap = Maps.newHashMap();
        String dictMapJson = null;
        if (cluster) {
            dictMapJson = RedisUtil.getUserStr(CACHE_DICT_MAP);
        } else {
            dictMapJson = dataMap.get(CACHE_DICT_MAP);
            if (PublicUtil.isEmpty(dictMapJson))
                dictMapJson = RedisUtil.getUserStr(CACHE_DICT_MAP);
        }
        if (PublicUtil.isEmpty(dictMapJson) || dictMapJson.equals("{}")) {
            String parentCode = null;
            List<Dict> list = DictUtil.getDictList();
            for (Dict dict : list) {
                if (PublicUtil.isNotEmpty(dict.getParentCode())) {
                    parentCode = dict.getParentCode();
                    List<Dict> tempList = dictMap.get(parentCode);
                    if (tempList != null) {
                        tempList.add(dict);
                    } else {
                        dictMap.put(parentCode, Lists.newArrayList(dict));
                    }
                }
            }
            dictMapJson = Json.toJSONString(dictMap, Dict.F_PARENT, Dict.F_CREATOR, Dict.F_MODIFIER);
            RedisUtil.putUser(CACHE_DICT_MAP, dictMapJson);
        }

        if (!cluster && PublicUtil.isNotEmpty(dictMapJson)) {
            dataMap.put(CACHE_DICT_MAP, dictMapJson);
        }

        if (PublicUtil.isNotEmpty(dictMapJson) && PublicUtil.isEmpty(dictMap)) {
            JSONObject jsonObject = Json.parseObject(dictMapJson);
            // 将json字符串转换成jsonObject
            Iterator<String> it = jsonObject.keySet().iterator();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = it.next();
                String value = jsonObject.getString(key);
                dictMap.put(key, Json.parseArray(value, Dict.class));
            }
        }

        return dictMap;
    }

    /**
     * 根据code 和 原始值 获取数据字典name
     *
     * @param code
     * @param val
     * @return
     */
    public static String getVal(String code, String val) {
        Dict dict = getDictByCodeAndVal(code, val);
        return dict == null ? null : dict.getName();
    }

    /**
     * 根据code 和 原始值 获取数据字典name
     *
     * @param code
     * @param values
     * @return
     */
    public static String getNamesByValues(String code, String values) {
        String[] split = values.split(StringUtil.SPLIT_DEFAULT);
        List<String> names = Lists.newArrayList();
        List<Dict> dictList = getDictListBycode(code);
        for (Dict dict : dictList) {
            for (String s : split) {
                if (PublicUtil.isNotEmpty(dict.getId()) && dict.getVal().equals(s)) {
                    names.add(dict.getName());
                }
            }
        }
        return StringUtils.join(names, StringUtil.SPLIT_DEFAULT);
    }

    /**
     * 根据code 和 name 获取数据字典原始值 下级
     *
     * @param code
     * @param name
     * @return
     */
    public static String getValByName(String code, String name) {
        List<Dict> dictList = getDictListBycode(code);
        for (Dict dict : dictList) {
            if (PublicUtil.isNotEmpty(dict.getId()) && dict.getName().equals(name)) {
                return dict.getVal();
            }
        }
        return null;
    }

    public static String getNameByVal(String code, Object val) {
        String valStr = val == null ? "" : String.valueOf(val);
        return getNameByVal(code, valStr);
    }

    /**
     * 根据code 和 name 获取数据字典原始值 下级
     *
     * @param code
     * @param val
     * @return
     */
    public static String getNameByVal(String code, String val) {
        List<Dict> dictList = getDictListBycode(code);
        for (Dict dict : dictList) {
            if (PublicUtil.isNotEmpty(dict.getId()) && dict.getVal().equals(val)) {
                return dict.getName();
            }
        }
        return null;
    }

    /**
     * 根据val 和 原始值 获取数据字典name
     *
     * @param code
     * @param val
     * @return
     */
    public static String getVal(String code, String val, String defaultStr) {
        Dict dict = getDictByCodeAndVal(code, val);
        return dict == null ? defaultStr : dict.getName();
    }

    /**
     * 根据code 和 编码 获取数据字典name
     *
     * @param code
     * @param codeVal
     * @return
     */
    public static String getCode(String code, String codeVal) {
        Dict dict = null;
        List<Dict> dictList = getDictListBycode(code);
        for (Dict item : dictList) {
            if (PublicUtil.isNotEmpty(item.getId()) && item.getCode().equals(codeVal)) {
                dict = item;
                break;
            }
        }
        return dict == null ? null : dict.getName();
    }

    /**
     * 根据code 和 原始值 获取数据字典对象
     *
     * @param code
     * @param val
     * @return
     */
    public static Dict getValItem(String code, String val) {
        Dict dict = getDictByCodeAndVal(code, val);
        return dict == null ? null : dict;
    }

    /**
     * 根据code 和 编码 获取数据字典对象
     *
     * @param code
     * @param codeVal
     * @return
     */
    public static Dict getCodeItem(String code, String codeVal) {
        Dict dict = null;
        List<Dict> dictList = getDictListBycode(code);
        for (Dict item : dictList) {
            if (PublicUtil.isNotEmpty(item.getId()) && item.getCode().equals(codeVal)) {
                dict = item;
                break;
            }
        }
        return dict == null ? null : dict;
    }

    /**
     * 根据code 和 编码 获取数据字典对象
     *
     * @param code
     * @param val
     * @return
     */
    public static Dict getCodeItemByVal(String code, String val) {
        Dict dict = null;
        List<Dict> dictList = getDictListBycode(code);
        for (Dict item : dictList) {
            if (PublicUtil.isNotEmpty(item.getId()) && item.getVal().equals(val)) {
                dict = item;
                break;
            }
        }
        return dict == null ? null : dict;
    }

    /**
     * 根据code 和 编码 获取数据字典对象
     *
     * @param code
     * @param val
     * @return
     */
    public static String getCodeValByVal(String code, String val) {
        Dict dict = null;
        List<Dict> dictList = getDictListBycode(code);
        for (Dict item : dictList) {
            if (PublicUtil.isNotEmpty(item.getId()) && item.getVal().equals(val)) {
                dict = item;
                break;
            }
        }
        return dict == null ? null : dict.getVal();
    }

    /**
     * 根据code 和 编码 获取数据字典对象
     *
     * @param code
     * @param val
     * @return
     */
    public static String getCodeNameByVal(String code, String val) {
        Dict dict = null;
        List<Dict> dictList = getDictListBycode(code);
        for (Dict item : dictList) {
            if (PublicUtil.isNotEmpty(item.getId()) && item.getVal().equals(val)) {
                dict = item;
                break;
            }
        }
        return dict == null ? null : dict.getName();
    }

    /**
     * 根据code 和 编码 获取数据字典对象
     *
     * @param code
     * @return
     */
    public static Dict getCodeItem(String code) {
        Dict dict = null;
        List<Dict> list = DictUtil.getDictList();
        for (Dict item : list) {
            if (item.getCode().equals(code)) {
                dict = item;

            }
        }
        return dict == null ? null : dict;
    }

    /**
     * 根据code 和 编码 获取数据字典对象 本级
     *
     * @param code
     * @return
     */
    public static String getCodeItemVal(String code) {
        Dict dict = getCodeItem(code);
        return dict == null ? null : dict.getVal();
    }

    public static List<Dict> getDictList(DictQuery dictQuery) {
        if (PublicUtil.isNotEmpty(dictQuery.getFilter())) {
            return getDictListFilterVal(dictQuery.getCode(), dictQuery.getFilter());
        }
        return getDictList(dictQuery.getCode());
    }

    public static List<Dict> getDictList(String code) {
        List<Dict> itemList = Lists.newArrayList();
        for (Dict item : getDictListBycode(code)) {
            if (Dict.FLAG_NORMAL.equals(item.getStatus())) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public static List<Dict> getDictListFilterVal(String code, String filters) {
        List<Dict> itemList = Lists.newArrayList();
        List<String> filterList = PublicUtil.isEmpty(filters) ? null : Lists.newArrayList(filters.split(StringUtil.SPLIT_DEFAULT));
        for (Dict item : getDictListBycode(code)) {
            if (Dict.FLAG_NORMAL.equals(item.getStatus())
                && (PublicUtil.isEmpty(filterList) || !filterList.contains(item.getVal()))) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public static List<Dict> getDictListContainVal(String code, String contains) {
        List<Dict> itemList = Lists.newArrayList();
        List<String> filterList = PublicUtil.isEmpty(contains) ? null : Lists.newArrayList(contains.split(StringUtil.SPLIT_DEFAULT));
        for (Dict item : getDictListBycode(code)) {
            if (Dict.FLAG_NORMAL.equals(item.getStatus()) && PublicUtil.isNotEmpty(filterList)
                && filterList.contains(item.getVal())) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public static List<Dict> getAllDictList(String code) {
        return getDictListBycode(code);
    }

    private static Dict getDictByCodeAndVal(String code, String val) {
        List<Dict> dictList = getDictListBycode(code);
        for (Dict dict : dictList) {
            if (PublicUtil.isNotEmpty(dict.getId()) && dict.getVal().equals(val)) {
                return dict;
            }
        }
        return null;
    }

    private static List<Dict> getDictListBycode(String code) {
        List<Dict> dictList = getDictMap().get(code);
        if (dictList == null) {
            dictList = Lists.newArrayList();
        }
        return dictList;
    }

    public static List<DictVm> parseDictVm(List<Dict> dictList) {
        List<DictVm> rsList = null;
        if (PublicUtil.isNotEmpty(dictList)) {
            rsList = Lists.newArrayList();
            for (Dict dict : dictList) {
                rsList.add(new DictVm(dict.getName(), dict.getCode(), dict.getVal()));
            }
        }
        return rsList;
    }

    public static Map<String,List<SelectResult>> getCodes(String codes) {
        return dictService.findCodes(codes);
    }


    public static Map<String,List<SelectResult>> getCodeList(List<String> kindIds) {
        return dictService.findCodeList(kindIds);
    }
}
