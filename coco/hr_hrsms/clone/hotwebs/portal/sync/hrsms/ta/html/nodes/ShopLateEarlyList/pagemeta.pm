<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="" langDir="" caption="店员手工考勤" controllerClazz="nc.bs.hrsms.ta.sss.lateearly.ctrl.ShopLateEarlyListWin" id="ShopLateEarlyList" sourcePackage="src/public/" windowType="win">
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
                    <Key>$SimpleQueryController</Key>
                    <Value>
                    	nc.bs.hrsms.ta.sss.lateearly.ShopLateEarlyQueryCtrl
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
        <Connector id="connSearch" pluginId="Search" plugoutId="qryout" source="pubview_simplequery" target="main">
            <Maps>
                <Map inValue="row" outValue="">
                    <outValue></outValue>
                    <inValue>row</inValue>
                </Map>
            </Maps>
        </Connector>
    	<Connector connType="" id="condept" source="pv_hrss_manage_dept_selector" plugoutId="po_mng_dept_changed" target="main" pluginId="DeptChange">
        </Connector>
        
    </Connectors>
</PageMeta>