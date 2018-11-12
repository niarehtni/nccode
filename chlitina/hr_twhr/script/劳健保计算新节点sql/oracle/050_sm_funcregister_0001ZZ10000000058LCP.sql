
delete sm_menuitemreg where ( funcode in ('68J61707') and (dr=0 or dr is null) );


delete sm_paramregister where ( parentid in ('0001ZZ10000000058LCP') and (dr=0 or dr is null) );


delete sm_funcregister where ( cfunid='0001ZZ10000000058LCP' and (dr=0 or dr is null) );

--删除原来的功能节点和菜单节点
delete sm_funcregister where ( cfunid='0001ZZ10000000006Y03' and (dr=0 or dr is null) );


delete sm_paramregister where ( parentid in ('0001ZZ10000000006Y03') and (dr=0 or dr is null) );


delete sm_menuitemreg where ( funcode in ('68J61705') and (dr=0 or dr is null) );


insert into sm_funcregister(cfunid,class_name,dr,fun_desc,fun_name,fun_property,funcode,funtype,help_name,isbuttonpower,iscauserusable,isenable,isfunctype,mdid,orgtypecode,own_module,parent_id,pk_group,ts) values( '0001ZZ10000000058LCP','nc.ui.pubapp.uif2app.ToftPanelAdaptorEx',0,null,'计算劳健保',0,'68J61707',0,null,'N',null,'Y','N',null,'HRORGTYPE00000000000','68J6','1001ZZ10000000003WFM',null,'2018-09-10 10:58:55');



insert into sm_paramregister(dr,paramname,paramvalue,parentid,pk_param,ts) values( 0,'BeanConfigFilePath','nc/ui/twhr/nhicalc/ace/view/Nhicalc_config.xml','0001ZZ10000000058LCP','0001ZZ10000000058LCQ','2018-09-10 10:58:55');



insert into sm_menuitemreg(dr,funcode,iconpath,ismenutype,menudes,menuitemcode,menuitemname,nodeorder,pk_menu,pk_menuitem,resid,ts) values( 0,'68J61707',null,'N',null,'60401701','计算劳健保',null,'1004ZZ10000000000FFL','0001ZZ10000000058LCR','D60401701','2018-09-10 10:58:56');

