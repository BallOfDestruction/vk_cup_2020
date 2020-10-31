package com.c.v.ui.check_in_flow.places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.c.v.R
import com.c.v.databinding.CellPlaceBinding
import com.c.v.ui.WithIdDiffCallback
import com.c.v.ui.base.BaseAdapter
import com.c.v.ui.check_in_flow.places.dto.PlacePresentationDto
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.rxkotlin.addTo

class PlacesListRecyclerViewAdapter() : BaseAdapter<PlacesViewHolder, PlacePresentationDto>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellDocumentBinding = CellPlaceBinding.inflate(layoutInflater, parent, false)
        val holder = PlacesViewHolder(cellDocumentBinding)

        holder.binding.apply {

            RxView.clicks(root).subscribe {
            }.addTo(compositeDisposable)
        }

        return holder
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {

        val item = items[position]

        holder.binding.apply {
            titleTextView.text = item.title
            subtitleTextView.text = item.subtitle

            if(item.icon.isEmpty()) {
                icon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_location))
            } else {
                Glide.with(holder.itemView.context)
                    .load(item.icon)
                    .into(icon)
            }

            mapView.onCreate(null)
            mapView.onStart()
            mapView.onResume()

            mapView.getMapAsync {
                val latLng = LatLng(item.latitude, item.longitude)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))

                val marker = MarkerOptions().position(latLng).title(item.title)
                it.addMarker(marker)
            }
        }
    }

    fun updateListItems(documents: List<PlacePresentationDto>) {
        val diffResult = DiffUtil.calculateDiff(WithIdDiffCallback(documents, this.items))
        this.items = documents.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }
}
