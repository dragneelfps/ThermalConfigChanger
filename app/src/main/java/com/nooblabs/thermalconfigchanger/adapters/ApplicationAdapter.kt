package com.nooblabs.thermalconfigchanger.adapters

import android.content.pm.PackageInfo
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.extensions.log
import kotlinx.android.synthetic.main.item_app.view.*

class ApplicationAdapter : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    var list: MutableList<Pair<PackageInfo, ThermalMode>> = ArrayList()
        set(value) {
            log("new data")
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }
    var modeChangeListener: OnModeChangeListener? = null

//    fun updateList(newData: List<Pair<PackageInfo, ThermalMode>>)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ApplicationViewHolder =
        ApplicationViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_app, p0, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {

        val context = holder.itemView.context

        val state = list[position]
        val adapter = ArrayAdapter<ThermalMode>(
            context, android.R.layout.simple_spinner_dropdown_item,
            ThermalMode.values()
        )

        val pm = holder.itemView.context.packageManager

        Glide.with(context).load(pm.getApplicationIcon(state.first.applicationInfo))
            .into(holder.itemView.img_app_icon)

        holder.itemView.tv_app_name.text =
                pm.getApplicationLabel(state.first.applicationInfo).toString()

        holder.itemView.modes.adapter = adapter
        holder.itemView.modes.setSelection(ThermalMode.values().indexOf(state.second), false)
        holder.itemView.modes.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        modeChangeListener?.onModeChange(
                            state.first.packageName,
                            adapter.getItem(position)!!
                        )
                    }
                }
    }

    interface OnModeChangeListener {
        fun onModeChange(packageName: String, newMode: ThermalMode)
    }

    class ApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
