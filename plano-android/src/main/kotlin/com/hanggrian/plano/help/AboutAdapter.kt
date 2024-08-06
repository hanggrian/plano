package com.hanggrian.plano.help

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanggrian.plano.BuildConfig
import com.hanggrian.plano.GitHubApi
import com.hanggrian.plano.R
import com.hanggrian.plano.util.longSnackbar
import com.hanggrian.plano.util.openUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AboutAdapter(private val onCheck: () -> View) :
    RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun getItemCount(): Int = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_about, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.image.setImageResource(R.drawable.ic_update)
                holder.text.setText(R.string.check_for_update)
                holder.itemView.setOnClickListener {
                    val snackbarRoot = onCheck()
                    runBlocking(Dispatchers.Main) {
                        val release = withContext(Dispatchers.IO) { GitHubApi.getRelease(".apk") }
                        when {
                            release.isNewerThan(BuildConfig.VERSION_NAME) ->
                                snackbarRoot.longSnackbar(
                                    context
                                        .getString(R.string._update_available)
                                        .format(release.name),
                                    context.getString(R.string.btn_download),
                                ) {
                                    context.openUrl(
                                        release.assets
                                            .first { it.name.endsWith("apk") }
                                            .downloadUrl,
                                    )
                                }
                            else ->
                                snackbarRoot
                                    .longSnackbar(context.getString(R.string._update_unavailable))
                        }
                    }
                }
            }
            1 -> {
                holder.image.setImageResource(R.drawable.ic_github)
                holder.text.setText(R.string.view_on_github)
                holder.itemView.setOnClickListener {
                    context.openUrl(BuildConfig.WEB)
                }
            }
            2 -> {
                holder.image.setImageResource(R.drawable.ic_licenses)
                holder.text.setText(R.string.open_source_licenses)
                holder.itemView.setOnClickListener {
                    context.startActivity(Intent(context, LicensesActivity::class.java))
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val text: TextView = itemView.findViewById(R.id.text)
    }
}
