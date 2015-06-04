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
import cp.json.PostData;

/**
 * Servlet implementation class AwardView
 */
@WebServlet("/AwardView")
public class AwardView extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AwardView() {
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
		String result = DataBase.selectWithLabel("(SELECT  act_id,stg_rank,code,MAX(time) AS time,TIMEDIFF(MAX(time),MIN(time)) AS spent FROM post_data WHERE act_id = ? AND state <> 0 GROUP BY code+stg_rank ORDER BY code,MAX(time),stg_rank)",new String[]{aid});
		List<PostData> PDLst = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
		String year = "", barcode_id = "", last_stg = "";
		int stage_count = 0;
		long time = 0;
		int line = 0;
		out.println("[");
		
		for(int i = 0;i < PDLst.size();i++)
		{
			PostData post_data = PDLst.get(i);
			if(year.length() == 0){
				year = post_data.getCode().substring(0,1);
				barcode_id = post_data.getCode().substring(1);
				last_stg = post_data.getStgRank();
				stage_count = 1;
				String[] timeArr = post_data.getSpent().split(":");
				time = Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
			}
			else if(post_data.getCode().compareTo(year+barcode_id) != 0){
				if(line++ != 0)
					out.println(",");
				out.println("{");
				out.println("\"year\":" + "\"" + year + "\",");
				out.println("\"barcode_id\":" + "\"" + barcode_id + "\",");
				out.println("\"last_stg\":" + "\"" + last_stg + "\",");
				out.println("\"stage_count\":" + "\"" + stage_count + "\",");
				out.println("\"spentTime\":" + "\"" + time + "\"");
				out.println("}");
				year = post_data.getCode().substring(0,1);
				barcode_id = post_data.getCode().substring(1);
				last_stg = post_data.getStgRank();
				stage_count = 1;
				String[] timeArr = post_data.getSpent().split(":");
				time = Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
			}
			else{
				last_stg = post_data.getStgRank();
				stage_count++;
				String[] timeArr = post_data.getSpent().split(":");
				time += Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
			}
			if(i == (PDLst.size() - 1)){
				if(line++ != 0)
					out.println(",");
				out.println("{");
				out.println("\"year\":" + "\"" + year + "\",");
				out.println("\"barcode_id\":" + "\"" + barcode_id + "\",");
				out.println("\"last_stg\":" + "\"" + last_stg + "\",");
				out.println("\"stage_count\":" + "\"" + stage_count + "\",");
				out.println("\"spentTime\":" + "\"" + time + "\"");
				out.println("}");
			}
		}
		out.println("]");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
