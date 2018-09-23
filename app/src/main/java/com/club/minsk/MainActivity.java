package com.club.minsk;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.club.minsk.db.Members;
import com.club.minsk.db.Messages;
import com.club.minsk.db.Owners;
import com.club.minsk.db.tables.PushTable;
import com.club.minsk.invite.InviteFragment;
import com.club.minsk.invites.InvitesFragment;
import com.club.minsk.menu.MenuAdapter;
import com.club.minsk.menu.MenuItemHolder;
import com.club.minsk.messages.MessagesFragment;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.Cookies;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class MainActivity extends AppCompatActivity
        implements
        EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    static MenuAdapter menuAdapter;
    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (Owners.self() == null) {
            Intent intent = new Intent(this, StartActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
            return;
        }

        if (App.getActiveActivity() != this) {

            App.getInstance().setActiveActivity(this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.empty, R.string.empty) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    AndroidUtils.hideKeyboard(App.getActiveActivity());
                    new Messages().chats(new Messages.ChatsListener() {
                        @Override
                        public void run(Messages.ChatsList response) {
                            App.chats = response.chats;
                            menuAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
            drawer.setDrawerListener(toggle);
            toggle.syncState();


            RecyclerView menuList = (RecyclerView) findViewById(R.id.menuList);
            menuList.setLayoutManager(new LinearLayoutManager(this));
            menuAdapter = new MenuAdapter();
            menuList.setAdapter(menuAdapter);
        }
        if (getIntent().getData() == null)
            openFragmentFromBundle(getIntent().getExtras());
        else
            openFragmentFromLink(getIntent().getData());
        if (server == null)
            try {
                server = new MyServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    private MyServer server;
    public class MyServer extends NanoHTTPD {
        private final static int PORT = 18846;

        public MyServer() throws IOException {
            super(PORT);
            start();
            System.out.println( "\nRunning! Point your browers to http://localhost:8080/ \n" );
        }

        @Override
        public Response serve(IHTTPSession session) {
            String msg = "<html><body><h1>Hello server</h1>\n";
            msg += "<p>We serve " + session.getUri() + " !</p>";

            Response response = Response.newFixedLengthResponse( msg + "</body></html>\n" );
            response.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT");
            response.addHeader("Access-Control-Allow-Origin",  "*");
            response.addHeader("Access-Control-Allow-Headers", "X-Requested-With");

            return response;
        }
    }

    public static void openFragmentFromLink(Uri link) {
        if (link != null) {
            switch (link.getPath()){
                case "/invite":
                    App.startFragment(new InviteFragment(), "event_id", AndroidUtils.toLong(link.getQueryParameter("id")));
                    break;
                default:
                    App.startFragment(new InvitesFragment());
                    menuAdapter.select(MenuItemHolder.invites);
            }
        } else {
            App.startFragment(new InvitesFragment());
            menuAdapter.select(MenuItemHolder.invites);
        }
    }

    void openFragmentFromBundle(Bundle data) {
        if (data != null) {
            if (data.get("notification_tag") != null)
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(data.getString("notification_tag"), 0);

            switch (data.getString(PushTable.PUSH_TYPE, "")) {
                case "" + PushTable.PUSH_TYPE_MESSAGE_INSERT:
                    if (data.getString("attach_type", "").equals("" + Members.ATTACH_TYPE_DIALOG)) {
                        App.startFragment(new MessagesFragment(),
                                "attach_type", AndroidUtils.toLong(data.getString("attach_type")),
                                "attach_id", AndroidUtils.toLong(data.getString("owner_id"))
                        );
                    } else {
                        App.startFragment(new MessagesFragment(),
                                "attach_type", AndroidUtils.toLong(data.getString("attach_type")),
                                "attach_id", AndroidUtils.toLong(data.getString("attach_id"))
                        );
                    }
                    break;
                case "" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE:
                case "" + PushTable.PUSH_TYPE_INVITE_CREATE:
                case "" + PushTable.PUSH_TYPE_INVITE_CANCELED:
                case "" + PushTable.PUSH_TYPE_INVITE_UPDATED:
                    App.startFragment(new InviteFragment(),
                            "event_id", AndroidUtils.toLong(data.getString("event_id")),
                            "action", data.getString("action", "")
                    );
                    break;
                case "" + PushTable.PUSH_TYPE_OWNER_LIKE:
                case "" + PushTable.PUSH_TYPE_MEMBER_INSERT:
                    App.startFragment(new OwnerFragment(),
                            "owner_id", AndroidUtils.toLong(data.getString("owner_id"))
                    );
                    break;
                default:
                    App.startFragment(new InvitesFragment());
                    menuAdapter.select(MenuItemHolder.invites);
            }
        } else {
            App.startFragment(new InvitesFragment());
            menuAdapter.select(MenuItemHolder.invites);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        openFragmentFromBundle(intent.getExtras());
    }

    public static boolean isShow = false;

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isShow = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
        if (App.getActiveFragment() instanceof InvitesFragment)
            if (App.rq_link != null){
                MainActivity.openFragmentFromLink(Uri.parse(App.rq_link));
                App.rq_link = null;
            }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            finish();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                drawer.openDrawer(GravityCompat.START);
            } else
                super.onBackPressed();
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        if (App.getActiveFragment() instanceof MessagesFragment)
            ((MessagesFragment) App.getActiveFragment()).onEmojiconClicked(emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        if (App.getActiveFragment() instanceof MessagesFragment)
            ((MessagesFragment) App.getActiveFragment()).onEmojiconBackspaceClicked(v);
    }

}
