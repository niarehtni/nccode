<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="a_pub-000179" langDir="node_pub-res" caption="店员考勤日报列表界面" controllerClazz="nc.bs.hrsms.ta.sss.dailyreport.ctrl.DailyReportForCleWin" id="DailyReportForCle" sourcePackage="src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
        </Widget>
        <Widget canFreeDesign="true" id="DailyReportDetail" refId="DailyReportDetail">
        </Widget>
        <Widget canFreeDesign="false" id="pubview_simplequery" refId="../pubview_simplequery">
            <Attributes>
                <Attribute>
                    <Key>$SimpleQueryController</Key>
                    <Value>nc.bs.hrsms.ta.sss.dailyreport.ctrl.DailyReportForCleViewLeft</Value>
                    <Desc>
                    </Desc>
                </Attribute>
            </Attributes>
        </Widget>
        <Widget canFreeDesign="false" id="pv_hrss_manage_dept_selector" refId="../pv_hrss_manage_dept_selector">
            <Attributes>
                <Attribute>
                    <Key>$QueryTemplate</Key>
                    <Value>
                    </Value>
                    <Desc>
                    </Desc>
                </Attribute>
            </Attributes>
        </Widget>
    </Widgets>
    <Attributes>
        <Attribute>
            <Key>$QueryTemplate</Key>
            <Value>
            </Value>
            <Desc>
            </Desc>
        </Attribute>
    </Attributes>
    <Events>
        <Event async="true" jsEventClaszz="nc.uap.lfw.core.event.conf.PageListener" methodName="sysWindowClosed" name="onClosed" onserver="true">
            <SubmitRule cardSubmit="false" panelSubmit="false" tabSubmit="false">
            </SubmitRule>
            <Params>
                <Param>
                    <Name>event</Name>
                    <Value>
                    </Value>
                    <Desc>                        <![CDATA[nc.uap.lfw.core.event.PageEvent]]>
                    </Desc>
                </Param>
            </Params>
            <Action>
            </Action>
        </Event>
    </Events>
    <Connectors>
        <Connector id="deptchangeconn" pluginId="DeptChange" plugoutId="po_mng_dept_changed" source="pv_hrss_manage_dept_selector" target="main">
        </Connector>
        <Connector id="connSearch" pluginId="Search" plugoutId="qryout" source="pubview_simplequery" target="main">
            <Maps>
                <Map inValue="" outValue="">
                    <outValue>
                    </outValue>
                    <inValue>
                    </inValue>
                </Map>
            </Maps>
        </Connector>
    </Connectors>
</PageMeta>
