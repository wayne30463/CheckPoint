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
import cp.json.Stage;

/**
 * Servlet implementation class JoinStageView
 */
@WebServlet("/ActivityStageView")
public class ActivityStageView extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivityStageView() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String aid = request.getParameter("aid");
		String result = DataBase.selectWithLabel("SELECT * FROM join_stage WHERE act_id = ?",
				new String[] {aid});
		List<JoinStage> JSLst = gson.fromJson(
				result
				,new TypeToken<List<JoinStage>>(){}.getType()
			);
		result = DataBase.selectWithLabel("SELECT * FROM stage WHERE id NOT IN (SELECT stg_id FROM join_stage WHERE act_id = ?)",
				new String[] {aid});
		List<Stage> SLst = gson.fromJson(
				result
				,new TypeToken<List<Stage>>(){}.getType()
			);
		out.println("[");
		for(JoinStage joinstage : JSLst){
			if(JSLst.indexOf(joinstage) != 0)
				out.println(",");
			out.println("{");
			out.println("\"stg_id\":\""+joinstage.getStgId()+"\",");
			out.println("\"stg_name\":\""+joinstage.getStgName()+"\",");
			out.println("\"isJoin\":true");
			out.println("}");
		}
		for(Stage stage : SLst){
			if(SLst.indexOf(stage) != 0 || JSLst.size() != 0)
				out.println(",");
			out.println("{");
			out.println("\"stg_id\":\""+stage.getId()+"\",");
			out.println("\"stg_name\":\""+stage.getName()+"\",");
			out.println("\"isJoin\":false");
			out.println("}");
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
