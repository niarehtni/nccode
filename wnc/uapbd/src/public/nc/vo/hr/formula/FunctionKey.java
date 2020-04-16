package nc.vo.hr.formula;

import nc.hr.utils.ResHelper;

/**
 * @author: zhangg
 * @date: 2010-5-6 11:54:20
 * 
 */
public class FunctionKey {
	public static final String BUDGETITEM = "BUDGETITEM";//

	public static final String CALMONTH = "CALMONTH";// ��Ȼ�·�

	public static final String CALMONTH_DES = ResHelper.getString("6001formula", "06001formula0022")
	/* @res "��Ȼ�·�" */;// ��Ȼ�·�

	public String getCALMONTH_DES() {
		return ResHelper.getString("6001formula", "06001formula0022")
		/* @res "��Ȼ�·�" */;// ��Ȼ�·�
	}

	public static final String CALYEAR = "CALYEAR";// ��Ȼ���

	public static final String CALYEAR_DES = ResHelper.getString("6001formula", "06001formula0023")
	/* @res "��Ȼ���" */;// ��Ȼ���

	public String getCALYEAR_DES() {
		return ResHelper.getString("6001formula", "06001formula0023")
		/* @res "��Ȼ���" */;// ��Ȼ���
	}

	public static final String CURRDATE = "CURRDATE";// ��ǰϵͳ����

	public static final String CURRDATE_DES = ResHelper.getString("6001formula", "06001formula0024")
	/* @res "��ǰϵͳ����" */;// ��ǰϵͳ����

	public String getCURRDATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0024")
		/* @res "��ǰϵͳ����" */;// ��ǰϵͳ����
	}

	public static final String CURRORGCODE = "CURRORGCODE";// ��ǰ��֯����

	public static final String CURRORGCODE_DES = ResHelper.getString("6001formula", "06001formula0025")
	/* @res "��ǰ��֯����" */;// ��ǰ��֯����

	public String getCURRORGCODE_DES() {
		return ResHelper.getString("6001formula", "06001formula0025")
		/* @res "��ǰ��֯����" */;// ��ǰ��֯����
	}

	public static final String CURRORGNAME = "CURRORGNAME";// ��ǰ��֯����

	public static final String CURRORGNAME_DES = ResHelper.getString("6001formula", "06001formula0026")
	/* @res "��ǰ��֯����" */;// ��ǰ��֯����

	public String getCURRORGNAME_DES() {
		return ResHelper.getString("6001formula", "06001formula0026")
		/* @res "��ǰ��֯����" */;// ��ǰ��֯����
	}

	public static final String CURRORGPK = "CURRORGPK";// ��ǰ��֯����

	public static final String CURRORGPK_DES = ResHelper.getString("6001formula", "06001formula0027")
	/* @res "��ǰ��֯����" */;// ��ǰ��֯����

	public String getCURRORGPK_DES() {
		return ResHelper.getString("6001formula", "06001formula0027")
		/* @res "��ǰ��֯����" */;// ��ǰ��֯����
	}

	public static final String EXCHANGERATE = "EXCHANGERATE";// ���ʺ���

	public static final String EXCHANGERATE_DES = ResHelper.getString("6001formula", "06001formula0028")
	/* @res "���ʺ���" */;// ���ʺ���

	public String getEXCHANGERATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0028")
		/* @res "���ʺ���" */;// ���ʺ���
	}

	public static final String FIRSTMONWORKDAYS = "FIRSTMONWORKDAYS";// ��Ƹ�¹���������

	public static final String FIRSTMONWORKDAYS_DES = ResHelper.getString("6001formula", "06001formula0029")
	/* @res "��Ƹ�¹���������" */;// ��Ƹ�¹���������

	public String getFIRSTMONWORKDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0029")
		/* @res "��Ƹ�¹���������" */;// ��Ƹ�¹���������
	}

	public static final String FUNPAYDATA = "FUNPAYDATA";// ȡָ�����ָ���ڼ��н�ʻ�������

	public static final String GRADEINFO = "GRADEINFO";// ȡָ��н�ʱ�׼����µ�н�ʼ���͵���
	public static final String GRADEVALUE = "GRADEVALUE";// ȡָ��н�ʱ�׼����µ���ֵ

	public static final String LASTMONWORKDAYS = "LASTMONWORKDAYS";// ��ְ�¹���������
	public static final String LASTMONWORKDAYS_DES = ResHelper.getString("6001formula", "06001formula0030")
	/* @res "��ְ�¹���������" */;// ��ְ�¹���������

	public String getLASTMONWORKDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0030")
		/* @res "��ְ�¹���������" */;// ��ְ�¹���������
	}

	/*
	 * public static final String AVEPAYDATA = "AVEPAYDATA";//ȡн�����ݵ�ƽ��ֵ public
	 * static final String MAXPAYDATA = "MAXPAYDATA";//ȡн�����ݵ����ֵ public static
	 * final String MINPAYDATA = "MINPAYDATA";//ȡн�����ݵ���Сֵ
	 */
	public static final String PAYDATA = "PAYDATA";// ȡָ��������ͬн���ڼ��н����Ŀ����
	public static final String PAYDATAAB = "PAYDATAAB";// н��ͳ����_���ʱ���

	public static final String PAYDATAITEM = "PAYDATAITEM";// н�ʺ���:���ڼ���Ŀȡ��
	public static final String PAYDATAOP = "PAYDATAOP";// н��ͳ����_����ʱ���

	public static final String PAYDATAPD = "PAYDATAPD";// ȡָ�����ָ���ڼ��н������
	public static final String PAYDATASF = "PAYDATASF";// �Ŷ�ͳ�ƺ���

	public static final String PSNAGE = "PSNAGE";// ���º���:����
	public static final String PSNAGE_DES = ResHelper.getString("common", "UC000-0001813")
	/* @res "����" */;// ���º���:����

	public static final String TAXRATE = "TAXRATE";// ��˰����
	public static final String TAXRATE_DES = ResHelper.getString("6001formula", "06001formula0031")
	/* @res "��˰����" */;// ��˰����

	public static final String UNITAGE = "UNITAGE";// ���º���:�ڲ�����
	public static final String UNITAGE_DES = ResHelper.getString("common", "UC000-0000437")
	/* @res "�ڲ�����" */;// ���º���:�ڲ�����

	public static final String USERCODE = "USERCODE";// ��¼�û�����
	public static final String USERCODE_DES = ResHelper.getString("6001formula", "06001formula0032")
	/* @res "��¼�û�����" */;// ��¼�û�����

	public static final String USERNAME = "USERNAME";// ��¼�û�����
	public static final String USERNAME_DES = ResHelper.getString("6001formula", "06001formula0033")
	/* @res "��¼�û�����" */;// ��¼�û�����

	public static final String USERPK = "USERPK";// ��¼�û�����
	public static final String USERPK_DES = ResHelper.getString("6001formula", "06001formula0034")
	/* @res "��¼�û�����" */;// ��¼�û�����

	public static final String VALUEOFWADOC = "VALUEOFWADOC";// ����������
	public static final String WAGEFORM = "WAGEFORM";// ȡָн�ʹ��������

	public static final String OTHERSOURCE = "OTHERSOURCE";// ȡ���ⲿ����Դ

	public static final String WAGEMONTH = "WAGEMONTH";// н���ڼ�
	public static final String WAGEMONTH_DES = ResHelper.getString("6001formula", "06001formula0035")
	/* @res "н���ڼ�" */;// н���ڼ�

	public String getWAGEMONTH_DES() {
		return ResHelper.getString("6001formula", "06001formula0035")
		/* @res "н���ڼ�" */;// н���ڼ�
	}

	public static final String WAGEYEAR = "WAGEYEAR";// н�����
	public static final String WAGEYEAR_DES = ResHelper.getString("6001formula", "06001formula0036")
	/* @res "н�����" */;// н�����

	public String getWAGEYEAR_DES() {
		return ResHelper.getString("6001formula", "06001formula0036")
		/* @res "н�����" */;// н�����
	}

	public static final String WAPERIODDAYS = "WAPERIODDAYS";// �ڼ���������
	public static final String WAPERIODDAYS_DES = ResHelper.getString("6001formula", "06001formula0037")
	/* @res "�ڼ���������" */;// �ڼ���������

	public String getWAPERIODDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0037")
		/* @res "�ڼ���������" */;// �ڼ���������
	}

	public static final String WAPERIODENDDATE = "WAPERIODENDDATE";// �ڼ��������
	public static final String WAPERIODENDDATE_DES = ResHelper.getString("6001formula", "06001formula0038")
	/* @res "�ڼ��������" */;// �ڼ��������

	public String getWAPERIODENDDATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0038")
		/* @res "�ڼ��������" */;// �ڼ��������
	}

	public static final String WAPERIODSTARTDATE = "WAPERIODSTARTDATE";// �ڼ俪ʼ����
	public static final String WAPERIODSTARTDATE_DES = ResHelper.getString("6001formula", "06001formula0039")
	/* @res "�ڼ俪ʼ����" */;// �ڼ俪ʼ����

	public String getWAPERIODSTARTDATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0039")
		/* @res "�ڼ俪ʼ����" */;// �ڼ俪ʼ����
	}

	public static final String WAPERIODWORKDAYS = "WAPERIODWORKDAYS";// �ڼ乤��������
	public static final String WAPERIODWORKDAYS_DES = ResHelper.getString("6001formula", "06001formula0040")
	/* @res "�ڼ乤��������" */;// �ڼ乤��������

	public String getWAPERIODWORKDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0040")
		/* @res "�ڼ乤��������" */;// �ڼ乤��������
	}

	public static final String WORKAGE = "WORKAGE";// ���º���:����
	public static final String WORKAGE_DES = ResHelper.getString("common", "UC000-0001696")
	/* @res "����" */;// ���º���:����

	public String getWORKAGE_DES() {
		return ResHelper.getString("common", "UC000-0001696")
		/* @res "����" */;// ���º���:����
	}

	// mod by Connie.ZH
	// 2019-05-28 started
	public static final String Cal_Accumulate_Paid = "CalAccumulatePaid";// ��ί����Ӌ�o�����~
	public static final String Cal_Paid_Welfare = "CalPaidWelfare";// ��ί����Ӌ���۸�����
	// 2019-05-28 ended
	public static final String PREPERIODSTARTDATE = "PREPERIODSTARTDATE";// ���ڼ俪ʼ����
	public static final String PREPERIODENDDATE = "PREPERIODENDDATE";// ���ڼ��������
	public static final String REGISTERDATE = "REGISTERDATE";// ��ְ����
	public static final String DISMISSIONDATE = "DISMISSIONDATE";// ��ְ����
	public static final String WAGEDAYS = "WAGEDAYS";// ��н������

	// ssx added on 2020-02-21
	public static final String TERMWAGEDAYS = "TERMWAGEDAYS";// ��ְ�ڼ��н������

	public static final String INTERVALDURATION = "INTERVALDURATION";// �^�g��ͣ�씵
	public static final String OVERTIMEPAY = "OVERTIMEPAY";// ��˰�Ӱ�н��
	public static final String TAXABLEOVERTIMEPAY = "TAXABLEOVERTIMEPAY";// Ӧ˰�Ӱ�н��
	public static final String PREADJUSTDATE = "PREADJUSTDATE";// �ϴε�н����
	public static final String STATISTICLEAVECHARGE = "STATISTICLEAVECHARGE";// �yӋՈ�ٿۿ��
																				// add
																				// by
																				// ward
																				// 20180511
	public static final String ITEMDATARATIO = "ITEMDATARATIO";// ��Ŀռ��
	public static final String COURTDEDUCT = "COURTDEDUCT";// ��Ժǿ�ƿۿ� by he
	public static final String YEARLYRESTHOURS = "YEARLYRESTHOURS"; // ����a��ʣ�N�r��
																	// add by
																	// ssx on
																	// 2019-01-25
	public static final String LEAVEYEARLYRESTHOURS = "LEAVEYEARLYRESTHOURS"; // �x�ˆT����a��ʣ�N�r��
	public static final String BONUSBASEOFLASTTERM = "BONUSBASEOFLASTTERM"; // ������ĩ�������
	public static final String LASTWADOCAMOUNT = "LASTWADOCAMOUNT"; // ���¶�н����
}
