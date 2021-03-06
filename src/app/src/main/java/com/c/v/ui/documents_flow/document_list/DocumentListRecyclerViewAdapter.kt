package com.c.v.ui.documents_flow.document_list

import android.content.Context
import android.os.Handler
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.c.v.R
import com.c.v.data.network.vkApi.model.VKDocument
import com.c.v.data.network.vkApi.model.VKDocumentType
import com.c.v.databinding.CellDocumentBinding
import com.c.v.ui.WithIdDiffCallback
import com.c.v.ui.base.BaseAdapter
import com.c.v.utils.CalendarReadableUtil
import com.c.v.utils.SizeHumanReadableUtil
import com.c.v.utils.dpToPx
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import java.util.*

class DocumentListRecyclerViewAdapter(
    private val locale: Locale,
    private val viewModel: DocumentListViewModel,
    private val context: Context) : BaseAdapter<DocumentViewHolder, VKDocument>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellDocumentBinding = CellDocumentBinding.inflate(layoutInflater, parent, false)
        val holder = DocumentViewHolder(cellDocumentBinding)

        holder.binding.apply {
            RxView.clicks(dots).subscribe {
                showMenuPopup(holder)
            }.addTo(compositeDisposable)

            RxView.focusChanges(nameTextView).onErrorResumeNext(Observable.empty()).subscribe {
                if (!it && holder.adapterPosition in items.indices) {
                    val item = items[holder.adapterPosition]
                    viewModel.renameDocument(item, holder.binding.nameTextView.text.toString() + "." + item.ext)
                    holder.binding.nameTextView.setSelection(0)
                    holder.binding.nameTextView.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    holder.binding.nameTextView.isEnabled = false
                }
            }.addTo(compositeDisposable)

            nameTextView.setOnEditorActionListener { _, _, _ ->
                holder.binding.nameTextView.clearFocus()
                true
            }

            RxView.clicks(root).subscribe {
                val item = items[holder.adapterPosition]
                viewModel.loadFileAndOpen(item, context)
            }.addTo(compositeDisposable)
        }

        return holder
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {

        val item = items[position]

        var title = item.title
        if (title.endsWith("." + item.ext, true)) {
            val removeLength = 1 + item.ext.length
            title = title.removeRange(title.length - removeLength, title.length)
        }
        val backDrawableRes = getBackDrawable(item)
        val typeIcon = getTypeIcon(item)
        val tagsText = item.tags.joinToString(", ")

        holder.binding.apply {
            nameTextView.setText(title)
            infoTextView.text = generateInfoString(item)
            typeContainer.setBackgroundResource(backDrawableRes)
            typeImage.setImageResource(typeIcon)
            tagsTextView.text = tagsText

            if (item.tags.isNotEmpty()) {
                tagsContainer.visibility = View.VISIBLE
                nameTextView.inputType = InputType.TYPE_CLASS_TEXT
                nameTextView.maxLines = 1
            } else {
                tagsContainer.visibility = View.GONE
                nameTextView.maxLines = 2
            }

            if (item.preview.isNotEmpty()) {
                Glide.with(context).load(item.preview).placeholder(typeIcon)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(6F.dpToPx(context).toInt())))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(loadedImageView)
                typeImage.visibility = View.GONE
            } else {
                loadedImageView.setImageBitmap(null)
                typeImage.visibility = View.VISIBLE
            }
        }
    }

    fun updateDocumentsListItems(documents: List<VKDocument>) {
        val diffResult = DiffUtil.calculateDiff(WithIdDiffCallback(documents, this.items))
        this.items = documents.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    private fun showMenuPopup(holder: DocumentViewHolder) {
        val popup = PopupMenu(context, holder.binding.dots, Gravity.END, 0, R.style.PopupMenu)
        popup.inflate(R.menu.document_list_context_menu)
        popup.setOnMenuItemClickListener {
            val document = items[holder.adapterPosition]
            when (it.itemId) {
                R.id.documentListContextMenu_remove -> viewModel.removeDocument(document)
                R.id.documentListContextMenu_rename -> {
                    holder.binding.nameTextView.isEnabled = true
                    holder.binding.nameTextView.setRawInputType(InputType.TYPE_CLASS_TEXT)
                    holder.binding.nameTextView.requestFocus()
                    holder.binding.nameTextView.setSelection(holder.binding.nameTextView.text?.length ?: 0)

                    Handler().postDelayed({
                        val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm?.showSoftInput(holder.binding.nameTextView, InputMethodManager.SHOW_FORCED)
                    }, 200)
                }
            }
            true
        }
        popup.show()
    }

    private fun getBackDrawable(item: VKDocument): Int {
        return when (item.type) {
            VKDocumentType.Book -> R.drawable.type_book
            VKDocumentType.Audio -> R.drawable.type_audio
            VKDocumentType.Video -> R.drawable.type_video
            VKDocumentType.TextDocument -> R.drawable.type_text
            VKDocumentType.Zip -> R.drawable.type_zip
            else -> R.drawable.type_other
        }
    }

    private fun getTypeIcon(item: VKDocument): Int {
        return when (item.type) {
            VKDocumentType.Book -> R.drawable.ic_book_vector
            VKDocumentType.Audio -> R.drawable.ic_audio_vector
            VKDocumentType.Video -> R.drawable.ic_video_vector
            VKDocumentType.TextDocument -> R.drawable.ic_text_vector
            VKDocumentType.Zip -> R.drawable.ic_zip_vector
            else -> R.drawable.ic_other_vector
        }
    }

    private fun generateInfoString(item: VKDocument): String {
        val ext = item.ext.toUpperCase(locale)
        val size = SizeHumanReadableUtil.format(item.size.toLong())
        val date = CalendarReadableUtil.format(item.calendar)

        val values = if (item.ext.isNotEmpty()) listOf(ext, size, date) else listOf(date, size)

        return values.joinToString(" · ")
    }
}
