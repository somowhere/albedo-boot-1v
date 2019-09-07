/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.albedo.java.common.persistence.service;

import com.albedo.java.common.config.ApplicationProperties;
import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.PageQuery;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.domain.BaseEntity;
import com.albedo.java.common.persistence.domain.GeneralEntity;
import com.albedo.java.common.persistence.repository.BaseRepository;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.QueryUtil;
import com.albedo.java.util.StringUtil;
import com.albedo.java.util.base.Assert;
import com.albedo.java.util.base.Reflections;
import com.albedo.java.util.domain.Order;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Service基类
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Transactional(rollbackFor = Exception.class)
public abstract class BaseService<Repository extends BaseRepository<T, pk>,
    T extends GeneralEntity, pk extends Serializable> extends ServiceImpl<Repository, T> {
    public final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    @Autowired
    public Repository repository;
    private Class<T> persistentClass;
    private String classNameProfix;
    @Autowired
    ApplicationProperties applicationProperties;


    @SuppressWarnings("unchecked")
    public BaseService() {
        Class<?> c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            persistentClass = (Class<T>) parameterizedType[1];
        }

    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public String getClassNameProfix(){
        if (persistentClass!=null && PublicUtil.isEmpty(classNameProfix)){
            classNameProfix =  StringUtil.toFirstLowerCase(persistentClass.getSimpleName())+".";
        }
        return classNameProfix;
    }
    public String getClassNameProfix(String property) {
        return String.format("%s%s", getClassNameProfix(),property);
    }


    public QueryWrapper<T> createEntityWrapper(List<Order> orders, QueryCondition... queryConditions){

        return DynamicSpecifications.
            bySearchQueryCondition(getPersistentClass(), queryConditions).setOrders(orders)
            .toEntityWrapper();
    }
    public QueryWrapper<T> createEntityWrapper(List<QueryCondition> queryConditions){
        return DynamicSpecifications.
            bySearchQueryCondition(getPersistentClass(), queryConditions)
            .toEntityWrapper();
    }
    public QueryWrapper<T> createEntityWrapper(QueryCondition... queryConditions){
        return createEntityWrapper(null, queryConditions);
    }


    public boolean doCheckWithEntity(T entity, Map<String, QueryCondition.Operator> maps) {
        boolean rs = false;
        if (PublicUtil.isNotEmpty(entity)) {
//            Map<String, Object> paramsMap = Maps.newHashMap();
            List<QueryCondition> conditionList = QueryUtil.convertObjectToQueryCondition(entity, maps);
//            String sqlConditionDsf = QueryUtil.convertQueryConditionToStr(conditionList,
//                    null,
//                    paramsMap, true, true);
//            paramsMap.put(DynamicSpecifications.MYBITS_SEARCH_DSF, sqlConditionDsf);
            QueryWrapper<T> entityWrapper = createEntityWrapper(conditionList);
            Integer obj = countBasicAll(entityWrapper);
            if (obj == null || obj == 0) {
                rs = true;
            }
        }
        return rs;
    }

    public boolean doCheckByProperty(T entity) {
        Map<String, QueryCondition.Operator> maps = Maps.newHashMap();
        try {
            maps.put(BaseEntity.F_ID, QueryCondition.Operator.ne);
            maps.put(BaseEntity.F_STATUS, QueryCondition.Operator.ne);
            Reflections.setProperty(entity, BaseEntity.F_STATUS, GeneralEntity.FLAG_DELETE);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return doCheckWithEntity(entity, maps);

    }

    public boolean doCheckByPK(T entity) {
        Map<String, QueryCondition.Operator> maps = Maps.newHashMap();
        try {
            maps.put(BaseEntity.F_STATUS, QueryCondition.Operator.ne);
            Reflections.setProperty(entity, BaseEntity.F_STATUS, GeneralEntity.FLAG_DELETE);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return doCheckWithEntity(entity, maps);
    }



    //	public void checkSave(T entity){
//		if(entity.isNew()){
//			entity.preInssert();
//		}else{
//			entity.preUpdate();
//		}
//	}


    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public T findTopOne(Wrapper<T> wrapper) {
        return findOne(wrapper, 1);
    }
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public T findOne(Wrapper<T> wrapper, int max) {
        IPage<T> iPage = repository.selectPage(new PageQuery(0, max), wrapper);
        return iPage!=null && PublicUtil.isNotEmpty(iPage.getRecords()) ? iPage.getRecords().get(0) : null;
    }
    public List<T> findAll() {
        return repository.selectList(null);
    }
    public List<T> findAll(Map<String, Object> paramsMap) {
        return repository.selectByMap(paramsMap);
    }

    public IPage<T> findAll(Pageable pageable, Wrapper<T> queryWrapper) {
        return super.page(new PageQuery(pageable), queryWrapper);
    }

    public Integer countBasicAll(Wrapper<T> wrapper) {
        return repository.selectCount(wrapper);
    }

    public List<Sort.Order> toOrders(List<Order> orders) {
        List<Sort.Order> orderList = Lists.newArrayList();
        if (PublicUtil.isEmpty(orders)) {
            return orderList;
        }
        for (Order order : orders) {
            if (order == null) {
                continue;
            }
            String property = order.getProperty();
            Order.Direction direction = order.getDirection();
            if (PublicUtil.isEmpty(property) || direction == null) {
                continue;
            }
            orderList.add(new Sort.Order(direction.equals(Order.Direction.asc) ?
                Sort.Direction.ASC : Sort.Direction.DESC, property));
        }
        return orderList;
    }


    /**
     * 动态集合查询
     *
     * @param specificationDetail 动态条件对象
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Integer count(SpecificationDetail specificationDetail) {
        try {
            return repository.selectCount(specificationDetail.toEntityWrapper(getPersistentClass()));
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.buildException(e.getMessage());
        }
        return null;
    }

    /**
     * 动态集合查询
     *
     * @param specificationDetail 动态条件对象
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<T> findAll(SpecificationDetail specificationDetail) {
        return findAll(specificationDetail.toEntityWrapper(getPersistentClass()));
    }
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<T> findAll(Wrapper<T> wrapper) {
        try {
            return repository.selectList(wrapper);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.buildException(e.getMessage());
        }
        return null;
    }
    public PageModel<T> findPage(PageModel<T> pm) {
        return findPage(pm, null);
    }
    /**
     * 动态分页查询
     *
     * @param pm                  分页对象
     * @param specificationDetail 动态条件对象
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<T> findPage(PageModel<T> pm, SpecificationDetail<T> specificationDetail) {
        if(specificationDetail!=null){
            return findPageWrapper(pm, specificationDetail.toEntityWrapper(getPersistentClass()));
        }
        return findPageWrapper(pm, null);

    }

    /**
     * 动态分页查询
     *
     * @param pm                  分页对象
     * @param wrapper 动态条件对象
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<T> findPageWrapper(PageModel<T> pm, Wrapper<T> wrapper) {
        try {
            IPage page = findAll(pm, wrapper);
            pm.setData(page.getRecords());
            pm.setRecordsTotal(page.getTotal());

            return pm;
        } catch (Exception e) {
            log.error("error: {}", e);
            Assert.buildException(e.getMessage());
        }
        return null;
    }
    /**
     * 动态分页查询
     *
     * @param pm                  分页对象
     * @param specificationDetail 动态条件对象
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<T> findRelationPage(PageModel<T> pm, SpecificationDetail<T> specificationDetail) {
        if(specificationDetail!=null){
            specificationDetail.setRelationQuery(true);
            return findRelationPageWrapper(pm, specificationDetail.toEntityWrapper(getPersistentClass()));
        }
        return findRelationPageWrapper(pm, null);

    }

    /**
     * 动态分页查询
     *
     * @param pm                  分页对象
     * @param wrapper 动态条件对象
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<T> findRelationPageWrapper(PageModel<T> pm, Wrapper<T> wrapper) {
        try {
            PageQuery<T> page = new PageQuery(pm);
            page.setRecords(repository.findRelationPage(page, wrapper));
            pm.setData(page.getRecords());
            pm.setRecordsTotal(page.getTotal());
            return pm;
        } catch (Exception e) {
            log.error("error: {}", e);
            Assert.buildException(e.getMessage());
        }
        return null;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Optional<T> findById(pk id){
        return Optional.ofNullable(repository.selectById(id));
    }
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public T findOneById(pk id){
        return repository.selectById(id);
    }

    public long findCount() {
        return repository.selectCount(null);
    }
    public long findCount(SpecificationDetail<T> specificationDetail) {
        return repository.selectCount(specificationDetail.toEntityWrapper(persistentClass));
    }
    public T findOne(QueryWrapper<T> queryWrapper) {
        return repository.selectOne(queryWrapper);
    }

    public List<T> findListByIds(Collection<pk> ids) {
        return repository.selectBatchIds(ids);
    }

    public Integer deleteBatchIds(Collection<pk> ids){
        return repository.deleteBatchIds(ids);
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    public void deleteById(pk id) {
        repository.deleteById(id);
    }

    public Integer deleteAll() {
        return repository.delete(null);
    }
}
