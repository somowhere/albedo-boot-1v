package com.albedo.java.common.persistence.injector;

import com.albedo.java.common.persistence.injector.methods.LogicFindRelationList;
import com.albedo.java.common.persistence.injector.methods.LogicFindRelationPage;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.Configuration;

import java.util.List;
/**
 * 1.逻辑删除字段sql注入
 * 2.多对一关联对象查询sql注入
 * @return
 */
public class EntityMetaSqlInjector extends LogicSqlInjector {

    public final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    @Override
    public List<AbstractMethod> getMethodList() {
        List<AbstractMethod> methodList = super.getMethodList();
        methodList.addAll(Lists.newArrayList(new LogicFindRelationList(), new LogicFindRelationPage()));
        return methodList;
    }

    @Override
    public void injectSqlRunner(Configuration configuration) {
        super.injectSqlRunner(configuration);

    }


}
