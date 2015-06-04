package cp.json;

public class Student {
	private String year;
	private String barcode_id;
	private String last_stg;
	private String stage_count;
	private String spentTime;
	public void Student() {};
	public void setYear(String year)
	{
		this.year = year;
	}
	public String getYear()
	{
		return this.year;
	}
	public void setBarCodeId(String barcode_id)
	{
		this.barcode_id = barcode_id;
	}
	public String getBarCodeId()
	{
		return this.barcode_id;
	}
	public void setLastStg(String last_stg)
	{
		this.last_stg = last_stg;
	}
	public String getLastStg()
	{
		return this.last_stg;
	}
	public void setStgCount(String stage_count)
	{
		this.stage_count = stage_count;
	}
	public String getStgCount()
	{
		return this.stage_count;
	}
	public void setSpentTime(String spentTime)
	{
		this.spentTime = spentTime;
	}
	public String getSpentTime()
	{
		return this.spentTime;
	}
}
