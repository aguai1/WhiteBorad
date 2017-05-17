package cn.scooper.com.whiteboard.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.scooper.cn.whiteboard.R;

import java.util.List;

import cn.scooper.com.whiteboard.db.domain.UserBean;

/**
 * Created by Aguai on 2016/11/20.
 * 会议成员适配器
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<EMMessage> userBeens;
    private Context mContext;

    public ChatAdapter(Context context, List<EMMessage> list,
                       RecyclerView target) {
        super();
        mContext = context;
        userBeens = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EMMessage userBean = userBeens.get(position);
        holder.name.setText(userBean.getFrom());
        holder.content.setText(userBean.getBody().toString());
    }

    @Override
    public int getItemCount() {
        return userBeens.size();
    }

    public void setUserList(List<EMMessage> users) {
        this.userBeens = users;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

}