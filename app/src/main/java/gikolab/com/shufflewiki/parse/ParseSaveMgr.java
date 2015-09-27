package gikolab.com.shufflewiki.parse;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Map;

import gikolab.com.shufflewiki.application.CustomApplication;
import gikolab.com.shufflewiki.value.Save;


/**
 * Created by stalin on 2015/9/26.
 */
public class ParseSaveMgr
{
  static ParseSaveMgr mInstance;

  static public ParseSaveMgr getInstance()
  {
    if (mInstance == null)
    {
      mInstance = new ParseSaveMgr();
    }
    return mInstance;
  }

  String mID;
  ParseObject mObject;
  Map<String, SaveFile> mSaveMap;

  private ParseSaveMgr()
  {
    mSaveMap = new HashMap<String, SaveFile>();
  }

  public void loadFromParse(final String filename, final ParseSaveCallBack callBack)
  {
    ParseQuery<ParseObject> query = ParseQuery.getQuery(filename);
    query.getInBackground(Save.Appdata.Account, new GetCallback<ParseObject>()
    {
      public void done(ParseObject object, ParseException e)
      {
        if (object == null)
        {
          if (e.getCode() == 101)
          {
            mObject = new ParseObject(filename);
            mObject.put(Save.Appdata.Account, mID);
            callBack.onLoadComplete();
          }
          else
          {
            callBack.onError(e);
          }
        }
        else
        {
          mObject = object;
          callBack.onLoadComplete();
        }
      }
    });
  }

  public void loadFromParse(final ParseSaveCallBack callBack)
  {
    mID = CustomApplication.getAccount();
    ParseQuery<ParseObject> query = ParseQuery.getQuery(Save.FileName.Appdata);
    query.whereEqualTo(Save.Appdata.Account, mID);
    query.getFirstInBackground(new GetCallback<ParseObject>() {
      public void done(ParseObject object, ParseException e)
      {
        if (object == null) {
          if (e.getCode() == 101) {
            mObject = new ParseObject(Save.FileName.Appdata);
            mObject.put(Save.Appdata.Account, mID);
            mSaveMap.put(Save.FileName.Appdata, new SaveFile(mObject));
            callBack.onLoadComplete();
          } else {
            callBack.onError(e);
          }
        } else {
          mObject = object;
          mSaveMap.put(Save.FileName.Appdata, new SaveFile(mObject));
          mObject.saveInBackground();
          callBack.onLoadComplete();
        }
      }
    });
  }

  public SaveFile getSaveFile(String key)
  {
    return mSaveMap.get(key);
  }


  public interface ParseSaveCallBack
  {
    public void onLoadComplete();

    public void onError(ParseException error);
  }
}
