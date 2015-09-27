package gikolab.com.shufflewiki.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gikolab.com.shufflewiki.R;
import gikolab.com.shufflewiki.retrive.GetNetCallBack;
import gikolab.com.shufflewiki.retrive.Wiki;
import gikolab.com.shufflewiki.retrive.WikiException;
import gikolab.com.shufflewiki.value.ImageGet;

public class DetailActivity extends ActionBarActivity
{
  private TextView mCategoryText;
  List<AsyncTask> mAsynsList;
  String mTitleStr;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    initToolbar();
    TextView titleText = (TextView) findViewById(R.id.detailTitle);
    mCategoryText = (TextView) findViewById(R.id.detailText);

    Button add = (Button) findViewById(R.id.bookmark);
    Button remove = (Button) findViewById(R.id.remove);
    add.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Wiki.getInstance().addBookMark(mTitleStr);
      }
    });
    remove.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Wiki.getInstance().removeBookMark(mTitleStr);
      }
    });

    Bundle bundle = getIntent().getExtras();
    mTitleStr = bundle.getString("name");
    titleText.setText("Title:"+mTitleStr);
    updateCategory(mTitleStr);
    updateImage(mTitleStr);

    mAsynsList = new ArrayList<>();
  }

  @Override
  protected void onStop()
  {
    super.onStop();
    for(AsyncTask task:mAsynsList)
    {
      task.cancel(true);
    }
  }

  private void initToolbar()
  {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
    setSupportActionBar(toolbar);
  }

  public void updateCategory(String value)
  {

    Wiki.getInstance().getCategoryInBackground(value, new GetNetCallBack() {
      @Override
      public void done(JSONObject object, WikiException e) {
        Log.i("Activity", "Object:" + object.toString());
        final ArrayList<String> list = new ArrayList<String>();
        if (object != null) {
          try {
            JSONObject pagesObj = object.getJSONObject("query").getJSONObject("pages");
            for (int i = 0; i < pagesObj.length(); i++) {
              JSONObject pageobj = pagesObj.getJSONObject(pagesObj.names().getString(i));
              JSONArray cateArray = pageobj.getJSONArray("categories");
              for (int j = 0; j < cateArray.length(); j++) {
                String value = cateArray.getJSONObject(j).getString("title");
                String[] strPair = value.split(":");
                list.add(strPair[1]);
              }

              DetailActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                  StringBuilder sb = new StringBuilder();
                  sb.append("Category:");
                  for (String str : list) {
                    sb.append(str).append(";");
                  }
                  mCategoryText.setText(sb.toString());
                }
              });
            }

          } catch (JSONException e2) {
            e2.printStackTrace();
          }
        }
      }
    });
  }

  public void updateImage(final String value)
  {
    Wiki.getInstance().getIamgeInBackground(value, new GetNetCallBack() {
      @Override
      public void done(JSONObject object, WikiException e) {
        Log.i("Activity", "Key:" + value);
        Log.i("Activity", "ImageJson:" + object.toString());

        if (object != null && object.length() > 0) {
          final ArrayList<String> list = new ArrayList<String>();
          try {
            JSONObject pagesObj = object.getJSONObject("query").getJSONObject("pages");
            for (int i = 0; i < pagesObj.length(); i++) {
              JSONObject pageobj = pagesObj.getJSONObject(pagesObj.names().getString(i));
              JSONArray imageInfoArray = pageobj.getJSONArray("imageinfo");
              for (int j = 0; j < imageInfoArray.length(); j++) {
                String value = imageInfoArray.getJSONObject(j).getString("url");
                value = value.replace("image:", "");
                list.add(value);
              }
            }
            boolean isFoundImage = false;
            for (String str : list) {
              if (str.contains("jpg") || str.contains("png")) {
                String out = StringEscapeUtils.unescapeJava(str);
                downloadImage(out);
                isFoundImage = true;
              }
            }

            if (isFoundImage == false) {
              DetailActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                  ImageView image = new ImageView(DetailActivity.this);
                  LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.imageScrollLayout);
                  linearLayout1.addView(image);
                  image.setImageResource(R.drawable.noimage);
                }
              });
            }
          } catch (JSONException e2) {
            e2.printStackTrace();
          }
        } else {
          DetailActivity.this.runOnUiThread(new Runnable() {
            public void run() {
              ImageView image = new ImageView(DetailActivity.this);
              LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.imageScrollLayout);
              linearLayout1.addView(image);
              image.setImageResource(R.drawable.noimage);
            }
          });
        }
      }
    });
  }

  private void downloadImage(String urlStr)
  {
    // 建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑

    AsyncTask<String,Void,Bitmap> task=new AsyncTask<String, Void, Bitmap>()
    {
      @Override
      protected Bitmap doInBackground(String... params)
      {
        String url = params[0];
        return (new ImageGet(url).get());
      }

      @Override
      protected void onPostExecute(Bitmap result)
      {
        ImageView image = new ImageView(DetailActivity.this);
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.imageScrollLayout);
        linearLayout1.addView(image);
        image.setImageBitmap(result);
        super.onPostExecute(result);
      }
    }.execute(urlStr);

    mAsynsList.add(task);
  }



}
