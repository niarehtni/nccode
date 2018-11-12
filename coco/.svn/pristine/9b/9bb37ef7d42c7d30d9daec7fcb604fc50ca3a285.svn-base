<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="" langDir="" caption="店员工作日历按时段查看界面" controllerClazz="nc.bs.hrsms.ta.sss.calendar.ctrl.WorkCalendarListWin" id="WorkCalendarList" sourcePackage="src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="false" id="main" refId="main">
        </Widget>
        <Widget canFreeDesign="false" id="pv_hrss_manage_dept_selector" refId="../pv_hrss_manage_dept_selector">
        </Widget>
        <Widget canFreeDesign="false" id="pv_hrss_catagory_selector" refId="../pv_hrss_catagory_selector">
        </Widget>
        <Widget canFreeDesign="false" id="pubview_queryplan" refId="../pubview_queryplan">
        </Widget>
        <Widget canFreeDesign="false" id="pubview_simplequery" refId="../pubview_simplequery">
        	<Attributes>
                <Attribute>
                    <Key>$SimpleQueryController</Key>
                    <Value>
                    	nc.bs.hrsms.ta.sss.calendar.ctrl.WorkCalendarListQueryCtrl
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
        <Attribute>
            <Key>$MODIFY_TS</Key>
            <Value>1333615067186</Value>
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
        <Connector id="connCatagory" pluginId="Catagory" plugoutId="po_catagory_changed" source="pv_hrss_catagory_selector" target="main">
            <Maps>
                <Map inValue="catagory_info" outValue="">
                    <outValue>
                    </outValue>
                    <inValue>catagory_info</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector id="connSearch" pluginId="Search" plugoutId="qryout" source="pubview_simplequery" target="main">
        </Connector>
        <Connector id="connDeptChange" pluginId="DeptChange" plugoutId="po_mng_dept_changed" source="pv_hrss_manage_dept_selector" target="main">
        </Connector>
    </Connectors>
</PageMeta>
