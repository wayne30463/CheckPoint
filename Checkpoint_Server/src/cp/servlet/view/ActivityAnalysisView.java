package cp.servlet.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cp.dao.DataBase;
import cp.json.Activity;
import cp.json.JoinStage;
import cp.json.PostData;

/**
 * Servlet implementation class getData
 */
@WebServlet("/ActivityViewer/actview1.view")
public class ActivityAnalysisView extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivityAnalysisView() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String aid = request.getParameter("aid");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String result = DataBase.selectWithLabel("SELECT * FROM activity WHERE ID=?",new String[]{aid});
		List<Activity> act = gson.fromJson(result,new TypeToken<List<Activity>>(){}.getType());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String today = sdf.format(date);
		sdf = new SimpleDateFormat("HH:mm");
		String nowtime = sdf.format(date);
		
		String state,start_time,end_time,most_pass,all_pass;
		StringBuilder sb = new StringBuilder();
		//state
		if(today.equals(act.get(0).getDate()) )
			if(nowtime.compareTo(act.get(0).getSTtime()) < 0)
				state = "活動尚未開始";
			else if(nowtime.compareTo(act.get(0).getEDtime()) < 0)
				state = "";
			else
				state = "活動已結束";
		else if(today.compareTo(act.get(0).getDate()) > 0)
			state = "活動已結束";
		else
			state = "活動尚未開始";
		sb.append("\"state\" : \"" + state + "\"");
		//start_time
		start_time = act.get(0).getSTtime();
		sb.append(",\"start_time\" : \"" + start_time + "\"");
		//end_time
		end_time = act.get(0).getEDtime();
		sb.append(",\"end_time\" : \"" + end_time + "\"");
		//most_pass
		result = DataBase.select("SELECT COUNT(*) AS count FROM post_data WHERE act_id = ? AND state = 2 GROUP BY code ORDER BY count DESC", 
											new String[]{aid});
		List<String> passLst = gson.fromJson(result,new TypeToken<List<String>>(){}.getType());
		if(passLst.size() == 0)
			most_pass = "0";
		else
			most_pass = passLst.get(0);
		sb.append(",\"most_pass\" : " + most_pass);
		//all_pass  
		result = DataBase.selectWithLabel("SELECT * FROM join_stage WHERE act_id = ?",new String[]{aid});
		List<JoinStage> jstageLst = gson.fromJson(result,new TypeToken<List<JoinStage>>(){}.getType());
		int allpass_counter = 0;
		for(String pass : passLst) {
			if(pass.compareTo(String.valueOf(jstageLst.size())) == 0)
				allpass_counter++;
		}
		all_pass = String.valueOf(allpass_counter);
		sb.append(",\"all_pass\" : " + all_pass);
		//stage_data
		sb.append(",\"stage_data\" : [");
		int index=0;
		for(JoinStage jstage : jstageLst) {
			List<PostData> post_data;
			//challenge
			result = DataBase.selectWithLabel("SELECT * FROM post_data WHERE act_id = ? AND stg_rank=? AND state = 1", 
							new String[]{aid,jstage.getRank()});
			post_data = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
			String challenge = String.valueOf(post_data.size());
			//pass
			result = DataBase.selectWithLabel("SELECT * FROM post_data WHERE act_id = ? AND stg_rank=? AND state = 2", 
							new String[]{aid,jstage.getRank()});
			post_data = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
			String pass = String.valueOf(post_data.size());
			
			//avg_time
			result = DataBase.select("SELECT TIMEDIFF(MAX(time),MIN(time)) FROM post_data WHERE act_id = ? AND stg_rank = ? AND state <> 0 GROUP BY code", 
					new String[]{aid,jstage.getRank()});
			List<String> timeLst = gson.fromJson(result,new TypeToken<List<String>>(){}.getType());
			int avg_timeTotal = 0, count = 0;
			for(String time : timeLst){
				String[] splitStr = time.split(":");
				int avg_time = Integer.valueOf(splitStr[0]) * 3600;
				avg_time += Integer.valueOf(splitStr[1]) * 60;
				avg_time += Integer.valueOf(splitStr[2]);
				if(avg_time != 0) {
					avg_timeTotal += avg_time;
					count++;
				}
			}
			if(count != 0)
				avg_timeTotal = avg_timeTotal / count;
			if(index++ > 0)
				sb.append(",");
			sb.append("{ \"stg_rank\" : \""+jstage.getRank()+"\", \"stg_name\" : \""+jstage.getStgName()+"\" , \"challenge\" : "+challenge+", \"pass\" : "+pass+", \"avg_time\" : \""+String.valueOf(avg_timeTotal)+"\"}");
		}
		sb.append("]");
		out.println("{"+sb.toString()+"}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String aid = request.getParameter("aid");
		System.out.println("{aid:"+aid+"}");
		Gson gson = new Gson();
		String result = DataBase.selectWithLabel("SELECT * FROM activity WHERE id=?",new String[]{aid});
		List<Activity> act = gson.fromJson(result,new TypeToken<List<Activity>>(){}.getType());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String today = sdf.format(date);
		PrintWriter out = response.getWriter();
		if(today.equals(act.get(0).getDate())){
			System.out.println("Y");
			System.out.println(act.get(0).getDate());
		}
		else{
			System.out.println("N");
			System.out.println(act.get(0).getDate());
		}
		String re = "{"
				+"	\"state\" : true,"
				+"	\"stage_count\" : 10,"
				+"	\"start_time\" : \"00:00:00\","
				+"	\"end_time\" : \"16:52:52\","
				+"	\"most_pass\" : 2,"
				+"	\"all_pass\" : 0,"
				+"	\"stage_data\" : "
				+"	["
				+"		{ \"stg_id\" : \"00\" , \"challenge\" : 2, \"pass\" : 2, \"avg_time\" : \"01:00\"}"
				+"	]"
				+"}";
		out.println(re);
		
	}

}
