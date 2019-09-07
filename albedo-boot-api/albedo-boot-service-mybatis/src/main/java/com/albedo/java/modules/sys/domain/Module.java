package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.TreeEntity;
import com.albedo.java.util.StringUtil;
import com.albedo.java.util.annotation.DictType;
import com.albedo.java.util.annotation.SearchField;
import com.albedo.java.util.domain.RequestMethod;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

/**
 * Copyright 2013 albedo All right reserved Author somewhere Created on 2013-10-23 下午4:29:21
 */
@TableName("sys_module_t")
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Module extends TreeEntity<Module> {

    public static final String F_ID = "id";
    /*** 菜单模块 MENUFLAG = 0 */
    public static final String TYPE_MENU = "1";
    /*** 权限模块 MODULEFLAG = 1 */
    public static final String TYPE_OPERATE = "2";
    public static final String F_PERMISSION = "permission";
    private static final long serialVersionUID = 1L;
    /*** 模块类型 0 菜单模块 1权限模块 */
    @TableField("type_")
    @SearchField
    @DictType(name = "sys_module_type")
    private String type;
    /*** 目标 */
    @TableField("target_")
    private String target;
    /*** 请求方法*/
    @TableField("request_method")
    @DictType(name = "sys_request_method")
    private String requestMethod;
    /*** 链接地址 */
    @TableField("url_")
    private String url;
    /*** 图标class */
    @TableField("icon_cls")
    private String iconCls;
    /*** 权限标识 */
    @TableField("permission_")
    @SearchField
    private String permission;
    /*** 针对顶层菜单，0 普通展示下级菜单， 1以树形结构展示 */
    @TableField("show_type")
    private String showType;
    /*** 服务名称 */
    @TableField("microservice_")
    private String microservice;

//    @ManyToMany
//    @JoinTable(
//            name = "sys_role_module_t",
//            joinColumns = {@JoinTableField("module_id", referencedColumnName = "id_")},
//            inverseJoinColumns = {@JoinTableField("role_id", referencedColumnName = "id_")})
//    @JSONField(serialize = false)
//    private Set<Role> roles = Sets.newHashSet(); // 拥有角色列表

    /*** 父模块名称 */
    @TableField(exist = false)
    private String parentName;


    public Module(String id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHref() {
        if (url != null) {
            int indexSplit = url.indexOf(StringUtil.SPLIT_DEFAULT);
            return indexSplit == -1 ? url : url.substring(0, indexSplit);
        }
        return url;
    }

    public String getHrefName() {
        String temp = getHref();
        return StringUtil.toCamelCase(temp, '/');
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod.name();
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public boolean isShow() {
        return FLAG_NORMAL.equals(status);
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
