--在职人员劳保法人组织等信息初始化
update hi_psndoc_glbdef1 set legalpersonorg = '0001A110000000000XBZ',insuranceform = 1,glbdef17 = (case when glbdef11 = 'Y' then '1001A110000000023GNP' else ''end)
where (lastflag = 'Y' or nvl(enddate,'9999-12-31') = '9999-12-31'or to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate) ) 
and pk_psndoc in (select pk_psndoc from hi_psnorg where dr = 0 and endflag = 'N' and lastflag = 'Y'and to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate));

--在职人员-健保:法人组织等信息初始化
update hi_psndoc_glbdef2 set legalpersonorg = '0001A110000000000XBZ',insuranceform = 1,glbdef17 = '1001A110000000023GNS'
where (lastflag = 'Y' or nvl(enddate,'9999-12-31') = '9999-12-31'or to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate) ) AND GLBDEF2 = '本人' 
and pk_psndoc in (select pk_psndoc from hi_psnorg where dr = 0 and endflag = 'N' and lastflag = 'Y'and to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate));

--离职人员-劳保劳退 法人组织等信息初始化
update hi_psndoc_glbdef1 set legalpersonorg = '0001A110000000000XBZ',insuranceform = 2
where (lastflag = 'Y' or nvl(enddate,'9999-12-31') = '9999-12-31'or to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate) ) 
and pk_psndoc not in (select pk_psndoc from hi_psnorg where dr = 0 and endflag = 'N' and lastflag = 'Y'and to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate));

--离职人员-健保法人组织等信息初始化
update hi_psndoc_glbdef2 set legalpersonorg = '0001A110000000000XBZ',insuranceform = 2 
where (lastflag = 'Y' or nvl(enddate,'9999-12-31') = '9999-12-31'or to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate) )  AND GLBDEF2 = '本人' 
and pk_psndoc not in (select pk_psndoc from hi_psnorg where dr = 0 and endflag = 'N' and lastflag = 'Y'and to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate));



--在职人员眷属-健保:法人组织等信息初始化
update hi_psndoc_glbdef2 set legalpersonorg = '0001A110000000000XBZ',insuranceform = 1,glbdef17 = '1001A110000000023GNT'
where (lastflag = 'Y' or nvl(enddate,'9999-12-31') = '9999-12-31'or to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate) ) AND GLBDEF2 <> '本人' 
and pk_psndoc in (select pk_psndoc from hi_psnorg where dr = 0 and endflag = 'N' and lastflag = 'Y'and to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate));


--离职人员眷属-健保法人组织等信息初始化
update hi_psndoc_glbdef2 set legalpersonorg = '0001A110000000000XBZ',insuranceform = 2 
where (lastflag = 'Y' or nvl(enddate,'9999-12-31') = '9999-12-31'or to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate) )  AND GLBDEF2 <> '本人' 
and pk_psndoc not in (select pk_psndoc from hi_psnorg where dr = 0 and endflag = 'N' and lastflag = 'Y'and to_date(nvl(enddate,'9999-12-31'),'yyyy-mm-dd') >= trunc(sysdate));



