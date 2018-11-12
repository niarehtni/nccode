package nc.ui.bd.ref.model;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefDocEdit;
import nc.ui.bd.ref.IRefMaintenanceHandler;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.ml.NCLangRes4VoTransl;

public class ProjectGridRefModel extends AbstractRefModel {
    private DefdocDefaultModelUtil util = new DefdocDefaultModelUtil();

    public ProjectGridRefModel() {
        super();
    }

    @Override
    public void filterValueChanged(ValueChangedEvent changedValue) {
        super.filterValueChanged(changedValue);
        util.filterValueChanged(this, changedValue);
    }

    @Override
    public String getRefTitle() {
        return util.getRefTitle(this.getRefNodeName(), this.getPara1());
    }

    @Override
    public void reset() {
        this.setFieldCode(new String[] {
            DefdocVO.CODE, DefdocVO.NAME, DefdocVO.MEMO, DefdocVO.MNECODE
        });
        this.setFieldName(new String[] {
            NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
                    "UC000-0003279")/* "编码" */,
            NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
                    "UC000-0001155") /* "名称" */,
            NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
                    "UC000-0001376")
            /* 备注 */,
            NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
                    "UC000-0000703")
        /* 助记码 */
        });
        this.setHiddenFieldCode(new String[] {
            DefdocVO.PK_DEFDOC
        });
        this.setRefCodeField(DefdocVO.CODE);
        this.setRefNameField(DefdocVO.NAME);
        this.setPkFieldCode(DefdocVO.PK_DEFDOC);
        this.setTableName(this.getPara2());
        this.setMnecode(new String[] {
            DefdocVO.MNECODE
        });
        this.setOrderPart(DefdocVO.CODE);
        // 设置数据权限
        this.setResourceID(this.getResourceCode());
        final String[] funcodes = util.getFuncode(this.getPara1());
        // 设置默认不显示停用
        this.setAddEnableStateWherePart(true);
        if (util.isContainBuData(this.getPara1())) {
            this.setFilterRefNodeName(new String[] {
                "业务单元" /* -=notranslate=- */
            });
        }
        // 维护
        setRefMaintenanceHandler(new IRefMaintenanceHandler() {

            @Override
            public String[] getFucCodes() {
                return funcodes;
            }

            @Override
            public IRefDocEdit getRefDocEdit() {
                return null;
            }
        });
        this.resetFieldName();
    }

    @Override
    protected String getEnvWherePart() {
        return util.getEnvWherePart(this.getPara1(), this.getPk_group(),
                this.getPk_org()) + " and pk_org='"+this.getPk_org()+"'";
    }

    private String getResourceCode() {
        return util.getResourceCode(this.getRefNodeName());
    }

    /**
     * 重写数据权限缓存Key值
     */
    @Override
    protected String getDataPowerSqlKey(String strTableName,
            String dataPowerField) {
        String pk_org = null;
        String tableName = strTableName;
        if (strTableName != null) {
            tableName = strTableName.trim();
        }
        String dataPowerKey =
                tableName + this.getPara1() + "_" + dataPowerField
                        + getDataPowerOperation_code() + pk_org;
        return dataPowerKey;
    }

}
