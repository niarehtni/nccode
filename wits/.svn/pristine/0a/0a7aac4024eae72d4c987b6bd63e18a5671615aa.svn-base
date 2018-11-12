CREATE TABLE [dbo].[tbm_daystatb_notcurrmonth](	
	[approve_date] [nchar](10) NOT NULL,
	[calendar] [nchar](10) NOT NULL,
	[dr] [smallint] NULL,
	[hournum] [decimal](16, 4) NOT NULL,
	[pk_daystat] [nchar](20) NOT NULL,
	[pk_daystatb_notcurrmonth] [nchar](20) NOT NULL,
	[pk_group] [nchar](20) NOT NULL,
	[pk_org] [nchar](20) NOT NULL,
	[pk_timeitem] [nchar](20) NOT NULL,
	[pk_billsource] [nvarchar](20) NULL,
	[timeitemunit] [smallint] NOT NULL,
	[toresthour] [decimal](16, 4) NULL,
	[ts] [nchar](19) NULL,
	[type] [smallint] NOT NULL,
 CONSTRAINT [pk_daystatb_notcurrmonth] PRIMARY KEY CLUSTERED 
(
	[pk_daystatb_notcurrmonth] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [NNC_DATA01]
) ON [NNC_DATA01]
GO

alter table tbm_overtimereg add waperiod nchar(10)
GO
alter table tbm_leavereg add waperiod nchar(10)
GO