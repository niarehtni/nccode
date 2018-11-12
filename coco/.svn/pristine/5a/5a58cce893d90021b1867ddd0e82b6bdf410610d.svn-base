<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="w_hi-000165" langDir="node_hi-res" caption="部门人员信息" controllerClazz="nc.bs.hrsms.hi.deptpsn.DeptPsnWinContr" id="DeptPsnInfo" sourcePackage="src/public/">
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
        <Widget id="pubview_simplequery" refId="../pubview_simplequery" canFreeDesign="false">
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
    <Connectors>
        <Connector id="8cf706a2-076e-48ee-9bb4-fd51f6c993e3" pluginId="" plugoutId="" source="pv_hrss_manage_dept_selector" target="">
        </Connector>
        <Connector id="PsnRelation" pluginId="inMain" plugoutId="leftOut" source="left" target="main">
            <Maps>
                <Map inValue="selid" outValue="ParaLeftOut">
                    <outValue>ParaLeftOut</outValue>
                    <inValue>selid</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector id="a4c18b0d-1fac-41fa-bb61-a54860848f19" pluginId="inMain" plugoutId="qryout" source="pubview_simplequery" target="main">
        </Connector>
        <Connector id="deptchangeconn" pluginId="DeptChange" plugoutId="po_mng_dept_changed" source="pv_hrss_manage_dept_selector" target="main">
        </Connector>
    </Connectors>
</PageMeta>