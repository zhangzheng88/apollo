#apolloportaldb
Use `apolloportaldb`;

update `App` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `AppNamespace` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `Favorite` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;

# handle roles and permissions
update `RolePermission` set `IsDeleted` = 1 where `PermissionId` in (select `Id` from `Permission` where (`TargetId` = '357706' or `TargetId` like CONCAT('357706', '+%'))  and `IsDeleted` = 0);
update `Permission` set `IsDeleted` = 1  where (`TargetId` = '357706' or `TargetId` like CONCAT('357706', '+%'))  and `IsDeleted` = 0;

update `UserRole` set `IsDeleted` = 1 where `RoleId` in (select `Id` from `Role` where (`RoleName` = CONCAT('Master+', '357706') or `RoleName` like CONCAT('ModifyNamespace+', '357706', '+%') or `RoleName` like CONCAT('ReleaseNamespace+', '357706', '+%')) and `IsDeleted` = 0);
update `ConsumerRole` set `IsDeleted` = 1 where `RoleId` in (select `Id` from `Role` where (`RoleName` = CONCAT('Master+', '357706') or `RoleName` like CONCAT('ModifyNamespace+', '357706', '+%') or `RoleName` like CONCAT('ReleaseNamespace+', '357706', '+%')) and `IsDeleted` = 0);
update `Role` set `IsDeleted` = 1 where (`RoleName` = CONCAT('Master+', '357706') or `RoleName` like CONCAT('ModifyNamespace+', '357706', '+%') or `RoleName` like CONCAT('ReleaseNamespace+', '357706', '+%')) and `IsDeleted` = 0;



#apolloconfigdb

Use `apolloconfigdb`;

update `App` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `AppNamespace` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `Cluster` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `Commit` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `GrayReleaseRule` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `Release` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
update `ReleaseHistory` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;
delete from `Instance` where `AppId` = '357706';
delete from `InstanceConfig` where `ConfigAppId` = '357706';
delete from `ReleaseMessage` where `Message` like CONCAT('357706', '+%');

# handle namespaces and items
update `Item` set `IsDeleted` = 1 where `NamespaceId` in (select `Id` from `Namespace` where `AppId` = '357706' and `IsDeleted` = 0);
delete from `NamespaceLock` where `NamespaceId` in (select `Id` from `Namespace` where `AppId` = '357706' and `IsDeleted` = 0);
update `Namespace` set `IsDeleted` = 1 where `AppId` = '357706' and `IsDeleted` = 0;

