package com.ssttkkl.fgoplanningtool.ui.planlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class PlanListFragmentViewModel : ViewModel() {
    val addPlanEvent = SingleLiveEvent<Void>()
    val editPlanEvent = SingleLiveEvent<Plan>()
    val calcResultEvent = SingleLiveEvent<Collection<Plan>>()
    val removePlansEvent = SingleLiveEvent<Collection<Plan>>()
    val changeOriginEvent = SingleLiveEvent<Collection<Servant>>()

    init {
        Repo.planListLiveData.observeForever {
            changeOriginEvent.call(it?.mapNotNull { plan -> plan.servant })
        }
    }

    val inSelectMode = object : MutableLiveData<Boolean>() {
        override fun setValue(value: Boolean?) {
            val old = this.value == true
            val new = value == true
            if (old != new)
                super.setValue(new)
        }
    }

    val data = MutableLiveData<List<Plan>>()

    val existsServantIDs: LiveData<Set<Int>> = Transformations.map(data) { data ->
        data.map { it.servantId }.toSet()
    }

    val selectedServantIDs = object : MutableLiveData<Set<Int>>() {
        init {
            existsServantIDs.observeForever { existsServantID ->
                value = value?.filter { existsServantID?.contains(it) == true }?.toSet()
            }
            inSelectMode.observeForever {
                value = setOf()
            }
        }
    }

    val selection
        get() = selectedServantIDs.value?.mapNotNull { servantID -> Repo.planRepo[servantID] }
                ?: listOf()

    fun onPlanClick(plan: Plan) {
        if (inSelectMode.value == true) {
            if (selectedServantIDs.value?.contains(plan.servantId) == true)
                selectedServantIDs.value = selectedServantIDs.value?.minus(plan.servantId)
            else
                selectedServantIDs.value = selectedServantIDs.value?.plus(plan.servantId) ?: setOf()
        } else
            editPlanEvent.call(plan)
    }

    fun onPlanLongClick(plan: Plan): Boolean {
        return if (inSelectMode.value != true) {
            inSelectMode.value = true
            selectedServantIDs.value = setOf(plan.servantId)
            true
        } else false
    }

    fun onFabClick() {
        val plans = if (inSelectMode.value == true)
            selection
        else
            data.value
        calcResultEvent.call(plans)
        inSelectMode.value = false
    }

    fun onBackPressed(): Boolean {
        return if (inSelectMode.value == true) {
            inSelectMode.value = false
            true
        } else false
    }

    fun onHomeClick() {
        inSelectMode.value = false
    }

    fun onEnterSelectModeClick() {
        inSelectMode.value = true
    }

    fun onAddClick() {
        addPlanEvent.call()
    }

    fun onSelectAllClick() {
        selectedServantIDs.value = if (selectedServantIDs.value == existsServantIDs.value)
            setOf()
        else
            existsServantIDs.value
    }

    fun onRemoveClick() {
        removePlansEvent.call(selection)
        inSelectMode.value = false
    }

    fun onRemoveWarningUIResult(plans: Collection<Plan>,
                                deductItems: Boolean) {
        remove(plans, if (deductItems)
            plans.costItems
        else
            null)
    }

    fun onFilter(filtered: List<Servant>) {
        val servantIDs = filtered.map { it.id }
        data.value = Repo.planRepo.all.asSequence()
                .sortedBy { servantIDs.indexOf(it.servantId) }
                .filter { servantIDs.contains(it.servantId) }
                .toList()
    }

    fun getPlanByServantID(servantID: Int) = Repo.planRepo[servantID]

    private fun remove(plans: Collection<Plan>, itemsToDeduct: Collection<Item>?) {
        Repo.planRepo.remove(plans.map { it.servantId }, HowToPerform.Launch)
        if (itemsToDeduct != null && itemsToDeduct.isNotEmpty())
            Repo.itemRepo.deductItems(itemsToDeduct, HowToPerform.Launch)
    }
}