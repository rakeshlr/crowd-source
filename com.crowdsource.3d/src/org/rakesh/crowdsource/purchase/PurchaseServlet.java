package org.rakesh.crowdsource.purchase;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rakesh.crowdsource.dao.Dao;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class PurchaseServlet extends HttpServlet {

	Seller_Info seller = new Seller_Info();
	final String ISSUER = seller.ISSUER;
	final String SIGNING_KEY = seller.SIGNING_KEY;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// Handles get requests.
		String jwtString = null;
		try {
			// create JWT for the item
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();

			JWT_Handler handler = new JWT_Handler(ISSUER, SIGNING_KEY);
			String itemId = req.getParameter("item");
			System.out.println("ItemId : " + itemId);
			jwtString = handler.getJWT(itemId, user + "");
			System.out.println("JWT : " + jwtString);

			// set and forward the HTTP request and response
			// RequestDispatcher dispatcher = req
			// .getRequestDispatcher("index.jsp?token=" + token1);
			// if (dispatcher != null) {
			// dispatcher.forward(req, resp);
			// }
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
			// } catch (ServletException e) {
			// e.printStackTrace();
		}
		resp.getWriter().print(jwtString);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println("In postback response..");
		// "Handles post request.
		String jwt = request.getParameter("jwt");
		String orderID;
		String jwt_response = new JWT_Handler(ISSUER, SIGNING_KEY)
				.deserialize(jwt);
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().create();
		JsonArray payload = parser.parse("[" + jwt_response + "]")
				.getAsJsonArray();
		Payload payload_1 = gson.fromJson(payload.get(0), Payload.class);
		// validate the payment request and respond back to Google
		log("In postback response..iss getter" + payload_1.iss_getter());
		log("In postback response.. Issser" + ISSUER);
		log("In postback response..aud" + payload_1.aud_getter());
		if (payload_1.iss_getter().equals("Google")
				&& payload_1.aud_getter().equals(ISSUER)) {
			if (payload_1.response_getter() != null
					&& payload_1.response_getter().orderId_getter() != null) {
				orderID = payload_1.response_getter().orderId_getter();
				if (payload_1.request_getter().currencyCode_getter() != null
						&& payload_1.request_getter().sellerData_getter() != null
						&& payload_1.request_getter().name_getter() != null
						&& payload_1.request_getter().price_getter() != null) {
					// optional - update local database
					// UserService userService = UserServiceFactory
					// .getUserService();
					// User user = userService.getCurrentUser();

					try {
						Dao.INSTANCE.addPurchase(payload_1);
						log("Purchase Added : " + payload_1);
						// respond back to complete payment
						response.setStatus(200);
						PrintWriter writer = response.getWriter();
						writer.write(orderID);
					} catch (Exception e) {
						log("Purchase not added : " + e.getMessage()
								+ e.getStackTrace()[0].getMethodName()
								+ e.getStackTrace()[0].toString());
						e.printStackTrace();
					}

				}
			}
		}
	}
}
