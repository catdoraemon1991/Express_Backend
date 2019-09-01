package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class shippingMethod
 */
@WebServlet("/shippingMethod")
public class shippingMethod extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public shippingMethod() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// step 1: read JSON object from HTTP request
		// step 2: From the JSON object, read shipping address and destination, and package dimension & weight
		// step 3: read station information from database using the getStation method in db/DBConnection.java
		// step 4: read robot information from database using the getMachine and getMachineByType method in db/DBConnection.java
		// step 5: calculate distance & time from GoogleAPI from each station using each machine type
		// step 6: calculate prices based on package dimension & weight & distance for each station using each machine type
		// step 7: convert what you get from step 5&6 into JSON and write into response
	}

}
