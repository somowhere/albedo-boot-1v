package com.albedo.java.common.audit;

import com.albedo.java.common.config.audit.AuditEventConverter;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.modules.sys.domain.PersistentAuditEvent;
import com.albedo.java.modules.sys.service.PersistenceAuditEventService;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Service for managing audit events.
 * <p>
 * This is the default implementation to support SpringBoot Actuator AuditEventRepository
 * </p>
 */
@Service
@Transactional
public class AuditEventService {

    private PersistenceAuditEventService persistenceAuditEventService;

    private AuditEventConverter auditEventConverter;

    public AuditEventService(
        PersistenceAuditEventService persistenceAuditEventService,
            AuditEventConverter auditEventConverter) {

        this.persistenceAuditEventService = persistenceAuditEventService;
        this.auditEventConverter = auditEventConverter;
    }

    public PageModel<AuditEvent> findAll(PageModel<PersistentAuditEvent> pm) {
        return persistenceAuditEventService.findPage(pm)
            .map(auditEventConverter::convertToAuditEvent);
    }
    public PageModel<AuditEvent> findByDates(Date fromDate, Date toDate, PageModel<PersistentAuditEvent> pm) {
        SpecificationDetail<PersistentAuditEvent> specificationDetail = new SpecificationDetail();
        if(fromDate!=null && toDate!=null){
            specificationDetail.and(
                QueryCondition.between(PersistentAuditEvent.F_AUDITEVENTDATE,
                    fromDate, toDate
                ));
        }
        return persistenceAuditEventService.findPage(pm, specificationDetail).map(auditEventConverter::convertToAuditEvent);
    }

    public Optional<AuditEvent> find(Long id) {
        return Optional.ofNullable(persistenceAuditEventService.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(auditEventConverter::convertToAuditEvent);
    }
}
