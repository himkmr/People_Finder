
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static Connection conn;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		connectDB();
		String message ="";
		String lname = request.getParameter("lastname");
		if(lname.length()<2 || lname.length()>15){
			message = "<br><b>Query Too Short or Long</b><br>";
			disconnectDB();

			request.setAttribute("message", message);
			response.setContentType("text/html");
			getServletContext().getRequestDispatcher("/output.jsp").include(request , response);
		
		}
			ResultSet rst =null;
		PreparedStatement pst = null;
		String query1 = "Select firstname, lastname, city_id, state_id, company_id from customers where UPPER(lastname) = UPPER("+"'"+lname+"')";

		
		try {
			pst = conn.prepareStatement(query1);
			rst = pst.executeQuery();
			
			if(!rst.isBeforeFirst())
			{
				message = message+"<b>Where lastname is LIKE "+lname+"</b></br>";

				String query2 ="Select firstname, lastname, city_id, state_id, company_id from customers where lastname LIKE '%"+lname+"%'";
				String query_company ="Select company from company where company LIKE '%"+lname+"%'";

				PreparedStatement pst2 = conn.prepareStatement(query2);
				rst =pst2.executeQuery();
				ResultSet rs= null;
				PreparedStatement ps =null;
				
				while(rst.next())
				{
					String name = rst.getString("firstname");
					String city_id= rst.getString("city_id");
					String state_id= rst.getString("state_id");
					String query5 ="Select city from city where cityid ="+city_id;
					String query6 ="Select states from states where stateid ="+state_id;
					
					ps = conn.prepareStatement(query5);
					rs = ps.executeQuery();
					rs.next();				
					String city = rs.getString("city");
					
					PreparedStatement pst4 = conn.prepareStatement(query6);
					rs = pst4.executeQuery();
					rs.next();
					String state = rs.getString("states");
					
					message = message + name+", "+ city +" "+state +"<br>";
					rs.close();
					ps.close();
					
				}
				
				//find companies
				PreparedStatement pst_company = conn.prepareStatement(query_company);
				rst =pst_company.executeQuery();
				message +="<b><br>Where company is LIKE "+lname+"<br></b>";
				while(rst.next())
				{
					String company = rst.getString("company");
					
					message = message +company+"<br>";
					
				}
				
			}
			else
			{
				System.out.println("There is somthing in resultset");
				message += "<b>People whose last name is "+ lname +"</b><br>";
				while(rst.next()){
				String name = rst.getString("firstname");
				String city_id= rst.getString("city_id");
				String state_id= rst.getString("state_id");

				String query2 ="Select city from city where cityid ="+city_id;
				String query3 ="Select states from states where stateid ="+state_id;
				System.out.println(query3);
				
				PreparedStatement pst2 = conn.prepareStatement(query2);
				ResultSet rs = pst2.executeQuery();
				rs.next();				
				String city = rs.getString("city");
				
				PreparedStatement pst3 = conn.prepareStatement(query3);
				ResultSet rs3 = pst3.executeQuery();
				if(rs3==null)
					System.out.println("resultset null");
				rs3.next();
				String state = rs3.getString("states");
				
				message = message + name+", "+ city +" "+state +"<br>";
				
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		disconnectDB();

		request.setAttribute("message", message);
		response.setContentType("text/html");
		getServletContext().getRequestDispatcher("/output.jsp").forward(
				request, response);
	}

	private void disconnectDB() {
	try {
		conn.close();
		System.out.println("Disconnected!");
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}

	private void connectDB() {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String url = "jdbc:oracle:thin:testuser/password@localhost";

		// properties for creating connection to Oracle database
		Properties props = new Properties();
		props.setProperty("user", "testdb");
		props.setProperty("password", "password");

		// creating connection to Oracle database using JDBC
		try {
			conn = DriverManager.getConnection(url, props);
			System.out.println("Connected!");
			if (conn == null)
				throw new SQLException();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
