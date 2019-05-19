package cn.http.pdata;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class PData {

	protected String method;
	private JSONObject body;
	private JSONObject pdata;

	public PData() {
		pdata = new JSONObject();
		body = new JSONObject();
		method();
	}

	public String getPStr() {
		try {
			pdata.put("method", method);
			pdata.put("params", body);
			return pdata.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void bodyAdd(String key, int value) {
		try {
			body.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void bodyAdd(String key, String value) {
		try {
			if(value != null) {
				body.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void bodyAdd(String key, boolean value) {
		try {
			body.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void bodyAdd(String key, JSONObject value) {
		try {
			body.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void bodyAdd(String key, JSONArray elementList) {
		try {
			body.put(key, elementList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected abstract void method();

}
