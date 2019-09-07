package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.domain.BaseEntity;
import com.albedo.java.common.persistence.service.TreeVoService;
import com.albedo.java.modules.sys.domain.Module;
import com.albedo.java.modules.sys.repository.ModuleRepository;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.StringUtil;
import com.albedo.java.util.base.Assert;
import com.albedo.java.util.domain.RequestMethod;
import com.albedo.java.vo.sys.ModuleVo;
import com.albedo.java.vo.sys.query.ModuleMenuTreeResult;
import com.albedo.java.vo.sys.query.ModuleTreeQuery;
import com.albedo.java.vo.sys.query.TreeResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing modules.
 */
@Service
public class ModuleService extends TreeVoService<ModuleRepository, Module, String, ModuleVo> {


    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<ModuleMenuTreeResult> findMenuData(ModuleTreeQuery moduleTreeQuery, List<Module> resourceList) {
        String type = moduleTreeQuery != null ? moduleTreeQuery.getType() : null,
            all = moduleTreeQuery != null ? moduleTreeQuery.getAll() : null;

        List<ModuleMenuTreeResult> mapList = Lists.newArrayList();
        for (Module e : resourceList) {
            ModuleMenuTreeResult moduleMenuTreeResult = null;
            if ((all != null || (all == null && BaseEntity.FLAG_NORMAL.equals(e.getStatus())))) {

                if ("menu".equals(type) && !Module.TYPE_MENU.equals(e.getType())) {
                    continue;
                }
                if (moduleTreeQuery != null && moduleTreeQuery.getRoot() && PublicUtil.isEmpty(e.getParentId())) {
                    continue;
                }

                moduleMenuTreeResult = new ModuleMenuTreeResult();
                moduleMenuTreeResult.setId(e.getId());
                moduleMenuTreeResult.setBpid(e.getParentId() != null ? e.getParentId() : "0");
                moduleMenuTreeResult.setMpid(moduleMenuTreeResult.getBpid());
                moduleMenuTreeResult.setName(e.getName());
                moduleMenuTreeResult.setRoute(e.getHref());
                moduleMenuTreeResult.setIcon(e.getIconCls());
                mapList.add(moduleMenuTreeResult);
            }
        }
        return mapList;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<TreeResult> findTreeData(ModuleTreeQuery moduleTreeQuery, List<Module> resourceList) {
        String extId = moduleTreeQuery != null ? moduleTreeQuery.getExtId() : null,
            type = moduleTreeQuery != null ? moduleTreeQuery.getType() : null,
            all = moduleTreeQuery != null ? moduleTreeQuery.getAll() : null;
        Collections.sort(resourceList, Comparator.comparing(Module::getSort));
        List<TreeResult> mapList = Lists.newArrayList();
        for (Module e : resourceList) {
            TreeResult treeResult;
            if ((PublicUtil.isEmpty(extId)|| PublicUtil.isEmpty(e.getParentIds()) || (PublicUtil.isNotEmpty(extId) && !extId.equals(e.getId()) && e.getParentIds() != null && e.getParentIds().indexOf(StringUtil.SPLIT_DEFAULT + extId + StringUtil.SPLIT_DEFAULT) == -1))
                && (all != null || (all == null && BaseEntity.FLAG_NORMAL.equals(e.getStatus())))) {

                if ("menu".equals(type) && !Module.TYPE_MENU.equals(e.getType())) {
                    continue;
                }
                if (moduleTreeQuery != null && moduleTreeQuery.getRoot() && PublicUtil.isEmpty(e.getParentId())) {
                    continue;
                }
                treeResult = new TreeResult();
                treeResult.setId(e.getId());
                treeResult.setPid(e.getParentId() != null ? e.getParentId() : "0");
                treeResult.setLabel(e.getName());
                treeResult.setKey(e.getName());
                treeResult.setValue(e.getId());
                mapList.add(treeResult);
            }
        }
        return mapList;
    }


    public void generatorModuleData(String moduleName, String parentModuleId, String url) {
        Module currentModule = repository.selectOne(new QueryWrapper<Module>().eq(Module.F_SQL_NAME, moduleName));
        if (currentModule != null) {
            repository.delete(new QueryWrapper<Module>().eq(Module.F_SQL_ID, currentModule.getId()).or()
                .eq(Module.F_SQL_PARENTID, currentModule.getId()));
        }
        Module parentModule = repository.selectById(parentModuleId);
        if (parentModule == null) {
            new Exception(PublicUtil.toAppendStr("根据模块id[", parentModuleId, "无法查询到模块信息]"));
        }
        String permission = url.replace("/", "_").substring(1);
        Module resource = new Module();
        resource.setPermission(permission.substring(0, permission.length() - 1));
        resource.setName(moduleName);
        resource.setParentId(parentModule.getId());
        resource.setType(Module.TYPE_MENU);
        resource.setRequestMethod(RequestMethod.GET);
        resource.setIconCls("fa-file");
        resource.setUrl(url);
        resource.setComponent(PublicUtil.toAppendStr("views/modules", url,"index"));
        saveOrUpdate(resource);

        Module resourceView = new Module();
        resourceView.setParent(resource);
        resourceView.setName("查看");
        resourceView.setIconCls("fa-info-circle");
        resourceView.setPermission(permission + "view");
        resourceView.setParentId(resource.getId());
        resourceView.setType(Module.TYPE_OPERATE);
        resourceView.setRequestMethod(RequestMethod.GET);
        resourceView.setSort(20);
        resourceView.setUrl(url + "page");
        saveOrUpdate(resourceView);
        Module resourceEdit = new Module();
        resourceEdit.setParent(resource);
        resourceEdit.setName("编辑");
        resourceEdit.setIconCls("fa-pencil");
        resourceEdit.setPermission(permission + "edit");
        resourceEdit.setParentId(resource.getId());
        resourceEdit.setType(Module.TYPE_OPERATE);
        resourceEdit.setSort(40);
        resourceEdit.setUrl(url + "edit");
        resourceEdit.setRequestMethod(PublicUtil.toAppendStr(RequestMethod.GET, StringUtil.SPLIT_DEFAULT, RequestMethod.POST));
        saveOrUpdate(resourceEdit);
        Module resourceLock = new Module();
        resourceLock.setParent(resource);
        resourceLock.setName("锁定");
        resourceLock.setIconCls("fa-lock");
        resourceLock.setPermission(permission + "lock");
        resourceLock.setParentId(resource.getId());
        resourceLock.setType(Module.TYPE_OPERATE);
        resourceLock.setSort(60);
        resourceLock.setUrl(url + "lock");
        resourceLock.setRequestMethod(RequestMethod.POST);
        saveOrUpdate(resourceLock);
        Module resourceDelete = new Module();
        resourceDelete.setParent(resource);
        resourceDelete.setName("删除");
        resourceDelete.setIconCls("fa-trash-o");
        resourceDelete.setPermission(permission + "delete");
        resourceDelete.setParentId(resource.getId());
        resourceDelete.setType(Module.TYPE_OPERATE);
        resourceDelete.setSort(80);
        resourceDelete.setUrl(url + "delete");
        resourceDelete.setRequestMethod(RequestMethod.DELETE);
        saveOrUpdate(resourceDelete);

    }

    public List<Module> findAllAuthByUser(String userId) {
        Assert.assertIsTrue(PublicUtil.isNotEmpty(userId), "userId 不能为空");
        return repository.findAllAuthByUser(userId);
    }

    public List<ModuleVo> findMenuDataVo(ModuleTreeQuery moduleTreeQuery,
                                           List<Module> resourceList,
                                           List<Module> resourceAllList) {
        String type = moduleTreeQuery != null ? moduleTreeQuery.getType() : null,
            all = moduleTreeQuery != null ? moduleTreeQuery.getAll() : null;

        List<ModuleVo> mapList = Lists.newArrayList();
        for (Module e : resourceList) {
            if("编辑用户".equals(e.getName())){
                System.out.println(e);
            }
            if ((all != null || (all == null && BaseEntity.FLAG_NORMAL.equals(e.getStatus())))) {
                if ("menu".equals(type) && !Module.TYPE_MENU.equals(e.getType())) {
                    continue;
                }
                if (moduleTreeQuery != null && moduleTreeQuery.getRoot() && PublicUtil.isEmpty(e.getParentId())) {
                    continue;
                }
                mapList.add(copy(e, resourceList));
            }
        }
        List<String> parentIdList = Lists.newArrayList();
        for(ModuleVo resourceVo : mapList){
            if(resourceVo.getParentIds()!=null){
                String[] parentIds = resourceVo.getParentIds().split(",");
                for(String parentId : parentIds){
                    if(!parentIdList.contains(parentId)){
                        parentIdList.add(parentId);
                    }
                }
            }

        }
        if(PublicUtil.isNotEmpty(parentIdList)){
            for(String parenId: parentIdList){
                if(!contain(parenId, mapList)){
                    ModuleVo copy = copy(get(parenId, resourceAllList), resourceAllList);
                    if(copy!=null){
                        mapList.add(copy);
                    }
                }
            }
        }
        return mapList;
    }

    private boolean contain(String id, List<ModuleVo> resourceList){
        for(ModuleVo resource : resourceList){
            if(resource.getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    private Module get(String id, List<Module> resourceList){
        for(Module resource : resourceList){
            if(resource.getId().equals(id)){
                return resource;
            }
        }
        return null;
    }
    private ModuleVo copy(Module e, List<Module> resourceList){
        if(e == null){
            return null;
        }
        ModuleVo resourceVo = copyBeanToVo(e);
        resourceVo.setMenuLeaf(resourceList.stream()
            .filter(item->
                ModuleVo.TYPE_MENU.equals(item.getType()) && PublicUtil.isNotEmpty(item.getParentIds()) && item.getParentIds().startsWith(resourceVo.getParentIds()+ resourceVo.getId())).count()<1);
        resourceVo.setMenuTop(ModuleVo.ROOT_ID.equals(resourceVo.getParentId()));
        resourceVo.setHref(e.getHref());
        return resourceVo;
    }
}
