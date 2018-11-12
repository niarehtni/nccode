<?xml version="1.0" encoding='UTF-8'?>
<PageMeta caption="店员调动" componentId="annoyuicomponnet" controllerClazz="nc.bs.hrsms.hi.employ.ShopTransfer.ShopTransferWinController" foldPath="/sync/hrss/hi/html/nodes/ShopTransfer/" i18nName="a_hi-0000019" id="ShopTransfer" langDir="node_hi-res" sourcePackage="hi/src/public/">
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
        <Widget canFreeDesign="true" id="transtype" refId="transtype">
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
    	<Connector connType="" id="conSearch" source="pubview_simplequery"  plugoutId="qryout" target="main" pluginId="inParam" >
        </Connector>
    	<Connector connType="" id="condept" source="pv_hrss_manage_dept_selector" plugoutId="po_mng_dept_changed" target="main" pluginId="DeptChange">
        </Connector>
        <Connector id="f77eacb8-a391-4ba6-9e98-d29e81aa5676" pluginId="openMain" plugoutId="outOpenMain" 
        	source="transtype" target="main">
        </Connector>
    </Connectors>
    
</PageMeta>