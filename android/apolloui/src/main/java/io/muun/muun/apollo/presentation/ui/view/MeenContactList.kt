package io.meen.apollo.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import io.meen.apollo.R
import io.meen.apollo.domain.model.Contact
import io.meen.apollo.domain.model.PermissionState
import io.meen.apollo.domain.model.P2PState
import io.meen.apollo.presentation.ui.adapter.ItemAdapter
import io.meen.apollo.presentation.ui.adapter.holder.ViewHolderFactory
import io.meen.apollo.presentation.ui.adapter.viewmodel.ContactViewModel

class MeenContactList @JvmOverloads constructor(c: Context, a: AttributeSet? = null, s: Int = 0) :
    MeenView(c, a, s) {

    @BindView(R.id.contact_list_empty)
    lateinit var goToP2PSetupView: MeenEmptyScreen

    @BindView(R.id.contact_list_loading)
    lateinit var loadingView: LoadingView

    @BindView(R.id.contact_list_first_on_meen)
    lateinit var firstOnMeenView: FirstOnMeenView

    @BindView(R.id.contact_list_permission_denied_forever)
    lateinit var goToSettingsView: SimpleMessageView

    @BindView(R.id.contact_list_recycler)
    lateinit var listView: RecyclerView

    @BindView(R.id.contact_list_recycler_title)
    lateinit var listViewTitle: TextView

    private lateinit var listAdapter: ItemAdapter

    override val layoutResource: Int
        get() = R.layout.meen_contact_list

    var onGoToSettingsListener: () -> Unit = {}
    var onGoToP2PSetupListener: () -> Unit = {}
    var onSelectListener: (contact: Contact) -> Unit = {}

    var state: P2PState? = null
        set(newState) {
            internalSetP2PState(newState)
            field = newState
        }

    override fun setUp(context: Context, attrs: AttributeSet?) {
        super.setUp(context, attrs)

        goToP2PSetupView.setOnActionClickListener { onGoToP2PSetupListener() }
        goToP2PSetupView.setOnLinkClickListener { onGoToP2PSetupListener() }

        goToSettingsView.setOnActionClickListener { onGoToSettingsListener() }

        listAdapter = ItemAdapter(ViewHolderFactory()).apply {
            setOnItemClickListener { onSelectListener((it as ContactViewModel).model) }
        }

        listView.layoutManager = LinearLayoutManager(context)
        listView.addItemDecoration(DividerItemDecoration(context, 82, 0)) // 82, obviously
        listView.adapter = listAdapter
    }

    fun isShowingContacts() =
        listView.visibility == View.VISIBLE

    private fun internalSetP2PState(newP2PState: P2PState?) {
        // Reset visibility states:
        goToP2PSetupView.visibility = View.GONE
        goToSettingsView.visibility = View.GONE
        firstOnMeenView.visibility = View.GONE
        loadingView.visibility = View.GONE
        listView.visibility = View.GONE

        // Pick the new visible view:
        val visibleView = when {
            newP2PState == null ||
                !newP2PState.user.hasP2PEnabled ||
                newP2PState.permissionState == PermissionState.DENIED ->
                goToP2PSetupView

            newP2PState.permissionState == PermissionState.PERMANENTLY_DENIED ->
                goToSettingsView

            newP2PState.syncState.isLoading ->
                loadingView

            newP2PState.contacts.isEmpty() ->
                firstOnMeenView

            else ->
                listView
        }

        visibleView.visibility = View.VISIBLE
        listViewTitle.visibility = listView.visibility

        // Set the contact list items, if available:
        listAdapter.items = getFilteredContacts(newP2PState)?.map(::ContactViewModel) ?: listOf()
        listAdapter.notifyDataSetChanged()
    }

    /**
     * This is a TEMPORARY (yeah sure) quick and dirty hack to hide deprecated/deleted users.
     * In the future, we'll implement a proper solution for deleting and updating contacts.
     * For now, we just use the Unicode Character 'ZERO WIDTH SPACE' (U+200B) as a mark to hide
     * contacts representing deleted/deprecated users.
     */
    private fun getFilteredContacts(newP2PState: P2PState?): List<Contact>? {
        return newP2PState?.contacts
            ?.filter { contact ->
                !contact.publicProfile.firstName.contains("\u200B")
                    && !contact.publicProfile.lastName.contains("\u200B")
            }
    }
}