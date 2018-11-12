IF NOT EXISTS (SELECT * FROM pub_sysinittemp WHERE initcode='TWHR07')
	INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, '發放日期', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR07', '綜所稅申報薪資發放依據', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '1001ZZ100000000270WT', '發放日期：薪資實際發放日期所在期間，薪資期間：薪資在系統中所屬的薪資期間。', 'Y', 1, 'N', 0, '2015-12-01 17:39:39', '發放日期,薪資期間', 2);
GO

DROP TABLE [dbo].[wa_expformat_head];
DROP TABLE [dbo].[wa_expformat_item];

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[wa_expformat_head](
	[pk_formathead] [char](20) NOT NULL,
	[code] [nvarchar](50) NULL,
	[name] [nvarchar](50) NULL,
	[linecount] [int] NULL,
	[ts] [char](19) NULL,
	[dr] [smallint] NULL,
 CONSTRAINT [PK_wa_expformat_head] PRIMARY KEY CLUSTERED 
(
	[pk_formathead] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [NNC_DATA01]
) ON [NNC_DATA01]

GO

CREATE TABLE [dbo].[wa_expformat_item](
	[pk_formathead] [char](20) NULL,
	[pk_formatitem] [char](20) NOT NULL,
	[linenumber] [int] NULL,
	[posnumber] [int] NULL,
	[itemcode] [nvarchar](50) NULL,
	[itemname] [nvarchar](50) NULL,
	[datatype] [int] NULL,
	[byteLength] [int] NULL,
	[fillmode] [int] NULL,
	[fillstr] [nvarchar](50) NULL,
	[prefix] [nvarchar](50) NULL,
	[suffix] [nvarchar](50) NULL,
	[splitter] [nvarchar](50) NULL,
	[issum] [char](1) NULL,
	[datasource] [int] NULL,
	[datatable] [nvarchar](200) NULL,
	[joinkey] [nvarchar](1000) NULL,
	[datacontext] [nvarchar](1000) NULL,
	[ts] [char](19) NULL,
	[dr] [smallint] NULL,
 CONSTRAINT [PK_wa_expformat_item] PRIMARY KEY CLUSTERED 
(
	[pk_formatitem] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [NNC_DATA01]
) ON [NNC_DATA01]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[wa_expformat_head] ADD  CONSTRAINT [DF_wa_expformat_head_ts]  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO

ALTER TABLE [dbo].[wa_expformat_item] ADD  CONSTRAINT [DF_wa_expformat_item_ts]  DEFAULT (CONVERT([char](19),getdate(),(20))) FOR [ts]
GO


