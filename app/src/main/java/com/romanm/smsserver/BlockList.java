package com.romanm.smsserver;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.romanm.smsserver.client_locker.BlockedClients;
import com.romanm.smsserver.list_adapter.CustomListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlockList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlockList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlockList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView blockedIpList;
    private List<BlockedClients> blockedClientsList;
    private CustomListAdapter customListAdapter = null;
    private ArrayAdapter<String> arrayAdapter = null;

    private OnFragmentInteractionListener mListener;

    public BlockList() {

    }

    public BlockList(List<BlockedClients> blockedClientsList) {
        this.blockedClientsList = blockedClientsList;
      //  this.setTag();
    }

    // TODO: Rename and change types and number of parameters
    public static BlockList newInstance() {
        BlockList fragment = new BlockList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_block_list, container, false);

        blockedIpList = view.findViewById(R.id.blockIpList);

        if (blockedClientsList != null && blockedClientsList.size() == 0) {
            String[] empty = new String[]{"Нет блокировок"};
            arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, empty);
            blockedIpList.setAdapter(arrayAdapter);
        } else if (blockedClientsList != null && blockedClientsList.size() > 0) {
            if (customListAdapter == null) {
                customListAdapter = new CustomListAdapter(view.getContext(), blockedClientsList);
            }
            blockedIpList.setAdapter(customListAdapter);
        } else if (blockedClientsList == null) {
            String[] empty = new String[]{"Нет блокировок"};
            arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, empty);
            blockedIpList.setAdapter(arrayAdapter);
        }
        return view;
    }

    public void updateBlockList(List<BlockedClients> aBlockedClientsList) {
      this.blockedClientsList = aBlockedClientsList;
      if (customListAdapter != null) {
          customListAdapter.notifyDataSetChanged();
      }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
