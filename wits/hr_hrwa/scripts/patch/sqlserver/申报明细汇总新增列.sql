-- 申报明细汇总新增列
ALTER TABLE [hrwa_sumincometax]
	ADD [cyear] varchar(50) NULL;
ALTER TABLE [hrwa_sumincometax]
	ADD [isforeignmonthdec] char(1) NULL;