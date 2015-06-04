package cp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
	private static String addr = "mysql://localhost:3306/cp";
	private static String id = "root";
	private static String pwd = "";
	public DataBase() {}	
	private static Connection BuildConn()
	{
		Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:" + addr + "?useUnicode=true&characterEncoding=utf-8",id,pwd);
		} catch (ClassNotFoundException e){
			System.out.printf("找不到連線類別檔案");
		} catch (SQLException e) {
			System.out.printf("conn error:" + e.getMessage());
			//e.printStackTrace();
		}
		return con;
		
	}
	public static void delete(String cmd,String[] paras)
	{
		Connection conn = BuildConn();
		PreparedStatement pst = null;
		//List<String> result = new ArrayList<String>();
    	try {
    		pst = conn.prepareStatement(cmd);
    		if(paras != null)
    			for (int i = 0;i< paras.length;i++) 
    				pst.setString(i+1, paras[i]);
			pst.executeUpdate();
	        pst.close();
			conn.close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} 
	}
	public static void insert(String cmd,String[] paras)
	{
		Connection conn = BuildConn();
		PreparedStatement pst = null;
		//List<String> result = new ArrayList<String>();
    	try {
    		pst = conn.prepareStatement(cmd);
    		if(paras != null)
    			for (int i = 0;i< paras.length;i++) 
    				pst.setString(i+1, paras[i]);
			pst.executeUpdate();
	        pst.close();
			conn.close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} 
	}
	public static void update(String cmd,String[] paras)
	{
		Connection conn = BuildConn();
		PreparedStatement pst = null;
		//List<String> result = new ArrayList<String>();
    	try {
    		pst = conn.prepareStatement(cmd);
    		if(paras != null)
    			for (int i = 0;i< paras.length;i++) 
    				pst.setString(i+1, paras[i]);
			pst.executeUpdate();
	        pst.close();
			conn.close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} 
	}
	public static String select(String cmd,String[] paras)
	{
		Connection conn = BuildConn();
		PreparedStatement pst = null;
		//List<String> result = new ArrayList<String>();
		StringBuilder result = new StringBuilder();
    	try {
    		pst = conn.prepareStatement(cmd);
    		if(paras != null)
    			for (int i = 0;i< paras.length;i++) 
    				pst.setString(i+1, paras[i]);
    		
			ResultSet reSet = pst.executeQuery();
			
	        while(reSet.next()){
	        	String rowStr = "";
	    		for (int i = 0;i < reSet.getMetaData().getColumnCount();i++){
	    			if(i != 0)
	    				rowStr += ",";
	    			if(reSet.getString(i+1) == null)
	    				rowStr += ("\"\"");
	    			else
	    				rowStr += ("\"" + reSet.getString(i+1) + "\"");
	    		}
	    		if(result.length() != 0)
	    			result.append(",");
	    		result.append(rowStr);
	        	//result.add("{" + rowStr + "}");
	        }
	        pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return "[" + result.toString() + "]";
	}
	public static String selectWithLabel(String cmd,String[] paras)
	{
		Connection conn = BuildConn();
		PreparedStatement pst = null;
		//List<String> result = new ArrayList<String>();
		StringBuilder result = new StringBuilder();
    	try {
    		pst = conn.prepareStatement(cmd);
    		if(paras != null)
    			for (int i = 0;i< paras.length;i++) 
    				pst.setString(i+1, paras[i]);
    		
			ResultSet reSet = pst.executeQuery();
			
	        while(reSet.next()){
	        	String rowStr = "";
	    		for (int i = 0;i < reSet.getMetaData().getColumnCount();i++){
	    			if(i != 0)
	    				rowStr += ",";
	    			rowStr += ("\"" + reSet.getMetaData().getColumnName(i+1) + "\":");
	    			if(reSet.getString(i+1) == null)
	    				rowStr += ("\"\"");
	    			else
	    				rowStr += ("\"" + reSet.getString(i+1) + "\"");
	    		}
	    		if(result.length() != 0)
	    			result.append(",");
	    		result.append("{" + rowStr + "}");
	        	//result.add("{" + rowStr + "}");
	        }
	        pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return "[" + result.toString() + "]";
	}
}
