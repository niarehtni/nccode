
delete md_association where ( componentid in ('b428c0da-9396-46e6-8e25-8c07b73386ba') and (dr=0 or dr is null) )
go

delete md_property where ( classid in ('424e498e-f4f0-40fc-8c6c-8d6aac375d5e') and (dr=0 or dr is null) )
go

delete md_db_relation where ( endtableid in ('bd_defdoc') and (dr=0 or dr is null) )
go

delete md_db_relation where ( starttableid in ('bd_defdoc') and (dr=0 or dr is null) )
go

delete md_column where ( tableid in ('bd_defdoc') and (dr=0 or dr is null) )
go

delete md_table where ( id in ('bd_defdoc') and (dr=0 or dr is null) )
go

delete md_ormap where ( classid in ('424e498e-f4f0-40fc-8c6c-8d6aac375d5e') and (dr=0 or dr is null) )
go

delete md_bizitfmap where ( classid in ('424e498e-f4f0-40fc-8c6c-8d6aac375d5e') and (dr=0 or dr is null) )
go

delete md_class where ( componentid in ('b428c0da-9396-46e6-8e25-8c07b73386ba') and (dr=0 or dr is null) )
go

delete md_component where ( id='b428c0da-9396-46e6-8e25-8c07b73386ba' and (versiontype='0') and (dr=0 or dr is null) )
go


insert into md_component(createtime,creator,description,displayname,dr,fromsourcebmf,help,id,industry,isbizmodel,modifier,modifytime,name,namespace,ownmodule,preload,resid,resmodule,ts,version,versiontype) values( '2017-09-08 18:11:51','YONYOU NC',null,'Defdoc-UDHRTA001',0,'Y',null,'b428c0da-9396-46e6-8e25-8c07b73386ba','0','N','YONYOU NC','2017-09-11 10:40:05','Defdoc-UDHRTA001','hrta','hrta',null,'26033dc-000001','Defdoc','2017-09-11 14:31:13','6',0)
go


insert into md_class(accessorclassname,bizitfimpclassname,classtype,componentid,createtime,creator,defaulttablename,description,displayname,dr,fixedlength,fullclassname,help,id,industry,isactive,isauthen,iscreatesql,isextendbean,isprimary,keyattribute,modifier,modifytime,modinfoclassname,name,parentclassid,precise,refmodelname,resid,returntype,stereotype,ts,userdefclassname,versiontype) values( 'nc.md.model.access.javamap.NCBeanStyle',null,201,'b428c0da-9396-46e6-8e25-8c07b73386ba','2017-09-08 18:32:41',null,'bd_defdoc',null,'专案代码(自定义档案)',0,null,'nc.vo.bd.defdoc.DefdocVO',null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e','0',null,'Y','Y',null,'Y','c0e258dd-9315-45e5-97e3-66b00cd76f5c','YONYOU NC','2017-09-11 11:18:24',null,'Defdoc-UDHRTA001',null,null,null,null,null,null,'2017-09-11 14:31:13',null,0)
go


insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e',0,null,'5dd3c721-22ad-42b1-9c10-4351c236bc77','2017-09-11 14:31:13',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e',0,null,'89578a97-42fe-439b-827c-8eabd9e3604c','2017-09-11 14:31:13',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e',0,null,'a47e6cda-09e4-480b-923f-ec6f41e3e06c','2017-09-11 14:31:13',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e',0,null,'c8334364-7ab9-4266-8d4b-e74537935e46','2017-09-11 14:31:13',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e',0,null,'d32cc17b-f415-415a-923f-0764443eb102','2017-09-11 14:31:13',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'424e498e-f4f0-40fc-8c6c-8d6aac375d5e',0,null,'ecf1b76a-6e44-42e2-a55e-87596504775b','2017-09-11 14:31:13',null)
go


insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '04ea3909-9b7f-48b9-97d5-c2718632c9f0','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def13',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '0aa30614-03a7-4ca4-b03d-1c5e6d35a629','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@creationtime',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '132445b3-ec93-423c-a092-cd7c33abbbf3','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@datatype',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '1328f764-455a-4fec-8038-8ae3e4267d07','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def4',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '14d18cff-56de-4e75-8638-1dffffddfbd7','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def11',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '15f8ee34-2f5d-4339-9066-bc4a37038cdd','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def16',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '19c13b93-29c1-437d-b1af-5b33ef772d50','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def12',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '1abfd41c-c939-404b-821a-2dc87deaebf6','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@innercode',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '33bf004d-3df7-4484-95c2-604ae5519777','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def3',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '39618781-ac2a-4da2-824c-875197bb39e9','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def15',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '3bf58d9a-2b20-486a-b0be-64c168471c43','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@enablestate',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '3fccb838-3a87-4a48-8c0d-d8b00b88ee86','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def17',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '5c04e782-fb40-41e2-b184-c94dd079c363','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@shortname',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '5cb7ceca-e07f-4e97-9b5e-5daa1b22a8b2','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def9',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '6e15133e-d635-4f39-8bb7-368d3ee70f3b','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def20',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '7e42a961-cfa4-4635-a921-964ccfbec214','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def8',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '845da94d-6fc3-4aa3-b27f-3f193cf9da56','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def2',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '848a726b-d0f7-47c1-b078-991b5c5568cc','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def18',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '8559d924-1434-483f-95e8-3bf9ff24c94c','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def10',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '8781fc6b-2c16-4668-84bb-a8dc0cc3dc1d','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@pk_group',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '8a2a9969-25cc-4c66-8ef4-f593587c89f3','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@name',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '8e1d9085-1f55-43a5-85fd-10e3d5c8cfe8','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def5',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '9df33c5f-827d-4844-90e7-103984725a87','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@modifiedtime',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '9e178217-4c0b-4a9f-83fc-2b962be22385','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@pk_defdoclist',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'a5a818f5-36b6-4cd7-b99a-b37479e7e7b5','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@pk_org',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'a823d9fd-b697-43b7-a159-a21e32b9dc3b','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@modifier',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'abaf52bb-ee6e-40d1-bc25-071148c2f2ed','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@dataoriginflag',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'b6900fda-8b1c-4453-9ee1-744e27768656','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@pid',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'c04207fb-9162-415a-8eed-a7a656871055','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def14',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'c0e258dd-9315-45e5-97e3-66b00cd76f5c','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@PK@@',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'c8abc3d5-0904-40d1-81c1-7bd0ebe380f2','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def1',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'c9361890-0f9a-408d-b633-e07762afb140','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def6',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'cd619b59-1435-48c8-8f80-2bfff4c664e4','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@memo',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'cdf4b28e-6ec6-419a-bcb6-7415e6ad42da','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def7',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'ed172cc7-3fe3-4eaf-b43e-7345adf35920','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@def19',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'f161b7c9-8e9d-4218-8218-a673500f0cda','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@mnecode',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'f2953ad3-988a-4568-b2cf-5267a01a52fe','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@code',0,'bd_defdoc','2017-09-11 14:31:13')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'f41bbadb-4674-49e9-8c5c-d96e81c063b4','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','bd_defdoc@@@creator',0,'bd_defdoc','2017-09-11 14:31:13')
go


insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,30,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项13',0,'N',null,'N',null,'N','04ea3909-9b7f-48b9-97d5-c2718632c9f0','0','Y','Y',null,null,'def13','N','Y',0,'N',null,'UC000-0004270','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,19,null,null,11,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001034',300,null,null,'创建时间',0,'N',null,'N',null,'N','0aa30614-03a7-4ca4-b03d-1c5e6d35a629','0','Y','Y',null,null,'creationtime','N','Y',0,'N',null,'2UC000-000157','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,0,null,null,17,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','ba3f1197-8584-445d-88a0-102e505f94e5',300,'1',null,'数据类型',0,'N',null,'N',null,'N','132445b3-ec93-423c-a092-cd7c33abbbf3','0','Y','Y',null,null,'datatype','N','Y',0,'N',null,'2UC000-000458','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,21,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项4',0,'N',null,'N',null,'N','1328f764-455a-4fec-8038-8ae3e4267d07','0','Y','Y',null,null,'def4','N','Y',0,'N',null,'UC000-0004261','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,28,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项11',0,'N',null,'N',null,'N','14d18cff-56de-4e75-8638-1dffffddfbd7','0','Y','Y',null,null,'def11','N','Y',0,'N',null,'UC000-0004268','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,33,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项16',0,'N',null,'N',null,'N','15f8ee34-2f5d-4339-9066-bc4a37038cdd','0','Y','Y',null,null,'def16','N','Y',0,'N',null,'UC000-0004273','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,29,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项12',0,'N',null,'N',null,'N','19c13b93-29c1-437d-b1af-5b33ef772d50','0','Y','Y',null,null,'def12','N','Y',0,'N',null,'UC000-0004269','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,200,null,null,14,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001001',300,null,null,'内部编码',0,'N',null,'N',null,'N','1abfd41c-c939-404b-821a-2dc87deaebf6','0','Y','Y',null,null,'innercode','N','Y',0,'N',null,'2UC000-000136','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,20,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项3',0,'N',null,'N',null,'N','33bf004d-3df7-4484-95c2-604ae5519777','0','Y','Y',null,null,'def3','N','Y',0,'N',null,'UC000-0004260','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,32,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项15',0,'N',null,'N',null,'N','39618781-ac2a-4da2-824c-875197bb39e9','0','Y','Y',null,null,'def15','N','Y',0,'N',null,'UC000-0004272','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,0,null,null,15,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','6b533ffa-3e43-4147-8670-ba0f5471fd40',300,null,null,'启用状态',0,'N',null,'N',null,'N','3bf58d9a-2b20-486a-b0be-64c168471c43','0','Y','Y',null,null,'enablestate','N','Y',0,'N',null,'2UC000-000249','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,34,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项17',0,'N',null,'N',null,'N','3fccb838-3a87-4a48-8c0d-d8b00b88ee86','0','Y','Y',null,null,'def17','N','Y',0,'N',null,'UC000-0004274','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,200,null,null,6,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001058',300,null,null,'简称',0,'N',null,'N',null,'N','5c04e782-fb40-41e2-b184-c94dd079c363','0','Y','Y',null,null,'shortname','N','Y',0,'N',null,'2UC000-000675','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,26,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项9',0,'N',null,'N',null,'N','5cb7ceca-e07f-4e97-9b5e-5daa1b22a8b2','0','Y','Y',null,null,'def9','N','Y',0,'N',null,'UC000-0004266','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,37,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项20',0,'N',null,'N',null,'N','6e15133e-d635-4f39-8bb7-368d3ee70f3b','0','Y','Y',null,null,'def20','N','Y',0,'N',null,'UC000-0004277','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,25,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项8',0,'N',null,'N',null,'N','7e42a961-cfa4-4635-a921-964ccfbec214','0','Y','Y',null,null,'def8','N','Y',0,'N',null,'UC000-0004265','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,19,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项2',0,'N',null,'N',null,'N','845da94d-6fc3-4aa3-b27f-3f193cf9da56','0','Y','Y',null,null,'def2','N','Y',0,'N',null,'UC000-0004259','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,35,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项18',0,'N',null,'N',null,'N','848a726b-d0f7-47c1-b078-991b5c5568cc','0','Y','Y',null,null,'def18','N','Y',0,'N',null,'UC000-0004275','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,27,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项10',0,'N',null,'N',null,'N','8559d924-1434-483f-95e8-3bf9ff24c94c','0','Y','Y',null,null,'def10','N','Y',0,'N',null,'UC000-0004267','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,3,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','3b6dd171-2900-47f3-bfbe-41e4483a2a65',305,null,null,'所属集团',0,'N',null,'N',null,'N','8781fc6b-2c16-4668-84bb-a8dc0cc3dc1d','0','Y','Y',null,null,'pk_group','N','Y',0,'N','集团','2UC000-000367','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,200,null,null,5,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001058',300,null,null,'档案名称',0,'N',null,'N','URC','N','8a2a9969-25cc-4c66-8ef4-f593587c89f3','0','Y','Y',null,null,'name','N','Y',0,'N',null,'2UC000-000556','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,22,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项5',0,'N',null,'N',null,'N','8e1d9085-1f55-43a5-85fd-10e3d5c8cfe8','0','Y','Y',null,null,'def5','N','Y',0,'N',null,'UC000-0004262','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,19,null,null,13,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001034',300,null,null,'最后修改时间',0,'N',null,'N',null,'N','9df33c5f-827d-4844-90e7-103984725a87','0','Y','Y',null,null,'modifiedtime','N','Y',0,'N',null,'2UC000-000491','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,1,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','16c1dca0-e3ed-4dcc-8ed7-75fd59e2bef1',305,null,null,'自定义档案列表主键',0,'N',null,'N',null,'N','9e178217-4c0b-4a9f-83fc-2b962be22385','0','Y','Y',null,null,'pk_defdoclist','N','N',0,'N','自定义档案列表','2UC000-000748','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,2,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','985be8a4-3a36-4778-8afe-2d8ed3902659',305,null,null,'所属组织',0,'N',null,'N',null,'N','a5a818f5-36b6-4cd7-b99a-b37479e7e7b5','0','Y','Y',null,null,'pk_org','N','N',0,'N','业务单元','2UC000-000360','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,12,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','f6f9a473-56c0-432f-8bc7-fbf8fde54fee',305,null,null,'最后修改人',0,'N',null,'N',null,'N','a823d9fd-b697-43b7-a159-a21e32b9dc3b','0','Y','Y',null,null,'modifier','N','Y',0,'N','用户','2UC000-000490','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,0,null,null,16,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001004',300,null,null,'分布式',0,'N',null,'N',null,'N','abaf52bb-ee6e-40d1-bc25-071148c2f2ed','0','Y','Y',null,null,'dataoriginflag','N','Y',0,'N',null,'2UC000-000151','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,8,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001001',300,null,null,'上级档案',0,'N',null,'N',null,'N','b6900fda-8b1c-4453-9ee1-744e27768656','0','Y','Y',null,null,'pid','N','Y',0,'N',null,'2UC000-000007','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,31,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项14',0,'N',null,'N',null,'N','c04207fb-9162-415a-8eed-a7a656871055','0','Y','Y',null,null,'def14','N','Y',0,'N',null,'UC000-0004271','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,0,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001051',300,null,null,'自定义档案主键',0,'N',null,'N',null,'N','c0e258dd-9315-45e5-97e3-66b00cd76f5c','0','Y','Y',null,null,'pk_defdoc','N','N',0,'N',null,'2UC000-000747','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,18,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项1',0,'N',null,'N',null,'N','c8abc3d5-0904-40d1-81c1-7bd0ebe380f2','0','Y','Y',null,null,'def1','N','Y',0,'N',null,'UC000-0004258','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,23,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项6',0,'N',null,'N',null,'N','c9361890-0f9a-408d-b633-e07762afb140','0','Y','Y',null,null,'def6','N','Y',0,'N',null,'UC000-0004263','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,200,null,null,9,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001001',300,null,null,'备注',0,'N',null,'N',null,'N','cd619b59-1435-48c8-8f80-2bfff4c664e4','0','Y','Y',null,null,'memo','N','Y',0,'N',null,'2UC000-000258','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,24,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项7',0,'N',null,'N',null,'N','cdf4b28e-6ec6-419a-bcb6-7415e6ad42da','0','Y','Y',null,null,'def7','N','Y',0,'N',null,'UC000-0004264','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,101,null,null,36,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'Y','BS000010000100001056',300,null,null,'自定义项19',0,'N',null,'N',null,'N','ed172cc7-3fe3-4eaf-b43e-7345adf35920','0','Y','Y',null,null,'def19','N','Y',0,'N',null,'UC000-0004276','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,50,null,null,7,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001001',300,null,null,'助记码',0,'N',null,'N',null,'N','f161b7c9-8e9d-4218-8218-a673500f0cda','0','Y','Y',null,null,'mnecode','N','Y',0,'N',null,'2UC000-000172','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,40,null,null,4,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','BS000010000100001001',300,null,null,'档案编码',0,'N',null,'N','URC','N','f2953ad3-988a-4568-b2cf-5267a01a52fe','0','Y','Y',null,null,'code','N','N',0,'N',null,'2UC000-000557','2017-09-11 14:31:13',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,10,'N','424e498e-f4f0-40fc-8c6c-8d6aac375d5e',null,null,null,'N','f6f9a473-56c0-432f-8bc7-fbf8fde54fee',305,null,null,'创建人',0,'N',null,'N',null,'N','f41bbadb-4674-49e9-8c5c-d96e81c063b4','0','Y','Y',null,null,'creator','N','Y',0,'N','用户','2UC000-000155','2017-09-11 14:31:13',0,0)
go


insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'b428c0da-9396-46e6-8e25-8c07b73386ba',null,null,0,'1','985be8a4-3a36-4778-8afe-2d8ed3902659','62a67ad4-d124-4fcd-953d-d49f017c8e8a','0','Y',null,null,'Defdoc-UDHRTA001_pk_org','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','1','a5a818f5-36b6-4cd7-b99a-b37479e7e7b5','2017-09-11 14:31:13',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'b428c0da-9396-46e6-8e25-8c07b73386ba',null,null,0,'1','16c1dca0-e3ed-4dcc-8ed7-75fd59e2bef1','98ab3f65-b1e9-4359-aeb3-69baa3c91846','0','Y',null,null,'Defdoc-UDHRTA001_pk_defdoclist','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','1','9e178217-4c0b-4a9f-83fc-2b962be22385','2017-09-11 14:31:13',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'b428c0da-9396-46e6-8e25-8c07b73386ba',null,null,0,'1','f6f9a473-56c0-432f-8bc7-fbf8fde54fee','9eebddfa-9c5b-4527-a2f5-ffb47c8b1fae','0','Y',null,null,'Defdoc-UDHRTA001_creator','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','1','f41bbadb-4674-49e9-8c5c-d96e81c063b4','2017-09-11 14:31:13',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'b428c0da-9396-46e6-8e25-8c07b73386ba',null,null,0,'1','3b6dd171-2900-47f3-bfbe-41e4483a2a65','ef194932-fd94-472b-b18b-2b95357e044c','0','Y',null,null,'Defdoc-UDHRTA001_pk_group','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','1','8781fc6b-2c16-4668-84bb-a8dc0cc3dc1d','2017-09-11 14:31:13',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'b428c0da-9396-46e6-8e25-8c07b73386ba',null,null,0,'1','f6f9a473-56c0-432f-8bc7-fbf8fde54fee','ff5cfddf-e891-45a1-a300-4e0e3f795645','0','Y',null,null,'Defdoc-UDHRTA001_modifier','424e498e-f4f0-40fc-8c6c-8d6aac375d5e','1','a823d9fd-b697-43b7-a159-a21e32b9dc3b','2017-09-11 14:31:13',3,0)
go
