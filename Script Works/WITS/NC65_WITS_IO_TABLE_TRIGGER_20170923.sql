DROP TABLE WITS_IO_HR_DEPT
DROP TABLE WITS_IO_HR_EMPLOYEE
DROP TABLE WITS_IO_HR_PROJECT
DROP TABLE WITS_IO_HR_TIMECARD
GO

CREATE TABLE [dbo].[WITS_IO_HR_DEPT](
	[DEPT_ID] [varchar](50) NOT NULL,
	[DEPT_NAME] [nvarchar](200) NULL,
	[BUKRS] [char](4) NOT NULL,
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
	[DEPT_ID] ASC,
	[BUKRS] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[WITS_IO_HR_DEPT] ADD  CONSTRAINT [DF_HR_DEPT_IS_OPRN]  DEFAULT ('') FOR [IS_OPRN]
GO

ALTER TABLE [dbo].[WITS_IO_HR_DEPT] ADD  CONSTRAINT [DF_HR_DEPT_DEPT_LAYER]  DEFAULT ((0)) FOR [DEPT_LAYER]
GO

ALTER TABLE [dbo].[WITS_IO_HR_DEPT] ADD  CONSTRAINT [DF_HR_DEPT_DEPT_SUBS]  DEFAULT ((0)) FOR [DEPT_SUBS]
GO

ALTER TABLE [dbo].[WITS_IO_HR_DEPT] ADD  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
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



CREATE TABLE [dbo].[WITS_IO_HR_EMPLOYEE](
	[EMP_ID] [varchar](20) NOT NULL,
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_SYSTAB_GENDER]  DEFAULT ('X') FOR [SYSTAB_GENDER]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_BIRTH]  DEFAULT ('') FOR [D_BIRTH]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_REG]  DEFAULT ('') FOR [D_REG]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_QIT]  DEFAULT ('') FOR [D_QIT]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_EMP_STATUS]  DEFAULT ('X') FOR [EMP_STATUS]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_IS_DIRECT]  DEFAULT ('Y') FOR [IS_DIRECT]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_D_CRE]  DEFAULT ('') FOR [D_CRE]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  CONSTRAINT [DF_WITS_IO_HR_EMPLOYEE_T_CRE]  DEFAULT ('') FOR [T_CRE]
GO

ALTER TABLE [dbo].[WITS_IO_HR_EMPLOYEE] ADD  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
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


CREATE TABLE [dbo].[WITS_IO_HR_PROJECT](
	[PK_PROJECT] [char](20) NULL,
	[PROJECT_CODE] [varchar](20) NULL,
	[PROJECT_NAME] [varchar](200) NULL,
	[PROJECT_MANAGER] [varchar](50) NULL,
	[ts] [char](19) NULL
) ON [NNC_DATA01]
GO

ALTER TABLE [dbo].[WITS_IO_HR_PROJECT] ADD  CONSTRAINT [DF_WITS_IO_HR_PROJECT_ts]  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PK_PROJECT'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PROJECT_CODE'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PROJECT_NAME'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'项目经理编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_PROJECT', @level2type=N'COLUMN',@level2name=N'PROJECT_MANAGER'
GO






CREATE TABLE [dbo].[WITS_IO_HR_TIMECARD](
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

ALTER TABLE [dbo].[WITS_IO_HR_TIMECARD] ADD  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'公司' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'comno'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'人员编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'empno'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'工时日期' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'wdat'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'工作时数' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'WITS_IO_HR_TIMECARD', @level2type=N'COLUMN',@level2name=N'workhour'
GO


DROP TRIGGER [tg_INSERT_BD_PSNDOC]
DROP TRIGGER [tg_UPDATE_BD_PSNDOC]
DROP TRIGGER [tg_INSERT_ORG_DEPT]
DROP TRIGGER [tg_UPDATE_ORG_DEPT]
GO

CREATE TRIGGER [dbo].[tg_INSERT_BD_PSNDOC] ON [dbo].[bd_psndoc]
	FOR INSERT
AS 
IF NOT EXISTS(select * from WITS_IO_HR_EMPLOYEE where EMP_ID in (select code from inserted))
BEGIN
	insert into WITS_IO_HR_EMPLOYEE(EMP_ID,EMP_NAME,EMP_ENAME,BUKRS,DEPT_ID,KOSTL,TTL_ID,EMP_CLASS,EMP_RANK,EMP_EMAIL,EMP_TYPE,EMP_EXT,EMP_ID_R,SYSTAB_GENDER,CND_ID,D_BIRTH,D_REG,D_QIT,EMP_STATUS,IS_DIRECT,REMARK,US_ID_CRE,D_CRE,T_CRE,US_ID_UPD,D_UPD,T_UPD,CLS_NO,CLS_NAME,CAL_NAME,ST_PLACE,PLACE_NAME,BUS_FORM,IS_FILL,FILL_U_DATE,FILL_U_ID,COUNTRY,BANKCOUNTRY,BANKTYPE,BANK,BANKACC,ts)
	select 
		psndoc.code EMP_ID,
		psndoc.name EMP_NAME,
		psndoc.shortname EMP_ENAME,
		org.code BUKRS,
		dept.code DEPT_ID,
		dept.code KOSTL,
		pjob.jobname TTL_ID,
		null EMP_CLASS,
		rnk.jobrankcode EMP_RANK,
		psndoc.email EMP_EMAIL,
		null EMP_TYPE,
		null EMP_EXT,
		null EMP_ID_R,
		case when psndoc.sex=2 then 'F' else (case when psndoc.sex=1 then 'M' else 'X' end) end SYSTAB_GENDER,
		null CND_ID,
		replace(psndoc.birthdate,'-','') D_BIRTH,
		replace(porg.joinsysdate,'-','') D_REG,
		replace(porg.enddate,'-','') D_QIT,
		case when isnull(porg.enddate, '1')='1' then '' else 'X' end EMP_STATUS,
		case when cl.innercode like 'D3LH%' then 'T' else 'F' end IS_DIRECT,
		null REMARK,
		ct.user_code US_ID_CRE,
		replace(left(psndoc.creationtime, 10),'-','') D_CRE,
		replace(right(psndoc.creationtime, 8), ':', '') T_CRE,
		isnull(md.user_code, ct.user_code) US_ID_UPD,
		replace(left(isnull(psndoc.modifiedtime, psndoc.creationtime), 10),'-','') D_UPD,
		replace(right(isnull(psndoc.modifiedtime, psndoc.creationtime), 8), ':', '') T_UPD,
		null CLS_NO,
		null CLS_NAME,
		null CAL_NAME,
		null ST_PLACE,
		null PLACE_NAME,
		null BUS_FORM,
		'Y' IS_FILL,
		null FILL_U_DATE,
		null FILL_U_ID,
		cnt.codeth COUNTRY,
		null BANKCOUNTRY,
		null BANKTYPE,
		null BANK,
		null BANKACC,
		psndoc.ts ts
	from inserted psndoc
	left join org_orgs org on org.pk_org = psndoc.pk_org
	left join bd_psnjob job on job.pk_psnjob = (select top 1 bj.pk_psnjob from bd_psnjob bj where bj.pk_psndoc=psndoc.pk_psndoc and bj.ismainjob = 'Y' order by bj.indutydate desc)
	left join org_dept dept on job.pk_dept = dept.pk_dept
	left join hi_psnjob pos on pos.pk_psndoc = psndoc.pk_psndoc and pos.pk_psnjob = job.pk_psnjob
	left join om_job pjob on pjob.pk_job = pos.pk_job
	left join om_jobrank rnk on rnk.pk_jobrank = pos.pk_jobrank
	left join hi_psnorg porg on porg.pk_psnorg = pos.pk_psnorg
	left join sm_user ct on ct.cuserid = psndoc.creator
	left join sm_user md on md.cuserid = psndoc.modifier
	left join bd_countryzone cnt on cnt.pk_country = psndoc.country
	left join bd_psncl cl on job.pk_psncl = cl.pk_psncl
END
GO

ALTER TABLE [dbo].[bd_psndoc] ENABLE TRIGGER [tg_INSERT_BD_PSNDOC]
GO






CREATE TRIGGER [dbo].[tg_UPDATE_BD_PSNDOC] ON [dbo].[bd_psndoc] 
	AFTER UPDATE
AS 
BEGIN
	DELETE FROM WITS_IO_HR_EMPLOYEE WHERE EMP_ID IN (SELECT code FROM inserted);

	insert into WITS_IO_HR_EMPLOYEE(EMP_ID,EMP_NAME,EMP_ENAME,BUKRS,DEPT_ID,KOSTL,TTL_ID,EMP_CLASS,EMP_RANK,EMP_EMAIL,EMP_TYPE,EMP_EXT,EMP_ID_R,SYSTAB_GENDER,CND_ID,D_BIRTH,D_REG,D_QIT,EMP_STATUS,IS_DIRECT,REMARK,US_ID_CRE,D_CRE,T_CRE,US_ID_UPD,D_UPD,T_UPD,CLS_NO,CLS_NAME,CAL_NAME,ST_PLACE,PLACE_NAME,BUS_FORM,IS_FILL,FILL_U_DATE,FILL_U_ID,COUNTRY,BANKCOUNTRY,BANKTYPE,BANK,BANKACC,ts)
	select 
		psndoc.code EMP_ID,
		psndoc.name EMP_NAME,
		psndoc.shortname EMP_ENAME,
		org.code BUKRS,
		dept.code DEPT_ID,
		dept.code KOSTL,
		pjob.jobname TTL_ID,
		null EMP_CLASS,
		rnk.jobrankcode EMP_RANK,
		psndoc.email EMP_EMAIL,
		null EMP_TYPE,
		null EMP_EXT,
		null EMP_ID_R,
		case when psndoc.sex=2 then 'F' else (case when psndoc.sex=1 then 'M' else 'X' end) end SYSTAB_GENDER,
		null CND_ID,
		replace(psndoc.birthdate,'-','') D_BIRTH,
		replace(porg.joinsysdate,'-','') D_REG,
		replace(porg.enddate,'-','') D_QIT,
		case when isnull(porg.enddate, '1')='1' then '' else 'X' end EMP_STATUS,
		case when cl.innercode like 'D3LH%' then 'T' else 'F' end IS_DIRECT,
		null REMARK,
		ct.user_code US_ID_CRE,
		replace(left(psndoc.creationtime, 10),'-','') D_CRE,
		replace(right(psndoc.creationtime, 8), ':', '') T_CRE,
		isnull(md.user_code, ct.user_code) US_ID_UPD,
		replace(left(isnull(psndoc.modifiedtime, psndoc.creationtime), 10),'-','') D_UPD,
		replace(right(isnull(psndoc.modifiedtime, psndoc.creationtime), 8), ':', '') T_UPD,
		null CLS_NO,
		null CLS_NAME,
		null CAL_NAME,
		null ST_PLACE,
		null PLACE_NAME,
		null BUS_FORM,
		'Y' IS_FILL,
		null FILL_U_DATE,
		null FILL_U_ID,
		cnt.codeth COUNTRY,
		null BANKCOUNTRY,
		null BANKTYPE,
		null BANK,
		null BANKACC,
		psndoc.ts ts
	from inserted psndoc
	left join org_orgs org on org.pk_org = psndoc.pk_org
	left join bd_psnjob job on job.pk_psnjob = (select top 1 bj.pk_psnjob from bd_psnjob bj where bj.pk_psndoc=psndoc.pk_psndoc and bj.ismainjob = 'Y' order by bj.indutydate desc)
	left join org_dept dept on job.pk_dept = dept.pk_dept
	left join hi_psnjob pos on pos.pk_psndoc = psndoc.pk_psndoc and pos.pk_psnjob = job.pk_psnjob
	left join om_job pjob on pjob.pk_job = pos.pk_job
	left join om_jobrank rnk on rnk.pk_jobrank = pos.pk_jobrank
	left join hi_psnorg porg on porg.pk_psnorg = pos.pk_psnorg
	left join sm_user ct on ct.cuserid = psndoc.creator
	left join sm_user md on md.cuserid = psndoc.modifier
	left join bd_countryzone cnt on cnt.pk_country = psndoc.country
	left join bd_psncl cl on job.pk_psncl = cl.pk_psncl
END
GO

ALTER TABLE [dbo].[bd_psndoc] ENABLE TRIGGER [tg_UPDATE_BD_PSNDOC]
GO






CREATE TRIGGER [dbo].[tg_INSERT_ORG_DEPT] ON [dbo].[org_dept] 
	FOR INSERT
AS 
IF EXISTS(select * from inserted where code not like '%(V)')
BEGIN
	insert into WITS_IO_HR_DEPT(DEPT_ID,DEPT_NAME,BUKRS,EMP_ID,EMP_ID_TC,DEPT_ID_P,PRCTR,KOSTL,IS_OPRN,DEPT_LAYER,DEPT_SUBS,REMARK,US_ID_CRE,D_CRE,T_CRE,US_ID_UPD,D_UPD,T_UPD,IS_DIRECT,DEPT_ENAME,DEPT_TYPE,ST_PLACE,PLACE_NAME,ts)
	select 
		inserteddept.code DEPT_ID, 
		inserteddept.name DEPT_NAME, 
		org_orgs.code BUKRS,
		bd_psndoc.code EMP_ID,
		bd_psndoc.code EMP_ID_TC,
		parentdept.code DEPT_ID_P,
		inserteddept.code PRCTR,
		inserteddept.code KOSTL,
		CASE WHEN inserteddept.enablestate=2 THEN 'T' ELSE 'F' END IS_OPRN,
		LEN(inserteddept.innercode)/4 DEPT_LAYER,
		SUBLENS.maxsublen DEPT_SUBS,
		null REMARK,
		ct.user_code US_ID_CRE,
		replace(left(inserteddept.creationtime, 10),'-','') D_CRE,
		replace(right(inserteddept.creationtime, 8), ':', '') T_CRE,
		isnull(md.user_code, ct.user_code) US_ID_UPD,
		replace(left(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 10),'-','') D_UPD,
		replace(right(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 8), ':', '') T_UPD,
		null IS_DIRECT,
		null DEPT_ENAME,
		null DEPT_TYPE,
		null ST_PLACE,
		null PLACE_NAME,
		inserteddept.ts
	from inserted inserteddept
	inner join org_orgs on inserteddept.pk_org = org_orgs.pk_org
	left join bd_psndoc on inserteddept.principal = bd_psndoc.pk_psndoc
	left join org_dept parentdept on parentdept.pk_dept=inserteddept.pk_fatherorg
	inner join (select dept.innercode, max((len(subdept.innercode)/4-len(dept.innercode)/4)) maxsublen from org_dept subdept 
	inner join org_dept dept on subdept.innercode like dept.innercode + '%'
	group by dept.innercode) SUBLENS on inserteddept.innercode = SUBLENS.innercode
	left join sm_user ct on ct.cuserid = inserteddept.creator
	left join sm_user md on md.cuserid = inserteddept.modifier
END
GO

ALTER TABLE [dbo].[org_dept] ENABLE TRIGGER [tg_INSERT_ORG_DEPT]
GO





CREATE TRIGGER [dbo].[tg_UPDATE_ORG_DEPT] ON [dbo].[org_dept] 
	AFTER UPDATE
AS 
IF EXISTS(select * from inserted where code not like '%(V)')
BEGIN
	DELETE FROM WITS_IO_HR_DEPT WHERE DEPT_ID IN (SELECT code FROM inserted);

	insert into WITS_IO_HR_DEPT(DEPT_ID,DEPT_NAME,BUKRS,EMP_ID,EMP_ID_TC,DEPT_ID_P,PRCTR,KOSTL,IS_OPRN,DEPT_LAYER,DEPT_SUBS,REMARK,US_ID_CRE,D_CRE,T_CRE,US_ID_UPD,D_UPD,T_UPD,IS_DIRECT,DEPT_ENAME,DEPT_TYPE,ST_PLACE,PLACE_NAME,ts)
	select 
		inserteddept.code DEPT_ID, 
		inserteddept.name DEPT_NAME, 
		org_orgs.code BUKRS,
		bd_psndoc.code EMP_ID,
		bd_psndoc.code EMP_ID_TC,
		parentdept.code DEPT_ID_P,
		inserteddept.code PRCTR,
		inserteddept.code KOSTL,
		CASE WHEN inserteddept.enablestate=2 THEN 'T' ELSE 'F' END IS_OPRN,
		LEN(inserteddept.innercode)/4 DEPT_LAYER,
		SUBLENS.maxsublen DEPT_SUBS,
		null REMARK,
		ct.user_code US_ID_CRE,
		replace(left(inserteddept.creationtime, 10),'-','') D_CRE,
		replace(right(inserteddept.creationtime, 8), ':', '') T_CRE,
		isnull(md.user_code, ct.user_code) US_ID_UPD,
		replace(left(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 10),'-','') D_UPD,
		replace(right(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 8), ':', '') T_UPD,
		null IS_DIRECT,
		null DEPT_ENAME,
		null DEPT_TYPE,
		null ST_PLACE,
		null PLACE_NAME,
		inserteddept.ts
	from inserted inserteddept
	inner join org_orgs on inserteddept.pk_org = org_orgs.pk_org
	left join bd_psndoc on inserteddept.principal = bd_psndoc.pk_psndoc
	left join org_dept parentdept on parentdept.pk_dept=inserteddept.pk_fatherorg
	inner join (select dept.innercode, max((len(subdept.innercode)/4-len(dept.innercode)/4)) maxsublen from org_dept subdept 
	inner join org_dept dept on subdept.innercode like dept.innercode + '%'
	group by dept.innercode) SUBLENS on inserteddept.innercode = SUBLENS.innercode
	left join sm_user ct on ct.cuserid = inserteddept.creator
	left join sm_user md on md.cuserid = inserteddept.modifier
END
GO

ALTER TABLE [dbo].[org_dept] ENABLE TRIGGER [tg_UPDATE_ORG_DEPT]
GO


ALTER TRIGGER [tg_UPDATE_ORG_DEPT] ON [org_dept] 
	AFTER UPDATE
AS 
IF EXISTS(select * from inserted where code not like '%(V)')
BEGIN
	DELETE FROM WITS_IO_HR_DEPT WHERE DEPT_ID IN (SELECT code FROM inserted WHERE code not like '%(V)');

	insert into WITS_IO_HR_DEPT(DEPT_ID,DEPT_NAME,BUKRS,EMP_ID,EMP_ID_TC,DEPT_ID_P,PRCTR,KOSTL,IS_OPRN,DEPT_LAYER,DEPT_SUBS,REMARK,US_ID_CRE,D_CRE,T_CRE,US_ID_UPD,D_UPD,T_UPD,IS_DIRECT,DEPT_ENAME,DEPT_TYPE,ST_PLACE,PLACE_NAME,ts)
	select 
		inserteddept.code DEPT_ID, 
		inserteddept.name DEPT_NAME, 
		org_orgs.code BUKRS,
		bd_psndoc.code EMP_ID,
		bd_psndoc.code EMP_ID_TC,
		parentdept.code DEPT_ID_P,
		inserteddept.code PRCTR,
		inserteddept.code KOSTL,
		CASE WHEN inserteddept.enablestate=2 THEN 'T' ELSE 'F' END IS_OPRN,
		LEN(inserteddept.innercode)/4 DEPT_LAYER,
		SUBLENS.maxsublen DEPT_SUBS,
		null REMARK,
		ct.user_code US_ID_CRE,
		replace(left(inserteddept.creationtime, 10),'-','') D_CRE,
		replace(right(inserteddept.creationtime, 8), ':', '') T_CRE,
		isnull(md.user_code, ct.user_code) US_ID_UPD,
		replace(left(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 10),'-','') D_UPD,
		replace(right(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 8), ':', '') T_UPD,
		null IS_DIRECT,
		null DEPT_ENAME,
		null DEPT_TYPE,
		null ST_PLACE,
		null PLACE_NAME,
		inserteddept.ts
	from inserted inserteddept
	inner join org_orgs on inserteddept.pk_org = org_orgs.pk_org
	left join bd_psndoc on inserteddept.principal = bd_psndoc.pk_psndoc
	left join org_dept parentdept on parentdept.pk_dept=inserteddept.pk_fatherorg
	inner join (select dept.innercode, max((len(subdept.innercode)/4-len(dept.innercode)/4)) maxsublen from org_dept subdept 
	inner join org_dept dept on subdept.innercode like dept.innercode + '%'
	group by dept.innercode) SUBLENS on inserteddept.innercode = SUBLENS.innercode
	left join sm_user ct on ct.cuserid = inserteddept.creator
	left join sm_user md on md.cuserid = inserteddept.modifier
	where inserteddept.code not like '%(V)'
END
GO





ALTER TRIGGER [tg_INSERT_ORG_DEPT] ON [org_dept] 
	FOR INSERT
AS 
IF EXISTS(select * from inserted where code not like '%(V)')
BEGIN
	insert into WITS_IO_HR_DEPT(DEPT_ID,DEPT_NAME,BUKRS,EMP_ID,EMP_ID_TC,DEPT_ID_P,PRCTR,KOSTL,IS_OPRN,DEPT_LAYER,DEPT_SUBS,REMARK,US_ID_CRE,D_CRE,T_CRE,US_ID_UPD,D_UPD,T_UPD,IS_DIRECT,DEPT_ENAME,DEPT_TYPE,ST_PLACE,PLACE_NAME,ts)
	select 
		inserteddept.code DEPT_ID, 
		inserteddept.name DEPT_NAME, 
		org_orgs.code BUKRS,
		bd_psndoc.code EMP_ID,
		bd_psndoc.code EMP_ID_TC,
		parentdept.code DEPT_ID_P,
		inserteddept.code PRCTR,
		inserteddept.code KOSTL,
		CASE WHEN inserteddept.enablestate=2 THEN 'T' ELSE 'F' END IS_OPRN,
		LEN(inserteddept.innercode)/4 DEPT_LAYER,
		SUBLENS.maxsublen DEPT_SUBS,
		null REMARK,
		ct.user_code US_ID_CRE,
		replace(left(inserteddept.creationtime, 10),'-','') D_CRE,
		replace(right(inserteddept.creationtime, 8), ':', '') T_CRE,
		isnull(md.user_code, ct.user_code) US_ID_UPD,
		replace(left(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 10),'-','') D_UPD,
		replace(right(isnull(inserteddept.modifiedtime, inserteddept.creationtime), 8), ':', '') T_UPD,
		null IS_DIRECT,
		null DEPT_ENAME,
		null DEPT_TYPE,
		null ST_PLACE,
		null PLACE_NAME,
		inserteddept.ts
	from inserted inserteddept
	inner join org_orgs on inserteddept.pk_org = org_orgs.pk_org
	left join bd_psndoc on inserteddept.principal = bd_psndoc.pk_psndoc
	left join org_dept parentdept on parentdept.pk_dept=inserteddept.pk_fatherorg
	inner join (select dept.innercode, max((len(subdept.innercode)/4-len(dept.innercode)/4)) maxsublen from org_dept subdept 
	inner join org_dept dept on subdept.innercode like dept.innercode + '%'
	group by dept.innercode) SUBLENS on inserteddept.innercode = SUBLENS.innercode
	left join sm_user ct on ct.cuserid = inserteddept.creator
	left join sm_user md on md.cuserid = inserteddept.modifier
	where inserteddept.code not like '%(V)'
END
GO



alter table wits_io_hr_project
alter column PK_PROJECT char(20) NOT NULL
GO

alter table wits_io_hr_project
add PRIMARY KEY (PK_PROJECT)
GO