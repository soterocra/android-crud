package com.soterocra.crud.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soterocra.crud.R;
import com.soterocra.crud.activities.CadastraAlteraUsuario;
import com.soterocra.crud.activities.enums.CadastraAlteraEnum;
import com.soterocra.crud.dto.DtoUser;

import java.util.List;

import static com.soterocra.crud.activities.CadastraAlteraUsuario.CADASTRA_ALTERA;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioHolder> {
    private LayoutInflater mInflater; //objeto que "infla" o layout do item de lista do recyclerview
    private Context context; //activity que está exibindo o recyclerview
    private List<DtoUser> lista; //fonte dos dados da lista a ser exibida

    public UsuarioAdapter(Context context, List<DtoUser> lista) {
        this.context = context;
        this.lista = lista;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recyclerview_layout_item_todos_usuarios, parent, false);
        return new UsuarioHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioHolder holder, int position) {
        String nome = lista.get(position).getName();
        holder.nome.setText(nome);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CadastraAlteraUsuario.class);
                intent.putExtra("user", lista.get(position));
                intent.putExtra(CADASTRA_ALTERA, CadastraAlteraEnum.ALTERACAO);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class UsuarioHolder extends RecyclerView.ViewHolder {
        final UsuarioAdapter usuarioAdapter;
        public final TextView nome;

        public  UsuarioHolder(@NonNull View itemView, UsuarioAdapter usuarioAdapter) {
            super(itemView);
            this.usuarioAdapter = usuarioAdapter;
            nome = itemView.findViewById(R.id.tv_recyclerview_nome_usuario);
        }

        //    @Override
//    public void onClick(final View view) {
//        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
//        String item = mList.get(itemPosition);
//        Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
//    }
    }
}
