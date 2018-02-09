package com.ctrip.framework.apollo.portal.repository;

import com.ctrip.framework.apollo.portal.entity.po.Permission;
import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long> {

  /**
   * find permission by permission type and targetId
   */
  Permission findTopByPermissionTypeAndTargetId(String permissionType, String targetId);

  /**
   * find permissions by permission types and targetId
   */
  List<Permission> findByPermissionTypeInAndTargetId(Collection<String> permissionTypes,
      String targetId);
}
