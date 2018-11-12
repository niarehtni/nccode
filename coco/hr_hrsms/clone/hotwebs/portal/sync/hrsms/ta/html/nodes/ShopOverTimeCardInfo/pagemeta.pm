<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="" langDir="" caption="刷/签卡信息" controllerClazz="nc.bs.hrsms.ta.sss.overtime.ctrl.ShopOverTimeCardInfoWin" id="ShopOverTimeCardInfo" sourcePackage="src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="false" id="main" refId="main">
        </Widget>
    </Widgets>
    <Attributes>
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
    </Connectors>
</PageMeta>
