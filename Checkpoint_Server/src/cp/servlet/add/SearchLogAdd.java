package cp.servlet.add;

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

/**
 * Servlet implementation class SearchLogAdd
 */
@WebServlet("/SearchLogAdd")
public class SearchLogAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchLogAdd() {
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
		String code = request.getParameter("code");
		List<Activity> act = gson.fromJson(
				DataBase.selectWithLabel("SELECT * FROM activity WHERE date = ?", new String[]{getDate()}),
				new TypeToken<List<Activity>>(){}.getType()
			);
		if(act.size() == 0){
			out.write("TODAY NOT ANY ACTIVITY!");
			return;
		}
		DataBase.insert("INSERT INTO search_log (act_id,code,time) VALUES (?,?,?)", 
				new String[]{act.get(0).getId(), code, getDateTime()});
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
