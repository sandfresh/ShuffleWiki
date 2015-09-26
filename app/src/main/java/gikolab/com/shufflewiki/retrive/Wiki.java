package gikolab.com.shufflewiki.retrive;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gikolab.com.shufflewiki.parse.ParseSaveMgr;
import gikolab.com.shufflewiki.parse.SaveFile;
import gikolab.com.shufflewiki.value.Save;


public class Wiki 
{

	private static Wiki mInstance;

	static public Wiki getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new Wiki();
		}
		return mInstance;
	}

	final private String END_POINT = "https://en.wikipedia.org/w/api.php?action=";
	List<String> mBookTitleList;
	SaveFile mSave;

	public  Wiki()
	{
		mBookTitleList = new ArrayList<>();
	}

	public JSONObject getWikiData(String url) throws WikiException
	{
		url = url.replaceAll("\\s","%20");
		Log.i("wIKI", "Url:" + url);
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet (END_POINT+url);
			httpget.setHeader("User-Agent", "MySuperUserAgent");

			HttpResponse httpResponse = httpclient.execute(httpget);

			WikiResponse response = new WikiResponse(httpResponse);

			if (!response.isFailed())
			{
				return response.getJsonObject();
			}
			else
			{
				throw response.getException();
			}
		}
		catch (ClientProtocolException e)
		{
			throw WikiResponse.getConnectionFailedException(e.getMessage());
		}
		catch (IOException e)
		{
			throw WikiResponse.getConnectionFailedException(e.getMessage());
		}
	}
 
	public JSONObject getTitle() throws WikiException
	{
		String url = "query&list=random&format=json&rnnamespace=0&rnlimit=10&rawcontinue=&generator=random&grnnamespace=0";
		return getWikiData(url);
	}

	private class getTitleThread extends Thread
	{
		GetNetCallBack mGetCallback;

		getTitleThread(GetNetCallBack callback)
		{
			mGetCallback = callback;
		}

		public void run()
		{
			WikiException exception = null;
			JSONObject object = null;
			try
			{
				object = getTitle();
			}
			catch (WikiException e)
			{
				exception = e;
			}

			mGetCallback.done(object, exception);
		}
	}
	
	public void getTitleInBackground(GetNetCallBack callback)
	{
		getTitleThread thread = new getTitleThread(callback);
		thread.start();
	}

	public JSONObject getCategory(String value) throws WikiException
	{
		String url = "query&prop=categories&format=json&rawcontinue=&titles="+value;
		return getWikiData(url);
	}

	private class getCategoryThread extends Thread
	{
		GetNetCallBack mGetCallback;
		String mQueryValue;

		getCategoryThread(String value, GetNetCallBack callback)
		{
			mGetCallback = callback;
			mQueryValue= value;
		}

		public void run()
		{
			WikiException exception = null;
			JSONObject object = null;
			try
			{
				object = getCategory(mQueryValue);
			}
			catch (WikiException e)
			{
				exception = e;
			}

			mGetCallback.done(object, exception);
		}
	}

	public void getCategoryInBackground(String value,GetNetCallBack callback)
	{
		getCategoryThread thread = new getCategoryThread(value,callback);
		thread.start();
	}

	public JSONObject getImage(String value) throws WikiException
	{
		String url = "query&prop=imageinfo&format=json&iiprop=url&rawcontinue=&generator=images&titles="+value;

		return getWikiData(url);
	}

	private class getImageThread extends Thread
	{
		GetNetCallBack mGetCallback;
		String mQueryValue;

		getImageThread(String value, GetNetCallBack callback)
		{
			mGetCallback = callback;
			mQueryValue= value;
		}

		public void run() {
			WikiException exception = null;
			JSONObject object = null;
			try
			{
				object = getImage(mQueryValue);
			}
			catch (WikiException e)
			{
				exception = e;
			}

			mGetCallback.done(object, exception);
		}
	}

	public void getIamgeInBackground(String value,GetNetCallBack callback)
	{
		getImageThread thread = new getImageThread (value,callback);
		thread.start();
	}

	public void loadData()
	{
		mSave = ParseSaveMgr.getInstance().getSaveFile(Save.FileName.Appdata);
		String saveStr = mSave.getString(Save.Appdata.BookTitle);
		if(saveStr != null)
		{
			String[] strArr =  saveStr.split(";");
			for(int i=0;i<strArr.length;i++)
			{
				mBookTitleList.add(strArr[i]);
			}
		}
	}

	public void saveBookMark(String value)
	{
		for(String str:mBookTitleList)
		{
			if(str.equals(value))
			{
				return;
			}
		}

		mBookTitleList.add(value);

		StringBuilder sb = new StringBuilder();
		for(String str:mBookTitleList)
		{
			sb.append(str).append(";");
		}
		mSave.setValue(Save.Appdata.BookTitle,sb.toString());
		mSave.save();
	}

	public void removeBookMark(String value)
	{
		boolean isfound = false;
		for(String str:mBookTitleList)
		{
			if(str.equals(value))
			{
				mBookTitleList.remove(str);
				isfound = true;
				break;
			}
		}

		if(isfound == false)
			return;
		StringBuilder sb = new StringBuilder();
		for(String str:mBookTitleList)
		{
			sb.append(str).append(";");
		}
		mSave.setValue(Save.Appdata.BookTitle,sb.toString());
		mSave.save();
	}

	public List<String> getBookTitle()
	{
		List<String> copy = new ArrayList<String>(mBookTitleList);
		return copy;
	}
}
