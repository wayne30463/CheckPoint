package cp.json;

public class Activity {
	private String id;
	private String date;
	private String name;
	private String st_time;
	private String ed_time;
	private int award;
	public Activity() {}
	public String getId()
	{
		return this.id;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	public String getDate()
	{
		return this.date;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	public void setSTtime(String st_time)
	{
		this.st_time = st_time;
	}
	public String getSTtime()
	{
		return this.st_time;
	}
	public void setEDtime(String ed_time)
	{
		this.ed_time = ed_time;
	}
	public String getEDtime()
	{
		return this.ed_time;
	}
	public void setAward(int award)
	{
		this.award = award;
	}
	public int getAward()
	{
		return this.award;
	}
}
