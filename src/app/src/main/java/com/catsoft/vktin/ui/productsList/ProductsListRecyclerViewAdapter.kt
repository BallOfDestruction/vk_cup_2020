package com.catsoft.vktin.ui.productsList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.catsoft.vktin.R
import com.catsoft.vktin.ui.base.BaseAdapter
import com.catsoft.vktin.utils.DimensionUtil
import com.catsoft.vktin.vkApi.model.VKProduct
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.rxkotlin.addTo
import java.util.*

class ProductsListRecyclerViewAdapter(
    locale: Locale,
    private val context: Context) : BaseAdapter<ProductsViewHolder, VKProduct>() {

    private val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(locale).apply {
        maximumFractionDigits = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_product, parent, false)

        val holder = ProductsViewHolder(view)
        RxView.clicks(holder.itemView).subscribe {
            val item = items[holder.adapterPosition]
            val nav = view.findNavController()
            val bundle = bundleOf(Pair("item", item))
            nav.navigate(R.id.action_navigation_products_to_navigation_product, bundle)
        }.addTo(compositeDisposable)

        return holder
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {

        val item = items[position]

        val currency = Currency.getInstance(item.price.currency.name)
        currencyFormatter.currency = currency
        val price = currencyFormatter.format(item.price.amount)

        holder.name.text = item.title
        holder.price.text = price

        Glide.with(context).load(item.thumb_photo).transform(RoundedCorners(DimensionUtil.convertDpToPixel(8F, context).toInt()))
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image)
    }

    fun updateMarketsListItems(list: List<VKProduct>) {
        val diffResult = DiffUtil.calculateDiff(ProductsDiffCallback(list, this.items))
        this.items = list.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }
}
