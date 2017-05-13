package cn.scooper.com.whiteboard.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scooper.cn.whiteboard.R;

import java.util.List;

import cn.scooper.com.whiteboard.db.domain.UserBean;

/**
 * Created by Aguai on 2016/11/20.
 * 会议成员适配器
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private List<UserBean> userBeens;
    private Context mContext;

    public MemberAdapter(Context context, List<UserBean> list,
                         RecyclerView target) {
        super();
        mContext = context;
        userBeens = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meetinguser, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserBean userBean = userBeens.get(position);
        holder.name.setText(userBean.getName());
        if (position == 0) {
            holder.ivIcon.setImageResource(R.drawable.icon_zhuchiren);
        } else {
            holder.ivIcon.setImageResource(R.drawable.icon_yonghu);
        }
    }

    @Override
    public int getItemCount() {
        return userBeens.size();
    }

    public void setUserList(List<UserBean> users) {
        this.userBeens = users;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_userName);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_ic);
        }
    }

}