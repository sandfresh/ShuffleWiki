package gikolab.com.shufflewiki.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import gikolab.com.shufflewiki.R;
import gikolab.com.shufflewiki.fragment.BookMarkFragment;
import gikolab.com.shufflewiki.fragment.ListFragment;

public class MainActivity extends ActionBarActivity
{
    ListFragment mListFragment;
    BookMarkFragment mBookMark;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        mListFragment = new ListFragment();
        mBookMark = new BookMarkFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        transaction.add(R.id.fragementholder,mListFragment,"list");
        transaction.add(R.id.fragementholder,mBookMark,"book");
        transaction.hide(mBookMark);
        transaction.commit();


    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.memubookmark:
                showPage(Page.BookMark);
                return true;
            case R.id.menupage:
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
                transaction.show(mListFragment);
                transaction.hide(mBookMark);
                break;
            case BookMark:
                transaction.show(mBookMark);
                transaction.hide(mListFragment);
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



