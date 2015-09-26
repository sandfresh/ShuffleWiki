package gikolab.com.shufflewiki.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.ParseException;

import gikolab.com.shufflewiki.R;
import gikolab.com.shufflewiki.parse.ParseSaveMgr;
import gikolab.com.shufflewiki.retrive.Wiki;

public class SplashActivity extends Activity
{

    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);

        loadData();
    }

    private void loadData()
    {
        mProgressbar.setVisibility(View.VISIBLE);
        ParseSaveMgr.getInstance().loadFromParse(new ParseSaveMgr.ParseSaveCallBack()
        {
            @Override
            public void onLoadComplete()
            {
                mProgressbar.setVisibility(View.GONE);
                Wiki.getInstance().loadData();
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onError(ParseException error)
            {
                mProgressbar.setVisibility(View.GONE);
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setTitle("");
                dialog.setMessage("NetWork Error");
                dialog.setPositiveButton(R.string.Retry_Label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i)
                            {
                                loadData();
                            }
                        });
                dialog.setNegativeButton(R.string.Exit_Label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                System.exit(0);
                            }
                        });
                dialog.show();
            }
        });
    }
}
