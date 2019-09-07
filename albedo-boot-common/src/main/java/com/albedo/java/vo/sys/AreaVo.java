/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.vo.sys;

import com.albedo.java.vo.base.TreeEntityVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.validation.constraints.Size;

/**
 * 区域Entity 区域
 *
 * @author admin
 * @version 2017-11-10
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AreaVo extends TreeEntityVo {

    /**
     * F_SHORTNAME short_name  :  区域简称
     */
    public static final String F_SHORTNAME = "shortName";
    /**
     * F_GRADE grade_  :  区域等级
     */
    public static final String F_GRADE = "grade";
    /**
     * F_CODE code_  :  区域编码
     */
    public static final String F_CODE = "code";
    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * shortName 区域简称
     */
    @Size(max = 32)
    private String shortName;
    /**
     * grade 区域等级(1省/2市/3区县)
     */

    private Integer grade;
    /**
     * code 区域编码
     */
    @Size(max = 32)
    private String code;

    public AreaVo(String id) {
    }
    //columns END

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AreaVo == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AreaVo other = (AreaVo) obj;
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }
}
