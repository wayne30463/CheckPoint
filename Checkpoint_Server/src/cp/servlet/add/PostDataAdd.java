package cp.servlet.add;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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
import cp.json.PostData;
import cp.json.Stage;

/**
 * Servlet implementation class PostDataAdd
 */
@WebServlet("/PostDataAdd")
public class PostDataAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostDataAdd() {
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
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String stg_rank = request.getParameter("client_id");
		String code = request.getParameter("code");
		String check_time = getDateTime();
		String check_date = getDate();
		String state = "1";
		List<Activity> act = gson.fromJson(
				DataBase.selectWithLabel("SELECT * FROM activity WHERE date = ?", new String[]{check_date}),
				new TypeToken<List<Activity>>(){}.getType()
			);
		if(act.size() == 0){
			out.write("TODAY NOT ANY ACTIVITY!");
			return;
		}
		List<Stage> jstage = gson.fromJson(
				DataBase.selectWithLabel("SELECT * FROM join_stage WHERE rank = ? AND act_id = ?", 
											new String[]{stg_rank, act.get(0).getId()}),
				new TypeToken<List<Stage>>(){}.getType()
			);
		if(jstage.size() == 0) {
			out.write("STAGE ID IS WRONG!");
			return;
		}
		else if(check_time.substring(11).compareTo(act.get(0).getSTtime()) < 0) {
			out.write("ACTIVITY NOT YET!");
			return;
		}
		else if(check_time.substring(11).compareTo(act.get(0).getEDtime()) > 0) {
			out.write("ACTIVITY IS END!");
			return;
		}
		List<String> trs_codeLst = gson.fromJson(
				DataBase.select("SELECT * FROM translate WHERE act_id = ? AND trs_code=?", new String[]{act.get(0).getId(), code}),
				new TypeToken<List<String>>(){}.getType()
			);
		//¸É¸¹¨ú¥N
		for (String trs_code : trs_codeLst) {
			code = trs_code;
		}
		List<PostData> pdLst = gson.fromJson(
				DataBase.selectWithLabel("SELECT * FROM post_data WHERE code = ? AND act_id = ? AND stg_rank = ? ORDER BY state DESC", 
						new String[]{code, act.get(0).getId(), stg_rank}),
				new TypeToken<List<PostData>>(){}.getType()
			);
		if(pdLst.size() == 0){
			state = "1";
			out.write("check_in");
		}
		else if(pdLst.get(0).getState().compareTo("check_in") == 0){
			Date oldTime = StrToDate(pdLst.get(0).getTime());
			Date newTime = StrToDate(check_time);
			long passTime = newTime.getTime() - oldTime.getTime();
			System.out.println("oldTime:"+pdLst.get(0).getTime());
			System.out.println("newTime:"+check_time);
			if (passTime > 10000L && passTime < 300000L){
				state = "2";
				out.write("check_out");
			}
			else{
				state = "1";
				DataBase.update("UPDATE post_data SET state = '0' WHERE code = ? AND act_id = ? AND stg_rank = ?"
						,new String[]{code, act.get(0).getId(), stg_rank});
				out.write("check_in");
			}
		}
		else{
			out.write("check_out");
			return;
		}
		DataBase.insert("INSERT INTO post_data (act_id,stg_rank,code,time,state) VALUES (?,?,?,?,?)", 
							new String[]{act.get(0).getId(), stg_rank, code, check_time, state});
	}
 	public Date StrToDate(String str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
 	public String getDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String strDate = sdf.format(date);
		return strDate;
	}
 	public String getDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String strDate = sdf.format(date);
		return strDate;
	}
}
