<?xml version="1.0" encoding='UTF-8'?>
<PageMeta caption="店员档案窗口" componentId="annoyuicomponnet" controllerClazz="nc.bs.hrsms.hi.employ.EmpinfoWinWinController" id="EmpinfoWin" sourcePackage="hi/src/public/">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget id="main" refId="main" canFreeDesign="true">
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
        <Widget id="Opinion" refId="Opinion" canFreeDesign="false">
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
        <Widget canFreeDesign="false" id="pv_hrss_catagory_selector" refId="../pv_hrss_catagory_selector">
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
            <Key>DYN_FOLDPATH</Key>
            <Value>/html/nodes/test</Value>
            <Desc>
            </Desc>
        </Attribute>
        <Attribute>
            <Key>$QueryTemplate</Key>
            <Value>
            </Value>
            <Desc>
            </Desc>
        </Attribute>
        <Attribute>
            <Key>$MODIFY_TS</Key>
            <Value>1321151551588</Value>
            <Desc>
            </Desc>
        </Attribute>
    </Attributes>
    <Connectors>
        <Connector id="OpiRelation" pluginId="OpinionView" plugoutId="OpinionOut" source="main" target="Opinion">
            <Maps>
                <Map inValue="inOpiCon" outValue="outCon">
                    <outValue>outCon</outValue>
                    <inValue>inOpiCon</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector id="e36b7c2f-3ed8-4ec3-89e8-901be0fed078" pluginId="" plugoutId="" source="main" target="">
        </Connector>
        <Connector id="relation" pluginId="mainIn" plugoutId="leftOut" source="left" target="main">
            <Maps>
                <Map inValue="inTabId" outValue="tabId">
                    <outValue>tabId</outValue>
                    <inValue>inTabId</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector id="f77eacb8-a391-4ba6-9e98-d29e81aa5559" pluginId="mainIn" plugoutId="po_catagory_changed" source="pv_hrss_catagory_selector" target="main">
            <Maps>
                <Map inValue="inTabId" outValue="catagory_info">
                    <outValue>catagory_info</outValue>
                    <inValue>inTabId</inValue>
                </Map>
            </Maps>
        </Connector>
    </Connectors>
</PageMeta>