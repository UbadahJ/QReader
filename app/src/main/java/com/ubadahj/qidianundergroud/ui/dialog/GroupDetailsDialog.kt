package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.GroupInfoMenuBinding
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.models.isRead
import com.ubadahj.qidianundergroud.utils.ui.openLink
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupDetailsDialog(
    private val group: Group
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var groupRepo: GroupRepository

    private var binding: GroupInfoMenuBinding? = null
    private val lastRead: String
        get() = when {
            group.lastRead == 0 -> "Not started reading"
            group.isRead() -> "This group has been read"
            else -> "Currently reading chapter ${group.lastRead}"
        }
    private val downloaded: String
        get() = if (groupRepo.isDownloaded(group)) "This group is downloaded"
        else "This group is not downloaded"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return GroupInfoMenuBinding.inflate(inflater, container, false).apply {
            binding = this

            groupStartChapter.text = "Start: ${group.firstChapter}"
            groupEndChapter.text = "End: ${group.lastChapter}"
            readStatusText.text = lastRead
            downloadStatusText.text = downloaded

            recyclerView.adapter = MenuAdapter(
                listOf(
                    MenuDialogItem("Mark as read", R.drawable.check) {
                        groupRepo.updateLastRead(group, group.lastChapter.toInt())
                        dismiss()
                    },
                    MenuDialogItem("Open in browser", R.drawable.external_link) {
                        requireActivity().openLink(group.link)
                        dismiss()
                    },
                )
            )
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
