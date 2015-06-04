package cp.servlet.del;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cp.dao.DataBase;

/**
 * Servlet implementation class ActivityDelete
 */
@WebServlet("/ActivityDelete")
public class ActivityDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivityDelete() {
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
		String id = request.getParameter("id");
		DataBase.delete("DELETE FROM join_school  WHERE act_id=?", new String[]{id});
		DataBase.delete("DELETE FROM join_stage  WHERE act_id=?", new String[]{id});
		DataBase.delete("DELETE FROM post_data WHERE act_id=?", new String[]{id});
		DataBase.delete("DELETE FROM activity  WHERE id=?", new String[]{id});
		out.println("{\"success\":\"Y\",\"message\":\"§R°£¦¨¥\!\"}");
	}

}
