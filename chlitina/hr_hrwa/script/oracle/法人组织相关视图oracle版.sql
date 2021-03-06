-- 创建组织对应法人组织视图
-- oracle
Create view  org_leaglorg_mapping as
SELECT hrorginfo.pk_org,hrorginfo.code,hrorginfo.name,hrorginfo.innercode ,org_orgs.pk_org legal_pk_org,org_orgs.code legal_org_code,org_orgs.name legal_org_name,org_orgs.innercode legal_innercode from (
select hrorg.innercode hrinnercode, org_orgs.pk_org,org_orgs.code,org_orgs.name,org_orgs.innercode from
(select * from org_orgs where orgtype4='Y') hrorg,org_orgs
where org_orgs.innercode like  hrorg.innercode||'%'
and org_orgs.orgtype2='Y'
) hrorginfo, org_orgs
where hrorginfo.hrinnercode=org_orgs.innercode;

-- 人力资源组织下法人组织视图
-- oracle 
Create view hrorg_subleaglorg_mapping as
SELECT hrorginfo.pk_org,hrorginfo.code,hrorginfo.name,hrorginfo.innercode ,org_orgs.pk_org legal_pk_org,org_orgs.code legal_org_code,
org_orgs.name legal_org_name,org_orgs.innercode legal_innercode
from
(select * from org_orgs where orgtype4='Y') hrorginfo,org_orgs
where org_orgs.innercode like  hrorginfo.innercode||'%'
and org_orgs.orgtype2='Y';
