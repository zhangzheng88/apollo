package com.ctrip.framework.apollo.biz.service;

import static org.junit.Assert.assertEquals;

import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.biz.entity.Audit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create by zhangzheng on 2018/1/22
 */
public class AuditServiceTest extends AbstractIntegrationTest {

  @Autowired
  AuditService auditService;

  @Test
  public void audit() throws Exception {
    String entityName = "entityname";
    Long entityId = 10L;
    Audit.OP op = Audit.OP.INSERT;
    String owner = "owner";
    auditService.audit(entityName, entityId, op, owner);

    assertEquals(auditService.findByOwner(owner).size(), 1);

  }

}
