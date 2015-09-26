package gikolab.com.shufflewiki.retrive;

import org.json.JSONObject;

/**
 * Created by stalin on 2015/9/20.
 */
public abstract class GetNetCallBack
{
    public abstract void done(JSONObject object, WikiException e);
}
