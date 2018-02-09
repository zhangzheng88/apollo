package com.ctrip.framework.apollo.portal.repository;

import com.ctrip.framework.apollo.portal.entity.po.RolePermission;
import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface RolePermissionRepository extends PagingAndSortingRepository<RolePermission, Long> {

  /**
   * find role permissions by role ids
   */
  List<RolePermission> findByRoleIdIn(Collection<Long> roleId);

}
