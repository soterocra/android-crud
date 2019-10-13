package com.soterocra.crud.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soterocra.crud.R;
import com.soterocra.crud.activities.CadastraAlteraUsuario;
import com.soterocra.crud.activities.enums.CadastraAlteraEnum;
import com.soterocra.crud.dto.DtoUser;
import com.soterocra.crud.services.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.soterocra.crud.activities.CadastraAlteraUsuario.CADASTRA_ALTERA;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioHolder> {
    private LayoutInflater mInflater; //objeto que "infla" o layout do item de items do recyclerview
    private Context context; //activity que está exibindo o recyclerview
    private List<DtoUser> items; //fonte dos dados da items a ser exibida

    private String token;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu
    List<String> itemsPendingRemoval = new ArrayList<>();

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    public UsuarioAdapter(Context context, List<DtoUser> items, String token) {
        this.context = context;
        this.items = items;
        this.token = token;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public UsuarioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UsuarioHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioHolder holder, int position) {

        DtoUser user = items.get(position);

        String item = user.getId() + " - " + user.getName();
        holder.titleTextView.setText(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CadastraAlteraUsuario.class);
                intent.putExtra("user", items.get(position));
                intent.putExtra(CADASTRA_ALTERA, CadastraAlteraEnum.ALTERACAO);
                context.startActivity(intent);
            }
        });

        UsuarioHolder viewHolder = (UsuarioHolder) holder;

        if (itemsPendingRemoval.contains(item)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(Color.RED);
            viewHolder.titleTextView.setVisibility(View.GONE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(item));
                }
            });
        } else {
            // we need to show the "normal" state
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.titleTextView.setText(item);
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        DtoUser user = items.get(position);
        final String item = user.getId() + " - " + user.getName();
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
//                    remove(items.indexOf(item));
                    remove(position);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        DtoUser user = items.get(position);
        String item = user.getId() + " - " + user.getName();

        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }

        RetrofitService.getServico(context).deletaUsuario(items.get(position).getId(), "Bearer "+ token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Usuário deletado com sucesso.", Toast.LENGTH_SHORT).show();
                    items.remove(position);
                    notifyItemRemoved(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Falhs ao excluir.", Toast.LENGTH_SHORT).show();
            }
        });

//        if (items.contains(item)) {
//        items.remove(position);
//        notifyItemRemoved(position);


//        }
    }

    public boolean isPendingRemoval(int position) {
        DtoUser user = items.get(position);
        String item = user.getId() + " - " + user.getName();
        return itemsPendingRemoval.contains(item);
    }

    public class UsuarioHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button undoButton;

        public UsuarioHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout_item_todos_usuarios, parent, false));
            titleTextView = (TextView) itemView.findViewById(R.id.tv_recyclerview_nome_usuario);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }

        //    @Override
//    public void onClick(final View view) {
//        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
//        String item = mList.get(itemPosition);
//        Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
//    }
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
//    static class TestViewHolder extends RecyclerView.ViewHolder {
//
//        TextView titleTextView;
//        Button undoButton;
//
//        public TestViewHolder(ViewGroup parent) {
//            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout_item_todos_usuarios, parent, false));
//            titleTextView = (TextView) itemView.findViewById(R.id.tv_recyclerview_nome_usuario);
//            undoButton = (Button) itemView.findViewById(R.id.undo_button);
//        }
//
//    }
}
