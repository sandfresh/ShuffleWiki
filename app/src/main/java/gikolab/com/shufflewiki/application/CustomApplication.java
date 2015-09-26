package gikolab.com.shufflewiki.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import com.parse.Parse;

/**
 * Created by stalin on 2015/9/24.
 */
public class CustomApplication extends Application
{

    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "aI73fqkTW2L1ti6jNbKfzjwOTDmqurfuUOuZ367t",
                "omffQvlFITXRImSxL6WLB6fdOamfCgH359HaCCcx");
        mContext= getApplicationContext();
    }


    public static Context getAppContext()
    {
        return CustomApplication.mContext;
    }

    public static String getAccount()
    {
        Context context = CustomApplication.getAppContext();
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType("com.google");

        if(accounts.length > 0)
        {
            return accounts[0].name;
        }
        return "Anonymous";
    }
}
