
delete pub_billtemplet_t where ( pk_billtemplet in ('1001AA1000000001NOX8') and (dr=0 or dr is null) )
go

delete pub_billtemplet_b where ( pk_billtemplet in ('1001AA1000000001NOX8') and (dr=0 or dr is null) )
go

delete pub_billtemplet where ( pk_billtemplet='1001AA1000000001NOX8' and (bill_templetname='SYSTEM') and (dr=0 or dr is null) )
go


insert into pub_billtemplet(bill_templetcaption,bill_templetname,devorg,dividerproportion,dr,funccode,layer,metadataclass,model_type,modulecode,nodecode,options,pk_billtemplet,pk_billtypecode,pk_corp,pk_org,resid,shareflag,ts,validateformula) values( '日期输入对话框','SYSTEM',null,null,0,null,-1,null,null,null,'',null,'1001AA1000000001NOX8','dateDialog','@@@@',null,null,null,'2018-09-17 21:15:51',null)
go


insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,5,'人力資源組織',null,0,1,null,-1,'N',null,20,'hrOrg',0,'N',1,'N','Y',2,null,0,null,null,null,'N',0,null,'1001AA1000000001NOX8','1001ZZ1000000000S21C','@@@@',0,'人力资源组织',null,null,'N',1,2,'dateTime','dateTime',0,'2018-09-17 21:15:51','N',null,null,null,1,1,'N',1,null,1)
go
insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,5,'法人組織',null,0,1,null,-1,'N',null,20,'legalOrg',0,'N',1,'N','Y',1,null,0,null,null,null,'N',0,null,'1001AA1000000001NOX8','1001ZZ1000000000S21D','@@@@',0,'<nc.ui.org.ref.LPOrgDefaultRefModel>',null,null,'N',1,1,'dateTime','dateTime',0,'2018-09-17 21:15:51','N',null,null,null,1,1,'N',1,null,1)
go
insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,15,'生效日期',null,0,1,null,-1,'N',null,20,'dateTime',0,'N',1,'N','Y',3,null,0,null,null,null,'N',0,null,'1001AA1000000001NOX8','1001ZZ1000000000S21E','@@@@',0,null,null,null,'N',1,3,'dateTime','dateTime',0,'2018-09-17 21:15:51','N',null,null,null,1,1,'N',1,null,1)
go


insert into pub_billtemplet_t(basetab,dr,metadataclass,metadatapath,mixindex,pk_billtemplet,pk_billtemplet_t,pk_layout,pos,position,resid,tabcode,tabindex,tabname,ts,vdef1,vdef2,vdef3) values( null,0,null,null,null,'1001AA1000000001NOX8','1001ZZ1000000000S2T7',null,0,0,null,'dateTime',0,'dateTime','2018-09-17 21:15:57',null,null,null)
go
