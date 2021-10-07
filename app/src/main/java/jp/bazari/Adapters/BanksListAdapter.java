package jp.bazari.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import jp.bazari.R;

import java.util.ArrayList;

public class BanksListAdapter extends BaseAdapter {

    ArrayList<String> list;
    private static final int LIST_ITEM = 0;
    private static final int HEADER = 1;
    private LayoutInflater inflater;


    public BanksListAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).equals("主要金融機関") || list.get(position).equals("50音順")) {
            return HEADER;
        } else {
            return LIST_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            switch (getItemViewType(position)) {
                case LIST_ITEM:
                    convertView = inflater.inflate(R.layout.item_list_simple, null);
                    break;

                case HEADER:
                    convertView = inflater.inflate(R.layout.item_list_header, null);

                    break;
            }
        }
        switch (getItemViewType(position)) {
            case LIST_ITEM:
                TextView name = (TextView) convertView.findViewById(R.id.itemTv);
                name.setText(((String)list.get(position)));
                break;

            case HEADER:
                TextView title = (TextView) convertView.findViewById(R.id.itemListViewHeader);
                title.setText(((String) list.get(position)));
                break;
        }
        return convertView;
    }
}
