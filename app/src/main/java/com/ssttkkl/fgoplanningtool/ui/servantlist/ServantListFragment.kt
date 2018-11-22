package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantlistBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException

class ServantListFragment : BackHandlerFragment(), ServantFilterFragment.Callback, LifecycleOwner {
    interface OnClickServantListener {
        fun onClickServant(servantId: Int)
    }

    private var listener: OnClickServantListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnClickServantListener -> parentFragment as OnClickServantListener
            activity is OnClickServantListener -> activity as OnClickServantListener
            else -> throw NoInterfaceImplException(OnClickServantListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private val servantFilterFragment
        get() = childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) as? ServantFilterFragment

    private lateinit var binding: FragmentServantlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServantlistBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[ServantListFragmentViewModel::class.java].apply {
            hiddenServantIDs.value = arguments?.getIntArray(KEY_HIDDEN)?.toHashSet() ?: setOf()
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    private lateinit var itemDecoration: CommonRecViewItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        // setup ServantFilterFragment
        if (childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) == null) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, ServantFilterFragment(), ServantFilterFragment::class.qualifiedName)
                    .commit()
        }

        itemDecoration = CommonRecViewItemDecoration(context!!)
        binding.recView.apply {
            adapter = ServantListAdapter(context!!, this@ServantListFragment, binding.viewModel!!)
            setHasFixedSize(true)
        }

        binding.viewModel?.apply {
            start(context!!)
            originServantIDs.observe(this@ServantListFragment, Observer {
                onOriginChanged(it ?: setOf())
            })
            viewType.observe(this@ServantListFragment, Observer {
                onViewTypeChanged(it ?: return@Observer)
            })
            informClickServantEvent.observe(this@ServantListFragment, Observer {
                onInformClickServant(it ?: return@Observer)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.servantlist, menu)
        when (binding.viewModel?.viewType?.value) {
            ViewType.List -> menu.findItem(R.id.switchToListView)?.isVisible = false
            ViewType.Grid -> menu.findItem(R.id.switchToGridView)?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.finish()
            R.id.sortAndFilter -> binding.drawerlayout.openDrawer(GravityCompat.END)
            R.id.switchToListView -> binding.viewModel?.onClickSwitchToListView()
            R.id.switchToGridView -> binding.viewModel?.onClickSwitchToGridView()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return if (binding.drawerlayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerlayout.closeDrawer(GravityCompat.END)
            true
        } else super.onBackPressed()
    }

    override fun onFilter(filtered: List<Servant>) {
        binding.viewModel?.onFiltered(filtered)
    }

    override fun onRequestCostItems(servant: Servant): Collection<Item> {
        return binding.viewModel?.onRequestCostItems(servant) ?: listOf()
    }

    private fun onOriginChanged(originServantIDs: Set<Int>) {
        servantFilterFragment?.originServantIDs = originServantIDs
    }

    private fun onViewTypeChanged(viewType: ViewType) {
        when (viewType) {
            ViewType.Grid -> {
                binding.recView.layoutManager = FlexboxLayoutManager(context).apply {
                    flexWrap = FlexWrap.WRAP
                    justifyContent = JustifyContent.SPACE_AROUND
                }
                binding.recView.removeItemDecoration(itemDecoration)
            }
            ViewType.List -> {
                binding.recView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                binding.recView.addItemDecoration(itemDecoration)
            }
        }
        activity?.invalidateOptionsMenu()
    }

    private fun onInformClickServant(servantID: Int) {
        listener?.onClickServant(servantID)
    }

    companion object {
        fun newInstance(hiddenServantIDs: Set<Int>) = ServantListFragment().apply {
            arguments = Bundle().apply {
                putIntArray(KEY_HIDDEN, hiddenServantIDs.toIntArray())
            }
        }

        private const val KEY_HIDDEN = "hidden"
    }
}