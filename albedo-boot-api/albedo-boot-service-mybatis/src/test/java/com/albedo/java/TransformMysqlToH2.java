package com.albedo.java;

import com.albedo.java.common.persistence.annotation.ManyToOne;
import com.albedo.java.modules.sys.domain.Dict;
import com.albedo.java.util.base.Reflections;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by somewhere on 2017/4/19.
 */
public class TransformMysqlToH2 {

    public static void main(String[] args) {

        ManyToOne annotation = Reflections.getAnnotationByClazz(Dict.class, "parent", ManyToOne.class);
        System.out.println(annotation);
    }

    public static void main1(String[] args) throws Exception {
        File file = new File("E:\\soft\\hcxd-pay.sql");
        String content = Files.toString(file, Charsets.UTF_8);

        content = "SET MODE MYSQL;\n\n" + content;

        content = content.replaceAll("`", "");
        content = content.replaceAll("COLLATE.*(?=D)", "");
        content = content.replaceAll("COMMENT.*'(?=,)", "");
        content = content.replaceAll("\\).*ENGINE.*(?=;)", ")");
        content = content.replaceAll("DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", " AS CURRENT_TIMESTAMP");
        content = content.replaceAll("b'0'", "'0'");
        content = content.replaceAll("b'1'", "'1'");
        content = content.replaceAll("'\\\\0',", "'0',");
        content = content.replaceAll("'\u0001'", "'1'");

        content = uniqueKey(content);

        System.out.println(content);
    }

    /**
     * h2的索引名必须全局唯一
     *
     * @param content sql建表脚本
     * @return 替换索引名为全局唯一
     */
    private static String uniqueKey(String content) {
        int inc = 0;
        Pattern pattern = Pattern.compile("(?<=KEY )(.*?)(?= \\()");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group() + inc++);
        }
        matcher.appendTail(sb);
        content = sb.toString();
        return content;
    }


}
