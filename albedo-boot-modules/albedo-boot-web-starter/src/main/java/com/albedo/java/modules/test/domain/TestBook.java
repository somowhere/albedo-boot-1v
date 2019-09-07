/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.modules.test.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.albedo.java.util.PublicUtil;
import com.baomidou.mybatisplus.annotations.*;
import com.albedo.java.util.annotation.SearchField;
import com.albedo.java.common.persistence.domain.IdEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.albedo.java.util.annotation.DictType;


import org.hibernate.validator.constraints.*;

/**
 * 测试书籍Entity 测试书籍
 * @author admin
 * @version 2018-03-21
 */
@TableName(value = "test_book")
@Data @ToString @NoArgsConstructor @AllArgsConstructor
public class TestBook extends IdEntity<TestBook, String> {

	private static final long serialVersionUID = 1L;
	/** F_TITLE title_  :  标题 */
	public static final String F_TITLE = "title";
	/** F_SQL_TITLE title_  :  标题 */
	public static final String F_SQL_TITLE = "title_";
	/** F_AUTHOR author_  :  作者 */
	public static final String F_AUTHOR = "author";
	/** F_SQL_AUTHOR author_  :  作者 */
	public static final String F_SQL_AUTHOR = "author_";
	/** F_NAME name_  :  名称 */
	public static final String F_NAME = "name";
	/** F_SQL_NAME name_  :  名称 */
	public static final String F_SQL_NAME = "name_";
	/** F_EMAIL email_  :  邮箱 */
	public static final String F_EMAIL = "email";
	/** F_SQL_EMAIL email_  :  邮箱 */
	public static final String F_SQL_EMAIL = "email_";
	/** F_PHONE phone_  :  手机 */
	public static final String F_PHONE = "phone";
	/** F_SQL_PHONE phone_  :  手机 */
	public static final String F_SQL_PHONE = "phone_";
	/** F_ACTIVATED activated_  :  activated_ */
	public static final String F_ACTIVATED = "activated";
	/** F_SQL_ACTIVATED activated_  :  activated_ */
	public static final String F_SQL_ACTIVATED = "activated_";
	/** F_LANGKEY lang_key  :  key */
	public static final String F_LANGKEY = "langKey";
	/** F_SQL_LANGKEY lang_key  :  key */
	public static final String F_SQL_LANGKEY = "lang_key";
	/** F_ACTIVATIONKEY activation_key  :  activation_key */
	public static final String F_ACTIVATIONKEY = "activationKey";
	/** F_SQL_ACTIVATIONKEY activation_key  :  activation_key */
	public static final String F_SQL_ACTIVATIONKEY = "activation_key";
	/** F_RESETKEY reset_key  :  reset_key */
	public static final String F_RESETKEY = "resetKey";
	/** F_SQL_RESETKEY reset_key  :  reset_key */
	public static final String F_SQL_RESETKEY = "reset_key";
	/** F_RESETDATE reset_date  :  reset_date */
	public static final String F_RESETDATE = "resetDate";
	/** F_SQL_RESETDATE reset_date  :  reset_date */
	public static final String F_SQL_RESETDATE = "reset_date";

	//columns START
	/** title 标题 */@Length(max=32)@TableField("title_")
	private String title;
	/** author 作者 */@NotBlank @Length(max=50)@TableField("author_")
	private String author;
	/** name 名称 */@Length(max=50)@TableField("name_")
	private String name;
	/** email 邮箱 */@Email @Length(max=100)@TableField("email_")@SearchField
	private String email;
	/** phone 手机 */@Length(max=32)@TableField("phone_")
	private String phone;
	/** activated activated_ */@NotNull @TableField("activated_")
	private Integer activated;
	/** langKey key */@Length(max=5)@TableField("lang_key")
	private String langKey;
	/** activationKey activation_key */@Length(max=20)@TableField("activation_key")
	private String activationKey;
	/** resetKey reset_key */@Length(max=20)@TableField("reset_key")
	private String resetKey;
	/** resetDate reset_date */@TableField("reset_date")
	private Date resetDate;
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
