package cd.shuri.smaprtpay.merchant.screens.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.databinding.NotificationItemsBinding
import cd.shuri.smaprtpay.merchant.network.Notification

class NotificationsAdapter: ListAdapter<Notification, NotificationsAdapter.NotificationsViewHolder>(NotificationsDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        return NotificationsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificationsViewHolder private constructor(private val binding: NotificationItemsBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            binding.notification = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NotificationsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotificationItemsBinding.inflate(layoutInflater, parent, false)
                return NotificationsViewHolder(binding)
            }
        }
    }
}

class NotificationsDiffCallBack: DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.reference == newItem.reference
    }
}