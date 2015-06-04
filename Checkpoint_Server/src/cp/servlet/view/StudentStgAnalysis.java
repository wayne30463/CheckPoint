package cp.servlet.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cp.dao.DataBase;
import cp.json.JoinStage;
import cp.json.PostData;

/**
 * Servlet implementation class StudentStgAnalysis
 */
@WebServlet("/StudentStgAnalysis")
public class StudentStgAnalysis extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentStgAnalysis() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String aid = request.getParameter("aid");
		StringBuilder sbuilder  = new StringBuilder();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String result = DataBase.select("SELECT code FROM search_log WHERE act_id=? ORDER BY time DESC limit 1",new String[]{aid});
		List<String> codes = gson.fromJson(result,new TypeToken<List<String>>(){}.getType());

		if(codes.size() == 0)
			return;
		result = DataBase.selectWithLabel("SELECT act_id,stg_rank,code,MAX(time) AS time,MAX(state) AS state,TIMEDIFF(MAX(time),MIN(time)) AS spent FROM post_data WHERE act_id=? AND code=? AND state <> 0 GROUP BY stg_rank ORDER BY stg_rank",new String[]{aid,codes.get(0)});
		List<PostData> PDLst = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
		result = DataBase.selectWithLabel("SELECT * FROM join_stage WHERE act_id=?",new String[]{aid});
		List<JoinStage> JSLst = gson.fromJson(result,new TypeToken<List<JoinStage>>(){}.getType());
		long totaltime = 0;
		int passCount=0;
		for (JoinStage joinStage : JSLst) {
			boolean hasPass = false;
			for (int i=0;i<PDLst.size();i++){
				PostData postdata = PDLst.get(i);
				if(postdata.getStgRank().compareTo(joinStage.getRank()) == 0){
					String[] timeArr = postdata.getSpent().split(":");
					totaltime = Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
					hasPass = true;
					if(sbuilder.length() != 0)
						sbuilder.append(",");
					sbuilder.append("{");
					sbuilder.append("\"rank\":\""+postdata.getStgRank()+"\",");
					sbuilder.append("\"name\":\""+joinStage.getStgName()+"\",");
					if(postdata.getState().compareTo("check_out") == 0){
						sbuilder.append("\"state\":\"O\"");
						passCount++;
					}
					else
						sbuilder.append("\"state\":\"X\"");
					sbuilder.append("}");
					PDLst.remove(i);
					break;
				}
			}
			if(!hasPass){
				if(sbuilder.length() != 0)
					sbuilder.append(",");
				sbuilder.append("{");
				sbuilder.append("\"rank\":\""+joinStage.getRank()+"\",");
				sbuilder.append("\"name\":\""+joinStage.getStgName()+"\",");
				sbuilder.append("\"state\":\"X\"");
				sbuilder.append("}");
			}
			
		}
		out.println("{");
		out.println("\"code\":\""+codes.get(0)+"\",");
		out.println("\"passCount\":"+passCount+",");
		out.println("\"time\":"+totaltime+",");
		out.println("\"stages\":["+sbuilder.toString()+"]");
		out.println("}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	

}
