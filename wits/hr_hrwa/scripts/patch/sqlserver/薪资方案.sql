-- 薪资方案新增列
ALTER TABLE [wa_data]
	ADD [biztype] varchar(20) NULL DEFAULT ('~');
ALTER TABLE [wa_data]
	ADD [feetype] varchar(20) NULL DEFAULT ('~');
ALTER TABLE [wa_data]
	ADD [projectcode] varchar(20) NULL DEFAULT ('~');
/*wa_data*/
alter table [wa_data] 
add [taxorg] nchar(20) null,
[taxsumuid] nchar(20) null; 