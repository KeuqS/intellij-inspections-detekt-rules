package by.overpas.detekt.intellij.kotlin.coroutines

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.kotlin.coroutines.coroutinecontextwithjob.CoroutineContextWithJob
import by.overpas.detekt.intellij.kotlin.coroutines.deferredresultunused.DeferredResultUnused
import by.overpas.detekt.intellij.kotlin.coroutines.foreachjoinoncollectionofjob.ForEachJoinOnCollectionOfJob
import by.overpas.detekt.intellij.kotlin.coroutines.mapawaitoncollectionofdeferred.MapAwaitOnCollectionOfDeferred
import by.overpas.detekt.intellij.kotlin.coroutines.prefercurrentcoroutinecontexttocoroutinecontext.PreferCurrentCoroutineContextToCoroutineContext
import by.overpas.detekt.intellij.kotlin.coroutines.runblockinginsuspendfunction.RunBlockingInSuspendFunction
import by.overpas.detekt.intellij.kotlin.coroutines.simplifiableflowcall.SimplifiableFlowCall
import by.overpas.detekt.intellij.kotlin.coroutines.simplifiableflowcallchain.SimplifiableFlowCallChain
import by.overpas.detekt.intellij.kotlin.coroutines.suspendcoroutinelackscancellationguarantees.SuspendCoroutineLacksCancellationGuarantees
import by.overpas.detekt.intellij.kotlin.coroutines.suspiciousimplicitcoroutinescopereceiveraccess.SuspiciousImplicitCoroutineScopeReceiverAccess
import by.overpas.detekt.intellij.kotlin.coroutines.suspiciousmutablecollectioninstateflow.SuspiciousMutableCollectionInStateFlow
import by.overpas.detekt.intellij.kotlin.coroutines.unusedflow.UnusedFlow
import by.overpas.detekt.intellij.kotlin.coroutines.uselesscallonflow.UselessCallOnFlow
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.coroutine")
class CoroutinesRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-kotlin-coroutines")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::CoroutineContextWithJob,
                ::DeferredResultUnused,
                ::ForEachJoinOnCollectionOfJob,
                ::MapAwaitOnCollectionOfDeferred,
                ::PreferCurrentCoroutineContextToCoroutineContext,
                ::RunBlockingInSuspendFunction,
                ::SimplifiableFlowCall,
                ::SimplifiableFlowCallChain,
                ::SuspendCoroutineLacksCancellationGuarantees,
                ::SuspiciousImplicitCoroutineScopeReceiverAccess,
                ::SuspiciousMutableCollectionInStateFlow,
                ::UnusedFlow,
                ::UselessCallOnFlow,
            ),
        )
}
