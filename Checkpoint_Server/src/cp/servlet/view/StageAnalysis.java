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
 * Servlet implementation class StageAnalysis
 */
@WebServlet("/StageAnalysis")
public class StageAnalysis extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StageAnalysis() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String aid = request.getParameter("aid");
		String rank = request.getParameter("rank");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String result = "";
		result = DataBase.selectWithLabel("SELECT * FROM join_stage WHERE act_id=? AND rank=?",new String[]{aid,rank});
		List<JoinStage> jstageLst = gson.fromJson(result,new TypeToken<List<JoinStage>>(){}.getType());
		if(jstageLst.size() == 0) {
			out.println("{\"success\":\"N\",\"message\":\"查無此關卡!\"}");
			return;
		}
		StringBuilder sb = new StringBuilder();
		//stage_data
		int index=0;
		for(JoinStage jstage : jstageLst) {
			List<PostData> post_data;
			out.println("{\"success\":\"Y\",\"message\":[");
			//challenge總共闖關者人數
			result = DataBase.selectWithLabel("SELECT * FROM post_data WHERE act_id = ? AND stg_rank=? GROUP BY code ", 
							new String[]{aid,jstage.getRank()});
			post_data = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
			String challenge = String.valueOf(post_data.size());
			//pass過關人數
			result = DataBase.selectWithLabel("SELECT * FROM post_data WHERE act_id = ? AND stg_rank=? AND state = 2", 
							new String[]{aid,jstage.getRank()});
			post_data = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
			String pass = String.valueOf(post_data.size());
			
			//avg_time
			result = DataBase.selectWithLabel("SELECT act_id,stg_rank,code,MAX(state) AS state,MAX(time) AS time,TIMEDIFF(MAX(time),MIN(time)) AS spent FROM post_data WHERE act_id = ? AND stg_rank = ? GROUP BY code ORDER BY MAX(time)", 
					new String[]{aid,jstage.getRank()});
			List<PostData> postdataLst = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
			int avg_timeTotal = 0, count = 0;
			for(PostData std : postdataLst){
				if(std.getCode().compareTo(postdataLst.get(0).getCode()) != 0)
					sb.append(",");
				sb.append("{");
				sb.append("\"time\":\""+std.getTime()+"\",");
				sb.append("\"code\":\""+std.getCode()+"\",");
				sb.append("\"state\":\""+std.getState()+"\",");
				sb.append("\"spent\":\""+std.getSpent()+"\"");
				sb.append("}");
				String[] splitStr = std.getSpent().split(":");
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
			out.println("{ \"stg_rank\" : \""+jstage.getRank()+"\" , "
					+ "\"stg_name\" : \""+jstage.getStgName()+"\","
					+ " \"challenge\" : "+challenge+","
					+ " \"pass\" : "+pass+","
					+ " \"avg_time\" : \""+String.valueOf(avg_timeTotal)+"\","
					+ "\"student\" : ["+sb.toString()+"]");
			out.println("}]}");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
