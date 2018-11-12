
ALTER TABLE [dbo].[tbm_signreg]
	ADD [signarea] nchar(20) NULL, 
	[signshift] nchar(20) NULL
GO

ALTER TABLE [dbo].[tbm_signb]
	ADD [signshift] nchar(20) NULL, 
	[signarea] nchar(20) NULL
GO

INSERT INTO [dbo].[cp_menuitem]([code], [creationtime], [creator], [dr], [iconpath], [isnewpage], [isnotleaf], [isofen], [menuitemdes], [modifiedtime], [modifier], [name], [name2], [name3], [name4], [name5], [name6], [ordernum], [pk_funnode], [pk_group], [pk_menucategory], [pk_menuitem], [pk_org], [pk_parent], [providerclass], [ts], [winid])
VALUES(N'E20200917', N'2017-10-13 11:30:35', N'~', 0, NULL, N'N', N'N', N'N', NULL, NULL, N'~', N'店员签卡申请', NULL, NULL, NULL, NULL, NULL, 17, N'0000Z70E002009000017', N'~', N'0000Z701000000000001', N'0001ZZ100000000036Z2', N'~', N'0001Z710000000003PJN', NULL, N'2017-10-13 11:30:35', N'SignCardList')
GO