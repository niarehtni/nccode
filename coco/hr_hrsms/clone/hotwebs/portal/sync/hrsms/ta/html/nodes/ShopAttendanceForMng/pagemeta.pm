<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="a_pub-000197" langDir="node_pub-res" caption="员工出勤情况按人员按日期查看界面"  controllerClazz="nc.bs.hrsms.ta.sss.ShopAttendance.ctrl.ShopAttendanceForMngWin" id="ShopAttendanceForMng" sourcePackage="src/public/" windowType="win">
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
        <Widget canFreeDesign="false" id="pubview_simplequery" refId="../pubview_simplequery">
            <Attributes>
                <Attribute>
                    <Key>$SimpleQueryController</Key>
                    <Value>
                    	nc.bs.hrsms.ta.sss.ShopAttendance.ctrl.ShopAttendanceMngQueryCtrl
                    </Value>
                    <Desc>
                    </Desc>
                </Attribute>
            </Attributes>
        </Widget>
       <!-- <Widget canFreeDesign="false" id="pubview_queryplan" refId="../pubview_queryplan">
            <Attributes>
                <Attribute>
                    <Key>$QueryTemplate</Key>
                    <Value>
                    </Value>
                    <Desc>
                    </Desc>
                </Attribute>
            </Attributes>
        </Widget>-->
        <!--<Widget canFreeDesign="false" id="pv_hrss_catagory_selector" refId="../pv_hrss_catagory_selector">
            <Attributes>
                <Attribute>
                    <Key>$QueryTemplate</Key>
                    <Value>
                    </Value>
                    <Desc>
                    </Desc>
                </Attribute>
            </Attributes>
        </Widget>-->
        <Widget canFreeDesign="false" id="pv_hrss_manage_dept_selector" refId="../pv_hrss_manage_dept_selector">
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
            <Maps>
                <Map inValue="row" outValue="">
                    <outValue>
                    </outValue>
                    <inValue>row</inValue>
                </Map>
            </Maps>
        </Connector>
        
        <Connector id="deptchangeconn" pluginId="DeptChange" plugoutId="po_mng_dept_changed" source="pv_hrss_manage_dept_selector" target="main">
        </Connector>
    </Connectors>
</PageMeta>
