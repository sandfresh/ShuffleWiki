package gikolab.com.shufflewiki.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import gikolab.com.shufflewiki.R;
import gikolab.com.shufflewiki.fragment.BookMarkFragment;
import gikolab.com.shufflewiki.fragment.ListFragment;

public class MainActivity extends ActionBarActivity {


    ListFragment mListFragment;
    BookMarkFragment mBookMark;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();

        mListFragment = new ListFragment();
        mBookMark = new BookMarkFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        transaction.add(mListFragment,"list");
        transaction.add(mBookMark,"book");
        transaction.commit();


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        ViewGroup v = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.actionbarl_main, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(v,
                new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | Gravity.RIGHT));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.book:
                showPage(Page.BookMark);
                return true;
            case R.id.page:
                showPage(Page.List);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPage(Page value)
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (value)
        {
            case List:
                transaction.show(mBookMark);
                transaction.hide(mListFragment);
                break;
            case BookMark:
                transaction.show(mListFragment);
                transaction.hide(mBookMark);

                break;
        }
        transaction.commit();
    }

    enum Page
    {
        List,
        BookMark
    }

}



