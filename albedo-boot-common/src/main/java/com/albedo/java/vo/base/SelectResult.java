package com.albedo.java.vo.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author somewhere
 * @date 2017/3/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectResult implements Serializable {

    private static final long serialVersionUID = 1848699240546373048L;
    private String value;
    private String label;
    private Integer version;

    public SelectResult(String value, String label) {
        this.value = value;
        this.label = label;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SelectResult idEntity = (SelectResult) o;
        if (idEntity.toString() == null || toString() == null) {
            return false;
        }
        return Objects.equals(toString(), idEntity.toString());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }


}
