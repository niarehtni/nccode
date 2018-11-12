IF EXISTS (SELECT name FROM sys.objects WHERE type='U' and name='WITS_IO_HR_DEPT')
BEGIN
	DROP TABLE [WITS_IO_HR_DEPT];
END
GO

CREATE TABLE [WITS_IO_HR_DEPT](
 [DEPT_ID] [varchar](50) NOT NULL,
 [DEPT_NAME] [nvarchar](200) NULL,
 [BUKRS] [char](4) NULL,
 [EMP_ID] [varchar](20) NULL,
 [EMP_ID_TC] [varchar](20) NULL,
 [DEPT_ID_P] [varchar](20) NULL,
 [PRCTR] [varchar](10) NULL,
 [KOSTL] [varchar](10) NULL,
 [IS_OPRN] [char](1) NOT NULL,
 [DEPT_LAYER] [smallint] NOT NULL,
 [DEPT_SUBS] [smallint] NOT NULL,
 [REMARK] [nvarchar](500) NULL,
 [US_ID_CRE] [varchar](20) NULL,
 [D_CRE] [char](8) NULL,
 [T_CRE] [char](6) NULL,
 [US_ID_UPD] [varchar](20) NULL,
 [D_UPD] [char](8) NOT NULL,
 [T_UPD] [char](6) NULL,
 [IS_DIRECT] [char](1) NULL,
 [DEPT_ENAME] [nvarchar](200) NULL,
 [DEPT_TYPE] [char](1) NULL,
 [ST_PLACE] [char](4) NULL,
 [PLACE_NAME] [nvarchar](50) NULL,
 [ts] [char](19) NULL,
 CONSTRAINT [PK_WITS_IO_HR_DEPT] PRIMARY KEY CLUSTERED 
(
 [DEPT_ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'部门代号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'部门名稱' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'公司代码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'BUKRS'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'部門主管员工編號' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'EMP_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'工時核可人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'EMP_ID_TC'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'父層部門' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_ID_P'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'利润中心' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'PRCTR'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'成本中心' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'KOSTL'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'啟用' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'IS_OPRN'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'层级' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_LAYER'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'下層數' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_SUBS'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'備註' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'REMARK'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'建檔人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'US_ID_CRE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'建檔日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'D_CRE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'建檔時間' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'T_CRE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'US_ID_UPD'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'D_UPD'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改時間' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'T_UPD'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否為直接部門' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'IS_DIRECT'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'英文名稱' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_ENAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'部門類型' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'DEPT_TYPE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'地點別' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'ST_PLACE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'地點名稱' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_DEPT', @level2type=N'COLUMN',@level2name=N'PLACE_NAME'
GO

ALTER TABLE [WITS_IO_HR_DEPT] ADD  CONSTRAINT [DF_HR_DEPT_IS_OPRN]  DEFAULT ('') FOR [IS_OPRN]
GO

ALTER TABLE [WITS_IO_HR_DEPT] ADD  CONSTRAINT [DF_HR_DEPT_DEPT_LAYER]  DEFAULT ((0)) FOR [DEPT_LAYER]
GO

ALTER TABLE [WITS_IO_HR_DEPT] ADD  CONSTRAINT [DF_HR_DEPT_DEPT_SUBS]  DEFAULT ((0)) FOR [DEPT_SUBS]
GO

ALTER TABLE [WITS_IO_HR_DEPT] ADD  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO



IF EXISTS (SELECT name FROM sys.objects WHERE type='U' and name='WITS_IO_HR_EMPLOYEE')
BEGIN
	DROP TABLE [WITS_IO_HR_EMPLOYEE];
END
GO

CREATE TABLE [WITS_IO_HR_EMPLOYEE](
 [EMP_ID] [varchar](20) NOT NULL,
 [ID] [varchar](40) NOT NULL,
 [EMP_NAME] [nvarchar](100) NULL,
 [EMP_ENAME] [nvarchar](100) NULL,
 [BUKRS] [char](4) NULL,
 [DEPT_ID] [varchar](50) NULL,
 [KOSTL] [varchar](10) NULL,
 [TTL_ID] [nvarchar](200) NULL,
 [EMP_CLASS] [nvarchar](10) NULL,
 [EMP_RANK] [nvarchar](10) NULL,
 [EMP_EMAIL] [varchar](100) NULL,
 [EMP_TYPE] [varchar](5) NULL,
 [EMP_EXT] [varchar](20) NULL,
 [EMP_ID_R] [varchar](20) NULL,
 [SYSTAB_GENDER] [varchar](5) NOT NULL,
 [CND_ID] [varchar](20) NULL,
 [D_BIRTH] [char](8) NULL,
 [D_REG] [char](8) NULL,
 [D_QIT] [char](8) NULL,
 [EMP_STATUS] [varchar](5) NOT NULL,
 [IS_DIRECT] [varchar](5) NOT NULL,
 [REMARK] [nvarchar](500) NULL,
 [US_ID_CRE] [varchar](20) NULL,
 [D_CRE] [char](8) NOT NULL,
 [T_CRE] [char](6) NOT NULL,
 [US_ID_UPD] [varchar](20) NULL,
 [D_UPD] [char](8) NULL,
 [T_UPD] [char](6) NULL,
 [CLS_NO] [char](1) NULL,
 [CLS_NAME] [nvarchar](50) NULL,
 [CAL_NAME] [nvarchar](50) NULL,
 [ST_PLACE] [char](4) NULL,
 [PLACE_NAME] [nvarchar](50) NULL,
 [BUS_FORM] [varchar](20) NULL,
 [IS_FILL] [char](1) NULL,
 [FILL_U_DATE] [datetime] NULL,
 [FILL_U_ID] [varchar](20) NULL,
 [COUNTRY] [varchar](3) NULL,
 [BANKCOUNTRY] [varchar](3) NULL,
 [BANKTYPE] [varchar](60) NULL,
 [BANK] [varchar](100) NULL,
 [BANKACC] [varchar](50) NULL,
 [ts] [char](19) NULL,
 CONSTRAINT [PK_WITS_IO_HR_EMPLOYEE] PRIMARY KEY CLUSTERED 
(
 [EMP_ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'员工編號' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'员工姓名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'员工英文姓名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_ENAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'公司代码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'BUKRS'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'部门代号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'DEPT_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'薪資成本中心' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'KOSTL'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'職稱' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'TTL_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'職類' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_CLASS'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'職等' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_RANK'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'電子郵件' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_EMAIL'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'電子郵件' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_TYPE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'內線分機' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_EXT'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'資源經理' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_ID_R'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'性別' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'SYSTAB_GENDER'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'行事曆編號' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'CND_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'生日' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'D_BIRTH'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'到職日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'D_REG'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'離職日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'D_QIT'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'在職狀況' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'EMP_STATUS'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否為直接人力' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'IS_DIRECT'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'備註' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'REMARK'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'建檔人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'US_ID_CRE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'建檔日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'D_CRE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'建檔時間' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'T_CRE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'US_ID_UPD'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'D_UPD'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改時間' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'T_UPD'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班別代碼' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'CLS_NO'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'CLS_NAME' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'CLS_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'行事歷名稱' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'CAL_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'地點別' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'ST_PLACE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'地點名稱' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'PLACE_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'業務形態' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'BUS_FORM'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否必须填单(默认值为Y)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'IS_FILL'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改是否填单日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'FILL_U_DATE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'修改是否填单的人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'FILL_U_ID'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'国家' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'COUNTRY'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'银行所在国家' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'BANKCOUNTRY'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'银行名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'BANKTYPE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'分行名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'BANK'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'银行账号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_EMPLOYEE', @level2type=N'COLUMN',@level2name=N'BANKACC'
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_SYSTAB_GENDER]  DEFAULT ('X') FOR [SYSTAB_GENDER]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_BIRTH]  DEFAULT ('') FOR [D_BIRTH]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_REG]  DEFAULT ('') FOR [D_REG]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_QIT]  DEFAULT ('') FOR [D_QIT]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_EMP_STATUS]  DEFAULT ('X') FOR [EMP_STATUS]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_IS_DIRECT]  DEFAULT ('Y') FOR [IS_DIRECT]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_CRE]  DEFAULT ('') FOR [D_CRE]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_T_CRE]  DEFAULT ('') FOR [T_CRE]
GO

ALTER TABLE [WITS_IO_HR_EMPLOYEE] ADD  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO

IF EXISTS (SELECT name FROM sys.objects WHERE type='U' and name='WITS_IO_HR_TIMECARD')
BEGIN
	DROP TABLE [WITS_IO_HR_TIMECARD];
END
GO

CREATE TABLE [WITS_IO_HR_TIMECARD](
	[id] [float] NOT NULL,
	[comno] [nvarchar](4) NOT NULL,
	[empno] [nvarchar](9) NOT NULL,
	[prono] [nvarchar](10) NOT NULL,
	[scode] [nvarchar](100) NOT NULL,
	[bcode] [nvarchar](2) NOT NULL,
	[wdat] [nvarchar](10) NOT NULL,
	[workhour] [float] NOT NULL,
	[addhour] [float] NOT NULL,
	[source] [nvarchar](4) NOT NULL,
	[pyear] [nvarchar](4) NOT NULL,
	[pmonth] [nvarchar](2) NOT NULL,
	[offhour] [float] NOT NULL,
	[status] [nvarchar](50) NOT NULL,
	[is_leave_type] [nvarchar](1) NOT NULL,
	[ts] [char](19) NULL,
 CONSTRAINT [PK_WITS_IO_HR_TIMECARD] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [WITS_IO_HR_TIMECARD] ADD  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'公司' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'comno'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'人员编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'empno'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'工时日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'wdat'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'工作时数' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'workhour'
GO

IF EXISTS (SELECT name FROM sys.objects WHERE type='U' and name='WITS_IO_HR_PROJECT')
BEGIN
	DROP TABLE [WITS_IO_HR_PROJECT];
END
GO

CREATE TABLE [WITS_IO_HR_PROJECT](
	[PK_PROJECT] [char](20) NULL,
	[PROJECT_CODE] [varchar](20) NULL,
	[PROJECT_NAME] [varchar](200) NULL,
	[PROJECT_MANAGER] [varchar](50) NULL,
	[ts] [char](19) NULL
) ON [NNC_DATA01]
GO

ALTER TABLE [WITS_IO_HR_PROJECT] ADD  CONSTRAINT [DF_WITS_IO_HR_PROJECT_ts]  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PK_PROJECT'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PROJECT_CODE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PROJECT_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目经理编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PROJECT_MANAGER'
GO

