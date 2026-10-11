package by.overpas.detekt.intellij.gradle.bestpractices

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.gradle.bestpractices.avoidapplypluginmethod.AvoidApplyPluginMethod
import by.overpas.detekt.intellij.gradle.bestpractices.avoiddependencynamedargumentsnotation.AvoidDependencyNamedArgumentsNotation
import by.overpas.detekt.intellij.gradle.bestpractices.avoidduplicatedependencies.AvoidDuplicateDependencies
import by.overpas.detekt.intellij.gradle.bestpractices.avoidduplicaterepositories.AvoidDuplicateRepositories
import by.overpas.detekt.intellij.gradle.bestpractices.avoidrepositoriesinbuildgradle.AvoidRepositoriesInBuildGradle
import by.overpas.detekt.intellij.gradle.bestpractices.redundantkotlinstdlibdependency.RedundantKotlinStdLibDependency
import by.overpas.detekt.intellij.gradle.bestpractices.taskmissingdescription.TaskMissingDescription
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.best.practices")
class BestPracticesRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-gradle-best-practices")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::AvoidApplyPluginMethod,
                ::AvoidDependencyNamedArgumentsNotation,
                ::AvoidDuplicateDependencies,
                ::AvoidDuplicateRepositories,
                ::AvoidRepositoriesInBuildGradle,
                ::RedundantKotlinStdLibDependency,
                ::TaskMissingDescription,
            ),
        )
}
