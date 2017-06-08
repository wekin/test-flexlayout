package com.wekin.test.flexlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlexManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flex_manager);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setAdapter(new FlexAdapter());
        recyclerView.setLayoutManager(new FlexboxLayoutManager());
    }

    private static class Entity {
        String name;
        int type;
        boolean activated;
    }


    private static class FlexAdapter extends RecyclerView.Adapter<FlexHolder> {
        List<Entity> list = new ArrayList<>();
        int mLastResultPos = 1;

        {
            Entity entity = new Entity();
            entity.type = -2;
            entity.name = "SELECT RESULT";
            list.add(entity);
            for (int i = 0; i < 6; i++) {
                String catalog = "catalog_" + i;
                entity = new Entity();
                entity.type = -1;
                entity.name = catalog;
                list.add(entity);

                for (int j = 0; j < 20; j++) {
                    entity = new Entity();
                    entity.type = 1;
                    entity.name = catalog + "_item_" + j;
                    list.add(entity);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            int type = list.get(position).type;
            if (type >= 0) {
                return 1;
            }
            return 0;
        }

        @Override
        public FlexHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutRes = viewType == 1 ? R.layout.item_flex_select : R.layout.item_flex_title;
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
            return new FlexHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FlexHolder holder, int position) {
            Entity item = list.get(position);
            holder.textView.setText(item.name);
            holder.textView.setActivated(item.activated);
        }

        @Override
        public void onViewAttachedToWindow(FlexHolder holder) {
            super.onViewAttachedToWindow(holder);
            if (holder.getItemViewType() == 1) {
                holder.textView.setOnClickListener(onClickListener);
            }
        }


        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView parent = (RecyclerView) v.getParent();
                if (parent == null) {
                    return;
                }
                RecyclerView.ViewHolder holder = parent.getChildViewHolder(v);
                if (holder == null) {
                    return;
                }
                int position = holder.getAdapterPosition();
                Entity item = list.get(position);
                if (item == null) {
                    return;
                }
                if (item.type == 0) {
                    resultClick(position);
                } else {
                    selectClick(v, item);
                }
            }
        };

        private void selectClick(View v, Entity item) {
            if (item.activated) {
                item.activated = false;
                v.setActivated(false);
                Iterator<Entity> it = list.iterator();
                int pos = -1;
                while (it.hasNext()) {
                    pos++;
                    if (pos == mLastResultPos) {
                        break;
                    }
                    Entity next = it.next();
                    if (next.name.equals(item.name)) {
                        it.remove();
                        notifyItemRemoved(pos);
                        mLastResultPos--;
                        break;
                    }
                }
            } else {
                v.setActivated(true);
                item.activated = true;
                Entity entity = new Entity();
                entity.type = 0;
                entity.name = item.name;
                final int old = mLastResultPos;
                list.add(old, entity);
                notifyItemInserted(old);
                mLastResultPos++;
            }
        }

        private void resultClick(int position) {
            Entity remove = list.remove(position);
            if (remove != null) {
                notifyItemRemoved(position);
                mLastResultPos--;
                final int size = list.size();
                for (int i = mLastResultPos + 1; i < size; i++) {
                    Entity select = list.get(i);
                    if (remove.name.equals(select.name)) {
                        select.activated = false;
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    private static class FlexHolder extends RecyclerView.ViewHolder {
        TextView textView;

        FlexHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}
