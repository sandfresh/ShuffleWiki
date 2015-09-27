package gikolab.com.shufflewiki.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gikolab.com.shufflewiki.R;
import gikolab.com.shufflewiki.activity.DetailActivity;
import gikolab.com.shufflewiki.retrive.Wiki;

/**
 * Created by stalin on 2015/9/26.
 */
public class BookMarkFragment extends Fragment
{
  private ListView mListView;
  private Handler mUpdateHandler;
  private List<String> mDataList;
  private CustomAdapter mListAdapter;


  private View mView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mView =  inflater.inflate(R.layout.fragment_bookmark,container, false);
    Log.e("Fragement","CreatView : List");
    return mView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null)
    {
      Log.e("Fragement","SaveInstacne Not Null");

      FragmentManager fragmentManager = getFragmentManager();
      FragmentTransaction transaction = fragmentManager
              .beginTransaction();
      transaction.show(BookMarkFragment.this);
      transaction.commit();
    }
    else
    {
      mDataList = new ArrayList<String>();
      mListView = (ListView) mView.findViewById(R.id.listview2);
      mListAdapter = new CustomAdapter(this.getActivity(), R.id.listTextView2, mDataList);
      mListView.setAdapter(mListAdapter);

      mListView.setClickable(true);
      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
          String object = arg0.getItemAtPosition(position).toString();
          Intent intent = new Intent(getActivity(), DetailActivity.class);
          intent.putExtra("name", object);
          startActivity(intent);
        }
      });
      mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          String MsgString = (String) msg.obj;
          if (MsgString.equals("UpdateListView"))
            mListAdapter.notifyDataSetChanged();
            mListAdapter.getCount();
        }
      };

      Wiki.getInstance().registerUpdateListener(new Wiki.IupdateListener() {
        @Override
        public void onAddData(String value)
        {
          String obj = "UpdateListView";
          mDataList.add(value);
          Message message = mUpdateHandler.obtainMessage(1, obj);
          mUpdateHandler.sendMessage(message);
        }

        @Override
        public void onRemove(String value)
        {
          String obj = "UpdateListView";
          mDataList.remove(value);
          Message message = mUpdateHandler.obtainMessage(1, obj);
          mUpdateHandler.sendMessage(message);
        }
      });

    }
    Log.e("Fragement", "ActivityCreated : BookMark");
    initialView();
  }


  public void initialView()
  {
    List<String> list = Wiki.getInstance().getBookTitle();
    for(String str:list)
    {
      mDataList.add(str);
    }

    String obj = "UpdateListView";
    Message message = mUpdateHandler.obtainMessage(1, obj);
    mUpdateHandler.sendMessage(message);
  }

  public class CustomAdapter extends ArrayAdapter<String> {

    private List<String> mList;
    private LayoutInflater mInflater;

    public CustomAdapter(Context context, int resource, List<String> objects) {
      super(context, resource, objects);
      mInflater = LayoutInflater.from(context);
      mList = objects;
    }

    @Override
    public int getCount() {
      return mList.size();
    }

    @Override
    public String getItem(int position) {
      return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      //return null;
      View view;
      ViewHolder holder = null;
      if (convertView == null)
      {
        view = mInflater.inflate(R.layout.listview_bookmark, parent, false);
        holder = new ViewHolder();
        holder.listText = (TextView) view.findViewById(R.id.listTextView2);
        holder.listBtn = (Button) view.findViewById(R.id.listButton2);
        view.setTag(holder);
      }
      else
      {
        view = convertView;
        holder = (ViewHolder) convertView.getTag();
      }


      if (mList != null) {
        final String listStr = mList.get(position);
        holder.listText.setText(listStr);
        holder.listBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v)
          {
            Wiki.getInstance().removeBookMark(listStr);
          }
        });
      }

      return view;
    }

    private class ViewHolder {
      public Button listBtn;
      public TextView listText;
    }


  }
}
