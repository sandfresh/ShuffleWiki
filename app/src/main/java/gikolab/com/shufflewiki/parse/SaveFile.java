package gikolab.com.shufflewiki.parse;

import com.parse.ParseObject;

/**
 * Created by stalin on 2015/9/26.
 */
public class SaveFile
{
    ParseObject mObject;

    SaveFile(String name)
    {
        mObject = new ParseObject(name);
    }

    SaveFile(ParseObject object)
    {
        mObject = object;
    }

    public String getString(String key)
    {
        return mObject.getString(key);
    }

    public void setValue(String key,Object value)
    {
        mObject.put(key,value);
    }

    public void save()
    {
        mObject.saveInBackground();
    }
}
