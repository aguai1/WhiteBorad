package cn.scooper.com.whiteboard.adpter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scooper.cn.whiteboard.R;

import java.util.List;

import cn.scooper.com.easylib.views.dialog.AlertDialog;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.AbsShape;

/**
 * Created by Aguai on 2016/11/20.
 * 白板列表适配器
 */

public class BlackBoardAdapter extends RecyclerView.Adapter<BlackBoardAdapter.ViewHolder> {

    private List<List<AbsShape>> listList;
    private Activity mContext;
    private OnItemClickListener onClickListener;

    public BlackBoardAdapter(Activity context, List<List<AbsShape>> list,
                             RecyclerView target) {
        super();
        mContext = context;
        listList = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_whiteboard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.displayInfoView.addShapesAndShowResult(listList.get(position));
        if (listList.get(position).size()>0){
            holder.pageId.setText("第" + (Integer.parseInt(listList.get(position).get(0).getMeetingPage()) + 1) + "页");
        }
    }

    @Override
    public int getItemCount() {
        return listList.size();
    }

    public void setItemOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnItemClickListener {
        void onClick(List<AbsShape> absShapeList);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //        SfDisplayInfoView displayInfoView;
        TextView pageId;

        private ViewHolder(View itemView) {
            super(itemView);
//            displayInfoView = (SfDisplayInfoView) itemView.findViewById(R.id.dv_main);
//            displayInfoView.setCurrentMode(SfDisplayInfoView.MODE_STATIC);
            pageId = (TextView) itemView.findViewById(R.id.pageId);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = new AlertDialog(mContext);
                    dialog.setTitle("切换页面").setMsg("是否切换到当前页面").setPositiveButton("切换", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onClickListener != null) {
                                onClickListener.onClick(listList.get(getAdapterPosition()));
                            }
                        }
                    }).setNegativeButton("取消", null);
                    dialog.show();
                    return true;
                }
            });
        }
    }

}