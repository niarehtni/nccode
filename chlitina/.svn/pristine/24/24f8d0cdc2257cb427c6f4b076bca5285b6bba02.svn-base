
delete sm_menuitemreg where ( funcode in ('60170leaveplans') and (dr=0 or dr is null) )
go

delete sm_paramregister where ( parentid in ('0001AA1000000005HZVD') and (dr=0 or dr is null) )
go

delete sm_funcregister where ( cfunid='0001AA1000000005HZVD' and (dr=0 or dr is null) )
go


insert into sm_funcregister(cfunid,class_name,dr,fun_desc,fun_name,fun_property,funcode,funtype,help_name,isbuttonpower,iscauserusable,isenable,isfunctype,mdid,orgtypecode,own_module,parent_id,pk_group,ts) values( '0001AA1000000005HZVD','nc.ui.pubapp.uif2app.ToftPanelAdaptorEx',0,null,'休假计划-组织',0,'60170leaveplans',0,null,'N',null,'Y','N',null,'HRORGTYPE00000000000','6017','1002Z71000000001VJH8',null,'2018-09-21 15:39:27')
go


insert into sm_paramregister(dr,paramname,paramvalue,parentid,pk_param,ts) values( 0,'BeanConfigFilePath','nc/ui/hrta/leaveplan/ace/view/Leaveplan_config.xml','0001AA1000000005HZVD','0001AA1000000005HZVE','2018-09-21 15:39:28')
go


insert into sm_menuitemreg(dr,funcode,iconpath,ismenutype,menudes,menuitemcode,menuitemname,nodeorder,pk_menu,pk_menuitem,resid,ts) values( 0,'60170leaveplans',null,'N',null,'6017070420','休假计划-组织',null,'1004ZZ10000000000FFL','0001AA1000000005HZVF','D6017070420','2018-09-21 15:39:28')
go
