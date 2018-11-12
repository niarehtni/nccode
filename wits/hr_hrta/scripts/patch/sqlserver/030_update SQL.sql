
alter table tbm_leavereg add waperiod nchar(10)
GO

alter TABLE [dbo].[tbm_leaveh]
add changelength [decimal](16, 4) NULL
go
alter TABLE [dbo].[tbm_leavereg]
add changelength [decimal](16, 4) NULL
go

update tbm_leaveh   set  changelength=b.changelength
from tbm_leavebalance b
where tbm_leaveh.pk_psndoc =b.pk_psndoc
and tbm_leaveh.pk_org=b.pk_org
and tbm_leaveh.pk_leavetype=b.pk_timeitem
and tbm_leaveh.leaveyear=b.curyear

go


update tbm_leavereg   set  changelength=b.changelength
from tbm_leavebalance b
where tbm_leavereg.pk_psndoc =b.pk_psndoc
and tbm_leavereg.pk_org=b.pk_org
and tbm_leavereg.pk_leavetype=b.pk_timeitem
and tbm_leavereg.leaveyear=b.curyear