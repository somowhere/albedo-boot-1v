/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.modules.test.domain;

import com.albedo.java.util.annotation.DictType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import org.hibernate.validator.constraints.*;
import com.albedo.java.util.PublicUtil;
import com.baomidou.mybatisplus.annotations.*;
import com.albedo.java.util.annotation.SearchField;
import com.albedo.java.common.persistence.domain.TreeEntity;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 测试树管理Entity 测试树管理
 * @author admin
 * @version 2018-03-21
 */
@TableName(value = "test_tree")
@Data @ToString @NoArgsConstructor @AllArgsConstructor
public class TestTree extends TreeEntity<TestTree> {

	private static final long serialVersionUID = 1L;
	/** F_CODE code_  :  机构编码 */
	public static final String F_CODE = "code";
	/** F_SQL_CODE code_  :  机构编码 */
	public static final String F_SQL_CODE = "code_";
	/** F_GRADE grade_  :  机构等级 */
	public static final String F_GRADE = "grade";
	/** F_SQL_GRADE grade_  :  机构等级 */
	public static final String F_SQL_GRADE = "grade_";
	/** F_EN en_  :  英文 */
	public static final String F_EN = "en";
	/** F_SQL_EN en_  :  英文 */
	public static final String F_SQL_EN = "en_";
	/** F_TYPE type_  :  组织类型 */
	public static final String F_TYPE = "type";
	/** F_SQL_TYPE type_  :  组织类型 */
	public static final String F_SQL_TYPE = "type_";
	/** F_DEFAULTDATA default_data  :  默认日期 */
	public static final String F_DEFAULTDATA = "defaultData";
	/** F_SQL_DEFAULTDATA default_data  :  默认日期 */
	public static final String F_SQL_DEFAULTDATA = "default_data";

	//columns START
	/** code 机构编码 */@NotBlank @Length(max=64)@TableField("code_")
	private String code;
	/** grade 机构等级 */@NotBlank @Length(max=255)@TableField("grade_")
	private String grade;
	/** en 英文 */@Length(max=255)@TableField("en_")
	private String en;
	/** type 组织类型 */@Length(max=64)@TableField("type_")
	private String type;
	/** defaultData 默认日期 */@NotNull @TableField("default_data")
	private Date defaultData;
	//columns END


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
