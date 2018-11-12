---delete table (wa_item) column (isnhiitem_30,ishealthinsexsum_30)----
update wa_item set isnhiitem_30=null,ishealthinsexsum_30=null
go
alter table wa_item drop constraint DF__wa_item__isnhiit__29735BCD 
go
alter table wa_item drop column isnhiitem_30
go
alter table wa_item drop constraint DF__wa_item__ishealt__2A678006 
go
alter table wa_item drop column ishealthinsexsum_30
go