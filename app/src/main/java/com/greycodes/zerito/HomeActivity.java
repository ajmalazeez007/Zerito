package com.greycodes.zerito;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.greycodes.zerito.app.AppController;
import com.greycodes.zerito.helper.FriendAcceptService;
import com.greycodes.zerito.helper.FriendRequestService;
import com.greycodes.zerito.helper.HistoryService;
import com.greycodes.zerito.service.CheckUserService;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends ActionBarActivity {
   public static ListView listView;

    private static final int CONTACT_PICKER_RESULT = 1001;
    static AddFriendPopUp addFriendPopUp;

    @Override
    protected void onResume() {
        super.onResume();
        listView.setAdapter(AppController.myFriendsAdapter);
    }

    private static final String DEBUG_TAG = "Contact List";
    private final int RESULT_OK = -1;
    private static final int PICK_CONTACT_REQUEST = 1;
    private static final int PICK_CONTACT = 0;
    String selectedNumber;
    public static FragmentManager fragmentManager;
    public static ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {
            fragmentManager = getSupportFragmentManager();
            listView = (ListView) findViewById(R.id.home_listview);
            sharedPreferences= getSharedPreferences("zerito", Context.MODE_PRIVATE);

            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Fetching Data");
            progressDialog.setMessage("Please wait...");

            listView.setAdapter(AppController.myFriendsAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppController.selectedmob = AppController.mobnum[position];
                    AppController.selectedname=AppController.name[position];
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                }
            });


         fragmentManager = getSupportFragmentManager();
        listView = (ListView) findViewById(R.id.home_listview);


        FloatingActionMenu menu1 = (FloatingActionMenu) findViewById(R.id.menu1);
        menu1.setClosedOnTouchOutside(true);

        FloatingActionButton addfab = (FloatingActionButton) findViewById(R.id.menu_item1);
        FloatingActionButton pending_fab = (FloatingActionButton) findViewById(R.id.menu_item2);
        FloatingActionButton history_fab = (FloatingActionButton) findViewById(R.id.menu_item3);

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startActivity(new Intent(HomeActivity.this, NewFriendActivity.class));
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CONTACT_PICKER_RESULT);
                }
            }
        });

         pending_fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 startService(new Intent(HomeActivity.this, FriendRequestService.class));




             }
         });
        history_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(HomeActivity.this, HistoryService.class));

            }
        });


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please wait...");

        listView.setAdapter(AppController.myFriendsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppController.selectedmob = AppController.mobnum[position];
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });
        /* add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //    startActivity(new Intent(HomeActivity.this, NewFriendActivity.class));
               Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CONTACT_PICKER_RESULT);
                }
            }
        });
*/

            registerForContextMenu(listView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, "Delete");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String phoneNumber = "";
                    List<String> allNumbers = new ArrayList<String>();
                    int phoneIdx = 0;
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
                        phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        if (cursor.moveToFirst()) {
                            while (cursor.isAfterLast() == false) {
                                phoneNumber = cursor.getString(phoneIdx);
                                allNumbers.add(phoneNumber);
                                cursor.moveToNext();
                            }
                        } else {
                            //no results actions
                        }
                    } catch (Exception e) {
                        //error actions
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                        final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Choose a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                 selectedNumber = items[item].toString();

                                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                                try {
                                    Phonenumber.PhoneNumber phoneNumber1 = phoneUtil.parse(selectedNumber, sharedPreferences.getString("cc",""));
                                    String selected = phoneUtil.format(phoneNumber1, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                                   Intent intent= new Intent(HomeActivity.this, CheckUserService.class);
                                    intent.putExtra("mobile",selected);
                                    startService(intent);
                                    progressDialog.show();
                                } catch (NumberParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"Select a valid number",Toast.LENGTH_LONG).show();

                                }



                            }
                        });
                        AlertDialog alert = builder.create();
                        if(allNumbers.size() > 1) {
                            alert.show();
                        } else {

                            selectedNumber= phoneNumber.toString();
                            selectedNumber = selectedNumber.replace("-", "");
                            selectedNumber = selectedNumber.replace(" ", "");
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            try {
                                Phonenumber.PhoneNumber phoneNumber1 = phoneUtil.parse(selectedNumber,sharedPreferences.getString("cc",""));
                                String selected = phoneUtil.format(phoneNumber1, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                                progressDialog.show();
                                Intent intent= new Intent(HomeActivity.this, CheckUserService.class);
                                intent.putExtra("mobile",selected);
                                startService(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Select a valid number",Toast.LENGTH_LONG).show();

                            }


                        }

                        if (phoneNumber.length() == 0) {
                            //no numbers found actions
                        }
                    }

                    //PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                  //  PhoneNumber phNumberProto = null;


                    break;
            }
        } else {
            //activity result error actions
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.menu_fr:
                startService(new Intent(HomeActivity.this, FriendRequestService.class));
                return true;
            case R.id.menu_history:
                startService(new Intent(HomeActivity.this, HistoryService.class));
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //  info.position will give the index of selected item
        if (item.getTitle() == "Delete") {
            Intent intent = new Intent(HomeActivity.this, FriendAcceptService.class);
            intent.putExtra("mob2",AppController.mobnum[info.position]);
            intent.putExtra("type",3);
            startService(intent);
            // Code to execute when clicked on This Item
        }
return  true;
    }

    /*void popup(String number){
        AddFriendPopUp addFriendPopUp = new AddFriendPopUp();
        addFriendPopUp.show(getSupportFragmentManager().beginTransaction(),"addfriend");
    } */
    public  static void setpopup(){
        try {
            addFriendPopUp = new AddFriendPopUp();
            addFriendPopUp.show(fragmentManager,"addfriend");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public  static void removepopup(){
        try {
            addFriendPopUp.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static void updateListView(){
        try {
            listView.setAdapter(AppController.myFriendsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
