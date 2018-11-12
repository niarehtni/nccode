-- 创建组织对应法人组织视图
-- sqlserver
Create view org_leaglorg_mapping as
SELECT legalinfo.pk_org,legalinfo.code,legalinfo.name,legalinfo.innercode ,org_orgs.pk_org legal_pk_org,org_orgs.code legal_org_code,org_orgs.name legal_org_name,org_orgs.innercode legal_innercode from (
select MAX(legalorg.innercode) legalinnercode, org_orgs.pk_org,org_orgs.code,org_orgs.name,org_orgs.innercode from
(select * from org_orgs where orgtype2='Y') legalorg,org_orgs
where org_orgs.innercode like  legalorg.innercode+'%'
group by org_orgs.pk_org, org_orgs.code,org_orgs.name,org_orgs.innercode
) legalinfo, org_orgs
where legalinfo.legalinnercode=org_orgs.innercode;


-- 人力资源组织下法人组织视图
------sql server
create view hrorg_subleaglorg_mapping as
select hrorginfo.pk_org,hrorginfo.code,hrorginfo.name,hrorginfo.innercode ,org_orgs.pk_org legal_pk_org,org_orgs.code legal_org_code,
org_orgs.name legal_org_name,org_orgs.innercode legal_innercode
from
(select * from org_orgs where orgtype4='Y') hrorginfo,org_orgs
where org_orgs.innercode like  hrorginfo.innercode+'%'
and org_orgs.orgtype2='Y'