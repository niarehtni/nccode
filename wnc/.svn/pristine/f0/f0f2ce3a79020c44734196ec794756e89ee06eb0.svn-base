drop table wa_imp_bonusothdetail
/
create table wa_imp_bonusothdetail  ( 
	dataid          CHAR(20) NOT NULL,
    pk_group      	CHAR(20) NULL,
	pk_org        	CHAR(20) NULL,
    pk_wa_class     CHAR(20) NULL,
    pk_psndoc       CHAR(20) NULL,
    pk_dept         CHAR(20) NULL,
	deptcode     	VARCHAR2(50) NULL,
	deptname        VARCHAR2(100) NULL,
	psndoccode  	VARCHAR2(50) NULL,
	psndocname      VARCHAR2(100) NULL,
	cyearperiod   	VARCHAR2(20) NULL,
	paydate         VARCHAR2(20) NULL,
	schemecode   	VARCHAR2(50) NULL,
	schemename   	VARCHAR2(100) NULL,
	taxadd      	NUMBER(38,8) DEFAULT 0 NOT NULL,
	taxsub          NUMBER(38,8) DEFAULT 0 NOT NULL,
	notaxadd     	NUMBER(38,8) DEFAULT 0 NOT NULL,
	notaxsub        NUMBER(38,8) DEFAULT 0 NOT NULL,
	pldecode       	VARCHAR2(50) NULL,
	remark          VARCHAR2(500) NULL,
    DR            	NUMBER(10) DEFAULT 0 NULL,
	TS            	CHAR(19) DEFAULT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') NULL,
	CONSTRAINT pk_wa_impbod PRIMARY KEY(dataid)
	NOT DEFERRABLE
	VALIDATE
)
/

drop table wa_imp_salaryothdetail
/
create table wa_imp_salaryothdetail  ( 
	dataid          CHAR(20) NOT NULL,
    pk_group      	CHAR(20) NULL,
	pk_org        	CHAR(20) NULL,
    pk_wa_class     CHAR(20) NULL,
    pk_psndoc       CHAR(20) NULL,
    pk_dept         CHAR(20) NULL,
	deptcode     	VARCHAR2(50) NULL,
	deptname        VARCHAR2(100) NULL,
	psndoccode  	VARCHAR2(50) NULL,
	psndocname      VARCHAR2(100) NULL,
	cyearperiod   	VARCHAR2(20) NULL,
	pldecode       	VARCHAR2(50) NULL,
	pldename		VARCHAR2(100) NULL,
	paydate         VARCHAR2(50) NULL,
	taxadd      	NUMBER(38,8) DEFAULT 0 NOT NULL,
	taxsub          NUMBER(38,8) DEFAULT 0 NOT NULL,
	notaxadd     	NUMBER(38,8) DEFAULT 0 NOT NULL,
	notaxsub        NUMBER(38,8) DEFAULT 0 NOT NULL,
	remark          VARCHAR2(500) NULL,
    DR            	NUMBER(10) DEFAULT 0 NULL,
	TS            	CHAR(19) DEFAULT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') NULL,
	CONSTRAINT pk_wa_impsod PRIMARY KEY(dataid)
	NOT DEFERRABLE
	VALIDATE
)
/

drop table wa_imp_fieldmapping
/
create table wa_imp_fieldmapping  ( 
	id              NUMBER(10) NOT NULL, --主键
    imptype      	NUMBER(2) NULL, --类型，0:薪资 1:薪资加扣 2:奖金 3:奖金加扣
	colindex        NUMBER(10) NULL, --数据文件列号(csv或者excel文件的列序号从0开始)
    itemkey         VARCHAR(50) NULL, --对应薪资发放项目(如:f_34)
    code            VARCHAR(50) NULL, --对应加扣编码(4110)
    DR            	NUMBER(10) DEFAULT 0 NULL,
	TS            	CHAR(19) DEFAULT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') NULL,
	CONSTRAINT pk_imp_mapping PRIMARY KEY(id)
	NOT DEFERRABLE
	VALIDATE
)
/
create sequence FMapp_sequence   
       increment by 1 --每次增加几个，我这里是每次增加1  
       start with 1   --从1开始计数  
       nomaxvalue      --不设置最大值  
       nocycle         --一直累加，不循环  
       nocache        --不建缓冲区  
--创建一个触发器          
CREATE TRIGGER FMapp_trigger 
   BEFORE INSERT ON wa_imp_fieldmapping  
   FOR EACH ROW  
   WHEN (new.id is null) --只有在id为空时，启动该触发器生成id号  
begin  
   select FMapp_sequence.nextval into :new.id from sys.dual
	/
end
/

delete from wa_imp_fieldmapping
/
--0:薪资,列举四条对应,后续的对应薪资模板请自行添加
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,5,'f_34','2001') --本薪
/
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,6,'f_108','6006') --职务加给
/
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,7,'f_35','2002') --伙食津贴
/
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,8,'f_42','2009') --全勤津贴
/
--1:薪资加扣
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) 
select 1,-1,itemkey,code from wa_item
/

--2:奖金,列举一条对应,后续的请自行添加
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (2,6,'f_122','6210') --奖金
/

--3:奖金加扣
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) 
select 3,-1,itemkey,code from wa_item
/

--4:文件数量限制
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (4,50000,null,'batchnum') --导入笔数限制
/
