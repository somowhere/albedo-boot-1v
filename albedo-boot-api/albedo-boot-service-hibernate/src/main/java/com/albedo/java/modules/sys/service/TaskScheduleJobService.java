/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.service.DataService;
import com.albedo.java.modules.sys.domain.TaskScheduleJob;
import com.albedo.java.modules.sys.repository.TaskScheduleJobRepository;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务调度管理Service 任务调度
 *
 * @author lj
 * @version 2017-01-23
 */
@Service
@Transactional
public class TaskScheduleJobService extends DataService<TaskScheduleJobRepository,
        TaskScheduleJob, String> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.albedo.java.modules.sys.service.ITaskScheduleJobService#findAll(com.
     * albedo.java.common.domain.data.SpecificationDetail,
     * com.albedo.java.util.domain.PageModel)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageModel<TaskScheduleJob> findAll(PageModel<TaskScheduleJob> pm, List<QueryCondition> queryConditions) {
        SpecificationDetail<TaskScheduleJob> spec = DynamicSpecifications.
                buildSpecification(pm.getQueryConditionJson(),
                        queryConditions,
                        QueryCondition.ne(TaskScheduleJob.F_STATUS, TaskScheduleJob.FLAG_DELETE));
        pm.setPageInstance(repository.findAll(spec, pm));
        return pm;
    }


    public List<TaskScheduleJob> findByStatusAndJobStatus(Integer status, String jobStatus) {
        return repository.findByStatusAndJobStatus(status, jobStatus);
    }

    public TaskScheduleJob findTopBySourceIdAndStatusNot(String sourceId, Integer status) {
        return repository.findTopBySourceIdAndStatusNot(sourceId, status);
    }

    public List<TaskScheduleJob> findAllBySourceId(String sourceId) {
        return repository.findAllBySourceId(sourceId);
    }
}
