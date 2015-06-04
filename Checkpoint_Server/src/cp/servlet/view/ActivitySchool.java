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
import cp.json.JoinSchool;
import cp.json.JoinStage;

/**
 * Servlet implementation class ActivitySchool
 */
@WebServlet("/ActivitySchool")
public class ActivitySchool extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivitySchool() {
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
		String result = DataBase.selectWithLabel("SELECT *,IF(ed_code-st_code=0,0,ed_code-st_code+1) AS among FROM join_school WHERE act_id = ?",
				new String[] {aid});
		List<JoinSchool> JSLst = gson.fromJson(
				result
				,new TypeToken<List<JoinSchool>>(){}.getType()
			);
		String id = "";
		String name = "";
		StringBuilder sbuilder = new StringBuilder();
		int index=0;
		out.println("[");
		for(JoinSchool jschool : JSLst){
			if(JSLst.indexOf(jschool) == 0) {
				id = jschool.getSchId();
				name = jschool.getSchName();
			}
			if(jschool.getSchId().compareTo(id) != 0){
				if(index != 0)
					out.println(",");
				out.println("{");
				out.println("\"index\":\""+(index++)+"\",");
				out.println("\"id\":\""+id+"\",");
				out.println("\"name\":\""+name+"\",");
				out.println("\"detials\":["+sbuilder.toString()+"]");
				out.println("}");
				sbuilder.delete(0, sbuilder.length());
				id = jschool.getSchId();
				name = jschool.getSchName();
				if(sbuilder.length() != 0)
					sbuilder.append(",");
				sbuilder.append("{\"year\":\""+jschool.getYear()+"\",");
				sbuilder.append("\"among\":\""+jschool.getAmong()+"\"}");
				
			}
			else{
				if(sbuilder.length() != 0)
					sbuilder.append(",");
				sbuilder.append("{");
				sbuilder.append("\"year\":"+jschool.getYear()+",");
				sbuilder.append("\"among\":"+jschool.getAmong()+"");
				sbuilder.append("}");
			}
			if(JSLst.indexOf(jschool) == (JSLst.size()-1)){
				if(index != 0)
					out.println(",");
				out.println("{");
				out.println("\"index\":\""+(index++)+"\",");
				out.println("\"id\":\""+id+"\",");
				out.println("\"name\":\""+name+"\",");
				out.println("\"detials\":["+sbuilder.toString()+"]");
				out.println("}");
				sbuilder.delete(0, sbuilder.length());
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
