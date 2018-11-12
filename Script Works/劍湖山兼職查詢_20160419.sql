DECLARE @startdate NVARCHAR(20) ,
    @enddate NVARCHAR(20)
SET @startdate = '2016-02-01'
SET @enddate = '2016-02-29'

SELECT  --DISTINCT
        --def.pk_psndoc_sub ,
        --psn.pk_psndoc ,
		org.name AS 任職組織,
		org1.name AS 投保組織,
        psn.code AS 員工編號,
        psn.name AS 員工姓名,
        def.begindate AS 勞健保明細起始日期,
        def.enddate AS 勞健保明細截止日期 , 
        '有兼職記錄產生' AS 錯誤類型
FROM    dbo.hi_psndoc_glbdef5 def
        INNER JOIN bd_psndoc psn ON def.pk_psndoc = psn.pk_psndoc
        INNER JOIN dbo.hi_psnjob job ON def.pk_psndoc = job.pk_psndoc
                                        AND def.begindate <= ISNULL(job.enddate,
                                                              '9999-12-31')
                                        AND def.enddate >= job.begindate
                                        AND job.ismainjob = 'N'
                                        AND def.glbdef42 = job.pk_org
        INNER JOIN org_orgs org ON job.pk_hrorg = org.pk_org
		INNER JOIN org_orgs org1 ON job.pk_org = org1.pk_org
WHERE   def.begindate = @startdate
        AND def.enddate = @enddate
UNION
SELECT  --DISTINCT
        --def.pk_psndoc_sub ,
        --psn.pk_psndoc ,
		'' AS 任職組織,
		org.name AS 投保組織,
        psn.code AS 員工編號,
        psn.name AS 員工姓名,
        def.begindate AS 勞健保明細起始日期,
        def.enddate AS 勞健保明細截止日期 , 
        '無任職記錄' AS 錯誤類型
FROM    dbo.hi_psndoc_glbdef5 def
        INNER JOIN bd_psndoc psn ON def.pk_psndoc = psn.pk_psndoc
		INNER JOIN org_orgs org ON def.glbdef42 = org.pk_org
WHERE   def.begindate = @startdate
        AND def.enddate = @enddate
        AND NOT EXISTS ( SELECT *
                         FROM   hi_psnjob
                         WHERE  def.pk_psndoc = pk_psndoc
                                AND def.glbdef42 = pk_org
                                AND def.begindate <= ISNULL(enddate,
                                                            '9999-12-31')
                                AND def.enddate >= begindate )
ORDER BY org.name, psn.code
