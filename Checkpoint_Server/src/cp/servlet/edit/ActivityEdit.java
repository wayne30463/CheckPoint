package cp.servlet.edit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cp.dao.DataBase;
import cp.json.Activity;
import cp.json.JoinSchool;

/**
 * Servlet implementation class ActivityAdd
 */
@WebServlet("/ActivityEdit")
public class ActivityEdit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivityEdit() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String id = request.getParameter("aid");
		String name = request.getParameter("name");
		String date = request.getParameter("date");
		String st_time = request.getParameter("st_time");
		String ed_time = request.getParameter("ed_time");
		String join_stage = request.getParameter("join_stage");
		String join_school = request.getParameter("join_school");
		String award = request.getParameter("award");
		String hasClr =  request.getParameter("hasClr");
		List<JoinSchool> jschools_lst = gson.fromJson(join_school,new TypeToken<List<JoinSchool>>(){}.getType());
		List<Integer> jstages_lst = gson.fromJson(join_stage,new TypeToken<List<Integer>>(){}.getType());
		List<Activity> check_date = gson.fromJson(
													DataBase.selectWithLabel("SELECT * FROM activity WHERE date = ? AND id <> ?", new String[]{date, id}),
													new TypeToken<List<Activity>>(){}.getType()
												);
		int rank = 0;
		List<Integer> total = new ArrayList(){};
		
		//check date time
		if(date == "" || st_time == "" || ed_time == "") {
			out.println("{\"success\":\"N\",\"message\":\"日期與時間不能為空!\"}");
			return;
		}
		else if(date.length() >10)
		{
			out.println("{\"success\":\"N\",\"message\":\"日期錯誤!\"}");
			return;
		}
		else if(check_date.size() > 0)
		{
			out.println("{\"success\":\"N\",\"message\":\"當天已有活動!\"}");
			return;
		}
		else if(st_time.compareToIgnoreCase(ed_time) >= 0)
		{
			out.println("{\"success\":\"N\",\"message\":\"開始時間不能大於結束時間!\"}");
			return;
		}

		//check st_code & ed_code
		for(int i = 0; i < 10 ; i++)
			total.add(0);
		for(JoinSchool school : jschools_lst)
			total.set(Integer.valueOf(school.getYear()), total.get(Integer.valueOf(school.getYear())) + Integer.valueOf(school.getAmong()));
		for(int i = 0; i < 10 ; i++)
			if(total.get(i) > 998)
			{
				out.println("{\"success\":\"N\",\"message\":\"人數超出範圍!\"}");
				return;
			}
		total.clear();
		for(int i = 0; i < 10 ; i++)
			total.add(1);

		DataBase.delete("DELETE FROM join_school  WHERE act_id=?", new String[]{id});
		DataBase.delete("DELETE FROM join_stage  WHERE act_id=?", new String[]{id});
		DataBase.delete("DELETE FROM activity  WHERE id=?", new String[]{id});
		if(hasClr.equals("Y")){
			//delete old data
			DataBase.delete("DELETE FROM post_data WHERE act_id=?", new String[]{id});
		}
		//start
		String key = date.substring(2, 4) + "%%";
		
		DataBase.insert("INSERT INTO activity (id,date,name,st_time,ed_time,award) VALUES (?,?,?,?,?,?)", 
						new String[]{String.valueOf(id),date,name,st_time,ed_time,award});
		out.println("{\"success\":\"Y\",\"message\":\"修改成功!\"}");
		for(int stage : jstages_lst) {
			DataBase.insert("INSERT INTO join_stage (act_id,stg_id,rank,stg_name) VALUES (?,?,?,(SELECT name FROM stage WHERE id=? limit 1))", 
					new String[]{String.valueOf(id),  String.valueOf(stage), String.valueOf(++rank), String.valueOf(stage)});
		}
		for(JoinSchool school : jschools_lst) {
			if (Integer.valueOf(school.getAmong()) == 0)
				DataBase.insert("INSERT INTO join_school (act_id,sch_id,sch_name,year,st_code,ed_code) VALUES (?,?,?,?,?,?)", 
						new String[]{String.valueOf(id),  school.getSchId(), school.getSchName(), school.getYear(), "0", "0"});
			else
			{
				int st_code = total.get(Integer.valueOf(school.getYear()));
				int ed_code = st_code + Integer.valueOf(school.getAmong()) - 1;
				DataBase.insert("INSERT INTO join_school (act_id,sch_id,sch_name,year,st_code,ed_code) VALUES (?,?,?,?,?,?)", 
						new String[]{String.valueOf(id),  school.getSchId(), school.getSchName(), school.getYear(), String.valueOf(st_code), String.valueOf(ed_code)});
				total.set(Integer.valueOf(school.getYear()), total.get(Integer.valueOf(school.getYear())) + Integer.valueOf(school.getAmong()));
			}
		}
	}

}
