package com.maxieds.chameleonminilivedebugger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class TabFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int TAB_LOG = 0;
    public static final int TAB_TOOLS = 1;
    public static final int TAB_EXPORT = 2;
    public static final int TAB_SEARCH = 3;

    private int tabNumber;
    private static int layoutResRef;

    public static TabFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        switch(page) {
            case TAB_LOG:
                layoutResRef = R.layout.logging_tab;
                break;
            case TAB_TOOLS:
                layoutResRef = R.layout.tools_menu_tab;
                break;
            case TAB_EXPORT:
                layoutResRef = R.layout.export_tab;
                break;
            case TAB_SEARCH:
                layoutResRef = R.layout.search_tab;
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layoutResRef, container, false);
        return view;
    }


}
