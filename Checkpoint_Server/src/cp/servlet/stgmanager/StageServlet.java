package cp.servlet.stgmanager;

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
import cp.json.Stage;

/**
 * Servlet implementation class StrageServlet
 */
@WebServlet("/StageManager/stgmgr")
public class StageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String result = DataBase.selectWithLabel("SELECT * FROM stage", null);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String data = request.getParameter("data");
		DataBase.delete("DELETE FROM stage", null);
		List<Stage> stgs = gson.fromJson(data, new TypeToken<List<Stage>>(){}.getType());
		for(Stage stg : stgs) {
			DataBase.insert("INSERT INTO stage (id,name,memo) VALUES (?,?,?)", 
					new String[]{stg.getId(),stg.getName(),stg.getMemo()});
		}
		out.println("{\"success\":\"Y\",\"message\":\"更新成功!\"}");
	}

}
