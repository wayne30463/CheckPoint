package cp.json;

public class PostData {
	private String act_id;
	private String stg_rank;
	private String code;
	private String time;
	private String state;
	private String spent;
	public void PostData() {};
	public void setActId(String act_id)
	{
		this.act_id = act_id;
	}
	public String getActId()
	{
		return this.act_id;
	}
	public void setStgRank(String stg_rank)
	{
		this.stg_rank = stg_rank;
	}
	public String getStgRank()
	{
		return this.stg_rank;
	}
	public String getCode()
	{
		return this.code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public String getTime()
	{
		return this.time;
	}
	public void setTime(String time)
	{
		this.time = time;
	}
	public String getState()
	{
		int result = state.compareTo("1");
		if(result == 0)
			return "check_in";
		else if(result>0)
			return "check_out";
		else
			return "dead";
	}
	public void setState(String state)
	{
		this.state = state;
	}
	public String getSpent()
	{
			return this.spent;
	}
	public void setSpent(String spent)
	{
		this.spent = spent;
	}

}
