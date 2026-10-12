package by.overpas.detekt.intellij.kotlin.probablebugs

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.kotlin.probablebugs.ambiguousnonlocaljump.AmbiguousNonLocalJump
import by.overpas.detekt.intellij.kotlin.probablebugs.arrayindataclass.ArrayInDataClass
import by.overpas.detekt.intellij.kotlin.probablebugs.assignedvalueisneverread.AssignedValueIsNeverRead
import by.overpas.detekt.intellij.kotlin.probablebugs.cansealedsubclassbeobject.CanSealedSubClassBeObject
import by.overpas.detekt.intellij.kotlin.probablebugs.conflictingextensionproperty.ConflictingExtensionProperty
import by.overpas.detekt.intellij.kotlin.probablebugs.convertnanequality.ConvertNaNEquality
import by.overpas.detekt.intellij.kotlin.probablebugs.dataclassprivateconstructor.DataClassPrivateConstructor
import by.overpas.detekt.intellij.kotlin.probablebugs.delegationtovarproperty.DelegationToVarProperty
import by.overpas.detekt.intellij.kotlin.probablebugs.duplicateargumentsinsetofandmapoffunctions.DuplicateArgumentsInSetOfAndMapOfFunctions
import by.overpas.detekt.intellij.kotlin.probablebugs.emptyrange.EmptyRange
import by.overpas.detekt.intellij.kotlin.probablebugs.filterisinstanceresultisalwaysempty.FilterIsInstanceResultIsAlwaysEmpty
import by.overpas.detekt.intellij.kotlin.probablebugs.foreachparameternotused.ForEachParameterNotUsed
import by.overpas.detekt.intellij.kotlin.probablebugs.implicitnullablenothingtype.ImplicitNullableNothingType
import by.overpas.detekt.intellij.kotlin.probablebugs.incompletedestructuring.IncompleteDestructuring
import by.overpas.detekt.intellij.kotlin.probablebugs.javaioserializableobjectmusthavereadresolve.JavaIoSerializableObjectMustHaveReadResolve
import by.overpas.detekt.intellij.kotlin.probablebugs.kotlinarrayhashcode.KotlinArrayHashCode
import by.overpas.detekt.intellij.kotlin.probablebugs.kotlinarraytostring.KotlinArrayToString
import by.overpas.detekt.intellij.kotlin.probablebugs.kotlinequalsbetweeninconvertibletypes.KotlinEqualsBetweenInconvertibleTypes
import by.overpas.detekt.intellij.kotlin.probablebugs.kotlinmisorderedassertequalsarguments.KotlinMisorderedAssertEqualsArguments
import by.overpas.detekt.intellij.kotlin.probablebugs.kotlinthrowablenotthrown.KotlinThrowableNotThrown
import by.overpas.detekt.intellij.kotlin.probablebugs.lateinitvaroverrideslateinitvar.LateinitVarOverridesLateinitVar
import by.overpas.detekt.intellij.kotlin.probablebugs.lazywithoutdelegation.LazyWithoutDelegation
import by.overpas.detekt.intellij.kotlin.probablebugs.mainfunctionreturnunit.MainFunctionReturnUnit
import by.overpas.detekt.intellij.kotlin.probablebugs.recursiveequalscall.RecursiveEqualsCall
import by.overpas.detekt.intellij.kotlin.probablebugs.recursivepropertyaccessor.RecursivePropertyAccessor
import by.overpas.detekt.intellij.kotlin.probablebugs.redundantlabel.RedundantLabel
import by.overpas.detekt.intellij.kotlin.probablebugs.selfassignment.SelfAssignment
import by.overpas.detekt.intellij.kotlin.probablebugs.selfreferenceconstructorparameter.SelfReferenceConstructorParameter
import by.overpas.detekt.intellij.kotlin.probablebugs.setterbackingfieldassignment.SetterBackingFieldAssignment
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciouscalloncollectiontoaddorremovepath.SuspiciousCallOnCollectionToAddOrRemovePath
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciouscascadingif.SuspiciousCascadingIf
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciouscollectionreassignment.SuspiciousCollectionReassignment
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciousequalscombination.SuspiciousEqualsCombination
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciousgetterformutableobject.SuspiciousGetterForMutableObject
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciousjavaclasscallablereference.SuspiciousJavaClassCallableReference
import by.overpas.detekt.intellij.kotlin.probablebugs.suspiciousvarproperty.SuspiciousVarProperty
import by.overpas.detekt.intellij.kotlin.probablebugs.unuseddataclasscopyresult.UnusedDataClassCopyResult
import by.overpas.detekt.intellij.kotlin.probablebugs.unusedequals.UnusedEquals
import by.overpas.detekt.intellij.kotlin.probablebugs.unusedexpression.UnusedExpression
import by.overpas.detekt.intellij.kotlin.probablebugs.unusedlambdaexpression.UnusedLambdaExpression
import by.overpas.detekt.intellij.kotlin.probablebugs.unusedlambdaexpressionbody.UnusedLambdaExpressionBody
import by.overpas.detekt.intellij.kotlin.probablebugs.uselesscalloncollection.UselessCallOnCollection
import by.overpas.detekt.intellij.kotlin.probablebugs.variableneverread.VariableNeverRead
import by.overpas.detekt.intellij.kotlin.probablebugs.wrapunaryoperator.WrapUnaryOperator
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.probable.bugs")
class ProbableBugsRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-kotlin-probable-bugs")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::AmbiguousNonLocalJump,
                ::ArrayInDataClass,
                ::AssignedValueIsNeverRead,
                ::CanSealedSubClassBeObject,
                ::ConflictingExtensionProperty,
                ::ConvertNaNEquality,
                ::DataClassPrivateConstructor,
                ::DelegationToVarProperty,
                ::DuplicateArgumentsInSetOfAndMapOfFunctions,
                ::EmptyRange,
                ::FilterIsInstanceResultIsAlwaysEmpty,
                ::ForEachParameterNotUsed,
                ::ImplicitNullableNothingType,
                ::IncompleteDestructuring,
                ::JavaIoSerializableObjectMustHaveReadResolve,
                ::KotlinArrayHashCode,
                ::KotlinArrayToString,
                ::KotlinEqualsBetweenInconvertibleTypes,
                ::KotlinMisorderedAssertEqualsArguments,
                ::KotlinThrowableNotThrown,
                ::LateinitVarOverridesLateinitVar,
                ::LazyWithoutDelegation,
                ::MainFunctionReturnUnit,
                ::RecursiveEqualsCall,
                ::RecursivePropertyAccessor,
                ::RedundantLabel,
                ::SelfAssignment,
                ::SelfReferenceConstructorParameter,
                ::SetterBackingFieldAssignment,
                ::SuspiciousCallOnCollectionToAddOrRemovePath,
                ::SuspiciousCascadingIf,
                ::SuspiciousCollectionReassignment,
                ::SuspiciousEqualsCombination,
                ::SuspiciousGetterForMutableObject,
                ::SuspiciousJavaClassCallableReference,
                ::SuspiciousVarProperty,
                ::UnusedDataClassCopyResult,
                ::UnusedEquals,
                ::UnusedExpression,
                ::UnusedLambdaExpression,
                ::UnusedLambdaExpressionBody,
                ::UselessCallOnCollection,
                ::VariableNeverRead,
                ::WrapUnaryOperator,
            ),
        )
}
