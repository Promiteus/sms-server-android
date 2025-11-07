package com.romanm.smsserver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.client_locker.BlockedClients;
import com.romanm.smsserver.list_adapter.CustomListAdapter;

import java.util.ArrayList;
import java.util.List;

public class IPList extends AppCompatActivity {
    private ListView blockedIpList = null;
    private List<BlockedClients> blockedClientsList = null;
    private CustomListAdapter customListAdapter = null;
    private ArrayAdapter<String> arrayAdapter = null;
    private String selectedIP = null;




    private void updateBlockList(ListView view) {
        Intent i = getIntent();
        blockedClientsList =  i.getParcelableArrayListExtra("intentBlockList");
        String lockStr = getResources().getString(R.string.item_no_locks);

        if (view != null) {
            if (blockedClientsList != null && blockedClientsList.size() == 0) {
                String[] empty = new String[]{lockStr};
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item_empty, empty);
                view.setAdapter(arrayAdapter);
            } else if (blockedClientsList != null && blockedClientsList.size() > 0) {
                if (customListAdapter == null) {
                    customListAdapter = new CustomListAdapter(getApplicationContext(), blockedClientsList);
                }
                view.setAdapter(customListAdapter);
            } else if (blockedClientsList == null) {
                String[] empty = new String[]{lockStr};
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item_empty, empty);
                view.setAdapter(arrayAdapter);
            }
        }
    }


    private void removeFromBlackList(List<BlockedClients> clientsList, String ipAddress) {
       if (clientsList != null && ipAddress != null) {
           for (BlockedClients client : clientsList) {

               if (client.getIpAddress().equals(ipAddress)) {
                 if (clientsList.contains(client)) {
                     clientsList.remove(client);
                 }
                 /**Отправить уведомление в основное окна для передачи на TcpServer*/
                 notifyAuthListener(clientsList);
                 selectedIP = null;
                 /**Обновить список заблокированных пользователей*/
                 updateBlockList(blockedIpList);

                 String infoUnlock = String.format(getResources().getString(R.string.item_selected_ip_deleted), ipAddress);
                 Toast.makeText(getApplicationContext(), infoUnlock, Toast.LENGTH_SHORT).show();
                 break;
             }
           }
       }
    }



    private void notifyAuthListener(List<BlockedClients> blockedClientsList) {
        if (blockedClientsList != null) {

            Intent intentBlockList = new Intent();
            intentBlockList.setAction(Constants.BLOCK_LIST_MAIN_ACT_MSG);

            intentBlockList.putExtra("ipAddress", selectedIP);
            intentBlockList.putParcelableArrayListExtra("intentBlockList",
                    (ArrayList<? extends Parcelable>) blockedClientsList);

            sendBroadcast(intentBlockList);
        } else {
            Intent intentBlockList = new Intent();
            intentBlockList.setAction(Constants.BLOCK_LIST_MAIN_ACT_MSG);

            sendBroadcast(intentBlockList);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.block_list_setts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       // menuItem = item;
        switch(id){
            case R.id.del_item :
                /**Удалить IP-адрес из списка блокировок*/
                showDelConfirmDialog(selectedIP);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_block_list);

        blockedIpList = (ListView)findViewById(R.id.blockIpList);

        blockedIpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (blockedClientsList != null && blockedClientsList.size() > 0) {
                    TextView ipText = view.findViewById(R.id.textView_ip);
                    selectedIP = ipText.getText().toString();
                    Toast.makeText(getApplicationContext(), ipText.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateBlockList(blockedIpList);
    }



    public IPList() {}

    public void showDelConfirmDialog(String ip) {
        if (ip == null || ip.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_confirm_red_24dp)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(String.format(getResources().getString(R.string.dialog_delete_action), ip))
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  finish();
                        removeFromBlackList(blockedClientsList, selectedIP);
                    }

                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }
}
