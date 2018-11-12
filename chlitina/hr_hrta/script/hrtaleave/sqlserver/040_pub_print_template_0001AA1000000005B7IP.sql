
delete pub_print_cell where ( ctemplateid in ('0001AA1000000005B7IP') and (dr=0 or dr is null) )
go

delete pub_print_template where ( ctemplateid='0001AA1000000005B7IP' and (pk_corp='@@@@') and (dr=0 or dr is null) )
go


insert into pub_print_template(bdirector,bdispagenum,bdistotalpagenum,billspace,bnormalcolor,ctemplateid,devorg,dr,extendattr,ffontstyle,fpagination,ibotmargin,ibreakposition,ifontsize,igridcolor,ileftmargin,ipageheight,ipagelocate,ipagewidth,irightmargin,iscale,itopmargin,itype,layer,mdclass,model_type,modelheight,modelwidth,pk_corp,pk_org,prepare1,prepare2,ptemplateid,ts,vdefaultprinter,vfontname,vleftnote,vmidnote,vnodecode,vrightnote,vtemplatecode,vtemplatename) values( 'Y','N','N',null,'N','0001AA1000000005B7IP',null,0,'<nc.vo.pub.print.PrintTemplateExtVO>
  <isBindUp>false</isBindUp>
  <zdline__position>0.0</zdline__position>
  <pagehead__position>0.0</pagehead__position>
  <pagetail__position>0.0</pagetail__position>
  <pagenumber__position>0.0</pagenumber__position>
  <m__withFullPageNumber>false</m__withFullPageNumber>
  <baseLineWeight>0.65</baseLineWeight>
  <initPageNo>1</initPageNo>
</nc.vo.pub.print.PrintTemplateExtVO>',0,0,10,0,9,-4144960,10,596,'21',842,10,100,10,1,0,'57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'@@@@',null,null,null,null,'2018-09-11 18:14:21',null,'dialog.plain',null,null,'6017leaveextrarest',null,'6017leaveextrarest','外加补休天数')
go


insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000000000000','0001AA1000000005B7JY','1000000','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,0,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000000000005','0001AA1000000005B7K4','0','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,0,0,0,480.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000001000001','0001AA1000000005B7JZ','1000000','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,80,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000002000002','0001AA1000000005B7K0','1000000','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,160,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000003000003','0001AA1000000005B7K1','1000000','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,240,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000004000004','0001AA1000000005B7K2','1000000','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,320,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000005000005','0001AA1000000005B7K3','1000000','0110','10',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,400,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','外加补休天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001000001000','0001AA1000000005B7K5','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','人员基本信息',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001001001001','0001AA1000000005B7K6','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','人员基本信息','pk_psndoc.name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001002001002','0001AA1000000005B7K7','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','人员组织版本',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001003001003','0001AA1000000005B7K8','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','人员组织版本','pk_org_v.name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001004001004','0001AA1000000005B7K9','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','人员部门版本',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001005001005','0001AA1000000005B7KA','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','人员部门版本','pk_dept_v.name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002000002000','0001AA1000000005B7KB','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','集团主键',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002001002001','0001AA1000000005B7KC','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','集团主键','pk_group.name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002002002002','0001AA1000000005B7KD','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','组织主键',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002003002003','0001AA1000000005B7KE','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','组织主键','pk_org.name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002004002004','0001AA1000000005B7KF','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','创建人',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002005002005','0001AA1000000005B7KG','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','创建人','creator.user_name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003000003000','0001AA1000000005B7KH','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','单据日期',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003001003001','0001AA1000000005B7KI','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','单据日期','billdate')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003002003002','0001AA1000000005B7KJ','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','创建时间',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003003003003','0001AA1000000005B7KK','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','创建时间','creationtime')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003004003004','0001AA1000000005B7KL','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','修改人',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003005003005','0001AA1000000005B7KM','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','修改人','modifier.user_name')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004000004000','0001AA1000000005B7KN','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','修改时间',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004001004001','0001AA1000000005B7KO','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:21','Dialog','修改时间','modifiedtime')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004002004002','0001AA1000000005B7KP','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','变动前日历天',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004003004003','0001AA1000000005B7KQ','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','变动前日历天','beforechange')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004004004004','0001AA1000000005B7KR','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','变动后日历天',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004005004005','0001AA1000000005B7KS','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','变动后日历天','afterchange')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005000005000','0001AA1000000005B7KT','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','补休产生类型',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005001005001','0001AA1000000005B7KU','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','补休产生类型','changetype')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005002005002','0001AA1000000005B7KV','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','变动外加补休时天数',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005003005003','0001AA1000000005B7KW','0','1110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','变动外加补休时天数','changedayorhour')
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005004005004','0001AA1000000005B7KX','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005005005005','0001AA1000000005B7KY','0','0110','10',null,'11111111','0001AA1000000005B7IP',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006000006000','0001AA1000000005B7KZ','1006000','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006001006001','0001AA1000000005B7L0','1006000','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006002006002','0001AA1000000005B7L1','1006000','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006003006003','0001AA1000000005B7L2','1006000','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006004006004','0001AA1000000005B7L3','1006000','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006005006005','0001AA1000000005B7L4','1006000','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006000006005','0001AA1000000005B7L5','0','0020','Y',null,'00000000','0001AA1000000005B7IP',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,130,0,480.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-11 18:14:22','Dialog','',null)
go
