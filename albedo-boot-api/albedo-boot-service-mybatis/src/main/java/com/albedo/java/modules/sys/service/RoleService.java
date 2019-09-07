package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.domain.BaseEntity;
import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.sys.domain.Role;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.repository.OrgRepository;
import com.albedo.java.modules.sys.repository.RoleRepository;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import com.albedo.java.vo.sys.RoleVo;
import com.albedo.java.vo.sys.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Service class for managing roles.
 */
@Service
@Transactional
public class RoleService extends DataVoService<RoleRepository, Role, String, RoleVo> {

    @Resource
    OrgRepository orgRepository;

    @Override
    public RoleVo copyBeanToVo(Role role) {
        RoleVo userResult = new RoleVo();
        BeanUtils.copyProperties(role, userResult);
        if (role.getOrg() != null) {
            userResult.setOrgName(role.getOrg().getName());
        }
        return userResult;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public RoleVo findOneVo(String id) {
        Role role = findOne(id);
        if(role!=null){
            role.setOrg(orgRepository.selectById(role.getOrgId()));
        }
        return copyBeanToVo(role);
    }
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public PageModel<Role> findPage(PageModel<Role> pm, List<QueryCondition> authQueryConditions) {
        SpecificationDetail<Role> spec = DynamicSpecifications.buildSpecification(pm.getQueryConditionJson()
//            ,QueryCondition.ne(BaseEntity.F_STATUS, BaseEntity.FLAG_DELETE)
        );
        spec.orAll(authQueryConditions);
        findRelationPage(pm, spec);
        return pm;
    }

    public List<Role> findAllList(boolean admin, List<QueryCondition> authQueryList) {
        SpecificationDetail<Role> spd = new SpecificationDetail<Role>()
                .and(QueryCondition.eq(Role.F_STATUS, Role.FLAG_NORMAL));
        if (!admin) {
            spd.orAll(authQueryList);
        }
        spd.orderASC(Role.F_SORT);
        return findAll(spd);
    }

    @Override
    public void save(RoleVo roleVo) {
        Role role = PublicUtil.isNotEmpty(roleVo.getId()) ? repository.selectById(roleVo.getId()) : new Role();
        copyVoToBean(roleVo, role);
        role = super.save(role);
        if (PublicUtil.isNotEmpty(role.getModuleIdList())) {
            repository.deleteRoleModules(role);
            repository.addRoleModules(role);
        }

        if (PublicUtil.isNotEmpty(role.getOrgIdList())) {
            repository.deleteRoleOrgs(role);
            repository.addRoleOrgs(role);
        }
    }
}
