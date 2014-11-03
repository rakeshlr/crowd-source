package org.rakesh.crowdsource.purchase;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.regex.Pattern;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.rakesh.crowdsource.dao.Dao;
import org.rakesh.crowdsource.entity.Item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JWT_Handler {

	private String ISSUER;
	private String SIGNING_KEY;

	public JWT_Handler(String issuer, String key) {
		this.ISSUER = issuer;
		this.SIGNING_KEY = key;

	}

	protected String getJWT(String itemKey, String user)
			throws InvalidKeyException, SignatureException {
		JsonToken token;
		token = createToken(itemKey, user);
		return token.serializeAndSign();
	}

	private JsonToken createToken(String itemKey, String user)
			throws InvalidKeyException {

		if (itemKey == null)
			return null;

		Item item = Dao.INSTANCE.getItem(Long.parseLong(itemKey));
		// Current time and signing algorithm
		Calendar cal = Calendar.getInstance();
		HmacSHA256Signer signer = new HmacSHA256Signer(ISSUER, null,
				SIGNING_KEY.getBytes());

		// Configure JSON token
		JsonToken token = new net.oauth.jsontoken.JsonToken(signer);
		token.setAudience("Google");
		token.setParam("typ", "google/payments/inapp/item/v1");
		token.setIssuedAt(new org.joda.time.Instant(cal.getTimeInMillis()));
		token.setExpiration(new org.joda.time.Instant(
				cal.getTimeInMillis() + 60000L));

		// Configure request object, which provides information of the item
		JsonObject request = new JsonObject();
		request.addProperty("name", item.getTitle());
		request.addProperty("description", item.getDesc());
		request.addProperty("price", item.getPrice());
		request.addProperty("currencyCode", "INR");
		// request.addProperty("sellerData", itemKey);
		request.addProperty("sellerData", "user_id:" + this.ISSUER
				+ ",item_id:" + itemKey + ",buyer:" + user);
		JsonObject payload = token.getPayloadAsJsonObject();
		payload.add("request", request);
		return token;
	}

	// default
	@Deprecated
	private JsonToken createToken() throws InvalidKeyException {

		// Current time and signing algorithm
		Calendar cal = Calendar.getInstance();
		HmacSHA256Signer signer = new HmacSHA256Signer(ISSUER, null,
				SIGNING_KEY.getBytes());

		// Configure JSON token
		JsonToken token = new net.oauth.jsontoken.JsonToken(signer);
		token.setAudience("Google");
		token.setParam("typ", "google/payments/inapp/item/v1");
		token.setIssuedAt(new org.joda.time.Instant(cal.getTimeInMillis()));
		token.setExpiration(new org.joda.time.Instant(
				cal.getTimeInMillis() + 60000L));

		// Configure request object, which provides information of the item
		JsonObject request = new JsonObject();
		request.addProperty("name", "3D content");
		request.addProperty("description", "3d content purchase");
		request.addProperty("price", "1");
		request.addProperty("currencyCode", "INR");
		request.addProperty("sellerData", "user_id:" + this.ISSUER
				+ ", item_id:123213,affiliate:aksdfbovu9j");

		JsonObject payload = token.getPayloadAsJsonObject();
		payload.add("request", request);

		return token;
	}

	public String deserialize(String tokenString) {
		String[] pieces = splitTokenString(tokenString);
		String jwtPayloadSegment = pieces[1];
		JsonParser parser = new JsonParser();
		JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64
				.decodeBase64(jwtPayloadSegment)));
		return payload.toString();
	}

	/**
	 * @param tokenString
	 *            The original encoded representation of a JWT
	 * @return Three components of the JWT as an array of strings
	 */
	private String[] splitTokenString(String tokenString) {
		String[] pieces = tokenString.split(Pattern.quote("."));
		if (pieces.length != 3) {
			throw new IllegalStateException(
					"Expected JWT to have 3 segments separated by '" + "."
							+ "', but it has " + pieces.length + " segments");
		}
		return pieces;
	}
}
