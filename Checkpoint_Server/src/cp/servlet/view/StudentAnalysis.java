package cp.servlet.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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
import cp.json.Student;

/**
 * Servlet implementation class StudentAnalysis
 */
@WebServlet("/StudentAnalysis")
public class StudentAnalysis extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentAnalysis() {
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
		String result = DataBase.selectWithLabel("(SELECT  act_id,CONCAT(stg_rank,':',(SELECT stg_name FROM join_stage WHERE rank = post_data.stg_rank AND act_id = post_data.act_id limit 1)) AS stg_rank,code,MAX(time) AS time,TIMEDIFF(MAX(time),MIN(time)) AS spent FROM post_data WHERE act_id = ? AND state <> 0 GROUP BY CONCAT(stg_rank,':',code) ORDER BY code,MAX(time),stg_rank)",new String[]{aid});
		List<PostData> PDLst = gson.fromJson(result,new TypeToken<List<PostData>>(){}.getType());
		String year = "", barcode_id = "", last_stg = "";
		int stage_count = 0;
		long time = 0;
		int line = 0;
		result = DataBase.select("SELECT award FROM activity WHERE id = ?",new String[]{aid});
		List<String> award = gson.fromJson(result,new TypeToken<List<String>>(){}.getType());
		out.println("{");
		out.println("\"award\":"+award.get(0)+",\"student\":");
		out.println("[");
		List<Student> studentLst = new ArrayList<Student>();
		for(int i = 0;i < PDLst.size();i++)
		{
			PostData post_data = PDLst.get(i);
			if(year.length() == 0){
				year = post_data.getCode().substring(0,1);
				barcode_id = post_data.getCode().substring(1);
				if(post_data.getSpent().compareTo("00:00:00") != 0){
					stage_count = 1;
					last_stg = post_data.getStgRank();
				}
				else{
					stage_count = 0;
					last_stg = "�L";
				}
				String[] timeArr = post_data.getSpent().split(":");
				time = Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
			}
			else if(post_data.getCode().compareTo(year+barcode_id) != 0){
				/*if(line++ != 0)
					out.println(",");
				out.println("{");
				out.println("\"year\":" + "\"" + year + "\",");
				out.println("\"barcode_id\":" + "\"" + barcode_id + "\",");
				out.println("\"last_stg\":" + "\"" + last_stg + "\",");
				out.println("\"stage_count\":" + "\"" + stage_count + "\",");
				out.println("\"spentTime\":" + "\"" + time + "\"");
				out.println("}");*/
				
				Student student = new Student();
				student.setYear(year);
				student.setBarCodeId(barcode_id);
				student.setLastStg(last_stg);
				student.setStgCount(String.valueOf(stage_count));
				student.setSpentTime(String.valueOf(time));
				studentLst.add(student);
				
				year = post_data.getCode().substring(0,1);
				barcode_id = post_data.getCode().substring(1);
				if(post_data.getSpent().compareTo("00:00:00") != 0){
					stage_count = 1;
					last_stg = post_data.getStgRank();
				}
				else{
					stage_count = 0;
					last_stg = "�L";
				}
				String[] timeArr = post_data.getSpent().split(":");
				time = Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
			}
			else{
				if(post_data.getSpent().compareTo("00:00:00") != 0) {
					last_stg = post_data.getStgRank();
					stage_count++;
				}
				String[] timeArr = post_data.getSpent().split(":");
				time += Integer.valueOf(timeArr[0]) * 3600 + 
					   Integer.valueOf(timeArr[1]) * 60 +
					   Integer.valueOf(timeArr[2]);
			}
			if(i == (PDLst.size() - 1)){
				/*if(line++ != 0)
					out.println(",");
				out.println("{");
				out.println("\"year\":" + "\"" + year + "\",");
				out.println("\"barcode_id\":" + "\"" + barcode_id + "\",");
				out.println("\"last_stg\":" + "\"" + last_stg + "\",");
				out.println("\"stage_count\":" + "\"" + stage_count + "\",");
				out.println("\"spentTime\":" + "\"" + time + "\"");
				out.println("}");*/
				
				Student student = new Student();
				student.setYear(year);
				student.setBarCodeId(barcode_id);
				student.setLastStg(last_stg);
				student.setStgCount(String.valueOf(stage_count));
				student.setSpentTime(String.valueOf(time));
				studentLst.add(student);
				
			}
		}
		if(studentLst.size() > 1){
			for (int i=1;i<studentLst.size();i++) {
				Student std2 = studentLst.get(i);
				Student std1 = studentLst.get(i-1);
				if(std2.getStgCount().compareTo(std1.getStgCount()) > 0){
					Collections.swap(studentLst, i, i-1);
					i=0;
				}
				else if(std2.getStgCount().compareTo(std1.getStgCount()) < 0){
					continue;
				}
				else if(std2.getSpentTime().compareTo(std1.getSpentTime()) < 0){
					Collections.swap(studentLst, i, i-1);
					i=0;
				}
			}
		}
		for (Student student : studentLst) {
			if(studentLst.indexOf(student) != 0)
				out.println(",");
			out.println("{");
			out.println("\"year\":" + "\"" + student.getYear() + "\",");
			out.println("\"barcode_id\":" + "\"" + student.getBarCodeId() + "\",");
			out.println("\"last_stg\":" + "\"" + student.getLastStg() + "\",");
			out.println("\"stage_count\":" + "\"" + student.getStgCount() + "\",");
			out.println("\"spentTime\":" + "\"" + student.getSpentTime() + "\"");
			out.println("}");
		}
		out.println("]");
		out.println("}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
