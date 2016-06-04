package abrizzz.monews.viewcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import abrizzz.monews.R;
import abrizzz.monews.model.NewsItem;
import abrizzz.monews.model.NewsItems;
import abrizzz.monews.utils.ParserDefi;
import abrizzz.monews.utils.ParserIon;
import abrizzz.monews.utils.ParserLexpress;
import abrizzz.monews.viewcontroller.viewmodel.NewsArrayAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayAdapter<NewsItem> newsAdapter;
    private ListView listView;
    private NewsItems singleton;

    public boolean lexpressDone;
    public boolean defiDone;
    public boolean ionDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set Nav Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        singleton = NewsItems.getSingletonInstance();

        //Set Pull to refresh colors
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#F44336"), Color.parseColor("#2196F3"), Color.parseColor("#FFC107"),Color.parseColor("#4CAF50"));
        swipeRefreshLayout.measure(1,1); //workaround to make animation show at when loading first time
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                singleton.clearDefiItems();
                singleton.clearLexpressItems();
                singleton.clearIonItems();
                newsAdapter.notifyDataSetInvalidated();
                getNewsItems();
            }
        });

        // Get news articles async
        getNewsItems();

        //Set list adapter
        listView = (ListView) findViewById(R.id.list_view);
        newsAdapter = new NewsArrayAdapter(this);
        listView.setAdapter(newsAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.nav_about:
                displayAbout();
                break;
            case R.id.nav_reddit:
                openReddit();
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateList()
    {
        if(lexpressDone && defiDone && ionDone) {
            NewsArrayAdapter n = (NewsArrayAdapter) newsAdapter;
            n.updateAllList();
            newsAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void getNewsItems()
    {
        lexpressDone = false;
        ParserLexpress pe = new ParserLexpress(this);
        pe.execute();

        defiDone = false;
        ParserDefi pd = new ParserDefi(this);
        pd.execute();

        ionDone = false;
        ParserIon pi = new ParserIon(this);
        pi.execute();
    }

    // Displays a dialog info about the app
    public void displayAbout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SpannableString msg = new SpannableString(getText(R.string.app_info));
        Linkify.addLinks(msg,Linkify.WEB_URLS);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(msg);
        AlertDialog infoDialog = builder.create();
        infoDialog.show();
    }

    public void openReddit()
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://www.reddit.com/r/Mauritius"));
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsAdapter.notifyDataSetChanged();
    }
}
