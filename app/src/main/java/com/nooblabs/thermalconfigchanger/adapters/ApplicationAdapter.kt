package com.nooblabs.thermalconfigchanger.adapters

import android.content.Context
import android.content.pm.PackageInfo
import androidx.recyclerview.widget.RecyclerView
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

class ApplicationAdapter(private val context: Context) : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    private var list: List<Pair<PackageInfo, ThermalMode>> = ArrayList()
    private val filteredList: MutableList<Pair<PackageInfo, ThermalMode>> = ArrayList()
    var modeChangeListener: OnModeChangeListener? = null

//    fun updateList(newData: List<Pair<PackageInfo, ThermalMode>>)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ApplicationViewHolder =
        ApplicationViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_app, p0, false))

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {

        val context = holder.itemView.context

        val state = filteredList[position]
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

    fun initializeData(data: List<Pair<PackageInfo, ThermalMode>>) {
        log("new data")
        list = data
        filter("")
    }

    fun filter(filterTerm: String) {
        val pm = context.packageManager
        filteredList.clear()
        list.forEach { app ->
            val appName = pm.getApplicationLabel(app.first.applicationInfo).toString()
            if(appName.contains(filterTerm, true)) {
                filteredList.add(app)
            }
        }
        notifyDataSetChanged()
    }

    interface OnModeChangeListener {
        fun onModeChange(packageName: String, newMode: ThermalMode)
    }

    class ApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
