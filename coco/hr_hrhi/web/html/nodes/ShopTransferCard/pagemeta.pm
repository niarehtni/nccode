<?xml version="1.0" encoding='UTF-8'?>
<PageMeta caption="店员调动卡片窗口" componentId="annoyuicomponnet" controllerClazz="nc.bs.hrsms.hi.employ.ShopTransfer.ShopTransferCardWinController" id="ShopTransferCard" i18nName="a_hi-0000022" langDir="node_hi-res" sourcePackage="hi/src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget id="main" refId="main" canFreeDesign="true">
        </Widget>
    </Widgets>
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
</PageMeta>