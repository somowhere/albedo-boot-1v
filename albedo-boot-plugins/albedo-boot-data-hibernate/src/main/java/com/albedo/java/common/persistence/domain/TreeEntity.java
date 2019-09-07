package com.albedo.java.common.persistence.domain;

import com.albedo.java.common.persistence.pk.IdGen;
import com.albedo.java.util.annotation.SearchField;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.Objects;

/**
 * 数据TreeEntity类
 *
 * @author somewhere version 2013-12-27 下午12:27:10
 */
@MappedSuperclass
public abstract class TreeEntity<T extends DataEntity> extends TreeDataEntity<T> {

    /*** ID */
    public static final String F_ID = "id";
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_")
    @SearchField
    protected String id; // 编号

    public TreeEntity() {
        super();
    }

    public static boolean isRoot(String id) {
        return id != null && id.equals("1");
    }

    @PrePersist
    public void prePersist() {
        if (this.id != ROOT) {
            this.id = IdGen.uuid();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(serialize = false)
    public boolean isRoot() {
        return isRoot(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TreeEntity treeEntity = (TreeEntity) o;
        if (treeEntity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), treeEntity.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
