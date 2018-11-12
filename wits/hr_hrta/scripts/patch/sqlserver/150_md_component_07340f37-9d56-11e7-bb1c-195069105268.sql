
delete md_busiop where ( componentid in ('07340f37-9d56-11e7-bb1c-195069105268') and (dr=0 or dr is null) )
go

delete md_component where ( id='07340f37-9d56-11e7-bb1c-195069105268' and (versiontype='0') and (dr=0 or dr is null) )
go


insert into md_component(createtime,creator,description,displayname,dr,fromsourcebmf,help,id,industry,isbizmodel,modifier,modifytime,name,namespace,ownmodule,preload,resid,resmodule,ts,version,versiontype) values( '2017-09-20 00:17:27',null,null,'指定專案代碼維護(自定义档案)業務操作',null,'Y',null,'07340f37-9d56-11e7-bb1c-195069105268','0','Y',null,'2017-09-20 00:17:27','bo_Defdoc-projectcode','uap','uapbd',null,null,'bo_Defdoc-projectcode','2017-09-20 00:17:27','1',0)
go


insert into md_busiop(authorizable,componentid,createtime,creator,description,displayname,dr,help,id,industry,isbusiactivity,logtype,modifier,modifytime,name,needlog,ownertype,parentid,resid,ts,versiontype) values( 'Y','07340f37-9d56-11e7-bb1c-195069105268','2017-09-20 00:17:27',null,null,'新增',null,null,'07340f38-9d56-11e7-bb1c-195069105268','0','N','ADD',null,'2017-09-20 00:17:27','add','Y','1001A11000000000824P',null,'2UC000-001114','2017-09-20 00:17:27',0)
go
insert into md_busiop(authorizable,componentid,createtime,creator,description,displayname,dr,help,id,industry,isbusiactivity,logtype,modifier,modifytime,name,needlog,ownertype,parentid,resid,ts,versiontype) values( 'Y','07340f37-9d56-11e7-bb1c-195069105268','2017-09-20 00:17:27',null,null,'删除',null,null,'07340f39-9d56-11e7-bb1c-195069105268','0','N','DELETE',null,'2017-09-20 00:17:27','delete','Y','1001A11000000000824P',null,'2UC000-001104','2017-09-20 00:17:27',0)
go
insert into md_busiop(authorizable,componentid,createtime,creator,description,displayname,dr,help,id,industry,isbusiactivity,logtype,modifier,modifytime,name,needlog,ownertype,parentid,resid,ts,versiontype) values( 'Y','07340f37-9d56-11e7-bb1c-195069105268','2017-09-20 00:17:27',null,null,'停用',null,null,'07340f3a-9d56-11e7-bb1c-195069105268','0','N','MODIFY',null,'2017-09-20 00:17:27','disable','Y','1001A11000000000824P',null,'2UC000-001106','2017-09-20 00:17:27',0)
go
insert into md_busiop(authorizable,componentid,createtime,creator,description,displayname,dr,help,id,industry,isbusiactivity,logtype,modifier,modifytime,name,needlog,ownertype,parentid,resid,ts,versiontype) values( 'Y','07340f37-9d56-11e7-bb1c-195069105268','2017-09-20 00:17:27',null,null,'修改',null,null,'07340f3b-9d56-11e7-bb1c-195069105268','0','N','MODIFY',null,'2017-09-20 00:17:27','edit','Y','1001A11000000000824P',null,'2UC000-001103','2017-09-20 00:17:27',0)
go
insert into md_busiop(authorizable,componentid,createtime,creator,description,displayname,dr,help,id,industry,isbusiactivity,logtype,modifier,modifytime,name,needlog,ownertype,parentid,resid,ts,versiontype) values( 'Y','07340f37-9d56-11e7-bb1c-195069105268','2017-09-20 00:17:27',null,null,'启用',null,null,'07340f3c-9d56-11e7-bb1c-195069105268','0','N','MODIFY',null,'2017-09-20 00:17:27','enable','Y','1001A11000000000824P',null,'2UC000-001105','2017-09-20 00:17:27',0)
go
