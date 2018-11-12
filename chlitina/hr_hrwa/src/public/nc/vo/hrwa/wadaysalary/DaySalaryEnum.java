package nc.vo.hrwa.wadaysalary;

public abstract interface DaySalaryEnum {
	
	  public static final int TBMDAYSALARY = 1;//������н
	  public static final int TBMHOURSALARY = 2;//����ʱн
	  public static final int TAXABLEDAYSALARY = 3;//����Ӧ˰��н
	  public static final int TAXABLEHOURSALARY = 4;//����Ӧ˰ʱн
	  public static final int TAXFREEDAYSALARY = 5;//������˰��н
	  public static final int TAXFREEHOURSALARY = 6;//������˰ʱн
	  
	  
	  public static final double  HOURSALARYNUM= 8.0;//ʱн����ʱ��
	  
	  public static final double  TBMSALARYNUM01= 30.0;//�̶�ֵ30��
	  
	  public static final double  TBMSALARYNUM02= 21.75;//�̶�ֵ21.75��
	  
	  public static final double  DAYSAYSALARYNUM03= 30.0;//�̶�ֵ30��
	  
	  //����ʱн����ȡֵ��ʽ
	  public static final int TBMNUMTYPE1 = 0;//�̶�ֵ30��
	  public static final int TBMNUMTYPE2 = 1;//�̶�ֵ21.75��
	  public static final int TBMNUMTYPE3 = 2;//�����ڼ�
	  
	  //��н��������ȡֵ��ʽ
	  public static final int DAYNUMTYPE1 = 0;//н���ڼ��н������������+ƽ�գ�
	  public static final int DAYNUMTYPE2 = 1;//н���ڼ�����
	  public static final int DAYNUMTYPE3 = 2;//�̶�30��
	  
	  //����ע�����
	  public static final String DAYSYSINT="HRWA021";//��н��������ȡֵ��ʽ
	  public static final String TBMSYSINT="HRWA020";//����ʱн����ȡֵ��ʽ
	  
	  //��̨����Ԥ�Ʋ���
	  public static final int MAXRANGE=31;//ָ����нӋ�㹠�������ֵ
	  public static final int MAXRESERVE=90;//��нӋ��󔵓��������L�r�g   Reserved
	  public static final int MINRESERVE=31;//��нӋ��󔵓�������С�r�g
	  public static final int MAXCHECKRANGE=7;//��нӋ��У��Ƿ�Ӌ�㹠��
}
