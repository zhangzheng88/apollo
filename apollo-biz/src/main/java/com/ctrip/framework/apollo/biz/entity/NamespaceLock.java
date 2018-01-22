package com.ctrip.framework.apollo.biz.entity;

import com.ctrip.framework.apollo.common.entity.BaseEntity;

import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "NamespaceLock")
@Where(clause = "isDeleted = 0")
public class NamespaceLock extends BaseEntity{

  @Column(name = "NamespaceId", nullable = false)
  private long namespaceId = 0L;

  public long getNamespaceId() {
    return namespaceId;
  }

  public void setNamespaceId(long namespaceId) {
    this.namespaceId = namespaceId;
  }
}
