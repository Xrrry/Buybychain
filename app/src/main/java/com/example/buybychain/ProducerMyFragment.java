package com.example.buybychain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leon.lib.settingview.LSettingItem;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProducerMyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProducerMyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProducerMyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView signout;
    private Handler mHandler = new Handler();

    private OnFragmentInteractionListener mListener;

    public ProducerMyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProducerMyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProducerMyFragment newInstance(String param1, String param2) {
        ProducerMyFragment fragment = new ProducerMyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_producer_my, container, false);
        LSettingItem bt1 = view.findViewById(R.id.pro1);
        LSettingItem bt2 = view.findViewById(R.id.pro2);
        LSettingItem bt3 = view.findViewById(R.id.pro3);
        LSettingItem bt4 = view.findViewById(R.id.pro4);
        LSettingItem bt5 = view.findViewById(R.id.pro5);
        TextView tv2 = (TextView) bt3.findViewById(R.id.tv_lefttext);
        TextView tv = view.findViewById(R.id.shenfen);
        RelativeLayout rl = view.findViewById(R.id.rela);
        final TextView name = view.findViewById(R.id.name);
        Buybychain application = (Buybychain) getActivity().getApplication();
        bt1.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click() {
                Intent intent = new Intent(getActivity(), Modification.class);
                startActivity(intent);
            }
        });
        bt2.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click() {
                Intent intent = new Intent(getActivity(), UpPreCom.class);
                startActivity(intent);
            }
        });
        bt4.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click() {
                Intent intent = new Intent(getActivity(), Feedback.class);
                startActivity(intent);
            }
        });
        bt5.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click() {
                Intent intent = new Intent(getActivity(), About.class);
                startActivity(intent);
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("login", getActivity().getApplicationContext().MODE_PRIVATE);
        final String n = sp.getString("name","用户");
        System.out.println(n);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                name.setText(n);
            }
        });
        if(Integer.valueOf(application.getType())==3) {
            tv.setText("卖家");
            rl.setBackgroundColor(Color.parseColor("#262322"));
            LinearLayout ll = view.findViewById(R.id.lin);
            ll.removeView(bt2);
            tv2.setText("卖出记录");
            bt3.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
                @Override
                public void click() {
                    Intent intent = new Intent(getActivity(), SellHistory.class);
                    startActivity(intent);
                }
            });
        }
        else {
            bt3.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
                @Override
                public void click() {
                    Intent intent = new Intent(getActivity(), OutHistory.class);
                    startActivity(intent);
                }
            });
        }
        signout = view.findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("提示");
                builder.setMessage("确认退出吗？");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sp = getActivity().getSharedPreferences("login", getActivity().getApplicationContext().MODE_PRIVATE);
                        sp.edit()
                                .remove("phone")
                                .remove("name")
                                .remove("type")
                                .apply();
                        Intent i1 = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i1);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
