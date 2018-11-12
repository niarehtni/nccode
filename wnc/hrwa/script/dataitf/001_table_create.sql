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
	id              NUMBER(10) NOT NULL, --����
    imptype      	NUMBER(2) NULL, --���ͣ�0:н�� 1:н�ʼӿ� 2:���� 3:����ӿ�
	colindex        NUMBER(10) NULL, --�����ļ��к�(csv����excel�ļ�������Ŵ�0��ʼ)
    itemkey         VARCHAR(50) NULL, --��Ӧн�ʷ�����Ŀ(��:f_34)
    code            VARCHAR(50) NULL, --��Ӧ�ӿ۱���(4110)
    DR            	NUMBER(10) DEFAULT 0 NULL,
	TS            	CHAR(19) DEFAULT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') NULL,
	CONSTRAINT pk_imp_mapping PRIMARY KEY(id)
	NOT DEFERRABLE
	VALIDATE
)
/
create sequence FMapp_sequence   
       increment by 1 --ÿ�����Ӽ�������������ÿ������1  
       start with 1   --��1��ʼ����  
       nomaxvalue      --���������ֵ  
       nocycle         --һֱ�ۼӣ���ѭ��  
       nocache        --����������  
--����һ��������          
CREATE TRIGGER FMapp_trigger 
   BEFORE INSERT ON wa_imp_fieldmapping  
   FOR EACH ROW  
   WHEN (new.id is null) --ֻ����idΪ��ʱ�������ô���������id��  
begin  
   select FMapp_sequence.nextval into :new.id from sys.dual
	/
end
/

delete from wa_imp_fieldmapping
/
--0:н��,�о�������Ӧ,�����Ķ�Ӧн��ģ�����������
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,5,'f_34','2001') --��н
/
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,6,'f_108','6006') --ְ��Ӹ�
/
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,7,'f_35','2002') --��ʳ����
/
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (0,8,'f_42','2009') --ȫ�ڽ���
/
--1:н�ʼӿ�
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) 
select 1,-1,itemkey,code from wa_item
/

--2:����,�о�һ����Ӧ,���������������
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (2,6,'f_122','6210') --����
/

--3:����ӿ�
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) 
select 3,-1,itemkey,code from wa_item
/

--4:�ļ���������
insert into wa_imp_fieldmapping(imptype,colindex,itemkey,code) values (4,50000,null,'batchnum') --�����������
/
