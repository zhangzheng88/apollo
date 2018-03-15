#apollo-portal-db
Use `apolloportaldb`;

update `AppNamespace` set `IsDeleted` = 1 where `AppId` = 'yop' and `Name` = 'prj822.json' and `IsPublic` = 0 and `IsDeleted` = 0;

# handle roles and permissions
update `RolePermission` set `IsDeleted` = 1 where `PermissionId` in ( select `Id` from `Permission` where `TargetId` = 'yop+prj822.json'  and `IsDeleted` = 0);
update `Permission` set `IsDeleted` = 1 where  `TargetId` = 'yop+prj822.json'  and `IsDeleted` = 0;


update `UserRole` set `IsDeleted` = 1 where `RoleId` in (select `Id` from `Role` where (`RoleName` = 'ModifyNamespace+yop+prj822.json' or `RoleName` = 'ReleaseNamespace+yop+prj822.json') and `IsDeleted` = 0);
update `ConsumerRole` set `IsDeleted` = 1 where `RoleId` in (select `Id` from `Role` where (`RoleName` = 'ModifyNamespace+yop+prj822.json' or `RoleName` = 'ReleaseNamespace+yop+prj822.json') and `IsDeleted` = 0);
update `Role` set `IsDeleted` = 1  where (`RoleName` = 'ModifyNamespace+yop+prj822.json' or `RoleName` = 'ReleaseNamespace+yop+prj822.json') and `IsDeleted` = 0;

#apollo-config-db

Use `apolloconfigdb`;

update `AppNamespace` set `IsDeleted` = 1 where `AppId` = 'yop' and `Name` = 'prj822.json' and `IsPublic` = 0 and `IsDeleted` = 0;
update `Commit` set `IsDeleted` = 1 where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0;
update `GrayReleaseRule` set `IsDeleted` = 1 where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0;
update `Release` set `IsDeleted` = 1 where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0;
update `ReleaseHistory` set `IsDeleted` = 1 where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0;
delete from `InstanceConfig` where `ConfigAppId` = 'yop' and `ConfigNamespaceName` = 'prj822.json';

# handle namespaces, items and release messages
update `Item` set `IsDeleted` = 1 where `NamespaceId` in (select `Id` from `Namespace` where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0);
delete from `NamespaceLock` where `NamespaceId` in (select `Id` from `Namespace` where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0);
delete from `ReleaseMessage` where `Message` in (select CONCAT_WS('+', `AppId`, `ClusterName`, `NamespaceName`) from `Namespace` where `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0);
update `Namespace` set `IsDeleted` = 1 where  `AppId` = 'yop' and `NamespaceName` = 'prj822.json' and `IsDeleted` = 0;
