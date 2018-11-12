<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="" langDir="" caption="门店班次定义" controllerClazz="nc.bs.hrsms.ta.shift.ctrl.StoreShiftListWin" id="StoreShiftList" sourcePackage="src/public/" windowType="win">
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
        <Connector connType="" id="PsnRelation" pluginId="inMain" plugoutId="leftOut" source="left" sourceWindow="" target="main" targetWindow="">
        </Connector>
        <Connector connType="" id="conSearch" source="pubview_simplequery"  plugoutId="qryout" target="main" pluginId="inParam" >
        </Connector>
    	<Connector connType="" id="condept" source="pv_hrss_manage_dept_selector" plugoutId="po_mng_dept_changed" target="main" pluginId="DeptChange">
        </Connector>
        
    </Connectors>
</PageMeta>