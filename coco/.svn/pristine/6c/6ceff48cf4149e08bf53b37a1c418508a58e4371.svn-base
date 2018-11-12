<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="a_pub-000176" langDir="node_pub-res" caption="店长的店员假期登记列表界面" controllerClazz="nc.bs.hrsms.ta.empleavereg4store.win.EmpLeaveReg4StoreWin" id="EmpLeaveReg4StoreWin" sourcePackage="src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
        </Widget>
        <Widget canFreeDesign="false" id="pv_hrss_manage_dept_selector" refId="../pv_hrss_manage_dept_selector">
        </Widget>
        <Widget canFreeDesign="false" id="pubview_simplequery" refId="../pubview_simplequery">
            <Attributes>
                <Attribute>
                     <Key>$SimpleQueryController</Key>
                    <Value>nc.bs.hrsms.ta.empleavereg4store.win.EmpLeave4StoreQueryCtrl</Value>
                    <Desc>
                    </Desc>
                </Attribute>
            </Attributes>
        </Widget>
    </Widgets>
    <Attributes>
    </Attributes>
    <Connectors>
        <Connector id="connSearch" pluginId="Search" plugoutId="qryout" source="pubview_simplequery" target="main">
        </Connector>
        <Connector id="connDeptChange" pluginId="DeptChange" plugoutId="po_mng_dept_changed" source="pv_hrss_manage_dept_selector" target="main">
        </Connector>
    </Connectors>
</PageMeta>