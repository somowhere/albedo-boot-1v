package com.albedo.java.modules.sys.service;

import com.albedo.java.common.AuthoritiesConstants;
import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.sys.domain.Org;
import com.albedo.java.modules.sys.domain.PersistentAuditEvent;
import com.albedo.java.modules.sys.domain.Role;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.repository.UserRepository;
import com.albedo.java.util.BeanVoUtil;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.RandomUtil;
import com.albedo.java.util.base.Assert;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import com.albedo.java.util.exception.RuntimeMsgException;
import com.albedo.java.vo.account.PasswordChangeVo;
import com.albedo.java.vo.account.PasswordRestVo;
import com.albedo.java.vo.sys.UserDataVo;
import com.albedo.java.vo.sys.UserExcelVo;
import com.albedo.java.vo.sys.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.albedo.java.util.RedisUtil.redisTemplate;

/**
 * Service class for managing users.
 *
 * @author somewhere
 */
@Service
public class UserService extends DataVoService<UserRepository, User, String, UserVo> {


    private final OrgService orgService;

    private final RoleService roleService;

    private final CacheManager cacheManager;

    private final PersistenceAuditEventService persistenceAuditEventService;

    public UserService(OrgService orgService, RoleService roleService, CacheManager cacheManager, PersistenceAuditEventService persistenceAuditEventService) {
        this.orgService = orgService;
        this.roleService = roleService;
        this.cacheManager = cacheManager;
        this.persistenceAuditEventService = persistenceAuditEventService;
    }

    @Override
    public UserVo copyBeanToVo(User user) {
        UserVo userResult = new UserVo();
        super.copyBeanToVo(user, userResult);
        userResult.setRoleNames(user.getRoleNames());
        if (user.getOrg() != null) {
            userResult.setOrgName(user.getOrg().getName());
        }
        return userResult;
    }

    @Override
    public void copyVoToBean(UserVo userVo, User user) {
        super.copyVoToBean(userVo, user);
        user.setRoleIdList(userVo.getRoleIdList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public UserVo findOneVo(String id) {
        User relationOne = findRelationOne(id);
        relationOne.setRoles(roleService.selectListByUserId(id));
        return copyBeanToVo(relationOne);
    }

    @Override
    public void save(UserVo userVo) {
        User user = PublicUtil.isNotEmpty(userVo.getId()) ? repository.selectById(userVo.getId()) : new User();
        copyVoToBean(userVo, user);
        if (user.getLangKey() == null) {
            // default language
            user.setLangKey("zh-cn");
        } else {
            user.setLangKey(user.getLangKey());
        }
        user.setResetKey(RandomUtil.generateResetKey());


        user.setResetDate(PublicUtil.getCurrentDate());
        user.setActivated(true);
        super.saveOrUpdate(user);
        if (PublicUtil.isNotEmpty(user.getRoleIdList())) {
            repository.deleteUserRoles(user.getId());
            repository.addUserRoles(user);
        }
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
        log.debug("Save Information for User: {}", user);

    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Optional<UserVo> getUserWithAuthoritiesByLogin(String login) {
        return Optional.of(copyBeanToVo(repository.selectUserByLoginId(login)));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserVo getUserWithAuthorities(String id) {
        User user = repository.selectById(id);
        return copyBeanToVo(user);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserVo findVo(String id) {
        User user = repository.selectById(id);
        return copyBeanToVo(user);
    }


    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<User> findPage(PageModel<User> pm, List<QueryCondition> andQueryConditions, List<QueryCondition> orQueryConditions) {
        //拼接查询动态对象
        SpecificationDetail<User> spec = DynamicSpecifications.bySearchQueryCondition(
                andQueryConditions,
                QueryCondition.ne(User.F_STATUS, User.FLAG_DELETE),
                QueryCondition.ne(User.F_ID, "1"));
        spec.orAll(orQueryConditions);
        //自定义sql分页查询
        findRelationPage(pm, spec);


        return pm;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public PageModel<User> findPage(PageModel<User> pm, List<QueryCondition> authQueryConditions) {
        //拼接查询动态对象
        SpecificationDetail<User> spec = DynamicSpecifications.
                buildSpecification(pm.getQueryConditionJson(),
//                        QueryCondition.ne(User.F_STATUS, User.FLAG_DELETE),
                        QueryCondition.ne(User.F_ID,  "1"));
        spec.setPersistentClass(getPersistentClass()).orAll(authQueryConditions);
        //动态生成sql分页查询
        findRelationPage(pm, spec);

        return pm;
    }


    public void saveImageCode(String randomStr, String imageCode) {
        redisTemplate.opsForValue().set(AuthoritiesConstants.DEFAULT_CODE_KEY + randomStr, imageCode, AuthoritiesConstants.DEFAULT_IMAGE_EXPIRE, TimeUnit.SECONDS);
    }
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    public void sendSmsCode(String mobile) {
        Assert.assertIsTrue(PublicUtil.isNotEmpty(REGEX_MOBILE), "该账号没有手机号码，无法获取验证码albedo！");
        Assert.assertIsTrue(Pattern.matches(REGEX_MOBILE, mobile), "该账号手机号码有误，无法发送验证码！");
        Object tempCode = redisTemplate.opsForValue().get(AuthoritiesConstants.DEFAULT_CODE_KEY + mobile);
        Assert.assertIsTrue(tempCode==null, "验证码未失效，请失效后再次申请");
        Integer integer = repository.selectCount(new QueryWrapper<User>().eq(User.F_SQL_PHONE, mobile));
        Assert.assertIsTrue(integer>0, "该账号没有手机号码，无法获取验证码albedo！");

        String code = PublicUtil.getRandomNumber(4);
        log.info("短信发送请求消息中心 -> 手机号:{} -> 验证码：{}", mobile, code);
//        SMSMessageUtil.sendSMSMessage(mobile, code);
        redisTemplate.opsForValue().set(AuthoritiesConstants.DEFAULT_CODE_KEY + mobile, code, AuthoritiesConstants.DEFAULT_IMAGE_EXPIRE, TimeUnit.SECONDS);
    }

    public void resetPassword(@Valid PasswordRestVo passwordRestVo) {
        Object tempCode = redisTemplate.opsForValue().get(AuthoritiesConstants.DEFAULT_CODE_KEY + passwordRestVo.getPhone());
        Assert.assertIsTrue(passwordRestVo.getCode().equals(tempCode), "验证码输入有误");
        Optional.of(repository.selectOne(new QueryWrapper<User>()
            .eq(User.F_SQL_LOGINID, passwordRestVo.getLoginId()))).ifPresent(
            user -> updatePassword(user, passwordRestVo.getPasswordPlaintext(), passwordRestVo.getNewPassword())
        );
    }
    private void updatePassword(User user, String passwordPlaintext, String newPassword) {
        user.setPassword(newPassword);
//        user.setPasswordPlaintext(passwordPlaintext);
        repository.updateById(user);
//        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId());
        log.debug("Changed password for User: {}", user);
    }

    public void changePassword(String loginId, PasswordChangeVo passwordChangeVo) {
        Optional.of(repository.selectOne(new QueryWrapper<User>().eq(User.F_SQL_LOGINID, loginId))).ifPresent(
            user -> updatePassword(user, passwordChangeVo.getConfirmPassword(), passwordChangeVo.getNewPassword())
        );
    }

    public UserVo findOneByLoginId(String loginId) {
        return copyBeanToVo(repository.selectOne(new QueryWrapper<User>().eq(User.F_SQL_LOGINID, loginId)));
    }
    @Override
    public void lockOrUnLock(List<String> idList) {
        super.lockOrUnLock(idList);
        repository.selectBatchIds(idList).forEach(user ->
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId()));
    }


    @Override
    public Integer deleteBatchIds(Collection<String> idList) {
        repository.selectBatchIds(idList).forEach(user ->
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLoginId()));
        Integer rs = repository.deleteBatchIds(idList);
        return rs;
    }

    public void save(@Valid UserExcelVo userExcelVo) {
        User user = new User();
        BeanVoUtil.copyProperties(userExcelVo, user);
        Org org = orgService.findOne(new QueryWrapper<Org>().eq(Org.F_SQL_NAME, userExcelVo.getOrgName()));
        if(org!=null){
            user.setOrgId(org.getId());
        }
        Role role = roleService.findOne(new QueryWrapper<Role>().eq(Role.F_SQL_NAME, userExcelVo.getRoleNames()));
        if(role==null){
            throw new RuntimeMsgException("无法获取角色"+userExcelVo.getRoleNames()+"信息");
        }
        user.setRoleIdList(Lists.newArrayList(role.getId()));
        saveOrUpdate(user);
    }

    public UserVo findExcelOneVo() {
        User user = findOne(new QueryWrapper<User>().ne(User.F_SQL_ID, "1"));
        return BeanVoUtil.copyPropertiesByClass(user, UserVo.class);
    }

    public UserDataVo copyBeanToDataVo(User user) {
        UserDataVo userResult = new UserDataVo();
        if(user!=null){
            BeanVoUtil.copyProperties(user, userResult, true);
            userResult.setRoleNames(user.getRoleNames());
            if (user.getOrg() != null) {
                userResult.setOrgName(user.getOrg().getName());
            }
            if(user.getRoleIdList()!=null){
                userResult.setRoleIdList(user.getRoleIdList().get(0));
            }
            PersistentAuditEvent persistentAuditEvent = persistenceAuditEventService.findByPrincipalLast(user.getLoginId());
            if(PublicUtil.isNotEmpty(persistentAuditEvent)){
                userResult.setLoginTime(persistentAuditEvent.getAuditEventDate());
            }
            userResult.setCurrentTime(PublicUtil.getCurrentDate());
        }
        return userResult;
    }
}
