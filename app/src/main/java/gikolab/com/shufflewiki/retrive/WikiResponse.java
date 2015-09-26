package gikolab.com.shufflewiki.retrive;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WikiResponse 
{
	HttpResponse mHttpResponse;

	static final String RESPONSE_CODE_JSON_KEY = "code";
	static final String RESPONSE_ERROR_JSON_KEY = "error";

	public WikiResponse(HttpResponse httpresponse)
	{
		mHttpResponse = httpresponse;
	}

	public JSONObject getJsonObject()
	{
		try
		{
			return new JSONObject(EntityUtils.toString(mHttpResponse.getEntity()));
		}
		catch (org.apache.http.ParseException e)
		{
			return null;
		}
		catch (JSONException e)
		{
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public boolean isFailed()
	{
		return hasConnectionFailed() || hasErrorCode();
	}

	public boolean hasConnectionFailed()
	{
		return mHttpResponse.getEntity() == null;
	}

	public boolean hasErrorCode()
	{
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		
		return (statusCode < 200 || statusCode >= 300);
	}

	public WikiException getException()
	{
		if (hasConnectionFailed())
		{
			// connection failed situation

			return new WikiException(WikiException.CONNECTION_FAILED,
					"Connection to Wiki servers failed.");
		}

		if (!hasErrorCode())
		{
			// there's no error code so we shouldn't be here
			return new WikiException(WikiException.OPERATION_FORBIDDEN,
					"getException called with successful response");
		}
		// cases going forward had a successful with Wiki, but something
		// else went wrong, information about which is found in the
		// json-encoded http response

		JSONObject response = getJsonObject();

		if (response == null)
		{
			return new WikiException(WikiException.INVALID_JSON,
					"Invalid response from Wiki servers.");
		}

		// we have a valid json response

		// first attempt to read the error code
		// this key doesn't exist when the supplied Wiki keys are invalid

		int code;

		try
		{
			code = response.getInt(RESPONSE_CODE_JSON_KEY);
		}
		catch (JSONException e)
		{
			code = WikiException.NOT_INITIALIZED;
		}

		// read the error message

		String message;

		try
		{
			message = response.getString(RESPONSE_ERROR_JSON_KEY);
		}
		catch (JSONException e)
		{
			message = "Error undefinted by Wiki server.";
		}

		// build and return an exception with the supplied code and error
		
		return new WikiException(code, message);

	}

	static WikiException getConnectionFailedException(String message)
	{
		return new WikiException(WikiException.CONNECTION_FAILED,
				"Connection failed with Wiki servers.  Log: " + message);
	}
	
	static WikiException getConnectionFailedException(Throwable e)
	{
		return getConnectionFailedException(e.getMessage());
	}
 
}
