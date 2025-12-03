package com.example.roke_claves

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class EventoPendienteAdapter(private val eventos: List<EventoPendienteListarActivity.EventoPendiente>) : BaseAdapter() {

    override fun getCount(): Int = eventos.size

    override fun getItem(position: Int): EventoPendienteListarActivity.EventoPendiente = eventos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_evento_pendiente, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val evento = eventos[position]

        holder.textId.text = "ID: ${evento.id}"
        holder.textSensor.text = "Sensor: ${evento.sensor}"
        holder.textUsuario.text = "Usuario: ${evento.usuario}"
        holder.textFecha.text = "Fecha: ${evento.fecha}"

        return view
    }

    private class ViewHolder(view: View) {
        val textId: TextView = view.findViewById(R.id.textViewId)
        val textSensor: TextView = view.findViewById(R.id.textViewSensor)
        val textUsuario: TextView = view.findViewById(R.id.textViewUsuario)
        val textFecha: TextView = view.findViewById(R.id.textViewFecha)
    }
}