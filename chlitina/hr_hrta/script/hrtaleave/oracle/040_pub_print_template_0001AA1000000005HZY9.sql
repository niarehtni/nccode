
delete pub_print_cell where ( ctemplateid in ('0001AA1000000005HZY9') and (dr=0 or dr is null) );


delete pub_print_template where ( ctemplateid='0001AA1000000005HZY9' and (pk_corp='@@@@') and (dr=0 or dr is null) );



insert into pub_print_template(bdirector,bdispagenum,bdistotalpagenum,billspace,bnormalcolor,ctemplateid,devorg,dr,extendattr,ffontstyle,fpagination,ibotmargin,ibreakposition,ifontsize,igridcolor,ileftmargin,ipageheight,ipagelocate,ipagewidth,irightmargin,iscale,itopmargin,itype,layer,mdclass,model_type,modelheight,modelwidth,pk_corp,pk_org,prepare1,prepare2,ptemplateid,ts,vdefaultprinter,vfontname,vleftnote,vmidnote,vnodecode,vrightnote,vtemplatecode,vtemplatename) values( 'Y','N','N',null,'N','0001AA1000000005HZY9',null,0,'<nc.vo.pub.print.PrintTemplateExtVO>
  <isBindUp>false</isBindUp>
  <zdline__position>0.0</zdline__position>
  <pagehead__position>0.0</pagehead__position>
  <pagetail__position>0.0</pagetail__position>
  <pagenumber__position>0.0</pagenumber__position>
  <m__withFullPageNumber>false</m__withFullPageNumber>
  <baseLineWeight>0.65</baseLineWeight>
  <initPageNo>1</initPageNo>
</nc.vo.pub.print.PrintTemplateExtVO>',0,0,10,0,9,-4144960,10,596,'21',842,10,100,10,1,0,'5feea0da-60da-4707-9f77-da4dea35595b',null,null,null,'@@@@',null,null,null,null,'2018-09-21 15:39:32',null,'dialog.plain',null,null,'60170leaveplans',null,'60170leaveplans','休假计划');



insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000000000000','0001AA1000000005I000','1000000','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,0,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000000000005','0001AA1000000005I006','0','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,0,0,0,480.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000001000001','0001AA1000000005I001','1000000','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,80,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000002000002','0001AA1000000005I002','1000000','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,160,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000003000003','0001AA1000000005I003','1000000','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,240,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000004000004','0001AA1000000005I004','1000000','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,320,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000005000005','0001AA1000000005I005','1000000','0110','10',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,400,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假计划',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001000001000','0001AA1000000005I007','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','集团',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001001001001','0001AA1000000005I008','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','集团','pk_group.name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001002001002','0001AA1000000005I009','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','组织',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001003001003','0001AA1000000005I00A','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','组织','pk_org.name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001004001004','0001AA1000000005I00B','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','组织版本',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001005001005','0001AA1000000005I00C','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','组织版本','pk_org_v.name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002000002000','0001AA1000000005I00D','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','员工',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002001002001','0001AA1000000005I00E','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','员工','pk_psndoc.name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002002002002','0001AA1000000005I00F','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','部门',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002003002003','0001AA1000000005I00G','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','部门','pk_dept.name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002004002004','0001AA1000000005I00H','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','部门版本',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002005002005','0001AA1000000005I00I','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','部门版本','pk_dept_v.name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003000003000','0001AA1000000005I00J','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假类别',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003001003001','0001AA1000000005I00K','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','休假类别','pk_leavetype.timeitemname');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003002003002','0001AA1000000005I00L','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','可休开始日期',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003003003003','0001AA1000000005I00M','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','可休开始日期','begindate');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003004003004','0001AA1000000005I00N','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','可休结束日期',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003005003005','0001AA1000000005I00O','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','可休结束日期','enddate');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004000004000','0001AA1000000005I00P','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','可休天数',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004001004001','0001AA1000000005I00Q','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','可休天数','enableddays');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004002004002','0001AA1000000005I00R','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','已休天数',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004003004003','0001AA1000000005I00S','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','已休天数','useddays');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004004004004','0001AA1000000005I00T','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','剩余天数',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004005004005','0001AA1000000005I00U','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','剩余天数','remaineddays');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005000005000','0001AA1000000005I00V','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','是否附件',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005001005001','0001AA1000000005I00W','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','是否附件','isattachment');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005002005002','0001AA1000000005I00X','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','是否连续假',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005003005003','0001AA1000000005I00Y','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','是否连续假','iscontinuous');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005004005004','0001AA1000000005I00Z','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','最后修改时间',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005005005005','0001AA1000000005I010','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,110,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','最后修改时间','lastmaketime');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006000006000','0001AA1000000005I011','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','制单人',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006001006001','0001AA1000000005I012','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','制单人','billmaker');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006002006002','0001AA1000000005I013','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','所属组织',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006003006003','0001AA1000000005I014','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','所属组织','pkorg');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006004006004','0001AA1000000005I015','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','name',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006005006005','0001AA1000000005I016','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,130,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','name','name');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007000007000','0001AA1000000005I017','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,150,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','code',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007001007001','0001AA1000000005I018','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,150,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','code','code');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007002007002','0001AA1000000005I019','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,150,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','单据日期',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007003007003','0001AA1000000005I01A','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,150,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','单据日期','dbilldate');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007004007004','0001AA1000000005I01B','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,150,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','制单时间',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007005007005','0001AA1000000005I01C','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,150,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','制单时间','maketime');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008000008000','0001AA1000000005I01D','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,170,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','单据编码',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008001008001','0001AA1000000005I01E','0','1110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,170,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:32','Dialog','单据编码','billcode');

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008002008002','0001AA1000000005I01F','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,170,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008003008003','0001AA1000000005I01G','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,170,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008004008004','0001AA1000000005I01H','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,170,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008005008005','0001AA1000000005I01I','0','0110','10',null,'11111111','0001AA1000000005HZY9',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,170,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009000009000','0001AA1000000005I01J','1009000','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,190,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009001009001','0001AA1000000005I01K','1009000','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,190,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009002009002','0001AA1000000005I01L','1009000','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,190,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009003009003','0001AA1000000005I01M','1009000','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,190,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009004009004','0001AA1000000005I01N','1009000','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,190,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009005009005','0001AA1000000005I01O','1009000','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,190,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009000009005','0001AA1000000005I01P','0','0020','Y',null,'00000000','0001AA1000000005HZY9',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,190,0,480.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-21 15:39:33','Dialog','',null);

