<?xml version="1.0" encoding='UTF-8'?>
<!DOCTYPE PageMeta PUBLIC "-//Yonyou Co., Ltd.//UAP LFW WINDOW 6.3//EN" "http://uap.yonyou.com/dtd/uap_lfw_window_6_3.dtd">
<PageMeta caption="店员入职登记" componentId="annoyuicomponnet" controllerClazz="nc.bs.hrsms.hi.employ.PsnEmployWinController" foldPath="/sync/hrss/hi/html/nodes/PsnEmploy/" i18nName="w_hi-000667" id="PsnEmploy" langDir="node_hi-res" sourcePackage="src/public/">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
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
        <Widget canFreeDesign="false" id="pubview_simplequery" refId="../pubview_simplequery">
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
        <Widget canFreeDesign="true" id="onlyinfo" refId="onlyinfo">
        </Widget>
        <Widget canFreeDesign="true" id="psn_employ" refId="psn_employ">
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
            <Value>1321074825062</Value>
            <Desc>
            </Desc>
        </Attribute>
    </Attributes>
    <PlugoutDescs>
    </PlugoutDescs>
    <PluginDescs>
    </PluginDescs>
    <Connectors>
        <Connector connType="" id="deptchangeconn" pluginId="DeptChange" plugoutId="po_mng_dept_changed" source="pv_hrss_manage_dept_selector" sourceWindow="" target="main" targetWindow="">
        </Connector>
        <Connector connType="" id="PsnRelation" pluginId="inMain" plugoutId="leftOut" source="left" sourceWindow="" target="main" targetWindow="">
        </Connector>
        <Connector connType="" id="a4c18b0d-1fac-41fa-bb61-a54860848f19" pluginId="inMain" plugoutId="qryout" source="pubview_simplequery" sourceWindow="" target="main" targetWindow="">
        </Connector>
        <Connector connType="" id="8cf706a2-076e-48ee-9bb4-fd51f6c993e3" pluginId="" plugoutId="" source="pv_hrss_manage_dept_selector" sourceWindow="" target="" targetWindow="">
        </Connector>
    </Connectors>
</PageMeta>