package com.albedo.java.vo.sys.query;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * Created by somewhere on 2017/3/2.
 */
@Data
@ApiModel
@ToString
public class DictQuery {

    private String code;
    private String filter;

}
