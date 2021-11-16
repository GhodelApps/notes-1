package com.jankku.notes.ui

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jankku.notes.databinding.NoteItemBinding
import com.jankku.notes.db.Note


class NoteAdapter(
    private val clickListener: (Note) -> Unit,
    private val swipeListener: (Int) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffUtil) {
    lateinit var selectionTracker: SelectionTracker<Long>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemViewType(position: Int): Int = position

    fun deleteItem(itemPosition: Int) {
        swipeListener(itemPosition)
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var noteId: Long? = null

        fun bind(
            note: Note,
        ) = with(itemView) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteBody.text = note.getTruncatedBody()
            noteId = note.id
            itemView.setOnClickListener { clickListener(note) }
            bindSelectedState(
                this,
                selectionTracker.isSelected(getItem(absoluteAdapterPosition).id)
            )
        }

        private fun bindSelectedState(view: View, selected: Boolean) {
            view.isActivated = selected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = getItem(absoluteAdapterPosition).id
                override fun inSelectionHotspot(e: MotionEvent): Boolean = false
                override fun inDragRegion(e: MotionEvent): Boolean = true
            }
    }

    companion object {
        private val DiffUtil = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }
}
