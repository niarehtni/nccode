
--PUB_SYSINITTEMP���ɾ�����
delete  PUB_SYSINITTEMP WHERE PK_SYSINITTEMP IN ('1001AA100000002PPOKP','1001AA100000002PPPCH');
--PUB_SYSINITTEMPnull



Insert into PUB_SYSINITTEMP
(afterclass,apptag,checkclass,dataclass,dataoriginflag,defaultvalue,domainflag,dr,editcomponentctrlclass,groupcode,groupname,initcode,initname,mainflag,mutexflag,orgtypeconvertmode,paratype,pk_orgtype,pk_refinfo,pk_sysinittemp,remark,showflag,stateflag,sysflag,sysindex,ts,valuelist,valuetype)
Values(null,null,null,null,0,'0','6017',null,null,'~',null,'TWHRT12','�Ӱ�عܼ��e','N',0,'HRORGTYPE00000000000','business','HRORGTYPE00000000000','~','1001AA100000002PPOKP','���ƆT�����Ԅ����ɿ��ڙn������A���عܼ��e','Y',1,'N',0,'2018-10-17 17:28:07','C,��˾=0,���T=1,�T��=2',2);

Insert into PUB_SYSINITTEMP
(afterclass,apptag,checkclass,dataclass,dataoriginflag,defaultvalue,domainflag,dr,editcomponentctrlclass,groupcode,groupname,initcode,initname,mainflag,mutexflag,orgtypeconvertmode,paratype,pk_orgtype,pk_refinfo,pk_sysinittemp,remark,showflag,stateflag,sysflag,sysindex,ts,valuelist,valuetype)
Values(null,null,null,null,0,'0','6017',null,null,'~',null,'TWHRT13','�Ӱ�ع�Ĭ�J��� ','N',0,'HRORGTYPE00000000000','business','HRORGTYPE00000000000','~','1001AA100000002PPPCH','���ƆT�����Ԅ����ɿ��ڙn������A���عܼ��e','Y',1,'N',0,'2018-10-17 17:29:01','C,һ����=1,������=2',2);
update PUB_SYSINITTEMP set DEFAULTVALUE=1 where PK_SYSINITTEMP ='1001AA100000002PPPCH';
update PUB_SYSINITTEMP set VALUELIST='C,�������r=1,����׃�ι��r=2,����׃��=8,����׃�ι��r=4' ,DEFAULTVALUE=1 where PK_SYSINITTEMP='1001Z710000000002XH5';


