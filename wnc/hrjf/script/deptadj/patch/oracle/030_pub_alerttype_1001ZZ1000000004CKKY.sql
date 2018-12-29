
delete pub_timeconfig where ( pk_reference in ('1001ZZ1000000004CMYD') and (dr=0 or dr is null) )
/

delete pub_alertsendconfig where ( pk_alertregistry in ('1001ZZ1000000004CMYD') and (dr=0 or dr is null) )
/

delete pub_alertregistry where ( pk_alerttype in ('1001ZZ1000000004CKKY') and (dr=0 or dr is null) )
/

delete pub_alerttype where ( PK_ALERTTYPE='1001ZZ1000000004CKKY' and (dr=0 or dr is null) )
/


insert into pub_alerttype(apptag,belong_system,bizconfigclass,busi_plugin,category,descrip_resid,description,dr,executeinterval,exeintervalunit,hycode,name_resid,org_multiselectable,org_nullable,orgtype,pk_alertquery,pk_alerttype,pk_busiobj,pk_funcnode,tasktype,ts,type_code,type_name) values( null,'hrjf',null,'nc.impl.om.deptadj.plugin.DeptVerExecutorPlugin','preset',null,'部门版本化',0,1,2,null,null,'N','Y','GLOBLE00000000000000',null,'1001ZZ1000000004CKKY',null,null,1,'2018-11-06 16:07:54',null,'部门版本化')
/


insert into pub_alertregistry(accountpk,alertname,alertname2,alertname3,alertname4,alertname5,alertname6,creator,curlang,description,dr,enabled,filename,groupid,ismessagetemplate,istiming,maxlogs,message,messagefile,msgtitle,msgtitle2,msgtitle3,pk_alertregistry,pk_alerttype,pk_messagetemplate,pk_org,pushable,registrytype,reportlikework,scheduletaskid,simplemessage,simplemessage2,simplemessage3,ts,withtranscation) values( null,'部门版本化',null,null,null,null,null,'1001A1100000000001PF',null,null,0,'Y','部门版本化','0001A110000000000D5T',null,'Y',10,null,null,null,null,null,'1001ZZ1000000004CMYD','1001ZZ1000000004CKKY',null,null,'N',1,null,null,null,null,null,'2018-11-07 17:08:42','N')
/


insert into pub_alertsendconfig(dr,nodetype,pk_alertregistry,pk_alertsendconfig,receiveri_type_id,receiverid,receivername,sendmethod,syscode,ts) values( 0,null,'1001ZZ1000000004CMYD','1001ZZ1000000004DBL5',null,null,null,4,null,'2018-11-07 17:08:42')
/


insert into pub_timeconfig(dr,effibegindate,effibegintime,effienddate,effiendtime,endtime,executeday,executeinterval,executetime,exeintervalunit,frequencytype,intervaltime,isruncycledaily,pk_reference,pk_timeconfig,starttime,timezone,ts) values( 0,'2018-11-06','17:09:33',null,null,null,null,null,'17:09:05',null,3,1,'Y','1001ZZ1000000004CMYD','1001ZZ1000000004DBL4',null,'GMT+08:00','2018-11-07 17:08:42')
/
