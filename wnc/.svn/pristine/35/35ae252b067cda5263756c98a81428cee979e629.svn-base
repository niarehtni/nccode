--部门负责人和本人不相同情况
merge into HI_PSNJOB
using  org_dept 
on (HI_PSNJOB.pk_dept = org_dept.pk_dept and HI_PSNJOB.pk_psndoc != ORG_DEPT.principal)
when matched then
update set HI_PSNJOB.jobglbdef9 = org_dept.principal;



--部门负责人和本人相同情况
merge into HI_PSNJOB
using  org_dept 
on (HI_PSNJOB.pk_dept = org_dept.pk_dept and HI_PSNJOB.pk_psndoc = ORG_DEPT.principal)
when matched then
update set HI_PSNJOB.jobglbdef9 = org_dept.pk_fatherorg