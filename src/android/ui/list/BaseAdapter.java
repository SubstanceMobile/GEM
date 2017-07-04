/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.ui.list;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.animbus.music.tasks.Loader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 4/17/2016.
 */
public abstract class BaseAdapter<BINDING extends ViewDataBinding, TYPE> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> implements List<TYPE>, View.OnClickListener, View.OnLongClickListener {
    List<TYPE> data = new ArrayList<>();
    BINDING binding;
    Context context;

    public BaseAdapter(Context context){
        this(context, null);

    }

    public BaseAdapter(Context context, List<TYPE> data) {
        this.context = context;
        if (data != null) this.data = data;
    }

    protected abstract int getVarID();

    protected abstract BINDING getBinding(LayoutInflater inflater, ViewGroup parent);

    protected void configure(BINDING binding) {
        //Do nothing. Overridden if necessary
    }

    protected void registerListener(Loader.TaskListener<TYPE> listener) {
        //Register the listener. Override to then call the proper method
    }

    @Override
    public boolean onLongClick(View v) {
        //Do nothing. Can be overridden if it is necessary to do anything on a long click
        return false;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        binding.setVariable(getVarID(), data.get(position));
        configure(binding);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(getBinding(LayoutInflater.from(context), parent));
    }

    protected class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(BINDING binding) {
            super(binding.getRoot());
            itemView.setOnClickListener(BaseAdapter.this);
            itemView.setOnLongClickListener(BaseAdapter.this);
        }

    }

}
