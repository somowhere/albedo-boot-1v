package com.albedo.java.common.persistence.domain;

import com.albedo.java.util.config.SystemConfig;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * 通常的数据基类 copyright 2014 albedo all right reserved author somewhere created on 2014年12月31日 下午1:57:09
 */

@Data
public abstract class GeneralEntity<T extends Model>  extends Model<T> implements Serializable {

    /*** 状态 审核 */
    public static final Integer FLAG_AUDIT = 1;
    /*** 状态 正常 */
    public static final Integer FLAG_NORMAL = 0;
    /*** 状态 停用 */
    public static final Integer FLAG_UNABLE = -1;
    /*** 状态 已删除 */
    public static final Integer FLAG_DELETE = -2;
    /**
     * 状态（-1：删除；0：停用 1：正常）
     */
    public static final String F_STATUS = "status";
    /*** ID */
    public static final String F_ID = "id";
    public static final String F_CREATEDBY = "createdBy";
    public static final String F_CREATOR = "creator";
    public static final String F_CREATEDDATE = "createdDate";
    public static final String F_LASTMODIFIEDBY = "lastModifiedBy";
    public static final String F_MODIFIER = "modifier";
    public static final String F_LASTMODIFIEDDATE = "lastModifiedDate";
    public static final String F_VERSION = "version";
    public static final String F_DESCRIPTION = "description";

    public static final String F_SQL_ID = "id_";
    public static final String F_SQL_CREATEDBY = "created_by";
    public static final String F_SQL_CREATEDDATE = "created_date";
    public static final String F_SQL_LASTMODIFIEDBY = "last_modified_by";
    public static final String F_SQL_MODIFIER = "modifier";
    public static final String F_SQL_LASTMODIFIEDDATE = "last_modified_date";
    public static final String F_SQL_VERSION = "version_";
    public static final String F_SQL_DESCRIPTION = "description_";
    public static final String F_SQL_STATUS = "status_";
    private static final long serialVersionUID = 1L;


    @Transient
    @JSONField(serialize = false)
    @TableField(exist = false)
    protected String dbName = SystemConfig.get("jdbc.type");




}
